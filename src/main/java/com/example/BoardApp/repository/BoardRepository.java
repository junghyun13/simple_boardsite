package com.example.BoardApp.repository;


import com.example.BoardApp.dto.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAllByOrderByBoardIdDesc(Pageable pageable);
}
