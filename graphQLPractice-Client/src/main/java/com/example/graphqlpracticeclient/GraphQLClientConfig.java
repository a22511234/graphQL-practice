package com.example.graphqlpracticeclient;

import io.netty.channel.ChannelOption;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
public class GraphQLClientConfig {

    @Bean
    public HttpGraphQlClient graphQlClient() {
        return HttpGraphQlClient.builder(WebClient.builder()
                        .baseUrl("http://localhost:8080/graphql") // 請改成你的 server 真實地址
                        .build())
                .build();
    }
}
