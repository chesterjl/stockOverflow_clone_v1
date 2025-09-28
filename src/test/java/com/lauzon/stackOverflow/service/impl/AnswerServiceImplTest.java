package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.AnswerRequest;
import com.lauzon.stackOverflow.dto.request.UpdateAnswerRequest;
import com.lauzon.stackOverflow.dto.response.AnswerResponse;
import com.lauzon.stackOverflow.entity.AnswerEntity;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.enums.Role;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.AnswerDownvoteRepository;
import com.lauzon.stackOverflow.repository.AnswerRepository;
import com.lauzon.stackOverflow.repository.AnswerUpvoteRepository;
import com.lauzon.stackOverflow.repository.QuestionRepository;
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
@DisplayName("AnswerServiceImpl Unit Test")
public class AnswerServiceImplTest {

    @Mock
    private UtilMethod utilMethod;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private AnswerUpvoteRepository answerUpvoteRepository;
    @Mock
    private AnswerDownvoteRepository answerDownvoteRepository;

    @InjectMocks
    private AnswerServiceImpl answerService;

    private UserEntity user;
    private UserEntity user2;
    private AnswerEntity answerEntity;
    private AnswerRequest answerRequest;
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
                .lastName("Last")
                .email("test2@gmail.com")
                .password("testpass2")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();


        this.questionEntity = QuestionEntity.builder()
                .id(1L)
                .title("Demo: title 1")
                .description("Demo: description 1")
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        this.answerRequest = AnswerRequest.builder()
                .description("Demo: answer 1")
                .questionId(questionEntity.getId())
                .build();

        this.answerEntity = AnswerEntity.builder()
                .id(1L)
                .question(questionEntity)
                .user(user)
                .description(answerRequest.getDescription())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }


    @Nested
    @DisplayName("Answer Question Test")
    class AnswerQuestion {

        @Test
        @DisplayName("should answer question successfully")
        void shouldAnswerQuestionSuccessfully() {

            when(questionRepository.findById(answerRequest.getQuestionId()))
                    .thenReturn(Optional.of(questionEntity));
            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(answerRepository.save(any(AnswerEntity.class))).thenReturn(answerEntity);

            AnswerResponse response = answerService.answerQuestion(answerRequest);

            assertNotNull(response);
            assertEquals(response.getUserId(), user.getId());
            assertEquals(response.getQuestionId(), questionEntity.getId());
            assertEquals(response.getId(), answerEntity.getId());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(questionRepository, times(1)).findById(answerRequest.getQuestionId());
            verify(answerRepository, times(1)).save(argThat(answer ->
                    answer.getUser().equals(user) &&
                            answer.getQuestion().getId().equals(questionEntity.getId())
            ));
        }
    }

    @Nested
    @DisplayName("Update Answer Test")
    class UpdateAnswer {

        @Test
        @DisplayName("should update answer successfully")
        void shouldUpdateAnswerSuccessfully() {
            AnswerEntity existingAnswer = answerEntity;

            UpdateAnswerRequest request = UpdateAnswerRequest.builder()
                    .description("Demo: answer [update]")
                    .build();
            Long answerId = 1L;

            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(answerRepository.findByIdAndUserId(answerId, user.getId())).thenReturn(Optional.of(answerEntity));
            when(answerRepository.save(any(AnswerEntity.class))).thenAnswer(invocation -> {
                AnswerEntity savedAnswer = invocation.getArgument(0);
                // Simulate DB behavior: return updated entity with same ID
                savedAnswer.setId(existingAnswer.getId());
                return  savedAnswer;
            });

            AnswerResponse response = answerService.updateAnswer(request, answerId);

            assertNotNull(response);
            assertEquals(1L, response.getId());
            assertEquals(response.getDescription(), request.getDescription());
            assertEquals(response.getUserId(), user.getId());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerRepository, times(1)).findByIdAndUserId(answerId, user.getId());
            verify(answerRepository, times(1)).save(argThat(answer ->
                    answer.getUser().equals(user) &&
                        answer.getDescription().equals(request.getDescription())));
        }
    }

    @Nested
    @DisplayName("Delete Answer Test")
    class DeleteAnswer {

        @Test
        @DisplayName("should delete answer successfully")
        void shouldDeleteAnswerSuccessfully() {
            Long answerId = 1L;


            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(answerRepository.findByIdAndUserId(answerId, user.getId()))
                    .thenReturn(Optional.of(answerEntity));
            doNothing().when(answerRepository).delete(any(AnswerEntity.class));

            answerService.deleteAnswer(answerId);

            assertEquals(answerEntity.getUser().getId(), user.getId());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerRepository, times(1)).findByIdAndUserId(answerId, user.getId());
            verify(answerRepository, times(1)).delete(any(AnswerEntity.class));
        }
    }

    @Nested
    @DisplayName("Fetch answers for question")
    class FetchAnswers {

        @Test
        @DisplayName("should fetch all answers for question")
        void shouldFetchAllAnswersForQuestion() {
            int page = 0;
            int size = 10;
            Long questionId = 1L;

            when(questionRepository.findById(questionId)).thenReturn(Optional.of(questionEntity));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<AnswerEntity> mockAnswers = List.of(answerEntity);
            Page<AnswerEntity> mockPage = new PageImpl<>(mockAnswers, pageable, mockAnswers.size());
            when(answerRepository.findAllByQuestionId(questionId, pageable)).thenReturn(mockPage);

            Page<AnswerResponse> responses = answerService.viewAllAnswerForQuestion(page, size, questionId);

            assertNotNull(responses);
            assertEquals(1, responses.getContent().size());
            assertEquals(answerEntity.getDescription(), responses.getContent().getFirst().getDescription());
            assertEquals(answerEntity.getUser().getId(), responses.getContent().getFirst().getUserId());
            assertEquals(1L, responses.getContent().getFirst().getId());

            verify(questionRepository, times(1)).findById(questionId);
            verify(answerRepository, times(1)).findAllByQuestionId(questionId, pageable);
        }
    }

    @Nested
    @DisplayName("Answer Exception Test")
    class AnswerException {

        @Test
        @DisplayName("should throw ResourceNotFoundException when user try to answer a question that doesn't exist or found")
        void shouldThrowResourceNotFoundExceptionInAnswerQuestion() {
            AnswerRequest answerRequest1 = AnswerRequest.builder()
                    .description("Demo: description 2")
                    .questionId(2L)
                    .build();

            when(questionRepository.findById(answerRequest1.getQuestionId())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> answerService.answerQuestion(answerRequest1));

            assertEquals("Question id not found to answer", exception.getMessage());

            verify(questionRepository, times(1)).findById(answerRequest1.getQuestionId());
            verify(answerRepository, never()).save(any(AnswerEntity.class));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when user try to update an answer that doesn't exist or not authorized to update")
        void shouldThrowAccessDeniedExceptionInUpdateAnswer() {
            UpdateAnswerRequest request = UpdateAnswerRequest.builder()
                    .description("Demo: description 3")
                    .build();

            Long answerId = answerEntity.getId();
            when(utilMethod.getCurrentUser()).thenReturn(user2);
            when(answerRepository.findByIdAndUserId(answerId, user2.getId())).thenReturn(Optional.empty());

            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> answerService.updateAnswer(request, answerId));
            assertEquals("Answer id not found or you are not authorized to update it", exception.getMessage());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerRepository, times(1)).findByIdAndUserId(answerId, user2.getId());
            verify(answerRepository, never()).save(any(AnswerEntity.class));
        }

        @Test
        @DisplayName("should throw AccessDeniedException when user try to delete an answer that doesn't exist or not authorized to update")
        void shouldThrowAccessDeniedExceptionInDeleteAnswer() {
            Long answerId = 1L;

            when(utilMethod.getCurrentUser()).thenReturn(user2);
            when(answerRepository.findByIdAndUserId(answerId, user2.getId())).thenReturn(Optional.empty());

            AccessDeniedException exception = assertThrows(AccessDeniedException.class,
                    () -> answerService.deleteAnswer(answerId));

            assertEquals("Answer id not found or you are not authorized to delete it", exception.getMessage());

            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerRepository, times(1)).findByIdAndUserId(answerId, user2.getId());
            verify(answerRepository, never()).delete(any(AnswerEntity.class));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user try to get all answer for question that doesn't exist or found")
        void shouldThrowResourceNotFoundExceptionInViewAllAnswer() {
            int page = 0;
            int size = 10;
            Long answerId = 5L;

            when(questionRepository.findById(answerId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> answerService.viewAllAnswerForQuestion(page, size, answerId));

            assertEquals("Question id not found", exception.getMessage());

            verify(questionRepository, times(1)).findById(answerId);
        }
    }
}

