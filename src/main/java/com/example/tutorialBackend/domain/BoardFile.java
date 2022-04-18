package com.example.tutorialBackend.domain;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "boardFile")
public class BoardFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "nubes_file_name")
    private String nubesFileName;

    @JoinColumn(name = "original_file_name")
    private String originalFileName;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @JoinColumn(name = "size")
    private Long size;

    @JoinColumn(name = "created_at")
    private LocalDateTime createdAt;

    @JoinColumn(name = "created_by")
    private String createdBy;

}
