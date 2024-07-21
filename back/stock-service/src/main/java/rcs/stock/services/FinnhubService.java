package rcs.stock.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rcs.stock.models.StockPrice;
import rcs.stock.services.exceptions.StockNotFoundException;
import rcs.stock.utils.SearchableMap;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FinnhubService {

    public record QuoteResponse(
            double c,  // current price
            double d,  // change
            double dp, // percent change
            double h,  // high price of the day
            double l,  // low price of the day
            double o,  // open price of the day
            double pc, // previous close price
            long t){}  // timestamp

    public record StockResponse(
            String currency,
            String description,
            String displaySymbol,
            String figi,
            String isin,
            String mic,
            String symbol,
            String symbol2,
            String type) {

        @JsonIgnore
        public Collection<String> getSearch() {
            return Stream.concat(
                    Stream.of(symbol.toLowerCase()),
                    Arrays.stream(description.toLowerCase().split(" ")))
                    .collect(Collectors.toSet());
        }
    }

    private final Logger logger = LoggerFactory.getLogger(FinnhubService.class);

    private final SearchableMap<String, StockResponse> stocksSearchableMap =
            new SearchableMap<>(StockResponse::getSearch);

    private final String finnhubBaseUrl;
    private final String finnhubApiToken;
    private final Set<String> exchanges;
    private final RestTemplate restTemplate;

    public FinnhubService(
            String finnhubBaseUrl,
            String finnhubApiToken,
            Set<String> exchanges,
            RestTemplate restTemplate) {
        this.finnhubBaseUrl = finnhubBaseUrl;
        this.exchanges = exchanges;
        this.finnhubApiToken = finnhubApiToken;
        this.restTemplate = restTemplate;
        populateStocksSearchableMap();
    }

    public boolean stockExists(String stock) {
        return null != stocksSearchableMap.get(stock.toLowerCase());
    }

    public StockPrice getPrice(String symbol) {
        if (!stockExists(symbol)) {
            throw new StockNotFoundException(symbol);
        }

        ResponseEntity<QuoteResponse> response = restTemplate.getForEntity(
                buildQuoteUrl(symbol),
                QuoteResponse.class);

        QuoteResponse quote = response.getBody();

        StockResponse stockInfo = stocksSearchableMap.get(symbol.toLowerCase());

        return new StockPrice(
                stockInfo.symbol(),
                stockInfo.currency(),
                quote.c(),
                quote.dp());
    }

    public List<StockResponse> searchStocks(String search, int limit) {
        return stocksSearchableMap
                .searchBySubstring(search.toLowerCase(), search.length() > 3 ? search.length() - 1 : search.length())
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    private String buildQuoteUrl(String stock) {
        return finnhubBaseUrl + "/quote?symbol=" + stock + "&token=" + finnhubApiToken;
    }

    private String buildStockUrl(String exchange) {
        return finnhubBaseUrl + "/stock/symbol?exchange=" + exchange + "&token=" + finnhubApiToken;
    }

    private void populateStocksSearchableMap() {
        exchanges.forEach(exchange -> {
            logger.info("Fetching stocks from the exchange " + exchange);

            ResponseEntity<StockResponse[]> response = restTemplate.getForEntity(
                    buildStockUrl(exchange),
                    StockResponse[].class);

            logger.info("Done fetching stocks.");

            Arrays.stream(response.getBody())
                    .forEach(stock -> stocksSearchableMap.put(stock.symbol().toLowerCase(), stock));
        });
    }
}
