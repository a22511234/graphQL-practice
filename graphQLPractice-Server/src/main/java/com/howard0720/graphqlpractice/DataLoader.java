package com.howard0720.graphqlpractice;

import com.howard0720.graphqlpractice.Enity.PostStatus;
import com.howard0720.graphqlpractice.Repo.PostRepository;
import com.howard0720.graphqlpractice.Repo.UserRepository;
import com.howard0720.graphqlpractice.Enity.User;
import com.howard0720.graphqlpractice.Enity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Override
    public void run(String... args) throws Exception {
        loadTestData();
    }

    private void loadTestData() {
        // 檢查是否已有資料
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("載入測試資料...");

        // 創建測試用戶
        User user1 = new User("張小明", "ming@example.com", "0912345678");
        User user2 = new User("李小華", "hua@example.com", "0923456789");
        User user3 = new User("王小美", "mei@example.com", "0934567890");

        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // 創建測試文章
        Post post1 = new Post("Spring Boot 入門指南",
                "Spring Boot 是一個強大的 Java 框架，能夠快速建立微服務應用程式...",
                user1);
        post1.setStatus(PostStatus.PUBLISHED);

        Post post2 = new Post("GraphQL 與 REST API 比較",
                "GraphQL 提供了更靈活的資料查詢方式，讓前端能夠精確指定需要的資料...",
                user1);
        post2.setStatus(PostStatus.PUBLISHED);

        Post post3 = new Post("Java 開發最佳實踐",
                "在進行 Java 開發時，遵循一些最佳實踐能夠讓程式碼更加健壯和易於維護...",
                user2);
        post3.setStatus(PostStatus.DRAFT);

        Post post4 = new Post("微服務架構設計",
                "微服務架構是現代軟體開發的重要趨勢，它將大型應用拆分為多個小型服務...",
                user2);
        post4.setStatus(PostStatus.PUBLISHED);

        Post post5 = new Post("資料庫設計原則",
                "良好的資料庫設計是應用程式成功的關鍵因素之一...",
                user3);
        post5.setStatus(PostStatus.ARCHIVED);

        postRepository.save(post1);
        postRepository.save(post2);
        postRepository.save(post3);
        postRepository.save(post4);
        postRepository.save(post5);

        System.out.println("✅ 測試資料載入完成！");
        System.out.println("📊 用戶數量: " + userRepository.count());
        System.out.println("📝 文章數量: " + postRepository.count());
    }
}
