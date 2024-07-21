package rcs.auth.testutils;

import org.junit.Before;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class InMemoryDataSourceTestBase {

    protected JdbcTemplate template;

    @Before
    public final void setupTemplate() {
        template = new JdbcTemplate(dataSource());
    }

    private DataSource dataSource() {
        return DataSourceBuilder.create()
                .username("username")
                .password("password")
                .driverClassName("org.h2.Driver")
                .url("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE")
                .build();
    }
}
