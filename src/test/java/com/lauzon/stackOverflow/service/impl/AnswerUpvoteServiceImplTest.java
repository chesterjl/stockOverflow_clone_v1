package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.dto.request.AnswerVoteRequest;
import com.lauzon.stackOverflow.dto.response.AnswerVoteResponse;
import com.lauzon.stackOverflow.entity.AnswerEntity;
import com.lauzon.stackOverflow.entity.AnswerUpvoteEntity;
import com.lauzon.stackOverflow.entity.QuestionEntity;
import com.lauzon.stackOverflow.entity.UserEntity;
import com.lauzon.stackOverflow.enums.Role;
import com.lauzon.stackOverflow.exception.ResourceNotFoundException;
import com.lauzon.stackOverflow.repository.AnswerRepository;
import com.lauzon.stackOverflow.repository.AnswerUpvoteRepository;
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
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnswerUpvoteServiceImpl Unit Test")
public class AnswerUpvoteServiceImplTest {

    @Mock
    private UtilMethod utilMethod;
    @Mock
    private AnswerUpvoteRepository answerUpvoteRepository;
    @Mock
    private AnswerRepository answerRepository;

    @InjectMocks
    private AnswerUpvoteServiceImpl answerUpvoteService;

    private AnswerVoteRequest answerVoteRequest;
    private AnswerUpvoteEntity answerUpvoteEntity;
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

        this.answerUpvoteEntity = AnswerUpvoteEntity.builder()
                .id(1L)
                .user(user)
                .answer(answerEntity)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Toggle Upvote Test")
    class ToggleUpvote {

        @Test
        @DisplayName("should delete upvote successfully if upvote exist")
        void shouldUnToggleUpVoteSuccessfully() {

            when(answerRepository.findById(answerVoteRequest.getAnswerId())).thenReturn(Optional.of(answerEntity));
            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(answerUpvoteRepository.findByAnswerIdAndUserId(answerVoteRequest.getAnswerId(), user.getId()))
                    .thenReturn(Optional.of(answerUpvoteEntity));
            doNothing().when(answerUpvoteRepository).delete(answerUpvoteEntity);

            Map<String, Object> result = answerUpvoteService.toggleUpvote(answerVoteRequest);

            assertEquals("Upvote deleted", result.get("message"));

            verify(answerRepository, times(1)).findById(answerVoteRequest.getAnswerId());
            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerUpvoteRepository, times(1)).findByAnswerIdAndUserId(answerVoteRequest.getAnswerId(), user.getId());
            verify(answerUpvoteRepository, times(1)).delete(answerUpvoteEntity);
            verify(answerUpvoteRepository, never()).save(any(AnswerUpvoteEntity.class));
        }

        @Test
        @DisplayName("should add upvote successfully")
        void shouldToggleUpVoteSuccessfully() {
            AnswerVoteRequest answerVoteRequest1 = AnswerVoteRequest.builder()
                    .answerId(answerEntity2.getId())
                    .build();

            when(answerRepository.findById(answerVoteRequest1.getAnswerId())).thenReturn(Optional.of(answerEntity2));
            when(utilMethod.getCurrentUser()).thenReturn(user);
            when(answerUpvoteRepository.findByAnswerIdAndUserId(answerVoteRequest1.getAnswerId(), user.getId())).thenReturn(Optional.empty());
            when(answerUpvoteRepository.save(any(AnswerUpvoteEntity.class))).thenReturn(answerUpvoteEntity);

            Map<String, Object> result = answerUpvoteService.toggleUpvote(answerVoteRequest1);

            // ✅ cast and assert real response object
            AnswerVoteResponse response = (AnswerVoteResponse) result.get("response");
            assertEquals(answerEntity.getId(), response.getAnswerId());
            assertEquals(user.getId(), response.getUserId());

            verify(answerRepository, times(1)).findById(answerVoteRequest1.getAnswerId());
            verify(utilMethod, times(1)).getCurrentUser();
            verify(answerUpvoteRepository, times(1)).findByAnswerIdAndUserId(answerVoteRequest1.getAnswerId(), user.getId());
            verify(answerUpvoteRepository, times(1)).save(any(AnswerUpvoteEntity.class));
            verify(answerUpvoteRepository, never()).delete(any(AnswerUpvoteEntity.class));
        }
    }

    @Nested
    @DisplayName("Fetch Upvotes Test")
    class FetchUpvotesTest {

        @Test
        @DisplayName("should fetch upvotes for answers successfully")
        void shouldFetchUpvotesForAnswer() {
            int page = 0;
            int size = 10;
            Long answerId = 1L;

            when(answerRepository.findById(answerId)).thenReturn(Optional.of(answerEntity));

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            List<AnswerUpvoteEntity> mockUpvotes = List.of(answerUpvoteEntity);
            Page<AnswerUpvoteEntity> mockPage = new PageImpl<>(mockUpvotes, pageable, mockUpvotes.size());
            when(answerUpvoteRepository.findAllByAnswerId(answerId, pageable)).thenReturn(mockPage);

            Page<AnswerVoteResponse> responses = answerUpvoteService.viewAllUpvoteOfAnswer(page, size, answerId);

            assertNotNull(responses);
            assertEquals(1, responses.getContent().size());
            assertEquals(user.getId(), responses.getContent().getFirst().getUserId());

            verify(answerRepository, times(1)).findById(answerId);
            verify(answerUpvoteRepository, times(1)).findAllByAnswerId(answerId, pageable);
        }
    }

    @Nested
    @DisplayName("AnswerUpvote Exception Test")
    class AnswerUpvoteExceptionTest {

        @Test
        @DisplayName("should throw ResourceNotFoundException when user try to toggle upvote for answer that doesn't exist")
        void shouldThrowResourceNotFoundExceptionInToggleUpvote() {
            AnswerVoteRequest answerVoteRequest1 = AnswerVoteRequest.builder()
                    .answerId(3L)
                    .build();

            when(answerRepository.findById(answerVoteRequest1.getAnswerId())).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> answerUpvoteService.toggleUpvote(answerVoteRequest1));

            assertEquals("Answer not found", exception.getMessage());

            verify(answerRepository, times(1)).findById(answerVoteRequest1.getAnswerId());
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when user try to view upvotes for answer that doesn't exist")
        void shouldThrowResourceNotFoundExceptionInViewAllUpvotes() {
            int page = 0;
            int size = 10;
            Long answerId = 3L;

            when(answerRepository.findById(answerId)).thenReturn(Optional.empty());

            ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                    () -> answerUpvoteService.viewAllUpvoteOfAnswer(page, size, answerId));

            assertEquals("Answer not found", exception.getMessage());

            verify(answerRepository, times(1)).findById(answerId);
        }
    }
}
