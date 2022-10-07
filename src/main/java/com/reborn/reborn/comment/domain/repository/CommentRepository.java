package com.reborn.reborn.comment.domain.repository;

import com.reborn.reborn.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c join fetch c.member where c.article.id = :articleId")
    List<Comment> findAllByArticleIdWithMember(@Param("articleId") Long articleId);
}
