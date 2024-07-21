package rcs.stock.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "user-stocks")
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class UserStocks {

    public static final class Fields {
        public static final String username = "username";
        public static final String stocks = "stocks";
    }

    @Id
    private String username;
    private Set<String> stocks;
}
