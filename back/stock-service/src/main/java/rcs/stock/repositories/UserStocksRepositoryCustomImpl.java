package rcs.stock.repositories;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import rcs.stock.models.UserStocks;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserStocksRepositoryCustomImpl implements UserStocksRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public UserStocksRepositoryCustomImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public UserStocks followStock(String username, String stock) {
        return mongoTemplate.findAndModify(
                queryByUsername(username),
                new Update().addToSet(UserStocks.Fields.stocks, stock),
                new FindAndModifyOptions().returnNew(true).upsert(true),
                UserStocks.class);
    }

    @Override
    public UserStocks unfollowStock(String username, String stock) {
        return mongoTemplate.findAndModify(
                queryByUsername(username),
                new Update().pull(UserStocks.Fields.stocks, stock),
                new FindAndModifyOptions().returnNew(true).upsert(true),
                UserStocks.class);
    }

    @Override
    public Map<String, Long> getStocksWithFollowers() {
        // this could probably be an aggregation
        return mongoTemplate.findAll(UserStocks.class)
                .stream()
                .map(UserStocks::getStocks)
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Query queryByUsername(String username) {
        return Query.query(Criteria.where(UserStocks.Fields.username).is(username));
    }
}
