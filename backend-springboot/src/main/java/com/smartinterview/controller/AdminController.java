package com.smartinterview.controller;

import com.smartinterview.model.Question;
import com.smartinterview.model.ResultRecord;
import com.smartinterview.repository.QuestionRepository;
import com.smartinterview.repository.ResultRepository;
import com.smartinterview.service.SeedDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final QuestionRepository questionRepository;
    private final ResultRepository resultRepository;
    private final SeedDataService seedDataService;

    public AdminController(QuestionRepository questionRepository, ResultRepository resultRepository, SeedDataService seedDataService) {
        this.questionRepository = questionRepository;
        this.resultRepository = resultRepository;
        this.seedDataService = seedDataService;
    }

    @PostMapping("/seed-questions")
    public ResponseEntity<Map<String, Object>> seedQuestions() {
        int insertedCount = seedDataService.seedQuestions();
        return ResponseEntity.ok(Map.of("message", "Seed completed", "insertedCount", insertedCount));
    }

    @GetMapping("/questions")
    public ResponseEntity<Map<String, Object>> getAllQuestions() {
        List<Map<String, Object>> questions = questionRepository.findAllByOrderByIdAsc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("questions", questions));
    }

    @PostMapping("/questions")
    public ResponseEntity<Map<String, Object>> addQuestion(@RequestBody AdminQuestionRequest request) {
        if (request.getCategory() == null || request.getQuestion() == null || request.getOptions() == null || request.getCorrectAnswer() == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Missing fields");
        }

        if (questionRepository.findByCategoryAndQuestion(request.getCategory(), request.getQuestion()).isPresent()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Question already exists");
        }

        Question question = new Question();
        question.setCategory(request.getCategory());
        question.setQuestion(request.getQuestion());
        question.setOptions(request.getOptions());
        question.setCorrectAnswer(resolveCorrectAnswer(request.getOptions(), request.getCorrectAnswer()));
        question.setDifficulty(request.getDifficulty() == null ? "Medium" : request.getDifficulty());
        Question saved = questionRepository.save(question);
        return ResponseEntity.ok(Map.of("question", toDto(saved)));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<Map<String, Object>> updateQuestion(@PathVariable Long id, @RequestBody AdminQuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Question not found"));

        question.setCategory(request.getCategory());
        question.setQuestion(request.getQuestion());
        question.setOptions(request.getOptions());
        question.setCorrectAnswer(resolveCorrectAnswer(request.getOptions(), request.getCorrectAnswer()));
        question.setDifficulty(request.getDifficulty() == null ? "Medium" : request.getDifficulty());
        Question saved = questionRepository.save(question);
        return ResponseEntity.ok(Map.of("question", toDto(saved)));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<Map<String, Object>> deleteQuestion(@PathVariable Long id) {
        if (!questionRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Question not found");
        }
        questionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }

    @GetMapping("/results")
    public ResponseEntity<Map<String, Object>> getAllResults() {
        List<Map<String, Object>> results = resultRepository.findAll().stream()
                .map(this::toResultDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("results", results));
    }

    private Map<String, Object> toDto(Question question) {
        int correctIndex = question.getOptions().indexOf(question.getCorrectAnswer());
        if (correctIndex < 0) {
            correctIndex = 0;
        }
        return Map.of(
                "id", question.getId(),
                "category", question.getCategory(),
                "question", question.getQuestion(),
                "options", question.getOptions(),
                "difficulty", question.getDifficulty(),
                "correctAnswer", correctIndex
        );
    }

    private Map<String, Object> toResultDto(ResultRecord record) {
        return Map.of(
                "id", record.getId(),
                "category", record.getCategory(),
                "score", record.getScore(),
                "totalQuestions", record.getTotalQuestions(),
                "percentage", record.getPercentage(),
                "createdAt", record.getCreatedAt(),
                "user", Map.of(
                        "id", record.getUser().getId(),
                        "name", record.getUser().getName(),
                        "email", record.getUser().getEmail()
                )
        );
    }

    private String resolveCorrectAnswer(List<String> options, Object correctAnswer) {
        if (options == null || options.isEmpty()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Options must be provided");
        }

        if (correctAnswer instanceof Number) {
            int idx = ((Number) correctAnswer).intValue();
            if (idx >= 0 && idx < options.size()) {
                return options.get(idx);
            }
        }

        if (correctAnswer instanceof String) {
            String value = (String) correctAnswer;
            if (options.contains(value)) {
                return value;
            }
            try {
                int idx = Integer.parseInt(value);
                if (idx >= 0 && idx < options.size()) {
                    return options.get(idx);
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return options.get(0);
    }

    @SuppressWarnings("unused")
    private static class AdminQuestionRequest {
        private String category;
        private String question;
        private List<String> options;
        private Object correctAnswer;
        private String difficulty;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public Object getCorrectAnswer() {
            return correctAnswer;
        }

        public void setCorrectAnswer(Object correctAnswer) {
            this.correctAnswer = correctAnswer;
        }

        public String getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(String difficulty) {
            this.difficulty = difficulty;
        }
    }
}
