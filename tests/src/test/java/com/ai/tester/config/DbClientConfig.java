package com.ai.tester.config;

import com.ai.tester.client.db.DbClient;
import com.ai.tester.client.db.H2DbClient;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DbClientConfig {

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public ObjectMapper dbObjectMapper() {
        return JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .addModule(new JavaTimeModule())
            .build();
    }

    @Bean
    public DbClient dbClient(JdbcTemplate jdbcTemplate, ObjectMapper dbObjectMapper) {
        return new H2DbClient(jdbcTemplate, dbObjectMapper);
    }
}
