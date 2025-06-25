package com.example.graphqlpracticeclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Service
public class HealthCheckService {

    @Autowired(required = true)
    private WebClient webClient;
    @Autowired
    private HttpGraphQlClient graphQlClient;

    /**
     * 檢查 GraphQL 服務端是否可用
     */
    public Mono<Boolean> checkGraphQLServerHealth() {
        return webClient
                .get()
                .uri("/actuator/health") // Spring Boot Actuator 健康檢查端點
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> "UP".equals(response.get("status")))
                .timeout(Duration.ofSeconds(5))
                .onErrorReturn(false);
    }

    /**
     * 測試 GraphQL 連線
     */
    public Mono<Boolean> testGraphQLConnection() {
        String testQuery = """
                {
                    "query": "{ __schema { queryType { name } } }"
                }
                """;

        return graphQlClient
                .document(testQuery)
                .retrieve("__schema")      // 指定要取得的欄位名稱
                .toEntity(Map.class)       // 轉成 Map 來解析
                .map(response -> response != null && !response.isEmpty())
                .timeout(Duration.ofSeconds(10))
                .onErrorReturn(false);
    }
}