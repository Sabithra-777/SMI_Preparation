package com.smartinterview.config;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource(Environment environment) throws URISyntaxException {
        String jdbcUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        String username = environment.getProperty("SPRING_DATASOURCE_USERNAME");
        String password = environment.getProperty("SPRING_DATASOURCE_PASSWORD");
        String databaseUrl = environment.getProperty("DATABASE_URL");

        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            return DataSourceBuilder.create()
                    .driverClassName("org.postgresql.Driver")
                    .url(jdbcUrl)
                    .username(username)
                    .password(password)
                    .build();
        }

        if (databaseUrl != null && !databaseUrl.isBlank()) {
            if (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://")) {
                if (databaseUrl.startsWith("postgres://")) {
                    databaseUrl = "postgresql://" + databaseUrl.substring("postgres://".length());
                }
                URI uri = new URI(databaseUrl);
                String[] userInfo = uri.getUserInfo().split(":", 2);
                String dbUsername = userInfo[0];
                String dbPassword = userInfo.length > 1 ? userInfo[1] : "";
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String dbName = uri.getPath();
                String query = uri.getQuery() != null ? "?" + uri.getQuery() : "";
                String url = String.format("jdbc:postgresql://%s:%s%s%s", host, port, dbName, query);
                return DataSourceBuilder.create()
                        .driverClassName("org.postgresql.Driver")
                        .url(url)
                        .username(dbUsername)
                        .password(dbPassword)
                        .build();
            }
        }

        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/smart_interview")
                .username("postgres")
                .password("password")
                .build();
    }
}
