package com.example.BoardApp.service;


import com.example.BoardApp.dto.Board;
import com.example.BoardApp.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;


    public void addBoard(int userId, String title, String content) {
        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        board.setViewCnt(0);
        boardRepository.save(board);
    }

    public int getTotalCount() {
        return (int) boardRepository.count();
    }

    public List<Board> getBoards(int page) {
        Page<Board> boardPage = boardRepository.findAllByOrderByBoardIdDesc(PageRequest.of(page - 1, 10));
        return boardPage.getContent();
    }

    @Transactional
    public Board getBoard(int boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 조회수 증가
        board.setViewCnt(board.getViewCnt() + 1);
        boardRepository.save(board);

        return board;
    }


    public void updateBoard(int boardId, String title, String content) {
        Board board = getBoard(boardId);
        board.setTitle(title);
        board.setContent(content);
        boardRepository.save(board);
    }

    public void deleteBoard(int boardId) {
        boardRepository.deleteById(boardId);
    }
}
