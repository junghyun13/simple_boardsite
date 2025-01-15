package com.example.BoardApp.repository;

import com.example.BoardApp.dto.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoard_BoardId(int boardId);
}
