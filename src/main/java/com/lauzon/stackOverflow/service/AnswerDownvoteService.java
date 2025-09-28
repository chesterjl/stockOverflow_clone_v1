package com.lauzon.stackOverflow.service;

import com.lauzon.stackOverflow.dto.request.AnswerVoteRequest;
import com.lauzon.stackOverflow.dto.response.AnswerVoteResponse;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface AnswerDownvoteService {

    Map<String, Object> toggleDownvoteForAnswer(AnswerVoteRequest request);

    Page<AnswerVoteResponse> viewAllDownvoteForAnswer(int page, int size, Long answerId);
}
