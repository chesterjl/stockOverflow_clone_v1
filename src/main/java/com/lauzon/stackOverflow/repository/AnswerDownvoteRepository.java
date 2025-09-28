package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.AnswerDownvoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerDownvoteRepository extends JpaRepository<AnswerDownvoteEntity, Long> {

    Optional<AnswerDownvoteEntity> findByAnswerIdAndUserId(Long answerId, Long userId);

    Page<AnswerDownvoteEntity> findAllByAnswerId(Long answerId, Pageable pageable);

    Long countAllByAnswerId(Long answerId);
}
