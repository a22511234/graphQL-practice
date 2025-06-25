package com.example.graphqlpracticeclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/diagnostic")
public class DiagnosticController {

    @Autowired
    private HealthCheckService healthCheckService;

    /**
     * 診斷 GraphQL 服務端連線狀態
     */
    @GetMapping("/connection")
    public Mono<Map<String, Object>> diagnoseConnection() {
        Map<String, Object> result = new HashMap<>();

        return healthCheckService.checkGraphQLServerHealth()
                .zipWith(healthCheckService.testGraphQLConnection())
                .map(tuple -> {
                    boolean healthCheck = tuple.getT1();
                    boolean graphqlTest = tuple.getT2();

                    result.put("timestamp", java.time.Instant.now().toString());
                    result.put("healthCheckPassed", healthCheck);
                    result.put("graphqlConnectionWorking", graphqlTest);
                    result.put("overallStatus", healthCheck && graphqlTest ? "HEALTHY" : "UNHEALTHY");

                    if (!healthCheck) {
                        result.put("healthCheckMessage", "無法連接到服務端健康檢查端點");
                    }

                    if (!graphqlTest) {
                        result.put("graphqlMessage", "GraphQL 端點無法正常回應");
                    }

                    if (healthCheck && graphqlTest) {
                        result.put("message", "所有連線測試通過！");
                    } else {
                        result.put("troubleshooting", Map.of(
                                "step1", "確認服務端是否在 http://127.0.0.1:8080 運行",
                                "step2", "檢查防火牆設定",
                                "step3", "確認服務端 GraphQL 端點 /graphql 可用",
                                "step4", "檢查網路連線"
                        ));
                    }

                    return result;
                })
                .onErrorReturn(Map.of(
                        "overallStatus", "ERROR",
                        "error", "連線診斷過程中發生錯誤",
                        "recommendation", "請檢查服務端是否正常運行"
                ));
    }

    /**
     * 網路配置資訊
     */
    @GetMapping("/network-info")
    public Map<String, Object> getNetworkInfo() {
        Map<String, Object> info = new HashMap<>();

        info.put("clientPort", "8081");
        info.put("serverExpectedUrl", "http://127.0.0.1:8080");
        info.put("graphqlEndpoint", "/graphql");
        info.put("ipv4Preferred", System.getProperty("java.net.preferIPv4Stack", "false"));

        // 檢查常見的網路問題
        Map<String, String> commonIssues = new HashMap<>();
        commonIssues.put("localhost vs 127.0.0.1", "在某些系統上 localhost 可能解析為 IPv6，建議使用 127.0.0.1");
        commonIssues.put("防火牆", "確認防火牆允許 8080 和 8081 端口");
        commonIssues.put("端口占用", "確認 8080 端口未被其他程式占用");
        commonIssues.put("服務啟動順序", "必須先啟動服務端(8080)，再啟動客戶端(8081)");

        info.put("commonIssues", commonIssues);

        return info;
    }
}