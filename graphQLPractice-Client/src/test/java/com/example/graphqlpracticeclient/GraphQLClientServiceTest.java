package com.example.graphqlpracticeclient;

import com.example.graphqlpracticeclient.DTO.CreatePostInputDTO;
import com.example.graphqlpracticeclient.DTO.CreateUserInputDTO;
import com.example.graphqlpracticeclient.DTO.PostDTO;
import com.example.graphqlpracticeclient.DTO.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.graphql.client.HttpGraphQlClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphQLClientServiceTest {

    @Mock
    private HttpGraphQlClient mockGraphQlClient;

    @Mock
    private HttpGraphQlClient.RequestSpec mockRequestSpec;

    @Mock
    private HttpGraphQlClient.RetrieveSpec mockRetrieveSpec;

    private GraphQLClientService graphQLClientService;

    @BeforeEach
    void setUp() {
        graphQLClientService = new GraphQLClientService();
        // 使用反射設置私有字段
        setPrivateField(graphQLClientService, "graphQlClient", mockGraphQlClient);
    }

    // ==================== 用戶操作測試 ====================

    @Test
    void testGetAllUsers_Success() {
        // Given
        UserDTO user1 = createTestUser("1", "張小明", "ming@example.com", "0912345678");
        UserDTO user2 = createTestUser("2", "李小華", "hua@example.com", "0923456789");
        List<UserDTO> expectedUsers = Arrays.asList(user1, user2);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("users"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(UserDTO.class)).thenReturn(Mono.just(expectedUsers));

        // When & Then
        StepVerifier.create(graphQLClientService.getAllUsers())
                .expectNext(expectedUsers)
                .verifyComplete();

        verify(mockGraphQlClient).document(anyString());
        verify(mockRequestSpec).retrieve("users");
        verify(mockRetrieveSpec).toEntityList(UserDTO.class);
    }

    @Test
    void testGetAllUsers_Error() {
        // Given
        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("users"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(UserDTO.class))
                .thenReturn(Mono.error(new RuntimeException("GraphQL error")));

        // When & Then
        StepVerifier.create(graphQLClientService.getAllUsers())
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testGetUserById_Success() {
        // Given
        String userId = "1";
        UserDTO expectedUser = createTestUser(userId, "張小明", "ming@example.com", "0912345678");

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("user"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(UserDTO.class)).thenReturn(Mono.just(expectedUser));

        // When & Then
        StepVerifier.create(graphQLClientService.getUserById(userId))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(mockGraphQlClient).document(anyString());
        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("id") && map.get("id").equals(userId)
        ));
        verify(mockRequestSpec).retrieve("user");
        verify(mockRetrieveSpec).toEntity(UserDTO.class);
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        String userId = "999";

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("user"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(UserDTO.class)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(graphQLClientService.getUserById(userId))
                .verifyComplete();
    }

    @Test
    void testGetUserByEmail_Success() {
        // Given
        String email = "ming@example.com";
        UserDTO expectedUser = createTestUser("1", "張小明", email, "0912345678");

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("userByEmail"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(UserDTO.class)).thenReturn(Mono.just(expectedUser));

        // When & Then
        StepVerifier.create(graphQLClientService.getUserByEmail(email))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("email") && map.get("email").equals(email)
        ));
    }

    @Test
    void testSearchUsers_Success() {
        // Given
        String keyword = "張";
        UserDTO user = createTestUser("1", "張小明", "ming@example.com", "0912345678");
        List<UserDTO> expectedUsers = Arrays.asList(user);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("searchUsers"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(UserDTO.class)).thenReturn(Mono.just(expectedUsers));

        // When & Then
        StepVerifier.create(graphQLClientService.searchUsers(keyword))
                .expectNext(expectedUsers)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("keyword") && map.get("keyword").equals(keyword)
        ));
    }

    @Test
    void testSearchUsers_EmptyResult() {
        // Given
        String keyword = "不存在";
        List<UserDTO> emptyList = Arrays.asList();

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("searchUsers"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(UserDTO.class)).thenReturn(Mono.just(emptyList));

        // When & Then
        StepVerifier.create(graphQLClientService.searchUsers(keyword))
                .expectNext(emptyList)
                .verifyComplete();
    }

    @Test
    void testCreateUser_Success() {
        // Given
        CreateUserInputDTO input = new CreateUserInputDTO("新用戶", "new@example.com", "0934567890");
        UserDTO expectedUser = createTestUser("1", "新用戶", "new@example.com", "0934567890");

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("createUser"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(UserDTO.class)).thenReturn(Mono.just(expectedUser));

        // When & Then
        StepVerifier.create(graphQLClientService.createUser(input))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> inputMap = (Map<String, Object>) map.get("input");
            return inputMap.get("name").equals("新用戶") &&
                    inputMap.get("email").equals("new@example.com") &&
                    inputMap.get("phone").equals("0934567890");
        }));
    }

    @Test
    void testCreateUser_ValidationError() {
        // Given
        CreateUserInputDTO input = new CreateUserInputDTO("", "", ""); // 無效輸入

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("createUser"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(UserDTO.class))
                .thenReturn(Mono.error(new RuntimeException("Validation failed")));

        // When & Then
        StepVerifier.create(graphQLClientService.createUser(input))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void testUpdateUser_Success() {
        // Given
        String userId = "1";
        String newName = "更新後的名字";
        String newEmail = "updated@example.com";
        String newPhone = "0987654321";

        UserDTO expectedUser = createTestUser(userId, newName, newEmail, newPhone);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("updateUser"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(UserDTO.class)).thenReturn(Mono.just(expectedUser));

        // When & Then
        StepVerifier.create(graphQLClientService.updateUser(userId, newName, newEmail, newPhone))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> inputMap = (Map<String, Object>) map.get("input");
            return inputMap.get("id").equals(userId) &&
                    inputMap.get("name").equals(newName) &&
                    inputMap.get("email").equals(newEmail) &&
                    inputMap.get("phone").equals(newPhone);
        }));
    }

    @Test
    void testUpdateUser_PartialUpdate() {
        // Given
        String userId = "1";
        String newName = "新名字";
        UserDTO expectedUser = createTestUser(userId, newName, "old@example.com", "0912345678");

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("updateUser"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(UserDTO.class)).thenReturn(Mono.just(expectedUser));

        // When & Then
        StepVerifier.create(graphQLClientService.updateUser(userId, newName, null, null))
                .expectNext(expectedUser)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> inputMap = (Map<String, Object>) map.get("input");
            return inputMap.get("id").equals(userId) &&
                    inputMap.get("name").equals(newName) &&
                    !inputMap.containsKey("email") &&
                    !inputMap.containsKey("phone");
        }));
    }

    @Test
    void testDeleteUser_Success() {
        // Given
        String userId = "1";

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("deleteUser"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(Boolean.class)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(graphQLClientService.deleteUser(userId))
                .expectNext(true)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("id") && map.get("id").equals(userId)
        ));
    }

    // ==================== 文章操作測試 ====================

    @Test
    void testGetAllPosts_Success() {
        // Given
        UserDTO author = createTestUser("1", "作者", "author@example.com", "0912345678");
        PostDTO post1 = createTestPost("1", "文章1", "內容1", author);
        PostDTO post2 = createTestPost("2", "文章2", "內容2", author);
        List<PostDTO> expectedPosts = Arrays.asList(post1, post2);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("posts"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(PostDTO.class)).thenReturn(Mono.just(expectedPosts));

        // When & Then
        StepVerifier.create(graphQLClientService.getAllPosts())
                .expectNext(expectedPosts)
                .verifyComplete();
    }

    @Test
    void testGetPostById_Success() {
        // Given
        String postId = "1";
        UserDTO author = createTestUser("1", "作者", "author@example.com", "0912345678");
        PostDTO expectedPost = createTestPost(postId, "測試文章", "測試內容", author);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("post"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(PostDTO.class)).thenReturn(Mono.just(expectedPost));

        // When & Then
        StepVerifier.create(graphQLClientService.getPostById(postId))
                .expectNext(expectedPost)
                .verifyComplete();
    }

    @Test
    void testGetPostsByAuthor_Success() {
        // Given
        String authorId = "1";
        UserDTO author = createTestUser(authorId, "作者", "author@example.com", "0912345678");
        PostDTO post = createTestPost("1", "作者的文章", "內容", author);
        List<PostDTO> expectedPosts = Arrays.asList(post);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("postsByAuthor"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(PostDTO.class)).thenReturn(Mono.just(expectedPosts));

        // When & Then
        StepVerifier.create(graphQLClientService.getPostsByAuthor(authorId))
                .expectNext(expectedPosts)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("authorId") && map.get("authorId").equals(authorId)
        ));
    }

    @Test
    void testGetPostsByStatus_Success() {
        // Given
        PostDTO.PostStatus status = PostDTO.PostStatus.PUBLISHED;
        UserDTO author = createTestUser("1", "作者", "author@example.com", "0912345678");
        PostDTO post = createTestPost("1", "已發布文章", "內容", author);
        post.setStatus(status);
        List<PostDTO> expectedPosts = Arrays.asList(post);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("postsByStatus"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(PostDTO.class)).thenReturn(Mono.just(expectedPosts));

        // When & Then
        StepVerifier.create(graphQLClientService.getPostsByStatus(status))
                .expectNext(expectedPosts)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("status") && map.get("status").equals(status.toString())
        ));
    }

    @Test
    void testSearchPosts_Success() {
        // Given
        String keyword = "Spring";
        UserDTO author = createTestUser("1", "作者", "author@example.com", "0912345678");
        PostDTO post = createTestPost("1", "Spring Boot 教學", "Spring Boot 內容", author);
        List<PostDTO> expectedPosts = Arrays.asList(post);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("searchPosts"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntityList(PostDTO.class)).thenReturn(Mono.just(expectedPosts));

        // When & Then
        StepVerifier.create(graphQLClientService.searchPosts(keyword))
                .expectNext(expectedPosts)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("keyword") && map.get("keyword").equals(keyword)
        ));
    }

    @Test
    void testCreatePost_Success() {
        // Given
        CreatePostInputDTO input = new CreatePostInputDTO("新文章", "文章內容", "1");
        input.setStatus(PostDTO.PostStatus.DRAFT);

        UserDTO author = createTestUser("1", "作者", "author@example.com", "0912345678");
        PostDTO expectedPost = createTestPost("1", "新文章", "文章內容", author);
        expectedPost.setStatus(PostDTO.PostStatus.DRAFT);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("createPost"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(PostDTO.class)).thenReturn(Mono.just(expectedPost));

        // When & Then
        StepVerifier.create(graphQLClientService.createPost(input))
                .expectNext(expectedPost)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map -> {
            @SuppressWarnings("unchecked")
            Map<String, Object> inputMap = (Map<String, Object>) map.get("input");
            return inputMap.get("title").equals("新文章") &&
                    inputMap.get("content").equals("文章內容") &&
                    inputMap.get("authorId").equals("1") &&
                    inputMap.get("status").equals("DRAFT");
        }));
    }

    @Test
    void testPublishPost_Success() {
        // Given
        String postId = "1";
        UserDTO author = createTestUser("1", "作者", "author@example.com", "0912345678");
        PostDTO expectedPost = createTestPost(postId, "文章標題", "內容", author);
        expectedPost.setStatus(PostDTO.PostStatus.PUBLISHED);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("publishPost"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(PostDTO.class)).thenReturn(Mono.just(expectedPost));

        // When & Then
        StepVerifier.create(graphQLClientService.publishPost(postId))
                .expectNext(expectedPost)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("id") && map.get("id").equals(postId)
        ));
    }

    @Test
    void testArchivePost_Success() {
        // Given
        String postId = "1";
        UserDTO author = createTestUser("1", "作者", "author@example.com", "0912345678");
        PostDTO expectedPost = createTestPost(postId, "文章標題", "內容", author);
        expectedPost.setStatus(PostDTO.PostStatus.ARCHIVED);

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("archivePost"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(PostDTO.class)).thenReturn(Mono.just(expectedPost));

        // When & Then
        StepVerifier.create(graphQLClientService.archivePost(postId))
                .expectNext(expectedPost)
                .verifyComplete();
    }

    @Test
    void testDeletePost_Success() {
        // Given
        String postId = "1";

        when(mockGraphQlClient.document(anyString())).thenReturn(mockRequestSpec);
        when(mockRequestSpec.variables(any(Map.class))).thenReturn(mockRequestSpec);
        when(mockRequestSpec.retrieve(eq("deletePost"))).thenReturn(mockRetrieveSpec);
        when(mockRetrieveSpec.toEntity(Boolean.class)).thenReturn(Mono.just(true));

        // When & Then
        StepVerifier.create(graphQLClientService.deletePost(postId))
                .expectNext(true)
                .verifyComplete();

        verify(mockRequestSpec).variables(argThat(map ->
                map.containsKey("id") && map.get("id").equals(postId)
        ));
    }

    // ==================== 輔助方法 ====================
    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }

    private UserDTO createTestUser(String id, String name, String email, String phone) {
        UserDTO user = new UserDTO();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.setPostCount(0);
        return user;
    }

    private PostDTO createTestPost(String id, String title, String content, UserDTO author) {
        PostDTO post = new PostDTO();
        post.setId(id);
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        post.setStatus(PostDTO.PostStatus.DRAFT);
        post.setCreatedAt(OffsetDateTime.now());
        post.setUpdatedAt(OffsetDateTime.now());
        return post;
    }
}