package com.example.graphqlpracticeclient;

import com.example.graphqlpracticeclient.DTO.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(ClientController.class)
public class ClientControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private GraphQLClientService graphQLClientService;

    private UserDTO user;
    private PostDTO post;

    @BeforeEach
    void setup() {
        user = new UserDTO();
        user.setId("1");
        user.setName("張小明");
        user.setEmail("ming@example.com");
        user.setPhone("0912345678");

        post = new PostDTO();
        post.setId("100");
        post.setTitle("Test Title");
        post.setContent("Test Content");
        post.setAuthor(user);
        post.setStatus(PostDTO.PostStatus.DRAFT);
    }

    @Test
    void testGetAllUsers() {
        when(graphQLClientService.getAllUsers()).thenReturn(Mono.just(List.of(user)));

        webTestClient.get().uri("/api/users")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("張小明");
    }

    @Test
    void testGetUserById() {
        when(graphQLClientService.getUserById("1")).thenReturn(Mono.just(user));

        webTestClient.get().uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.email").isEqualTo("ming@example.com");
    }

    @Test
    void testGetUserByEmail() {
        when(graphQLClientService.getUserByEmail("ming@example.com")).thenReturn(Mono.just(user));

        webTestClient.get().uri("/api/users/email/ming@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("張小明");
    }

    @Test
    void testSearchUsers() {
        when(graphQLClientService.searchUsers("張")).thenReturn(Mono.just(List.of(user)));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/users/search").queryParam("keyword", "張").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].email").isEqualTo("ming@example.com");
    }

    @Test
    void testCreateUser() {
        CreateUserInputDTO input = new CreateUserInputDTO();
        input.setName("張小明");
        input.setEmail("ming@example.com");
        input.setPhone("0912345678");

        when(graphQLClientService.createUser(any())).thenReturn(Mono.just(user));

        webTestClient.post().uri("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("張小明");
    }

    @Test
    void testUpdateUser() {
        when(graphQLClientService.updateUser(eq("1"), eq("李小華"), eq("hua@example.com"), eq("0923456789")))
                .thenReturn(Mono.just(user));

        webTestClient.put().uri("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of(
                        "name", "李小華",
                        "email", "hua@example.com",
                        "phone", "0923456789")
                )
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void testDeleteUser() {
        when(graphQLClientService.deleteUser("1")).thenReturn(Mono.just(true));

        webTestClient.delete().uri("/api/users/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.deleted").isEqualTo(true);
    }

    @Test
    void testGetAllPosts() {
        when(graphQLClientService.getAllPosts()).thenReturn(Mono.just(List.of(post)));

        webTestClient.get().uri("/api/posts")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].title").isEqualTo("Test Title");
    }

    @Test
    void testGetPostById() {
        when(graphQLClientService.getPostById("100")).thenReturn(Mono.just(post));

        webTestClient.get().uri("/api/posts/100")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Test Title");
    }

    @Test
    void testCreatePost() {
        CreatePostInputDTO input = new CreatePostInputDTO();
        input.setTitle("Test Title");
        input.setContent("Test Content");
        input.setAuthorId("1");

        when(graphQLClientService.createPost(any())).thenReturn(Mono.just(post));

        webTestClient.post().uri("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(input)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isEqualTo("Test Content");
    }

    @Test
    void testPublishPost() {
        post.setStatus(PostDTO.PostStatus.PUBLISHED);
        when(graphQLClientService.publishPost("100")).thenReturn(Mono.just(post));

        webTestClient.put().uri("/api/posts/100/publish")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("PUBLISHED");
    }

    @Test
    void testArchivePost() {
        post.setStatus(PostDTO.PostStatus.ARCHIVED);
        when(graphQLClientService.archivePost("100")).thenReturn(Mono.just(post));

        webTestClient.put().uri("/api/posts/100/archive")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("ARCHIVED");
    }

    @Test
    void testDeletePost() {
        when(graphQLClientService.deletePost("100")).thenReturn(Mono.just(true));

        webTestClient.delete().uri("/api/posts/100")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.deleted").isEqualTo(true);
    }

    @Test
    void testGetPostsByAuthor() {
        when(graphQLClientService.getPostsByAuthor("1")).thenReturn(Mono.just(List.of(post)));

        webTestClient.get().uri("/api/posts/author/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("100");
    }

    @Test
    void testGetPostsByStatus() {
        when(graphQLClientService.getPostsByStatus(PostDTO.PostStatus.DRAFT)).thenReturn(Mono.just(List.of(post)));

        webTestClient.get().uri("/api/posts/status/DRAFT")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].status").isEqualTo("DRAFT");
    }

    @Test
    void testSearchPosts() {
        when(graphQLClientService.searchPosts("Test")).thenReturn(Mono.just(List.of(post)));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/posts/search").queryParam("keyword", "Test").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].title").isEqualTo("Test Title");
    }

    @Test
    void testHealth() {
        webTestClient.get().uri("/api/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    void testInfo() {
        webTestClient.get().uri("/api/info")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("GraphQL Client Service");
    }
}
