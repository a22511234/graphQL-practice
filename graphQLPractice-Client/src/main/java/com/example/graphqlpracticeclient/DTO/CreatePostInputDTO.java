package com.example.graphqlpracticeclient.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePostInputDTO {
    @NotBlank(message = "文章標題不能為空")
    @Size(max = 200, message = "標題長度不能超過200字元")
    private String title;

    private String content;

    @NotNull(message = "作者ID不能為空")
    private String authorId;

    private PostDTO.PostStatus status;

    // 預設建構子
    public CreatePostInputDTO() {
    }

    // 帶參數建構子
    public CreatePostInputDTO(String title, String content, String authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public PostDTO.PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostDTO.PostStatus status) {
        this.status = status;
    }
}