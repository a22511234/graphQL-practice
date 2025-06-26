package com.example.graphqlpracticeclient;

import com.example.graphqlpracticeclient.DTO.CreateUserInputDTO;
import com.example.graphqlpracticeclient.DTO.UserDTO;
import io.specto.hoverfly.junit.core.Hoverfly;
import io.specto.hoverfly.junit.core.HoverflyMode;
import io.specto.hoverfly.junit5.HoverflyExtension;
import io.specto.hoverfly.junit5.api.HoverflyConfig;
import io.specto.hoverfly.junit5.api.HoverflyCore;
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
@HoverflySimulate(source = @HoverflySimulate.Source(value = "test-service-https.json"), config = @HoverflyConfig(proxyLocalHost = true, proxyPort = 8090, destination = "localhost:8090"))
public class GraphQLClientServiceTest2 {
    private GraphQLClientService graphQLClientService;

    @BeforeEach
    void setup(Hoverfly hoverfly) {
        HttpGraphQlClient client = HttpGraphQlClient.builder()
                .url("http://localhost:8090/graphql") // 使用 hoverflyInfo.getProxyPort()
                .build();

        graphQLClientService = new GraphQLClientService();
        ReflectionTestUtils.setField(graphQLClientService, "graphQlClient", client);
    }

    @Test
    void testGetUserById() {
        UserDTO user = graphQLClientService.getUserById("1").block(); // 等待結果出來

        assertNotNull(user);
        assertEquals("1", user.getId());
        assertEquals("test@example.com", user.getEmail());
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
