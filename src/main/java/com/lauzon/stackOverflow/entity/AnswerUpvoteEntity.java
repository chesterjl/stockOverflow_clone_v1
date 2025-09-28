package com.lauzon.stackOverflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(schema = "answer_upvotes_tbl")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AnswerUpvoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "answer_id", nullable = false)
    private AnswerEntity answer;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
