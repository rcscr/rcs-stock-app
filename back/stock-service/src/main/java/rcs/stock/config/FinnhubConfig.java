package rcs.stock.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import rcs.stock.services.FinnhubService;

import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class FinnhubConfig {

    @Autowired
    private ApplicationContext context;

    @Value("${services.finnhub.baseUrl}")
    private String finnhubBaseUrl;

    @Value("${services.finnhub.token}")
    private String finnhubApiToken;

    @Value("${services.finnhub.exchanges}")
    private String exchanges;

    @Bean
    public FinnhubService getFinnhubService() {
        return new FinnhubService(
                finnhubBaseUrl,
                finnhubApiToken,
                Arrays.stream(exchanges.split(",")).collect(Collectors.toSet()),
                context.getBean(RestTemplate.class));
    }
}
