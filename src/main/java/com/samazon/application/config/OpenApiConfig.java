package com.samazon.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI samazonOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Samazon API")
                        .version("1.0.0")
                        .description("API documentation for Samazon e-commerce application")
                        .contact(new Contact()
                                .name("Samuel Ji")
                                .email("samuelji@acm.org")
                                .url("https://samuelji.vercel.app"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .addServersItem(new Server()
                        .url("http://samazon.us-east-1.elasticbeanstalk.com")
                        .description("Development server"));
    }
}