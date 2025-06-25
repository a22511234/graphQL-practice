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
    public WebClient webClient() {
        // 配置 HttpClient 解決連線問題
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 10秒連線超時
                .responseTimeout(Duration.ofSeconds(30)) // 30秒回應超時
                .keepAlive(true);

        return WebClient.builder()
                .baseUrl("http://127.0.0.1:8080") // 使用 IPv4 避免 IPv6 問題
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Accept", "application/json")
                .defaultHeader("User-Agent", "Spring-GraphQL-Client/1.0")
                // 增加緩衝區大小以處理大型回應
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                        .build())
                .build();
    }

    @Bean
    public HttpGraphQlClient graphQlClient() {
        return HttpGraphQlClient.builder(WebClient.builder()
                        .baseUrl("http://localhost:8080/graphql") // 請改成你的 server 真實地址
                        .build())
                .build();
    }
}
