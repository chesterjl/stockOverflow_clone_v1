package com.lauzon.stackOverflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lauzon.stackOverflow.dto.request.FilterRequest;
import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import com.lauzon.stackOverflow.service.impl.QuestionServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/questions")
public class QuestionController {

    private final QuestionServiceImpl questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(@RequestPart("request") String requestString,
                                                           @RequestPart("image") MultipartFile image) {
       try {
           ObjectMapper objectMapper = new ObjectMapper();
           QuestionRequest questionRequest = objectMapper.readValue(requestString, QuestionRequest.class);
           questionRequest.setImageFile(image);
           QuestionResponse savedQuestion = questionService.createQuestion(questionRequest);
           return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestion);
       } catch (Exception e) {
           throw new RuntimeException(e.getMessage());
       }
    }

    @PatchMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(@RequestPart("request") String requestString,
                                                           @RequestPart("image") MultipartFile image,
                                                           @PathVariable Long questionId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            QuestionRequest questionRequest = objectMapper.readValue(requestString, QuestionRequest.class);
            questionRequest.setImageFile(image);
            QuestionResponse updatedQuestion = questionService.updateQuestion(questionRequest, questionId);
            return ResponseEntity.ok(updatedQuestion);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/view/{questionId}")
    public ResponseEntity<QuestionResponse> viewQuestion(@PathVariable Long questionId) {
        QuestionResponse question = questionService.viewQuestion(questionId);
        return ResponseEntity.ok(question);
    }

    @GetMapping
    public ResponseEntity<Page<QuestionResponse>> viewAllQuestions(@RequestParam(defaultValue = "0") int page ,
                                                                   @RequestParam(defaultValue = "10") int size) {
       Page<QuestionResponse> questions = questionService.viewAllQuestions(page, size);
       return ResponseEntity.ok(questions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<QuestionResponse>> viewAllQuestionsForUser(@PathVariable Long userId,
                                                                          @RequestParam(defaultValue = "0") int page ,
                                                                          @RequestParam(defaultValue = "10") int size) {
        Page<QuestionResponse> questions = questionService.viewAllQuestionsForUser(page, size, userId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/feeds")
    public ResponseEntity<Page<QuestionResponse>> feeds(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Page<QuestionResponse> feedsData = questionService.feeds(page, size);
        return ResponseEntity.ok(feedsData);
    }


    @GetMapping("/search")
    public ResponseEntity<Page<QuestionResponse>> searchQuestions(@RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size,
                                                                  @RequestBody FilterRequest request) {
        Page<QuestionResponse> filterData = questionService.searchQuestions(page, size, request.getTitle());
        return ResponseEntity.ok(filterData);
    }


}

