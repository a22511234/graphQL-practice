package com.howard0720.graphqlpractice.Repo;


import com.howard0720.graphqlpractice.Enity.Post;
import com.howard0720.graphqlpractice.Enity.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(Long authorId);

    List<Post> findByStatus(PostStatus status);

    List<Post> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT p FROM Post p WHERE p.author.id = :authorId AND p.status = :status")
    List<Post> findByAuthorIdAndStatus(@Param("authorId") Long authorId, @Param("status") PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:keyword% OR p.content LIKE %:keyword%")
    List<Post> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.author.id = :authorId")
    Long countByAuthorId(@Param("authorId") Long authorId);
}
