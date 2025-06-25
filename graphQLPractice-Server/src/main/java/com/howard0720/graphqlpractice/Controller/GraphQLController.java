package com.howard0720.graphqlpractice.Controller;

import com.howard0720.graphqlpractice.Enity.Post;
import com.howard0720.graphqlpractice.Enity.PostStatus;
import com.howard0720.graphqlpractice.Enity.User;
import com.howard0720.graphqlpractice.Repo.PostRepository;
import com.howard0720.graphqlpractice.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class GraphQLController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // ==================== 查詢操作 ====================

    @QueryMapping
    public List<User> users() {
        return userRepository.findAll();
    }

    @QueryMapping
    public Optional<User> user(@Argument Long id) {
        return userRepository.findById(id);
    }

    @QueryMapping
    public Optional<User> userByEmail(@Argument String email) {
        return userRepository.findByEmail(email);
    }

    @QueryMapping
    public List<User> searchUsers(@Argument String keyword) {
        return userRepository.searchByKeyword(keyword);
    }

    @QueryMapping
    public List<Post> posts() {
        return postRepository.findAll();
    }

    @QueryMapping
    public Optional<Post> post(@Argument Long id) {
        return postRepository.findById(id);
    }

    @QueryMapping
    public List<Post> postsByAuthor(@Argument Long authorId) {
        return postRepository.findByAuthorId(authorId);
    }

    @QueryMapping
    public List<Post> postsByStatus(@Argument PostStatus status) {
        return postRepository.findByStatus(status);
    }

    @QueryMapping
    public List<Post> searchPosts(@Argument String keyword) {
        return postRepository.searchByKeyword(keyword);
    }

    // ==================== 變更操作 ====================

    @MutationMapping
    public User createUser(@Argument Map<String, Object> input) {
        User user = new User();
        user.setName((String) input.get("name"));
        user.setEmail((String) input.get("email"));
        user.setPhone((String) input.get("phone"));

        // 檢查 email 是否已存在
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        return userRepository.save(user);
    }

    @MutationMapping
    public User updateUser(@Argument Map<String, Object> input) {
        Long id = Long.valueOf(input.get("id").toString());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        if (input.get("name") != null) {
            user.setName((String) input.get("name"));
        }
        if (input.get("email") != null) {
            String newEmail = (String) input.get("email");
            // 檢查新 email 是否已被其他用戶使用
            if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email already exists: " + newEmail);
            }
            user.setEmail(newEmail);
        }
        if (input.get("phone") != null) {
            user.setPhone((String) input.get("phone"));
        }

        return userRepository.save(user);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        return true;
    }

    @MutationMapping
    public Post createPost(@Argument Map<String, Object> input) {
        Long authorId = Long.valueOf(input.get("authorId").toString());
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + authorId));

        Post post = new Post();
        post.setTitle((String) input.get("title"));
        post.setContent((String) input.get("content"));
        post.setAuthor(author);

        if (input.get("status") != null) {
            post.setStatus(PostStatus.valueOf((String) input.get("status")));
        }

        return postRepository.save(post);
    }

    @MutationMapping
    public Post updatePost(@Argument Map<String, Object> input) {
        Long id = Long.valueOf(input.get("id").toString());
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

        if (input.get("title") != null) {
            post.setTitle((String) input.get("title"));
        }
        if (input.get("content") != null) {
            post.setContent((String) input.get("content"));
        }
        if (input.get("status") != null) {
            post.setStatus(PostStatus.valueOf((String) input.get("status")));
        }

        return postRepository.save(post);
    }

    @MutationMapping
    public Boolean deletePost(@Argument Long id) {
        if (!postRepository.existsById(id)) {
            throw new RuntimeException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
        return true;
    }

    @MutationMapping
    public Post publishPost(@Argument Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setStatus(PostStatus.PUBLISHED);
        return postRepository.save(post);
    }

    @MutationMapping
    public Post archivePost(@Argument Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));
        post.setStatus(PostStatus.ARCHIVED);
        return postRepository.save(post);
    }

    // ==================== 欄位解析器 ====================

    @SchemaMapping
    public List<Post> posts(User user) {
        return postRepository.findByAuthorId(user.getId());
    }

    @SchemaMapping
    public Integer postCount(User user) {
        return postRepository.countByAuthorId(user.getId()).intValue();
    }

}
