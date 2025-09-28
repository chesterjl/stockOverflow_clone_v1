package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    Optional<QuestionEntity> findByIdAndUserId(Long questionId, Long userId);

    Page<QuestionEntity> findAllQuestionByUserId(Long userId, Pageable pageable);

    Page<QuestionEntity> findByTitleContainingIgnoreCase(
            String titleKeyword, Pageable pageable);

}
