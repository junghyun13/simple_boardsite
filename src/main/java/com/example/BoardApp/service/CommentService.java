package com.example.BoardApp.service;
import com.example.BoardApp.dto.Board;
import com.example.BoardApp.dto.Comment;
import com.example.BoardApp.dto.User;
import com.example.BoardApp.repository.BoardRepository;
import com.example.BoardApp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public void addComment(int boardId, int userId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedDate(LocalDateTime.now());
        comment.setBoard(board);

        // 사용자는 로그인을 통해 ID를 제공받으므로, User 객체를 사용하지 않음
        User user = new User();
        user.setUserId(userId);
        comment.setUser(user);

        commentRepository.save(comment);
    }

    public List<Comment> getCommentsByBoardId(int boardId) {
        return commentRepository.findByBoard_BoardId(boardId);
    }
}
