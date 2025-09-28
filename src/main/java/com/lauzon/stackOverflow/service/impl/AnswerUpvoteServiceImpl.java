package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.AnswerVoteRequest;
import com.lauzon.stackOverflow.dto.response.AnswerVoteResponse;
import com.lauzon.stackOverflow.entity.AnswerEntity;
import com.lauzon.stackOverflow.entity.AnswerUpvoteEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.AnswerRepository;
import com.lauzon.stackOverflow.repository.AnswerUpvoteRepository;
import com.lauzon.stackOverflow.service.AnswerUpvoteService;
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
public class AnswerUpvoteServiceImpl implements AnswerUpvoteService {

    private final UtilMethod utilMethod;
    private final AnswerUpvoteRepository answerUpvoteRepository;
    private final AnswerRepository answerRepository;

    @Override
    public Map<String, Object> toggleUpvote(AnswerVoteRequest upvoteRequest) {
        AnswerEntity answer = answerRepository.findById(upvoteRequest.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        UserEntity user = utilMethod.getCurrentUser();

        Optional<AnswerUpvoteEntity> existingUpvote = answerUpvoteRepository.findByAnswerIdAndUserId(answer.getId(), user.getId());

        if (existingUpvote.isPresent()) {
            answerUpvoteRepository.delete(existingUpvote.get());
            return Map.of("message", "Upvote deleted");
        }

        AnswerUpvoteEntity liked = convertToEntity(user, answer);
        liked = answerUpvoteRepository.save(liked);
        return Map.of("response", convertToResponse(liked));
    }

    @Override
    public Page<AnswerVoteResponse> viewAllUpvoteOfAnswer(int page, int size, Long answerId) {
        AnswerEntity existingAnswer = answerRepository.findById(answerId)
                .orElseThrow(() -> new ResourceNotFoundException("Answer not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<AnswerUpvoteEntity> allUpvote = answerUpvoteRepository.findAllByAnswerId(answerId, pageable);
        return allUpvote.map(this::convertToResponse);
    }

    public AnswerUpvoteEntity convertToEntity(UserEntity user, AnswerEntity answer) {
        return AnswerUpvoteEntity.builder()
                .user(user)
                .answer(answer)
                .build();
    }


    public AnswerVoteResponse convertToResponse(AnswerUpvoteEntity answerUpvoteEntity) {
        return AnswerVoteResponse.builder()
                .id(answerUpvoteEntity.getId())
                .answerId(answerUpvoteEntity.getAnswer().getId())
                .userId(answerUpvoteEntity.getUser().getId())
                .name(answerUpvoteEntity.getUser().getFirstName() + " " + answerUpvoteEntity.getUser().getLastName())
                .createdAt(answerUpvoteEntity.getCreatedAt())
                .build();
    }
}
