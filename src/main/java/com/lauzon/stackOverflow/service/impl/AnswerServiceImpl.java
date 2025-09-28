package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.AnswerRequest;
import com.lauzon.stackOverflow.dto.request.UpdateAnswerRequest;
import com.lauzon.stackOverflow.dto.response.AnswerResponse;
import com.lauzon.stackOverflow.entity.AnswerEntity;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.AnswerDownvoteRepository;
import com.lauzon.stackOverflow.repository.AnswerRepository;
import com.lauzon.stackOverflow.repository.AnswerUpvoteRepository;
import com.lauzon.stackOverflow.repository.QuestionRepository;
import com.lauzon.stackOverflow.service.AnswerService;
import com.lauzon.stackOverflow.util.UtilMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final UtilMethod utilMethod;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerUpvoteRepository answerUpvoteRepository;
    private final AnswerDownvoteRepository answerDownvoteRepository;


    @Override
    public AnswerResponse answerQuestion(AnswerRequest answerRequest) {
        QuestionEntity question = questionRepository.findById(answerRequest.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question id not found to answer"));

        UserEntity user = utilMethod.getCurrentUser();

        AnswerEntity answer = convertToEntity(answerRequest, user, question);
        answer = answerRepository.save(answer);
        return convertToResponse(answer);
    }

    @Override
    public AnswerResponse updateAnswer(UpdateAnswerRequest answerRequest, Long answerId) {
        UserEntity user = utilMethod.getCurrentUser();

        AnswerEntity existingAnswer = answerRepository.findByIdAndUserId(answerId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Answer id not found or you are not authorized to update it"));

        existingAnswer.setDescription(answerRequest.getDescription());

        existingAnswer = answerRepository.save(existingAnswer);
        return convertToResponse(existingAnswer);
    }

    @Override
    public void deleteAnswer(Long answerId) {
        UserEntity user = utilMethod.getCurrentUser();
        AnswerEntity existingAnswer = answerRepository.findByIdAndUserId(answerId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Answer id not found or you are not authorized to delete it"));
        answerRepository.delete(existingAnswer);
    }

    @Override
    public Page<AnswerResponse> viewAllAnswerForQuestion(int page, int size, Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question id not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AnswerEntity> answerResponses = answerRepository.findAllByQuestionId(questionId, pageable);
        return answerResponses.map(this::convertToResponse);
    }

    private AnswerEntity convertToEntity(AnswerRequest answerRequest, UserEntity user, QuestionEntity question) {
        return AnswerEntity.builder()
                .description(answerRequest.getDescription())
                .user(user)
                .question(question)
                .build();
    }


    private AnswerResponse convertToResponse(AnswerEntity answerEntity) {
        Long upvotes = answerUpvoteRepository.countAllByAnswerId(answerEntity.getId());
        Long downvotes = answerDownvoteRepository.countAllByAnswerId(answerEntity.getId());

        return AnswerResponse.builder()
                .id(answerEntity.getId())
                .questionId(answerEntity.getQuestion().getId())
                .description(answerEntity.getDescription())
                .upvote(upvotes)
                .downvote(downvotes)
                .name(answerEntity.getUser().getFirstName() + " " + answerEntity.getUser().getLastName())
                .userId(answerEntity.getUser().getId())
                .createdAt(answerEntity.getCreatedAt())
                .updatedAt(answerEntity.getUpdatedAt())
                .build();
    }
}
