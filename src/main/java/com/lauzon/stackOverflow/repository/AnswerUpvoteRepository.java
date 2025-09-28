package com.lauzon.stackOverflow.repository;

import com.lauzon.stackOverflow.entity.AnswerUpvoteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerUpvoteRepository extends JpaRepository<AnswerUpvoteEntity, Long> {

    Optional<AnswerUpvoteEntity> findByAnswerIdAndUserId(Long answerId, Long userId);

    Page<AnswerUpvoteEntity> findAllByAnswerId(Long answerId, Pageable pageable);

    Long countAllByAnswerId(Long answerId);

}
