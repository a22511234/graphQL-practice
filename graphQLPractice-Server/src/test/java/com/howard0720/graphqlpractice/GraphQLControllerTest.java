package com.howard0720.graphqlpractice;

import com.howard0720.graphqlpractice.Config.GraphQLConfig;
import com.howard0720.graphqlpractice.Controller.GraphQLController;
import com.howard0720.graphqlpractice.Enity.Post;
import com.howard0720.graphqlpractice.Enity.User;
import com.howard0720.graphqlpractice.Repo.PostRepository;
import com.howard0720.graphqlpractice.Repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@GraphQlTest(GraphQLController.class)
@Import(GraphQLConfig.class)
public class GraphQLControllerTest {

    @Autowired
    private GraphQlTester graphQlTester;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PostRepository postRepository;

    @Test
    public void testGetUsers() {
        // 準備測試資料
        User user1 = new User("測試用戶1", "test1@example.com", "0912345678");
        user1.setId(1L);
        User user2 = new User("測試用戶2", "test2@example.com", "0923456789");
        user2.setId(2L);

        List<User> users = Arrays.asList(user1, user2);
        when(userRepository.findAll()).thenReturn(users);

        // 執行 GraphQL 查詢
        this.graphQlTester
                .documentName("users") // 對應查詢名稱
                .execute()
                .path("users")
                .entityList(User.class)
                .hasSize(2);
    }

    @Test
    public void testGetUserById() {
        // 準備測試資料
        User user = new User("測試用戶", "test@example.com", "0912345678");
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // 執行 GraphQL 查詢
        this.graphQlTester
                .document("""
                        query {
                            user(id: 1) {
                                id
                                name
                                email
                            }
                        }
                        """)
                .execute()
                .path("user.name")
                .entity(String.class)
                .isEqualTo("測試用戶");
    }

    @Test
    public void testCreateUser() {
        // 準備測試資料
        User savedUser = new User("新用戶", "new@example.com", "0934567890");
        savedUser.setId(1L);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // 執行 GraphQL 變更操作
        this.graphQlTester
                .document("""
                        mutation {
                            createUser(input: {
                                name: "新用戶"
                                email: "new@example.com"
                                phone: "0934567890"
                            }) {
                                id
                                name
                                email
                                phone
                            }
                        }
                        """)
                .execute()
                .path("createUser.name")
                .entity(String.class)
                .isEqualTo("新用戶");
    }

    @Test
    public void testGetPosts() {
        // 準備測試資料
        User author = new User("作者", "author@example.com", "0912345678");
        author.setId(1L);

        Post post1 = new Post("文章1", "內容1", author);
        post1.setId(1L);
        Post post2 = new Post("文章2", "內容2", author);
        post2.setId(2L);

        List<Post> posts = Arrays.asList(post1, post2);
        when(postRepository.findAll()).thenReturn(posts);

        // 執行 GraphQL 查詢
        this.graphQlTester
                .document("""
                        query {
                            posts {
                                id
                                title
                                content
                            }
                        }
                        """)
                .execute()
                .path("posts")
                .entityList(Post.class)
                .hasSize(2);
    }
}
