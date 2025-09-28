package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.AnswerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<AnswerEntity, Long> {

    Page<AnswerEntity> findAllByQuestionId(Long questionId, Pageable pageable);

    Optional<AnswerEntity> findByIdAndUserId(Long questionId, Long userId);
}
