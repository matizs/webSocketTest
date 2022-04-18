package com.example.tutorialBackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tutorialBackend.domain.BoardBody;

public interface BoardBodyRepository extends JpaRepository<BoardBody, Integer> {
}
