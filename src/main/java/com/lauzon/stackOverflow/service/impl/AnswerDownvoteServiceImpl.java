package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.AnswerVoteRequest;
import com.lauzon.stackOverflow.dto.response.AnswerVoteResponse;
import com.lauzon.stackOverflow.entity.AnswerDownvoteEntity;
import com.lauzon.stackOverflow.entity.AnswerEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.AnswerDownvoteRepository;
import com.lauzon.stackOverflow.repository.AnswerRepository;
import com.lauzon.stackOverflow.service.AnswerDownvoteService;
import com.lauzon.stackOverflow.util.UtilMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AnswerDownvoteServiceImpl implements AnswerDownvoteService {

    private final AnswerDownvoteRepository answerDownvoteRepository;
    private final AnswerRepository answerRepository;
    private final UtilMethod utilMethod;

    @Override
    public Map<String, Object> toggleDownvoteForAnswer(AnswerVoteRequest request) {
        AnswerEntity answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        UserEntity user = utilMethod.getCurrentUser();

        Optional<AnswerDownvoteEntity> existingDownvote = answerDownvoteRepository.findByAnswerIdAndUserId(request.getAnswerId(), user.getId());

        if (existingDownvote.isPresent()) {
            answerDownvoteRepository.delete(existingDownvote.get());
            return Map.of("message", "Downvote deleted");
        }

        AnswerDownvoteEntity unliked = convertToEntity(user, answer);
        unliked = answerDownvoteRepository.save(unliked);
        return Map.of("response", convertToResponse(unliked));
    }

    @Override
    public Page<AnswerVoteResponse> viewAllDownvoteForAnswer(int page, int size, Long answerId) {
        AnswerEntity answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AnswerDownvoteEntity> fetchAllDownvoteForAnswer = answerDownvoteRepository.findAllByAnswerId(answerId, pageable);
        return fetchAllDownvoteForAnswer.map(this::convertToResponse);
    }


    private AnswerDownvoteEntity convertToEntity(UserEntity user, AnswerEntity answer) {
        return AnswerDownvoteEntity.builder()
                .user(user)
                .answer(answer)
                .build();
    }

    private AnswerVoteResponse convertToResponse(AnswerDownvoteEntity answerDownvoteEntity) {
        return AnswerVoteResponse.builder()
                .id(answerDownvoteEntity.getId())
                .answerId(answerDownvoteEntity.getAnswer().getId())
                .userId(answerDownvoteEntity.getUser().getId())
                .name(answerDownvoteEntity.getUser().getFirstName() + " " + answerDownvoteEntity.getUser().getLastName())
                .createdAt(answerDownvoteEntity.getCreatedAt())
                .build();
    }


}
