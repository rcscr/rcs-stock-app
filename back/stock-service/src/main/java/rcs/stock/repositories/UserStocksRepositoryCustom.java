package rcs.stock.repositories;

import rcs.stock.models.UserStocks;

import java.util.Map;

public interface UserStocksRepositoryCustom {

    UserStocks followStock(String username, String stock);
    UserStocks unfollowStock(String username, String stock);
    Map<String, Long> getStocksWithFollowers();
}
