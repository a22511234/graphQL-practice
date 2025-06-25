package com.example.graphqlpracticeclient;

import com.example.graphqlpracticeclient.DTO.CreatePostInputDTO;
import com.example.graphqlpracticeclient.DTO.CreateUserInputDTO;
import com.example.graphqlpracticeclient.DTO.PostDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web")
public class WebController {

    @Autowired
    private GraphQLClientService graphQLClientService;

    // ==================== 首頁 ====================

    @GetMapping({"", "/", "/index"})
    public String index(Model model) {
        return "index";
    }

    // ==================== 用戶頁面 ====================

    /**
     * 用戶列表頁面
     */
    @GetMapping("/users")
    public String usersList(Model model) {
        graphQLClientService.getAllUsers()
                .subscribe(users -> model.addAttribute("users", users));
        return "users/list";
    }

    /**
     * 用戶詳情頁面
     */
    @GetMapping("/users/{id}")
    public String userDetail(@PathVariable String id, Model model) {
        graphQLClientService.getUserById(id)
                .subscribe(user -> {
                    model.addAttribute("user", user);
                    if (user.getPosts() != null) {
                        model.addAttribute("posts", user.getPosts());
                    }
                });
        return "users/detail";
    }

    /**
     * 創建用戶頁面
     */
    @GetMapping("/users/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new CreateUserInputDTO());
        return "users/create";
    }

    /**
     * 處理創建用戶
     */
    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("user") CreateUserInputDTO user,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "users/create";
        }

        return graphQLClientService.createUser(user)
                .map(createdUser -> {
                    redirectAttributes.addFlashAttribute("success", "用戶創建成功！");
                    return "redirect:/web/users";
                })
                .onErrorReturn("users/create")
                .block();
    }

    /**
     * 搜尋用戶頁面
     */
    @GetMapping("/users/search")
    public String searchUsers(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            graphQLClientService.searchUsers(keyword)
                    .subscribe(users -> {
                        model.addAttribute("users", users);
                        model.addAttribute("keyword", keyword);
                    });
        }
        return "users/search";
    }

    // ==================== 文章頁面 ====================

    /**
     * 文章列表頁面
     */
    @GetMapping("/posts")
    public String postsList(Model model) {
        graphQLClientService.getAllPosts()
                .subscribe(posts -> model.addAttribute("posts", posts));
        return "posts/list";
    }

    /**
     * 文章詳情頁面
     */
    @GetMapping("/posts/{id}")
    public String postDetail(@PathVariable String id, Model model) {
        graphQLClientService.getPostById(id)
                .subscribe(post -> model.addAttribute("post", post));
        return "posts/detail";
    }

    /**
     * 創建文章頁面
     */
    @GetMapping("/posts/create")
    public String createPostForm(Model model) {
        model.addAttribute("post", new CreatePostInputDTO());

        // 載入用戶列表供選擇
        graphQLClientService.getAllUsers()
                .subscribe(users -> model.addAttribute("users", users));

        return "posts/create";
    }

    /**
     * 處理創建文章
     */
    @PostMapping("/posts/create")
    public String createPost(@Valid @ModelAttribute("post") CreatePostInputDTO post,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            // 重新載入用戶列表
            graphQLClientService.getAllUsers()
                    .subscribe(users -> model.addAttribute("users", users));
            return "posts/create";
        }

        return graphQLClientService.createPost(post)
                .map(createdPost -> {
                    redirectAttributes.addFlashAttribute("success", "文章創建成功！");
                    return "redirect:/web/posts";
                })
                .onErrorReturn("posts/create")
                .block();
    }

    /**
     * 根據狀態過濾文章
     */
    @GetMapping("/posts/status/{status}")
    public String postsByStatus(@PathVariable String status, Model model) {
        try {
            PostDTO.PostStatus postStatus = PostDTO.PostStatus.valueOf(status.toUpperCase());
            graphQLClientService.getPostsByStatus(postStatus)
                    .subscribe(posts -> {
                        model.addAttribute("posts", posts);
                        model.addAttribute("currentStatus", status);
                    });
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "無效的文章狀態");
        }
        return "posts/list";
    }

    /**
     * 搜尋文章頁面
     */
    @GetMapping("/posts/search")
    public String searchPosts(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            graphQLClientService.searchPosts(keyword)
                    .subscribe(posts -> {
                        model.addAttribute("posts", posts);
                        model.addAttribute("keyword", keyword);
                    });
        }
        return "posts/search";
    }

    /**
     * 發布文章
     */
    @PostMapping("/posts/{id}/publish")
    public String publishPost(@PathVariable String id, RedirectAttributes redirectAttributes) {
        return graphQLClientService.publishPost(id)
                .map(post -> {
                    redirectAttributes.addFlashAttribute("success", "文章發布成功！");
                    return "redirect:/web/posts/" + id;
                })
                .onErrorReturn("redirect:/web/posts/" + id)
                .block();
    }

    /**
     * 歸檔文章
     */
    @PostMapping("/posts/{id}/archive")
    public String archivePost(@PathVariable String id, RedirectAttributes redirectAttributes) {
        return graphQLClientService.archivePost(id)
                .map(post -> {
                    redirectAttributes.addFlashAttribute("success", "文章歸檔成功！");
                    return "redirect:/web/posts/" + id;
                })
                .onErrorReturn("redirect:/web/posts/" + id)
                .block();
    }

    // ==================== 統計頁面 ====================

    /**
     * 統計儀表板
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // 取得用戶統計
        graphQLClientService.getAllUsers()
                .subscribe(users -> {
                    model.addAttribute("totalUsers", users.size());
                    int totalUserPosts = users.stream()
                            .mapToInt(user -> user.getPostCount() != null ? user.getPostCount() : 0)
                            .sum();
                    model.addAttribute("totalUserPosts", totalUserPosts);
                });

        // 取得文章統計
        graphQLClientService.getAllPosts()
                .subscribe(posts -> {
                    model.addAttribute("totalPosts", posts.size());

                    long publishedCount = posts.stream()
                            .filter(post -> post.getStatus() == PostDTO.PostStatus.PUBLISHED)
                            .count();
                    model.addAttribute("publishedPosts", publishedCount);

                    long draftCount = posts.stream()
                            .filter(post -> post.getStatus() == PostDTO.PostStatus.DRAFT)
                            .count();
                    model.addAttribute("draftPosts", draftCount);

                    long archivedCount = posts.stream()
                            .filter(post -> post.getStatus() == PostDTO.PostStatus.ARCHIVED)
                            .count();
                    model.addAttribute("archivedPosts", archivedCount);
                });

        return "dashboard";
    }

    // ==================== API 測試頁面 ====================

    /**
     * API 測試頁面
     */
    @GetMapping("/api-test")
    public String apiTest() {
        return "api-test";
    }
}