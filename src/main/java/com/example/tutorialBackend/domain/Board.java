package com.example.tutorialBackend.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@SQLDelete(sql =
    "UPDATE board "
        + "SET delete_yn = 1 "
        + "WHERE id = ?")
@Where(clause = "delete_yn = 0")
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @Column
    @JsonProperty
    private String title;

    @OneToOne(mappedBy = "board", fetch = FetchType.LAZY)
    @JsonProperty
    private BoardBody body;

    @OneToMany(mappedBy = "board", fetch = FetchType.LAZY)
    @JsonProperty
    private List<BoardFile> files;

    @Column
    @JsonProperty
    private LocalDateTime createdAt;

    @Column
    @JsonProperty
    private LocalDateTime updatedAt;

    @Column
    @JsonProperty
    private String createdBy;

    @Column
    @JsonProperty
    private String updatedBy;

    @Column
    private boolean delete_yn;
}
