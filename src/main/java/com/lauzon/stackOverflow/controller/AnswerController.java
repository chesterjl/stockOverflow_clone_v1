package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.request.AnswerRequest;
import com.lauzon.stackOverflow.dto.request.UpdateAnswerRequest;
import com.lauzon.stackOverflow.dto.response.AnswerResponse;
import com.lauzon.stackOverflow.service.impl.AnswerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerServiceImpl answerService;

    @PostMapping
    public ResponseEntity<AnswerResponse> answerQuestion(@Valid @RequestBody AnswerRequest answerRequest) {
        AnswerResponse answerResponse = answerService.answerQuestion(answerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(answerResponse);
    }

    @PutMapping("/{answerId}")
    public ResponseEntity<AnswerResponse> updateAnswer(@PathVariable Long answerId, @Valid @RequestBody UpdateAnswerRequest answerRequest) {
        AnswerResponse updatedAnswer = answerService.updateAnswer(answerRequest, answerId);
        return ResponseEntity.ok(updatedAnswer);
    }

    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        answerService.deleteAnswer(answerId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<Page<AnswerResponse>> viewAllAnswerForQuestion(@RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size,
                                                                         @PathVariable Long questionId) {
        Page<AnswerResponse> allAnswerForQuestion = answerService.viewAllAnswerForQuestion(page, size, questionId);
        return ResponseEntity.ok(allAnswerForQuestion);
    }
}


