package com.example.graphqlpracticeclient;

import com.example.graphqlpracticeclient.DTO.CreatePostInputDTO;
import com.example.graphqlpracticeclient.DTO.CreateUserInputDTO;
import com.example.graphqlpracticeclient.DTO.PostDTO;
import com.example.graphqlpracticeclient.DTO.UserDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // 允許跨域請求
public class ClientController {

    @Autowired
    private GraphQLClientService graphQLClientService;

    // ==================== 用戶 API ====================

    /**
     * 取得所有用戶
     */
    @GetMapping("/users")
    public Mono<ResponseEntity<List<UserDTO>>> getAllUsers() {
        return graphQLClientService.getAllUsers()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 根據 ID 取得用戶
     */
    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable String id) {
        return graphQLClientService.getUserById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 根據 Email 取得用戶
     */
    @GetMapping("/users/email/{email}")
    public Mono<ResponseEntity<UserDTO>> getUserByEmail(@PathVariable String email) {
        return graphQLClientService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 搜尋用戶
     */
    @GetMapping("/users/search")
    public Mono<ResponseEntity<List<UserDTO>>> searchUsers(@RequestParam String keyword) {
        return graphQLClientService.searchUsers(keyword)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 創建用戶
     */
    @PostMapping("/users")
    public Mono<ResponseEntity<UserDTO>> createUser(@Valid @RequestBody CreateUserInputDTO input) {
        return graphQLClientService.createUser(input)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    /**
     * 更新用戶
     */
    @PutMapping("/users/{id}")
    public Mono<ResponseEntity<UserDTO>> updateUser(
            @PathVariable String id,
            @RequestBody Map<String, String> updates) {

        String name = updates.get("name");
        String email = updates.get("email");
        String phone = updates.get("phone");

        return graphQLClientService.updateUser(id, name, email, phone)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    /**
     * 刪除用戶
     */
    @DeleteMapping("/users/{id}")
    public Mono<ResponseEntity<Map<String, Boolean>>> deleteUser(@PathVariable String id) {
        return graphQLClientService.deleteUser(id)
                .map(result -> ResponseEntity.ok(Map.of("deleted", result)))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    // ==================== 文章 API ====================

    /**
     * 取得所有文章
     */
    @GetMapping("/posts")
    public Mono<ResponseEntity<List<PostDTO>>> getAllPosts() {
        return graphQLClientService.getAllPosts()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 根據 ID 取得文章
     */
    @GetMapping("/posts/{id}")
    public Mono<ResponseEntity<PostDTO>> getPostById(@PathVariable String id) {
        return graphQLClientService.getPostById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 根據作者 ID 取得文章
     */
    @GetMapping("/posts/author/{authorId}")
    public Mono<ResponseEntity<List<PostDTO>>> getPostsByAuthor(@PathVariable String authorId) {
        return graphQLClientService.getPostsByAuthor(authorId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 根據狀態取得文章
     */
    @GetMapping("/posts/status/{status}")
    public Mono<ResponseEntity<List<PostDTO>>> getPostsByStatus(@PathVariable String status) {
        try {
            PostDTO.PostStatus postStatus = PostDTO.PostStatus.valueOf(status.toUpperCase());
            return graphQLClientService.getPostsByStatus(postStatus)
                    .map(ResponseEntity::ok)
                    .defaultIfEmpty(ResponseEntity.notFound().build())
                    .onErrorReturn(ResponseEntity.internalServerError().build());
        } catch (IllegalArgumentException e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }

    /**
     * 搜尋文章
     */
    @GetMapping("/posts/search")
    public Mono<ResponseEntity<List<PostDTO>>> searchPosts(@RequestParam String keyword) {
        return graphQLClientService.searchPosts(keyword)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    /**
     * 創建文章
     */
    @PostMapping("/posts")
    public Mono<ResponseEntity<PostDTO>> createPost(@Valid @RequestBody CreatePostInputDTO input) {
        return graphQLClientService.createPost(input)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    /**
     * 發布文章
     */
    @PutMapping("/posts/{id}/publish")
    public Mono<ResponseEntity<PostDTO>> publishPost(@PathVariable String id) {
        return graphQLClientService.publishPost(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    /**
     * 歸檔文章
     */
    @PutMapping("/posts/{id}/archive")
    public Mono<ResponseEntity<PostDTO>> archivePost(@PathVariable String id) {
        return graphQLClientService.archivePost(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    /**
     * 刪除文章
     */
    @DeleteMapping("/posts/{id}")
    public Mono<ResponseEntity<Map<String, Boolean>>> deletePost(@PathVariable String id) {
        return graphQLClientService.deletePost(id)
                .map(result -> ResponseEntity.ok(Map.of("deleted", result)))
                .defaultIfEmpty(ResponseEntity.notFound().build())
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    // ==================== 健康檢查 ====================

    /**
     * 健康檢查端點
     */
    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, String>>> health() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "GraphQL Client",
                "timestamp", java.time.Instant.now().toString()
        )));
    }

    /**
     * 取得服務資訊
     */
    @GetMapping("/info")
    public Mono<ResponseEntity<Map<String, Object>>> info() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "name", "GraphQL Client Service",
                "version", "1.0.0",
                "description", "Spring Boot GraphQL Client for User and Post Management",
                "graphql-server", "http://localhost:8080/graphql",
                "endpoints", Map.of(
                        "users", "/api/users",
                        "posts", "/api/posts",
                        "health", "/api/health"
                )
        )));
    }
}