package com.smartinterview.controller;

import com.smartinterview.model.Question;
import com.smartinterview.repository.QuestionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @GetMapping("/{category}")
    public ResponseEntity<QuestionsResponse> getQuestionsByCategory(@PathVariable String category) {
        List<QuestionDto> questions = questionRepository.findTop10ByCategoryOrderByIdAsc(category)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(new QuestionsResponse(questions, 600));
    }

    private QuestionDto toDto(Question question) {
        int correctIndex = question.getOptions().indexOf(question.getCorrectAnswer());
        if (correctIndex < 0) {
            correctIndex = 0;
        }
        return new QuestionDto(
                question.getId().toString(),
                question.getCategory(),
                question.getQuestion(),
                question.getOptions(),
                question.getDifficulty() == null ? "Medium" : question.getDifficulty(),
                correctIndex
        );
    }

    private static class QuestionDto {
        private final String _id;
        private final String category;
        private final String question;
        private final List<String> options;
        private final String difficulty;
        private final int correctAnswer;

        public QuestionDto(String _id, String category, String question, List<String> options, String difficulty, int correctAnswer) {
            this._id = _id;
            this.category = category;
            this.question = question;
            this.options = options;
            this.difficulty = difficulty;
            this.correctAnswer = correctAnswer;
        }

        public String get_id() {
            return _id;
        }

        public String getCategory() {
            return category;
        }

        public String getQuestion() {
            return question;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public int getCorrectAnswer() {
            return correctAnswer;
        }
    }

    private static class QuestionsResponse {
        private final List<QuestionDto> questions;
        private final int timeLimit;

        public QuestionsResponse(List<QuestionDto> questions, int timeLimit) {
            this.questions = questions;
            this.timeLimit = timeLimit;
        }

        public List<QuestionDto> getQuestions() {
            return questions;
        }

        public int getTimeLimit() {
            return timeLimit;
        }
    }
}
