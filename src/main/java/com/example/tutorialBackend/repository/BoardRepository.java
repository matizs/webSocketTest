package com.example.tutorialBackend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.tutorialBackend.domain.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    Page<Board> findAll(Pageable pageable);
    Page<Board> findByTitleContainingIgnoreCase(Pageable pageable, String title);
}
