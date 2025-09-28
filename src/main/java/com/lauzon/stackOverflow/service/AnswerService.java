package com.lauzon.stackOverflow.service;

import com.lauzon.stackOverflow.dto.request.AnswerRequest;
import com.lauzon.stackOverflow.dto.request.UpdateAnswerRequest;
import com.lauzon.stackOverflow.dto.response.AnswerResponse;
import org.springframework.data.domain.Page;

public interface AnswerService {

    AnswerResponse answerQuestion(AnswerRequest answerRequest);

    AnswerResponse updateAnswer(UpdateAnswerRequest answerRequest, Long answerId);

    void deleteAnswer(Long answerId);

    Page<AnswerResponse> viewAllAnswerForQuestion(int page, int size, Long questionId);

}
