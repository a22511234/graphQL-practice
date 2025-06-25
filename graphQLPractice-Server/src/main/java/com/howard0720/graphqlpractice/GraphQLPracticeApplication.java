package com.howard0720.graphqlpractice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GraphQLPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQLPracticeApplication.class, args);
        System.out.println("\n==============================================");
        System.out.println("🚀 Spring Boot GraphQL 應用程式已啟動！");
        System.out.println("==============================================");
        System.out.println("📊 GraphiQL 介面: http://localhost:8080/graphiql");
        System.out.println("🔗 GraphQL 端點: http://localhost:8080/graphql");
        System.out.println("🗄️  H2 控制台:   http://localhost:8080/h2-console");
        System.out.println("==============================================\n");
    }

}
