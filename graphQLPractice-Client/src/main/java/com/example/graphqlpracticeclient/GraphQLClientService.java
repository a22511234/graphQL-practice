package com.example.graphqlpracticeclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.client.HttpGraphQlClient;
import com.example.graphqlpracticeclient.DTO.UserDTO;
import com.example.graphqlpracticeclient.DTO.PostDTO;
import com.example.graphqlpracticeclient.DTO.CreatePostInputDTO;
import com.example.graphqlpracticeclient.DTO.CreateUserInputDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphQLClientService {

    @Autowired
    private HttpGraphQlClient graphQlClient;

    // ==================== 用戶操作 ====================

    /**
     * 取得所有用戶
     */
    public Mono<List<UserDTO>> getAllUsers() {

        return graphQlClient
                .document(GraphQLQueries.GET_ALL_USERS)
                .retrieve("users")
                .toEntityList(UserDTO.class)
                .doOnNext(users -> System.out.println("取得 " + users.size() + " 個用戶"))
                .doOnError(error -> System.err.println("取得用戶失敗: " + error.getMessage()));
    }

    /**
     * 根據 ID 取得用戶
     */
    public Mono<UserDTO> getUserById(String id) {
        String query = """
                query($id: ID!) {
                    user(id: $id) {
                        id
                        name
                        email
                        phone
                        createdAt
                        updatedAt
                        postCount
                        posts {
                            id
                            title
                            content
                            status
                            createdAt
                        }
                    }
                }
                """;
        Map<String, Object> variables = Map.of("id", id);

        return graphQlClient
                .document(query)
                .variables(variables)
                .retrieve("user")
                .toEntity(UserDTO.class)
                .doOnNext(user -> System.out.println("取得用戶: " + user))
                .doOnError(error -> System.err.println("取得用戶失敗: " + error.getMessage()));
    }

    /**
     * 根據 Email 取得用戶
     */
    public Mono<UserDTO> getUserByEmail(String email) {
        Map<String, Object> variables = Map.of("email", email);

        return graphQlClient
                .document(GraphQLQueries.GET_USER_BY_EMAIL)
                .variables(variables)
                .retrieve("userByEmail")
                .toEntity(UserDTO.class)
                .doOnNext(user -> System.out.println("根據Email取得用戶: " + user))
                .doOnError(error -> System.err.println("根據Email取得用戶失敗: " + error.getMessage()));
    }

    /**
     * 搜尋用戶
     */
    public Mono<List<UserDTO>> searchUsers(String keyword) {
        Map<String, Object> variables = Map.of("keyword", keyword);

        return graphQlClient
                .document(GraphQLQueries.SEARCH_USERS)
                .variables(variables)
                .retrieve("searchUsers")
                .toEntityList(UserDTO.class)
                .doOnNext(users -> System.out.println("搜尋到 " + users.size() + " 個用戶"))
                .doOnError(error -> System.err.println("搜尋用戶失敗: " + error.getMessage()));
    }

    /**
     * 創建用戶
     */
    public Mono<UserDTO> createUser(CreateUserInputDTO input) {
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("name", input.getName());
        inputMap.put("email", input.getEmail());
        inputMap.put("phone", input.getPhone());
        variables.put("input", inputMap);

        return graphQlClient
                .document(GraphQLQueries.CREATE_USER)
                .variables(variables)
                .retrieve("createUser")
                .toEntity(UserDTO.class)
                .doOnNext(user -> System.out.println("成功創建用戶: " + user))
                .doOnError(error -> System.err.println("創建用戶失敗: " + error.getMessage()));
    }

    /**
     * 更新用戶
     */
    public Mono<UserDTO> updateUser(String id, String name, String email, String phone) {
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("id", id);
        if (name != null) inputMap.put("name", name);
        if (email != null) inputMap.put("email", email);
        if (phone != null) inputMap.put("phone", phone);
        variables.put("input", inputMap);

        return graphQlClient
                .document(GraphQLQueries.UPDATE_USER)
                .variables(variables)
                .retrieve("updateUser")
                .toEntity(UserDTO.class)
                .doOnNext(user -> System.out.println("成功更新用戶: " + user))
                .doOnError(error -> System.err.println("更新用戶失敗: " + error.getMessage()));
    }

    /**
     * 刪除用戶
     */
    public Mono<Boolean> deleteUser(String id) {
        Map<String, Object> variables = Map.of("id", id);

        return graphQlClient
                .document(GraphQLQueries.DELETE_USER)
                .variables(variables)
                .retrieve("deleteUser")
                .toEntity(Boolean.class)
                .doOnNext(result -> System.out.println("刪除用戶結果: " + result))
                .doOnError(error -> System.err.println("刪除用戶失敗: " + error.getMessage()));
    }

    // ==================== 文章操作 ====================

    /**
     * 取得所有文章
     */
    public Mono<List<PostDTO>> getAllPosts() {
        return graphQlClient
                .document(GraphQLQueries.GET_ALL_POSTS)
                .retrieve("posts")
                .toEntityList(PostDTO.class)
                .doOnNext(posts -> System.out.println("取得 " + posts.size() + " 篇文章"))
                .doOnError(error -> System.err.println("取得文章失敗: " + error.getMessage()));
    }

    /**
     * 根據 ID 取得文章
     */
    public Mono<PostDTO> getPostById(String id) {
        Map<String, Object> variables = Map.of("id", id);

        return graphQlClient
                .document(GraphQLQueries.GET_POST_BY_ID)
                .variables(variables)
                .retrieve("post")
                .toEntity(PostDTO.class)
                .doOnNext(post -> System.out.println("取得文章: " + post))
                .doOnError(error -> System.err.println("取得文章失敗: " + error.getMessage()));
    }

    /**
     * 根據作者 ID 取得文章
     */
    public Mono<List<PostDTO>> getPostsByAuthor(String authorId) {
        Map<String, Object> variables = Map.of("authorId", authorId);

        return graphQlClient
                .document(GraphQLQueries.GET_POSTS_BY_AUTHOR)
                .variables(variables)
                .retrieve("postsByAuthor")
                .toEntityList(PostDTO.class)
                .doOnNext(posts -> System.out.println("取得作者的 " + posts.size() + " 篇文章"))
                .doOnError(error -> System.err.println("取得作者文章失敗: " + error.getMessage()));
    }

    /**
     * 根據狀態取得文章
     */
    public Mono<List<PostDTO>> getPostsByStatus(PostDTO.PostStatus status) {
        Map<String, Object> variables = Map.of("status", status.toString());

        return graphQlClient
                .document(GraphQLQueries.GET_POSTS_BY_STATUS)
                .variables(variables)
                .retrieve("postsByStatus")
                .toEntityList(PostDTO.class)
                .doOnNext(posts -> System.out.println("取得狀態為 " + status + " 的 " + posts.size() + " 篇文章"))
                .doOnError(error -> System.err.println("根據狀態取得文章失敗: " + error.getMessage()));
    }

    /**
     * 搜尋文章
     */
    public Mono<List<PostDTO>> searchPosts(String keyword) {
        Map<String, Object> variables = Map.of("keyword", keyword);

        return graphQlClient
                .document(GraphQLQueries.SEARCH_POSTS)
                .variables(variables)
                .retrieve("searchPosts")
                .toEntityList(PostDTO.class)
                .doOnNext(posts -> System.out.println("搜尋到 " + posts.size() + " 篇文章"))
                .doOnError(error -> System.err.println("搜尋文章失敗: " + error.getMessage()));
    }

    /**
     * 創建文章
     */
    public Mono<PostDTO> createPost(CreatePostInputDTO input) {
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("title", input.getTitle());
        inputMap.put("content", input.getContent());
        inputMap.put("authorId", input.getAuthorId());
        if (input.getStatus() != null) {
            inputMap.put("status", input.getStatus().toString());
        }
        variables.put("input", inputMap);

        return graphQlClient
                .document(GraphQLQueries.CREATE_POST)
                .variables(variables)
                .retrieve("createPost")
                .toEntity(PostDTO.class)
                .doOnNext(post -> System.out.println("成功創建文章: " + post))
                .doOnError(error -> System.err.println("創建文章失敗: " + error.getMessage()));
    }

    /**
     * 發布文章
     */
    public Mono<PostDTO> publishPost(String id) {
        Map<String, Object> variables = Map.of("id", id);

        return graphQlClient
                .document(GraphQLQueries.PUBLISH_POST)
                .variables(variables)
                .retrieve("publishPost")
                .toEntity(PostDTO.class)
                .doOnNext(post -> System.out.println("成功發布文章: " + post))
                .doOnError(error -> System.err.println("發布文章失敗: " + error.getMessage()));
    }

    /**
     * 歸檔文章
     */
    public Mono<PostDTO> archivePost(String id) {
        Map<String, Object> variables = Map.of("id", id);

        return graphQlClient
                .document(GraphQLQueries.ARCHIVE_POST)
                .variables(variables)
                .retrieve("archivePost")
                .toEntity(PostDTO.class)
                .doOnNext(post -> System.out.println("成功歸檔文章: " + post))
                .doOnError(error -> System.err.println("歸檔文章失敗: " + error.getMessage()));
    }

    /**
     * 刪除文章
     */
    public Mono<Boolean> deletePost(String id) {
        Map<String, Object> variables = Map.of("id", id);

        return graphQlClient
                .document(GraphQLQueries.DELETE_POST)
                .variables(variables)
                .retrieve("deletePost")
                .toEntity(Boolean.class)
                .doOnNext(result -> System.out.println("刪除文章結果: " + result))
                .doOnError(error -> System.err.println("刪除文章失敗: " + error.getMessage()));
    }
}
