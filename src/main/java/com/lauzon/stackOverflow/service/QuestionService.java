package com.lauzon.stackOverflow.service;


import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import org.springframework.data.domain.Page;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest questionRequest);

    QuestionResponse updateQuestion(QuestionRequest questionRequest, Long questionId);

    void deleteQuestion(Long questionId);

    QuestionResponse viewQuestion(Long questionId);

    Page<QuestionResponse> viewAllQuestions(int page, int size); // user logged-in

    Page<QuestionResponse> viewAllQuestionsForUser(int page, int size, Long userId);

    Page<QuestionResponse> feeds(int page, int size);

    Page<QuestionResponse> searchQuestions(int page, int size, String question);
}
