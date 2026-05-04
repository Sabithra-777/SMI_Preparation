package com.smartinterview.repository;

import com.smartinterview.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findTop10ByCategoryOrderByIdAsc(String category);
    List<Question> findAllByOrderByIdAsc();
    Optional<Question> findByCategoryAndQuestion(String category, String question);
}
