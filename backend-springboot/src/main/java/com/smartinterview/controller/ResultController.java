package com.smartinterview.controller;

import com.smartinterview.model.Question;
import com.smartinterview.model.ResultRecord;
import com.smartinterview.model.User;
import com.smartinterview.repository.QuestionRepository;
import com.smartinterview.repository.ResultRepository;
import com.smartinterview.repository.UserRepository;
import com.smartinterview.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultRepository resultRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    public ResultController(ResultRepository resultRepository,
                            QuestionRepository questionRepository,
                            UserRepository userRepository,
                            JwtTokenProvider tokenProvider) {
        this.resultRepository = resultRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submit(@RequestBody SubmitRequest request, Authentication authentication) {
        if (request.getCategory() == null || request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category and non-empty answers required");
        }

        Long userId = extractUserId(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<Long> questionIds = request.getAnswers().stream()
                .map(AnswerDto::getQuestionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (questionIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No valid question IDs");
        }

        List<Question> questions = questionRepository.findAllById(questionIds);
        Map<Long, Integer> correctMap = questions.stream()
                .collect(Collectors.toMap(
                        Question::getId,
                        q -> Math.max(q.getOptions().indexOf(q.getCorrectAnswer()), 0)
                ));

        int score = 0;
        for (AnswerDto answer : request.getAnswers()) {
            if (answer.getSelectedAnswer() == null) continue;
            try {
                int selected = Integer.parseInt(answer.getSelectedAnswer().toString());
                Integer correct = correctMap.get(answer.getQuestionId());
                if (correct != null && selected == correct) {
                    score++;
                }
            } catch (NumberFormatException ignored) {
            }
        }

        BigDecimal percentage = BigDecimal.valueOf(score * 100.0 / request.getAnswers().size())
                .setScale(2, java.math.RoundingMode.HALF_UP);
        ResultRecord resultRecord = new ResultRecord();
        resultRecord.setUser(user);
        resultRecord.setCategory(request.getCategory());
        resultRecord.setScore(score);
        resultRecord.setTotalQuestions(request.getAnswers().size());
        resultRecord.setPercentage(percentage);
        ResultRecord saved = resultRepository.save(resultRecord);

        return ResponseEntity.ok(Map.of("result", toDto(saved)));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getResults(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<Map<String, Object>> results = resultRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(Map.of("results", results));
    }

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> analytics(Authentication authentication) {
        Long userId = extractUserId(authentication);
        List<ResultRecord> records = resultRepository.findByUserIdOrderByCreatedAtAsc(userId);

        Map<String, TopicPerformance> performance = new HashMap<>();
        List<Map<String, Object>> scoreHistory = new ArrayList<>();

        for (ResultRecord record : records) {
            performance.computeIfAbsent(record.getCategory(), key -> new TopicPerformance())
                    .update(record.getPercentage().doubleValue());
            scoreHistory.add(Map.of(
                    "category", record.getCategory(),
                    "score", record.getPercentage(),
                    "date", record.getCreatedAt()
            ));
        }

        List<Map<String, Object>> weaknesses = performance.entrySet().stream()
                .filter(e -> e.getValue().averageScore < 60)
                .map(e -> Map.<String, Object>of(
                        "topic", e.getKey(),
                        "averageScore", String.format("%.2f", e.getValue().averageScore),
                        "testsAttempted", e.getValue().totalTests
                ))
                .collect(Collectors.toList());

        double overallAverage = performance.isEmpty() ? 0 : performance.values().stream()
                .mapToDouble(tp -> tp.averageScore)
                .average()
                .orElse(0);

        Map<String, Object> analytics = Map.of(
                "totalTests", records.size(),
                "overallAverage", String.format("%.2f", overallAverage),
                "topicPerformance", performance.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().toMap())),
                "scoreHistory", reverseList(scoreHistory),
                "weaknesses", weaknesses
        );

        return ResponseEntity.ok(Map.of("analytics", analytics));
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getCredentials() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing auth token");
        }
        return tokenProvider.getUserId(authentication.getCredentials().toString());
    }

    private static List<Map<String, Object>> reverseList(List<Map<String, Object>> list) {
        List<Map<String, Object>> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    private Map<String, Object> toSummary(ResultRecord record) {
        return Map.of(
                "_id", record.getId().toString(),
                "category", record.getCategory(),
                "score", record.getScore(),
                "totalQuestions", record.getTotalQuestions(),
                "percentage", record.getPercentage(),
                "createdAt", record.getCreatedAt()
        );
    }

    private Map<String, Object> toDto(ResultRecord record) {
        return Map.of(
                "id", record.getId(),
                "userId", record.getUser().getId(),
                "category", record.getCategory(),
                "score", record.getScore(),
                "totalQuestions", record.getTotalQuestions(),
                "percentage", record.getPercentage(),
                "createdAt", record.getCreatedAt()
        );
    }

    @SuppressWarnings("unused")
    private static class SubmitRequest {
        private String category;
        private List<AnswerDto> answers;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public List<AnswerDto> getAnswers() {
            return answers;
        }

        public void setAnswers(List<AnswerDto> answers) {
            this.answers = answers;
        }
    }

    @SuppressWarnings("unused")
    private static class AnswerDto {
        private Long questionId;
        private Object selectedAnswer;

        public Long getQuestionId() {
            return questionId;
        }

        public void setQuestionId(Long questionId) {
            this.questionId = questionId;
        }

        public Object getSelectedAnswer() {
            return selectedAnswer;
        }

        public void setSelectedAnswer(Object selectedAnswer) {
            this.selectedAnswer = selectedAnswer;
        }
    }

    private static class TopicPerformance {
        private int totalTests = 0;
        private double totalScore = 0;
        private double bestScore = 0;
        private double averageScore = 0;

        public void update(double score) {
            totalTests++;
            totalScore += score;
            bestScore = Math.max(bestScore, score);
            averageScore = totalScore / totalTests;
        }

        public Map<String, Object> toMap() {
            return Map.of(
                    "totalTests", totalTests,
                    "totalScore", String.format("%.2f", totalScore),
                    "bestScore", String.format("%.2f", bestScore),
                    "averageScore", String.format("%.2f", averageScore)
            );
        }
    }
}
