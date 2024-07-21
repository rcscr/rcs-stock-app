package rcs.stock.services;

import org.springframework.stereotype.Service;
import rcs.stock.models.StockWithFollowers;
import rcs.stock.models.UserStocks;
import rcs.stock.repositories.UserStocksRepository;
import rcs.stock.services.exceptions.StockNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserStocksService {

    private static final int numberOfPopularStocks = 10;

    private final UserStocksRepository userStocksRepository;
    private final FinnhubService finnhubService;

    public UserStocksService(UserStocksRepository userStocksRepository, FinnhubService finnhubService) {
        this.userStocksRepository = userStocksRepository;
        this.finnhubService = finnhubService;
    }

    public UserStocks getUserStocks(String username) {
        return userStocksRepository.findById(username)
                .orElse(new UserStocks(username, Set.of())); // user is registered but hasn't followed any stocks
    }

    public UserStocks followStock(String username, String stock) {
        if (!finnhubService.stockExists(stock)) {
            throw new StockNotFoundException(stock);
        }
        return userStocksRepository.followStock(username, stock);
    }

    public UserStocks unfollowStock(String username, String stock) {
        return userStocksRepository.unfollowStock(username, stock);
    }

    public List<StockWithFollowers> getMostPopularStocks() {
        return userStocksRepository.getStocksWithFollowers()
                .entrySet()
                .stream()
                .sorted(Comparator.<Map.Entry<String, Long>>comparingLong(Map.Entry::getValue).reversed())
                .limit(numberOfPopularStocks)
                .map(entry -> new StockWithFollowers(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
