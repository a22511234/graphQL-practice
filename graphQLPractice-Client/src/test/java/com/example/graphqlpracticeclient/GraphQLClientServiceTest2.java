package com.example.graphqlpracticeclient;

import com.example.graphqlpracticeclient.DTO.CreateUserInputDTO;
import com.example.graphqlpracticeclient.DTO.UserDTO;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit5.HoverflyExtension;
import io.specto.hoverfly.junit5.api.HoverflyCapture;
import io.specto.hoverfly.junit5.api.HoverflyConfig;
import io.specto.hoverfly.junit5.api.HoverflySimulate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(HoverflyExtension.class)
//@HoverflySimulate(source = @HoverflySimulate.Source(value = "test-service-https.json"), config = @HoverflyConfig(proxyLocalHost = true, proxyPort = 8080, destination = "localhost:8080"))
@HoverflyCapture(path = "resources/test/hoverfly",
        filename = "captured-simulations.json",
        config = @HoverflyConfig(captureAllHeaders = true, proxyLocalHost = true))
public class GraphQLClientServiceTest2 {
    private GraphQLClientService graphQLClientService;

    @BeforeEach
    void setup(Hoverfly hoverfly) {
        HttpGraphQlClient client = HttpGraphQlClient.builder()
                .url("http://localhost:8080/graphql") // 使用 hoverflyInfo.getProxyPort()
                .build();

        graphQLClientService = new GraphQLClientService();
        ReflectionTestUtils.setField(graphQLClientService, "graphQlClient", client);
    }

    @Test
    void testGetUserById() {
        UserDTO user = graphQLClientService.getUserById("2").block(); // 等待結果出來

        assertNotNull(user);
        assertEquals("2", user.getId());
        assertEquals("hua@example.com", user.getEmail());
        ;
    }

    @Test
    void testGetUserByEmail() {
        StepVerifier.create(graphQLClientService.getUserByEmail("test@example.com"))
                .expectNextMatches(user -> user.getName().equals("Test User"))
                .verifyComplete();
    }

    @Test
    void testCreateUser() {
        CreateUserInputDTO input = new CreateUserInputDTO();
        input.setName("New User");
        input.setEmail("newuser@example.com");
        input.setPhone("987654321");

        StepVerifier.create(graphQLClientService.createUser(input))
                .expectNextMatches(user -> user.getEmail().equals("newuser@example.com"))
                .verifyComplete();
    }
}
