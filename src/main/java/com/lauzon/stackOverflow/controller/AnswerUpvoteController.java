package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.request.AnswerVoteRequest;
import com.lauzon.stackOverflow.dto.response.AnswerVoteResponse;
import com.lauzon.stackOverflow.service.impl.AnswerUpvoteServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users/answers/upvote")
@RequiredArgsConstructor
public class AnswerUpvoteController {

    private final AnswerUpvoteServiceImpl answerUpvoteService;

    @PostMapping
    public ResponseEntity<Map<String ,Object>> toggleAnswerUpVote(@Valid @RequestBody AnswerVoteRequest request) {
        Map<String, Object> response = answerUpvoteService.toggleUpvote(request);

        if (response.containsKey("message")) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{answerId}")
    public ResponseEntity<Page<AnswerVoteResponse>> viewAllUpvoteForAnswer(@RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "10") int size,
                                                                             @PathVariable Long answerId) {
        Page<AnswerVoteResponse> response = answerUpvoteService.viewAllUpvoteOfAnswer(page, size, answerId);
        return ResponseEntity.ok(response);
    }
}
