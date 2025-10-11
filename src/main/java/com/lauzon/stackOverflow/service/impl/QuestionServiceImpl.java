package com.lauzon.stackOverflow.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.exception.UserNotFoundException;
import com.lauzon.stackOverflow.repository.QuestionRepository;
import com.lauzon.stackOverflow.repository.UserRepository;
import com.lauzon.stackOverflow.service.QuestionService;
import com.lauzon.stackOverflow.util.UtilMethod;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final UtilMethod utilMethod;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final Cloudinary cloudinary;

    @Override
    public QuestionResponse createQuestion(QuestionRequest questionRequest) throws IOException {
        Map<String, Object> imageUploadResult = cloudinary.uploader().upload(questionRequest.getImageFile().getBytes(), ObjectUtils.asMap("resource_type", "image"));

        String imageUrl, imagePublicId;

        imageUrl = imageUploadResult.get("secure_url").toString();
        imagePublicId = (String) imageUploadResult.get("public_id");

        UserEntity user = utilMethod.getCurrentUser();
        QuestionEntity question = convertToEntity(questionRequest, imageUrl, imagePublicId, user);
        question = questionRepository.save(question);
        return convertToResponse(question);
    }

    @Override
    public QuestionResponse updateQuestion(QuestionRequest questionRequest, Long questionId) throws IOException {
        UserEntity user = utilMethod.getCurrentUser();
        QuestionEntity existingQuestion = questionRepository.findByIdAndUserId(questionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Question not found or you are not authorized to update this question"));

        existingQuestion.setTitle(questionRequest.getTitle());
        existingQuestion.setDescription(questionRequest.getDescription());

        if (questionRequest.getImageFile() != null && !questionRequest.getImageFile().isEmpty()) {
            if (existingQuestion.getImagePublicId() != null) {
                cloudinary.uploader().destroy(existingQuestion.getImagePublicId(), ObjectUtils.asMap("resource_type", "image"));
            }

            Map<String, Object> imageUploadResult = cloudinary.uploader().upload(questionRequest.getImageFile(), ObjectUtils.asMap("resource_type", "image"));

            existingQuestion.setImageUrl( imageUploadResult.get("secure_url").toString());
            existingQuestion.setImagePublicId( (String) imageUploadResult.get("public_id"));
        }

        existingQuestion = questionRepository.save(existingQuestion);

        return convertToResponse(existingQuestion);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        UserEntity user = utilMethod.getCurrentUser();
        QuestionEntity existingQuestion = questionRepository.findByIdAndUserId(questionId, user.getId())
                .orElseThrow(() -> new AccessDeniedException("Question not found or you are not authorized to delete this question"));
        try {
            if (existingQuestion.getImagePublicId() != null) {
                cloudinary.uploader().destroy(existingQuestion.getImagePublicId(), ObjectUtils.asMap("resource_type", "image"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete image from Cloudinary: " + e.getMessage());
        }

        questionRepository.delete(existingQuestion);
    }

    @Override
    public QuestionResponse viewQuestion(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        return convertToResponse(question);
    }

    @Override
    public Page<QuestionResponse> viewAllQuestions(int page, int size) {
        UserEntity user = utilMethod.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuestionEntity> questions = questionRepository.findAllQuestionByUserId(user.getId(), pageable);
        return questions.map(this::convertToResponse);
    }

    @Override
    public Page<QuestionResponse> viewAllQuestionsForUser(int page, int size, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuestionEntity> questions = questionRepository.findAllQuestionByUserId(user.getId(), pageable);
        return questions.map(this::convertToResponse);
    }

    @Override
    public Page<QuestionResponse> feeds(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuestionEntity> questions = questionRepository.findAll(pageable);
        return questions.map(this::convertToResponse);
    }

    @Override
    public Page<QuestionResponse> searchQuestions(int page, int size, String titleKeyWord) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuestionEntity> filteredQuestions = questionRepository.findByTitleContainingIgnoreCase(titleKeyWord, pageable);
        return filteredQuestions.map(this::convertToResponse);
    }

    private QuestionEntity convertToEntity(QuestionRequest questionRequest, String imageUrl, String imagePublicId, UserEntity user) {
        return QuestionEntity.builder()
                .title(questionRequest.getTitle())
                .description(questionRequest.getDescription())
                .imageUrl(imageUrl)
                .imagePublicId(imagePublicId)
                .user(user)
                .build();
    }

    private QuestionResponse convertToResponse(QuestionEntity question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .description(question.getDescription())
                .imageUrl(question.getImageUrl())
                .imagePublicId(question.getImagePublicId())
                .name(question.getUser().getFirstName() + " " + question.getUser().getLastName())
                .userId(question.getUser().getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
