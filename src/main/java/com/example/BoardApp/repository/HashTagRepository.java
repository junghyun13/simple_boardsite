package com.example.BoardApp.repository;

import com.example.BoardApp.dto.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {
    List<HashTag> findByTag(String tag);
}