package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.request.AnswerVoteRequest;
import com.lauzon.stackOverflow.dto.response.AnswerVoteResponse;
import com.lauzon.stackOverflow.service.impl.AnswerDownvoteServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/answers/downvote")
public class AnswerDownvoteController {

    private final AnswerDownvoteServiceImpl answerDownvoteService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> toggleDownvoteForAnswer(@Valid @RequestBody AnswerVoteRequest request) {
        Map<String, Object> response = answerDownvoteService.toggleDownvoteForAnswer(request);

        if (response.containsKey("message")) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<Page<AnswerVoteResponse>> viewAllDownvoteForAnswer(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @PathVariable Long answerId) {
        Page<AnswerVoteResponse> responses = answerDownvoteService.viewAllDownvoteForAnswer(page, size, answerId);
        return ResponseEntity.ok(responses);
    }
}
