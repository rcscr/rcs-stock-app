package rcs.stock.controllers;

import org.springframework.web.bind.annotation.*;
import rcs.auth.api.AuthUtils;
import rcs.auth.api.exceptions.UnauthorizedException;
import rcs.stock.models.StockWithFollowers;
import rcs.stock.models.UserStocks;
import rcs.stock.services.FinnhubService;
import rcs.stock.services.UserStocksService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class StocksRestController {

    private final FinnhubService finnhubService;
    private final UserStocksService userStocksService;
    private final AuthUtils authUtils;

    public StocksRestController(
            FinnhubService finnhubService,
            UserStocksService userStocksService,
            AuthUtils authUtils) {
        this.finnhubService = finnhubService;
        this.userStocksService = userStocksService;
        this.authUtils = authUtils;
    }

    @GetMapping("/stocks-with-followers")
    public List<StockWithFollowers> getMostPopularStocks() {
        return userStocksService.getMostPopularStocks();

    }

    @GetMapping("/stocks")
    public List<FinnhubService.StockResponse> searchStocks(
            @RequestParam(required = true) String search,
            @RequestParam(required = true) int limit) {
        return finnhubService.searchStocks(search, limit);
    }

    @GetMapping("/my-stocks")
    public UserStocks getMyStocks(HttpServletRequest request) {
        return authUtils.tryGetLoggedInUser(request)
                .map(user -> userStocksService.getUserStocks(user.getUsername()))
                .orElseThrow(UnauthorizedException::new);
    }

    @PutMapping("/my-stocks")
    public UserStocks followStock(
            HttpServletRequest request,
            @RequestParam(required = true) String stock) {

        return authUtils.tryGetLoggedInUser(request)
                .map(user -> userStocksService.followStock(user.getUsername(), stock))
                .orElseThrow(UnauthorizedException::new);
    }

    @DeleteMapping("/my-stocks")
    public UserStocks unfollowStock(
            HttpServletRequest request,
            @RequestParam(required = true) String stock) {

        return authUtils.tryGetLoggedInUser(request)
                .map(user -> userStocksService.unfollowStock(user.getUsername(), stock))
                .orElseThrow(UnauthorizedException::new);
    }
}
