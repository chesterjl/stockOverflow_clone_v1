package com.lauzon.stackOverflow.controller;

import com.lauzon.stackOverflow.dto.request.AnswerRequest;
import com.lauzon.stackOverflow.dto.request.QuestionRequest;
import com.lauzon.stackOverflow.dto.request.RegisterUserRequest;
import com.lauzon.stackOverflow.dto.response.QuestionResponse;
import com.lauzon.stackOverflow.service.impl.AnswerServiceImpl;
import com.lauzon.stackOverflow.service.impl.AuthServiceImpl;
import com.lauzon.stackOverflow.service.impl.QuestionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller // Note: NOT @RestController. This returns HTML.
@RequiredArgsConstructor
public class PageController {

    private final QuestionServiceImpl questionService;
    private final AnswerServiceImpl answerService;
    private final AuthServiceImpl authService;

    // === VIEW: HOME PAGE ===
    @GetMapping("/")
    public String home(Model model, @RequestParam(defaultValue = "0") int page) {
        // Fetch data from your existing service
        var questionPage = questionService.feeds(page, 20);
        model.addAttribute("questions", questionPage.getContent());
        return "index"; // Looks for index.html
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Looks for login.html
    }


    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userRequest", new RegisterUserRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute RegisterUserRequest request) {
        authService.register(request);
        return "redirect:/login?success";
    }

    @GetMapping("/question/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        var question = questionService.viewQuestion(id);
        model.addAttribute("question", question);

        var answers = answerService.viewAllAnswerForQuestion(0, 50, id);
        model.addAttribute("answers", answers.getContent());

        AnswerRequest newAnswer = new AnswerRequest();
        newAnswer.setQuestionId(id);
        model.addAttribute("answerRequest", newAnswer);

        return "question_detail";
    }

    @PostMapping("/question/{id}/answer")
    public String postAnswer(@PathVariable Long id, @ModelAttribute AnswerRequest answerRequest) {
        answerRequest.setQuestionId(id); // Ensure ID is set
        answerService.answerQuestion(answerRequest);
        return "redirect:/question/" + id;
    }

    @GetMapping("/ask")
    public String askQuestionForm(Model model) {
        model.addAttribute("questionRequest", new QuestionRequest());
        return "ask_question";
    }

    @PostMapping("/ask")
    public String createQuestion(@ModelAttribute QuestionRequest request,
                                 @RequestParam("image") MultipartFile image) {
        try {
            // Your service requires the file to be set inside the request object or handled separately
            // Depending on how your DTO maps, we might need to manually set it:
            request.setImageFile(image);
            questionService.createQuestion(request);
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/ask?error";
        }
        return "redirect:/";
    }
}