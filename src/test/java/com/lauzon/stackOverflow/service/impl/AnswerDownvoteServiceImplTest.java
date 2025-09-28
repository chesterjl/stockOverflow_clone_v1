package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.AnswerVoteRequest;
import com.lauzon.stackOverflow.dto.response.AnswerVoteResponse;
import com.lauzon.stackOverflow.entity.*;
import com.lauzon.stackOverflow.enums.Role;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.AnswerDownvoteRepository;
import com.lauzon.stackOverflow.repository.AnswerRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnswerDownvoteServiceImpl Unit Test")
public class AnswerDownvoteServiceImplTest {

    @Mock
    private AnswerDownvoteRepository answerDownvoteRepository;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private UtilMethod utilMethod;

    @InjectMocks
    private AnswerDownvoteServiceImpl answerDownvoteService;


    private AnswerVoteRequest answerVoteRequest;
    private AnswerDownvoteEntity answerDownvoteEntity;
    private AnswerEntity answerEntity;
    private AnswerEntity answerEntity2;
    private UserEntity user;

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

        QuestionEntity questionEntity = QuestionEntity.builder()
                .id(1L)
                .title("Demo: title 1")
                .description("Demo: description 2")
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        this.answerEntity = AnswerEntity.builder()
                .id(1L)
                .question(questionEntity)
                .user(user)
                .description("Demo: answer 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        this.answerEntity2 = AnswerEntity.builder()
                .id(2L)
                .question(questionEntity)
                .user(user)
                .description("Demo: answer 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        this.answerVoteRequest = AnswerVoteRequest.builder()
                .answerId(answerEntity.getId())
                .build();

        this.answerDownvoteEntity = AnswerDownvoteEntity.builder()
                .id(1L)
                .user(user)
                .answer(answerEntity)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Toggle Downvote Test")
    class ToggleDownvote {

        @Test
        @DisplayName("should delete downvote successfully if downvote exist")
        void shouldUnToggleDownVoteSuccessfully() {

            when(answerRepository.findById(answerVoteRequest.getAnswerId())).thenReturn(Optional.of(answerEntity));
            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(answerDownvoteRepository.findByAnswerIdAndUserId(answerVoteRequest.getAnswerId(), user.getId()))
                    .thenReturn(Optional.of(answerDownvoteEntity));
            doNothing().when(answerDownvoteRepository).delete(answerDownvoteEntity);

            Map<String, Object> result = answerDownvoteService.toggleDownvoteForAnswer(answerVoteRequest);

            assertEquals("Downvote deleted", result.get("message"));

            verify(answerRepository, times(1)).findById(answerVoteRequest.getAnswerId());
            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerDownvoteRepository, times(1)).findByAnswerIdAndUserId(answerVoteRequest.getAnswerId(), user.getId());
            verify(answerDownvoteRepository, times(1)).delete(answerDownvoteEntity);
            verify(answerDownvoteRepository, never()).save(any(AnswerDownvoteEntity.class));
        }

        @Test
        @DisplayName("should add downvote successfully")
        void shouldToggleDownVoteSuccessfully() {
            AnswerVoteRequest answerVoteRequest1 = AnswerVoteRequest.builder()
                    .answerId(answerEntity2.getId())
                    .build();

            when(answerRepository.findById(answerVoteRequest1.getAnswerId())).thenReturn(Optional.of(answerEntity2));
            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(answerDownvoteRepository.findByAnswerIdAndUserId(answerVoteRequest1.getAnswerId(), user.getId())).thenReturn(Optional.empty());
            when(answerDownvoteRepository.save(any(AnswerDownvoteEntity.class))).thenReturn(answerDownvoteEntity);

            Map<String, Object> result = answerDownvoteService.toggleDownvoteForAnswer(answerVoteRequest1);

            // ✅ cast and assert real response object
            AnswerVoteResponse response = (AnswerVoteResponse) result.get("response");
            assertEquals(answerEntity.getId(), response.getAnswerId());
            assertEquals(user.getId(), response.getUserId());

            verify(answerRepository, times(1)).findById(answerVoteRequest1.getAnswerId());
            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerDownvoteRepository, times(1)).findByAnswerIdAndUserId(answerVoteRequest1.getAnswerId(), user.getId());
            verify(answerDownvoteRepository, times(1)).save(any(AnswerDownvoteEntity.class));
            verify(answerDownvoteRepository, never()).delete(any(AnswerDownvoteEntity.class));
        }
    }

    @Nested
    @DisplayName("Fetch Downvotes Test")
    class FetchUpvotesTest {

        @Test
        @DisplayName("should fetch downvotes for answers successfully")
        void shouldFetchDownvotesForAnswer() {
            int page = 0;
            int size = 10;
            Long answerId = 1L;

            when(answerRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<AnswerDownvoteEntity> mockDownvotes = List.of(answerDownvoteEntity);
            Page<AnswerDownvoteEntity> mockPage = new PageImpl<>(mockDownvotes, pageable, mockDownvotes.size());
            when(answerDownvoteRepository.findAllByAnswerId(answerId, pageable)).thenReturn(mockPage);

            Page<AnswerVoteResponse> responses = answerDownvoteService.viewAllDownvoteForAnswer(page, size, answerId);

            assertNotNull(responses);
            assertEquals(1, responses.getContent().size());
            assertEquals(user.getId(), responses.getContent().getFirst().getUserId());

            verify(answerRepository, times(1)).findById(answerId);
            verify(answerDownvoteRepository, times(1)).findAllByAnswerId(answerId, pageable);
        }
    }

    @Nested
    @DisplayName("AnswerUpvote Exception Test")
    class AnswerUpvoteExceptionTest {

        @Test
        @DisplayName("should throw ResourceNotFoundException when user try to toggle downvote for answer that doesn't exist")
        void shouldThrowResourceNotFoundExceptionInToggleDownvote() {
            AnswerVoteRequest answerVoteRequest1 = AnswerVoteRequest.builder()
                    .answerId(3L)
                    .build();

            when(answerRepository.findById(answerVoteRequest1.getAnswerId())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> answerDownvoteService.toggleDownvoteForAnswer(answerVoteRequest1));

            assertEquals("Answer not found", exception.getMessage());

            verify(answerRepository, times(1)).findById(answerVoteRequest1.getAnswerId());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user try to view downvotes for answer that doesn't exist")
        void shouldThrowResourceNotFoundExceptionInViewAllDownvotes() {
            int page = 0;
            int size = 10;
            Long answerId = 3L;

            when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> answerDownvoteService.viewAllDownvoteForAnswer(page, size, answerId));

            assertEquals("Answer not found", exception.getMessage());

            verify(answerRepository, times(1)).findById(answerId);
        }
    }
}
