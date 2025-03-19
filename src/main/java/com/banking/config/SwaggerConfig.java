package com.banking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI mySwaggerConfig() {
        return new OpenAPI().info(new Info()
                        .title("Banking Service")
                        .description("By Tushar"))
                .servers(Arrays.asList(new io.swagger.v3.oas.models.servers.Server().
                                url("http://localhost:8080").description("local"),
                        new Server().url("http://localhost:8081").description("live")));
    }

    @Bean
    public ModelMapper getModelMapper(){
        return new ModelMapper();
    }
}
