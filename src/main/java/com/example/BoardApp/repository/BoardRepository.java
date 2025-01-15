package com.example.BoardApp.repository;


import com.example.BoardApp.dto.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAllByOrderByBoardIdDesc(Pageable pageable);
    List<Board> findByTitleContaining(String title);
    List<Board> findByUser_NameContaining(String author);
}
