package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.enums.Role;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.exception.UserNotFoundException;
import com.lauzon.stackOverflow.repository.QuestionRepository;
import com.lauzon.stackOverflow.repository.UserRepository;
import com.lauzon.stackOverflow.util.UtilMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuestionServiceImpl Unit Test")
public class QuestionServiceImplTest {

    @Mock
    private UtilMethod utilMethod;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionServiceImpl questionService;

    private UserEntity user;
    private UserEntity user2;
    private QuestionRequest questionRequest;
    private QuestionEntity questionEntity;

    @BeforeEach
    void setUp() {
        this.user = UserEntity.builder()
                .id(1L)
                .firstName("Chester")
                .lastName("Lauzon")
                .email("test@gmail.com")
                .password("testpass")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        this.user2 = UserEntity.builder()
                .id(2L)
                .firstName("Test2")
                .lastName("lastname")
                .email("tes2t@gmail.com")
                .password("testpass2")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        this.questionRequest = QuestionRequest.builder()
                .title("Demo: title 1")
                .description("Demo: description 1")
                .build();

        this.questionEntity = QuestionEntity.builder()
                .id(1L)
                .title(questionRequest.getTitle())
                .description(questionRequest.getDescription())
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


    }


    @Nested
    @DisplayName("Create Question Test")
    class CreateQuestionTest {

        @Test
        @DisplayName("should create question successfully")
        void shouldCreateQuestion() {

            // Mock dependencies
            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(questionRepository.save(any(QuestionEntity.class))).thenReturn(questionEntity);

            QuestionResponse response = questionService.createQuestion(questionRequest);

            assertNotNull(response);
            assertEquals(response.getId(), questionEntity.getId());
            assertEquals(response.getDescription(), questionEntity.getDescription());
            assertEquals(response.getTitle(), questionEntity.getTitle());
            assertEquals(response.getUserId(), user.getId());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(questionRepository, times(1)).save(any(QuestionEntity.class));
            verify(questionRepository).save(argThat(question -> question.getUser().equals(user)));
        }
    }

    @Nested
    @DisplayName("Update Question Test")
    class UpdateQuestionTest {

        @Test
        @DisplayName("should update question successfully")
        void shouldUpdateQuestionSuccessfully() {

            QuestionEntity existingQuestion = questionEntity;
            QuestionRequest questionRequest1 = QuestionRequest.builder()
                    .title("title updated")
                    .description("description updated")
                    .build();
            Long questionId = 1L;

            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(questionRepository.findByIdAndUserId(questionId, user.getId())).thenReturn(Optional.of(existingQuestion));
            when(questionRepository.save(any(QuestionEntity.class))).thenAnswer(invocation -> {
                QuestionEntity p = invocation.getArgument(0);
                // Simulate DB behavior: return updated entity with same ID
                p.setId(existingQuestion.getId());
                return  p;
            });

            QuestionResponse response = questionService.updateQuestion(questionRequest1, questionId);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(response.getTitle(), questionRequest1.getTitle());
            assertEquals(response.getDescription(), questionRequest1.getDescription());
            assertEquals(response.getUserId(), user.getId());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(questionRepository, times(1)).findByIdAndUserId(questionId, user.getId());
            verify(questionRepository, times(1)).save(any(QuestionEntity.class));
            verify(questionRepository).save(argThat( question ->
                    question.getUser().equals(user) &&
                        question.getTitle().equals(questionRequest1.getTitle())));
        }
    }

    @Nested
    @DisplayName("Delete Question Test")
    class DeleteQuestionTest {

        @Test
        @DisplayName("should delete question successfully")
        void shouldDeleteQuestionSuccessfully() {
            Long questionId = 1L;

            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(questionRepository.findByIdAndUserId(questionId, user.getId())).thenReturn(Optional.of(questionEntity));
            doNothing().when(questionRepository).delete(any(QuestionEntity.class));

            questionService.deleteQuestion(questionId);

            assertEquals(questionEntity.getUser().getId(), user.getId());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(questionRepository, times(1)).findByIdAndUserId(questionId, user.getId());
            verify(questionRepository, times(1)).delete(any(QuestionEntity.class));
        }
    }

    @Nested
    @DisplayName("Fetch Question Test")
    class fetchQuestionTest {

        @Test
        @DisplayName("should get question by question id successfully")
        void shouldGetQuestionSuccessfully() {
            Long questionId = 1L;

            when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionEntity));

            QuestionResponse response = questionService.viewQuestion(questionId);

            assertNotNull(response);
            assertEquals(response.getTitle(), questionEntity.getTitle());
            assertEquals(response.getDescription(), questionEntity.getDescription());
            assertEquals(response.getUserId(), user.getId());

            verify(questionRepository, times(1)).findById(questionId);
        }

        @Test
        @DisplayName("should get all question for user logged-in")
        void shouldGetAllQuestionSuccessfully() {
            int page = 0;
            int size = 10;

            when(utilMethod.getCurrentUser()).thenReturn(user);

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<QuestionEntity> mockQuestions = List.of(questionEntity);
            Page<QuestionEntity> mockPage = new PageImpl<>(mockQuestions, pageable, mockQuestions.size());
            when(questionRepository.findAllQuestionByUserId(user.getId(), pageable)).thenReturn(mockPage);

            Page<QuestionResponse> responses = questionService.viewAllQuestions(page, size);

            assertNotNull(responses);
            assertEquals(1, responses.getContent().size());
            assertEquals(responses.getContent().getFirst().getUserId(), user.getId());
            assertEquals(responses.getContent().getFirst().getTitle(), questionEntity.getTitle());
            assertEquals(responses.getContent().getFirst().getDescription(), questionEntity.getDescription());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(questionRepository, times(1)).findAllQuestionByUserId(user.getId(), pageable);
        }

        @Test
        @DisplayName("should get all question for user")
        void shouldGetAllQuestionForUserSuccessfully() {
            int page = 0;
            int size = 10;
            Long userId = 1L;

            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<QuestionEntity> mockQuestions = List.of(questionEntity);
            Page<QuestionEntity> mockPage = new PageImpl<>(mockQuestions, pageable, mockQuestions.size());
            when(questionRepository.findAllQuestionByUserId(user.getId(), pageable)).thenReturn(mockPage);

            Page<QuestionResponse> responses = questionService.viewAllQuestionsForUser(page, size, userId);

            assertNotNull(responses);
            assertEquals(1, responses.getContent().size());
            assertEquals(responses.getContent().getFirst().getUserId(), user.getId());
            assertEquals(responses.getContent().getFirst().getTitle(), questionEntity.getTitle());
            assertEquals(responses.getContent().getFirst().getDescription(), questionEntity.getDescription());

            verify(userRepository, times(1)).findById(userId);
            verify(questionRepository, times(1)).findAllQuestionByUserId(user.getId(), pageable);
        }


        @Test
        @DisplayName("should get all question for feeds data")
        void shouldGetAllQuestionForFeedsSuccessfully() {
            int page = 0;
            int size = 10;

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<QuestionEntity> mockQuestions = List.of(questionEntity);
            Page<QuestionEntity> mockPage = new PageImpl<>(mockQuestions, pageable, mockQuestions.size());
            when(questionRepository.findAll(pageable)).thenReturn(mockPage);

            Page<QuestionResponse> responses = questionService.feeds(page, size);

            assertNotNull(responses);
            assertEquals(1, responses.getContent().size());
            assertEquals(responses.getContent().getFirst().getTitle(), questionEntity.getTitle());
            assertEquals(responses.getContent().getFirst().getDescription(), questionEntity.getDescription());

            verify(questionRepository, times(1)).findAll(pageable);
        }

        @Test
        @DisplayName("should fetch all question by filter successfully")
        void shouldFetchAllQuestionFilteredSuccessfully() {
            int page = 0;
            int size = 10;
            String titleKeyword = "Demo";

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<QuestionEntity> mockQuestions = List.of(questionEntity);
            Page<QuestionEntity> mockPage = new PageImpl<>(mockQuestions, pageable, mockQuestions.size());
            when(questionRepository.findByTitleContainingIgnoreCase(titleKeyword, pageable)).thenReturn(mockPage);

            Page<QuestionResponse> responses = questionService.searchQuestions(page, size, titleKeyword);

            assertNotNull(responses);
            assertEquals(1, responses.getContent().size());
            assertEquals(responses.getContent().getFirst().getTitle(), questionEntity.getTitle());
            assertEquals(responses.getContent().getFirst().getDescription(), questionEntity.getDescription());

            verify(questionRepository, times(1)).findByTitleContainingIgnoreCase(titleKeyword, pageable);
        }
    }

    @Nested
    @DisplayName("Question Exception Test")
    class QuestionExceptionTest {

        @Test
        @DisplayName("should throw AccessDeniedException when user try to update a question doesn't exist or not his own question")
        void shouldThrowAccessDeniedExceptionInUpdateQuestion() {
            Long questionId = 1L;

            when(utilMethod.getCurrentUser()).thenReturn(user2);
            when(questionRepository.findByIdAndUserId(questionId, user2.getId()))
                    .thenReturn(Optional.empty());

            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> questionService.updateQuestion(questionRequest, questionId));

            assertEquals("Question not found or you are not authorized to update this question", exception.getMessage());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(questionRepository, times(1)).findByIdAndUserId(questionId, user2.getId());
            verify(questionRepository, never()).save(any(QuestionEntity.class));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when user try to delete a question doesn't exist or not his own question")
        void shouldThrowAccessDeniedExceptionInDeleteQuestion() {
            Long userId = user2.getId();
            Long questionId = 1L;

            when(utilMethod.getCurrentUser()).thenReturn(user2);
            when(questionRepository.findByIdAndUserId(questionId, userId))
                    .thenReturn(Optional.empty());


            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> questionService.deleteQuestion(questionId));

            assertEquals("Question not found or you are not authorized to delete this question", exception.getMessage());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(questionRepository, never()).delete(any(QuestionEntity.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user try to find a question that doesn't exist")
        void shouldThrowResourceNotFoundExceptionInViewQuestion() {
            Long questionId = 2L;

            when(questionRepository.findById(questionId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> questionService.viewQuestion(questionId));

            assertEquals("Question not found", exception.getMessage());

            verify(questionRepository, times(1)).findById(questionId);
        }

        @Test
        @DisplayName("should throw UserNotFoundException when user try to fetch all question for user that doesn't exist")
        void shouldThrowUserNotFoundExceptionInViewAllQuestionForUser() {
            int page = 0;
            int size = 10;
            Long userId = 3L;

            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () -> questionService.viewAllQuestionsForUser(page, size, userId));

            assertEquals("User not found", exception.getMessage());

            verify(userRepository, times(1)).findById(userId);
        }
    }
}
