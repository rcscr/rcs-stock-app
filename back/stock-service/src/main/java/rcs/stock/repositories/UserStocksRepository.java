package rcs.stock.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import rcs.stock.models.UserStocks;

public interface UserStocksRepository extends MongoRepository<UserStocks, String>, UserStocksRepositoryCustom {
}
