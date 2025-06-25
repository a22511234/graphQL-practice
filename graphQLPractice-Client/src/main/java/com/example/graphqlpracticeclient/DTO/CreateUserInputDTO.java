package com.example.graphqlpracticeclient.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUserInputDTO {

    @NotBlank(message = "姓名不能為空")
    @Size(max = 100, message = "姓名長度不能超過100字元")
    private String name;

    @NotBlank(message = "電子郵件不能為空")
    @Email(message = "請輸入有效的電子郵件格式")
    @Size(max = 150, message = "電子郵件長度不能超過150字元")
    private String email;

    @Size(max = 20, message = "電話號碼長度不能超過20字元")
    private String phone;

    // 預設建構子
    public CreateUserInputDTO() {
    }

    // 帶參數建構子
    public CreateUserInputDTO(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
