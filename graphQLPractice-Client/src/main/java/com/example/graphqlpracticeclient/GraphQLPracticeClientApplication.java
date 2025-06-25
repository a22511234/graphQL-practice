package com.example.graphqlpracticeclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphQLPracticeClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQLPracticeClientApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("🚀 GraphQL Client 應用程式已啟動！");
        System.out.println("==============================================");
        System.out.println("🌐 Web 介面:     http://localhost:8081/web");
        System.out.println("📊 統計儀表板:   http://localhost:8081/web/dashboard");
        System.out.println("🔗 REST API:     http://localhost:8081/api");
        System.out.println("❤️  健康檢查:     http://localhost:8081/api/health");
        System.out.println("📋 服務資訊:     http://localhost:8081/api/info");
        System.out.println("==============================================");
        System.out.println("📡 連線至服務端: http://localhost:8080/graphql");
        System.out.println("==============================================\n");
    }

}
