package com.example.graphqlpracticeclient;

public class GraphQLQueries {

    // ==================== 用戶查詢 ====================
    public static final String GET_ALL_USERS = """
            query {
                users {
                    id
                    name
                    email
                    phone
                    createdAt
                    updatedAt
                    postCount
                }
            }
            """;

    public static final String GET_USER_BY_ID = """
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

    public static final String GET_USER_BY_EMAIL = """
            query($email: String!) {
                userByEmail(email: $email) {
                    id
                    name
                    email
                    phone
                    createdAt
                    postCount
                }
            }
            """;

    public static final String SEARCH_USERS = """
            query($keyword: String!) {
                searchUsers(keyword: $keyword) {
                    id
                    name
                    email
                    phone
                    postCount
                }
            }
            """;

    // ==================== 文章查詢 ====================

    public static final String GET_ALL_POSTS = """
            query {
                posts {
                    id
                    title
                    content
                    status
                    createdAt
                    updatedAt
                    author {
                        id
                        name
                        email
                    }
                }
            }
            """;

    public static final String GET_POST_BY_ID = """
            query($id: ID!) {
                post(id: $id) {
                    id
                    title
                    content
                    status
                    createdAt
                    updatedAt
                    author {
                        id
                        name
                        email
                    }
                }
            }
            """;

    public static final String GET_POSTS_BY_AUTHOR = """
            query($authorId: ID!) {
                postsByAuthor(authorId: $authorId) {
                    id
                    title
                    content
                    status
                    createdAt
                    updatedAt
                }
            }
            """;

    public static final String GET_POSTS_BY_STATUS = """
            query($status: PostStatus!) {
                postsByStatus(status: $status) {
                    id
                    title
                    content
                    status
                    createdAt
                    author {
                        id
                        name
                    }
                }
            }
            """;

    public static final String SEARCH_POSTS = """
            query($keyword: String!) {
                searchPosts(keyword: $keyword) {
                    id
                    title
                    content
                    status
                    author {
                        name
                    }
                }
            }
            """;

    // ==================== 變更操作 ====================

    public static final String CREATE_USER = """
            mutation($input: CreateUserInput!) {
                createUser(input: $input) {
                    id
                    name
                    email
                    phone
                    createdAt
                }
            }
            """;

    public static final String UPDATE_USER = """
            mutation($input: UpdateUserInput!) {
                updateUser(input: $input) {
                    id
                    name
                    email
                    phone
                    updatedAt
                }
            }
            """;

    public static final String DELETE_USER = """
            mutation($id: ID!) {
                deleteUser(id: $id)
            }
            """;

    public static final String CREATE_POST = """
            mutation($input: CreatePostInput!) {
                createPost(input: $input) {
                    id
                    title
                    content
                    status
                    createdAt
                    author {
                        id
                        name
                    }
                }
            }
            """;

    public static final String UPDATE_POST = """
            mutation($input: UpdatePostInput!) {
                updatePost(input: $input) {
                    id
                    title
                    content
                    status
                    updatedAt
                }
            }
            """;

    public static final String DELETE_POST = """
            mutation($id: ID!) {
                deletePost(id: $id)
            }
            """;

    public static final String PUBLISH_POST = """
            mutation($id: ID!) {
                publishPost(id: $id) {
                    id
                    title
                    status
                    updatedAt
                }
            }
            """;

    public static final String ARCHIVE_POST = """
            mutation($id: ID!) {
                archivePost(id: $id) {
                    id
                    title
                    status
                    updatedAt
                }
            }
            """;
}