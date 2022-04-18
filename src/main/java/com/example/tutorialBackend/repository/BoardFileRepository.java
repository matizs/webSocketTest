package com.example.tutorialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutorialBackend.domain.BoardFile;

public interface BoardFileRepository extends JpaRepository<BoardFile, Integer> {
}
