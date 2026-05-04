package com.smartinterview.service;

import com.smartinterview.model.Question;
import com.smartinterview.model.User;
import com.smartinterview.repository.QuestionRepository;
import com.smartinterview.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SeedDataService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public SeedDataService(UserRepository userRepository, QuestionRepository questionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void ensureAdminUser() {
        if (!userRepository.existsByEmail("admin@example.com")) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@example.com");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole("admin");
            userRepository.save(admin);
        }
    }

    @Transactional
    public int seedQuestions() {
        Map<String, List<SeedQuestion>> seedData = buildSeedData();
        int insertedCount = 0;
        for (Map.Entry<String, List<SeedQuestion>> entry : seedData.entrySet()) {
            String category = entry.getKey();
            for (SeedQuestion seedQuestion : entry.getValue()) {
                if (questionRepository.findByCategoryAndQuestion(category, seedQuestion.question()).isPresent()) {
                    continue;
                }
                Question question = new Question();
                question.setCategory(category);
                question.setQuestion(seedQuestion.question());
                question.setOptions(seedQuestion.options());
                question.setCorrectAnswer(seedQuestion.correctAnswer());
                question.setDifficulty("Medium");
                questionRepository.save(question);
                insertedCount++;
            }
        }
        return insertedCount;
    }

    private Map<String, List<SeedQuestion>> buildSeedData() {
        Map<String, List<SeedQuestion>> data = new HashMap<>();

        data.put("DSA", List.of(
                new SeedQuestion("What is the time complexity of binary search?", List.of("O(n)", "O(log n)", "O(n²)", "O(1)"), "O(log n)"),
                new SeedQuestion("Which data structure uses LIFO?", List.of("Queue", "Stack", "Array", "Tree"), "Stack"),
                new SeedQuestion("What is the worst case time complexity of Quick Sort?", List.of("O(n log n)", "O(n²)", "O(n)", "O(log n)"), "O(n²)"),
                new SeedQuestion("Which traversal uses a queue?", List.of("Inorder", "Preorder", "Level Order", "Postorder"), "Level Order"),
                new SeedQuestion("What is the space complexity of merge sort?", List.of("O(1)", "O(log n)", "O(n)", "O(n²)"), "O(n)"),
                new SeedQuestion("Which data structure is used for BFS?", List.of("Stack", "Queue", "Tree", "Graph"), "Queue"),
                new SeedQuestion("What is a complete binary tree?", List.of("All levels filled", "All levels filled except last", "Only root exists", "Has one child"), "All levels filled except last"),
                new SeedQuestion("Hash table collision is resolved by?", List.of("Chaining", "Sorting", "Searching", "Indexing"), "Chaining"),
                new SeedQuestion("DFS uses which data structure?", List.of("Queue", "Stack", "Array", "List"), "Stack"),
                new SeedQuestion("Best case of bubble sort?", List.of("O(n²)", "O(n)", "O(log n)", "O(1)"), "O(n)")));

        data.put("OS", List.of(
                new SeedQuestion("What is a process?", List.of("Program in execution", "Program on disk", "CPU instruction", "Memory block"), "Program in execution"),
                new SeedQuestion("Which scheduling algorithm is non-preemptive?", List.of("Round Robin", "FCFS", "Priority", "Multilevel"), "FCFS"),
                new SeedQuestion("What causes deadlock?", List.of("Mutual exclusion", "Hold and wait", "No preemption", "All of these"), "All of these"),
                new SeedQuestion("What is thrashing?", List.of("High paging activity", "CPU scheduling", "Memory allocation", "Disk access"), "High paging activity"),
                new SeedQuestion("Which is fastest IPC?", List.of("Pipe", "Message Queue", "Shared Memory", "Socket"), "Shared Memory"),
                new SeedQuestion("What is a semaphore?", List.of("Synchronization tool", "Memory unit", "CPU register", "Disk block"), "Synchronization tool"),
                new SeedQuestion("Page replacement algorithm?", List.of("FIFO", "LRU", "Optimal", "All of these"), "All of these"),
                new SeedQuestion("What is context switching?", List.of("Switching processes", "Switching threads", "Switching memory", "Switching CPU"), "Switching processes"),
                new SeedQuestion("Virtual memory uses?", List.of("RAM", "Hard Disk", "Cache", "Register"), "Hard Disk"),
                new SeedQuestion("Critical section problem solved by?", List.of("Mutex", "Semaphore", "Monitor", "All of these"), "All of these")));

        data.put("DBMS", List.of(
                new SeedQuestion("What is normalization?", List.of("Remove redundancy", "Add redundancy", "Delete data", "Update data"), "Remove redundancy"),
                new SeedQuestion("ACID stands for?", List.of("Atomicity Consistency Isolation Durability", "All Correct In Database", "Atomic Clear Isolated Data", "None"), "Atomicity Consistency Isolation Durability"),
                new SeedQuestion("Primary key can be NULL?", List.of("Yes", "No", "Sometimes", "Depends"), "No"),
                new SeedQuestion("Which is DDL command?", List.of("SELECT", "INSERT", "CREATE", "UPDATE"), "CREATE"),
                new SeedQuestion("Foreign key references?", List.of("Primary key", "Unique key", "Any key", "No key"), "Primary key"),
                new SeedQuestion("What is a view?", List.of("Virtual table", "Physical table", "Index", "Key"), "Virtual table"),
                new SeedQuestion("JOIN combines?", List.of("Rows", "Columns", "Tables", "Databases"), "Tables"),
                new SeedQuestion("What is indexing?", List.of("Speed up queries", "Slow down queries", "Delete data", "Insert data"), "Speed up queries"),
                new SeedQuestion("Transaction property?", List.of("ACID", "BASE", "CAP", "SOLID"), "ACID"),
                new SeedQuestion("What is 3NF?", List.of("Third Normal Form", "Three Node Form", "Triple Null Form", "None"), "Third Normal Form")));

        data.put("CN", List.of(
                new SeedQuestion("OSI model has how many layers?", List.of("5", "6", "7", "8"), "7"),
                new SeedQuestion("TCP is?", List.of("Connection-oriented", "Connectionless", "Both", "None"), "Connection-oriented"),
                new SeedQuestion("IP address size in IPv4?", List.of("16 bits", "32 bits", "64 bits", "128 bits"), "32 bits"),
                new SeedQuestion("Which layer has routing?", List.of("Physical", "Data Link", "Network", "Transport"), "Network"),
                new SeedQuestion("HTTP uses which port?", List.of("21", "22", "80", "443"), "80"),
                new SeedQuestion("DNS converts?", List.of("Name to IP", "IP to Name", "Both", "None"), "Name to IP"),
                new SeedQuestion("Which is connection-less?", List.of("TCP", "UDP", "FTP", "HTTP"), "UDP"),
                new SeedQuestion("MAC address size?", List.of("32 bits", "48 bits", "64 bits", "128 bits"), "48 bits"),
                new SeedQuestion("Which protocol is reliable?", List.of("UDP", "TCP", "IP", "ICMP"), "TCP"),
                new SeedQuestion("Subnet mask for Class C?", List.of("255.0.0.0", "255.255.0.0", "255.255.255.0", "255.255.255.255"), "255.255.255.0")));

        data.put("Aptitude", List.of(
                new SeedQuestion("If 20% of x is 40, what is x?", List.of("100", "150", "200", "250"), "200"),
                new SeedQuestion("A train travels 60 km in 1 hour. Speed?", List.of("50 km/h", "60 km/h", "70 km/h", "80 km/h"), "60 km/h"),
                new SeedQuestion("Simple interest on 1000 at 5% for 2 years?", List.of("50", "100", "150", "200"), "100"),
                new SeedQuestion("Average of 10, 20, 30?", List.of("15", "20", "25", "30"), "20"),
                new SeedQuestion("If A:B = 2:3 and B:C = 4:5, then A:C?", List.of("8:15", "2:5", "3:5", "4:5"), "8:15"),
                new SeedQuestion("25% of 80 is?", List.of("15", "20", "25", "30"), "20"),
                new SeedQuestion("LCM of 12 and 18?", List.of("36", "48", "54", "72"), "36"),
                new SeedQuestion("HCF of 24 and 36?", List.of("6", "8", "12", "18"), "12"),
                new SeedQuestion("If x + 5 = 12, x = ?", List.of("5", "6", "7", "8"), "7"),
                new SeedQuestion("Square root of 144?", List.of("10", "11", "12", "13"), "12")));

        return data;
    }

    private static class SeedQuestion {
        private final String question;
        private final List<String> options;
        private final String correctAnswer;

        public SeedQuestion(String question, List<String> options, String correctAnswer) {
            this.question = question;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public String question() {
            return question;
        }

        public List<String> options() {
            return options;
        }

        public String correctAnswer() {
            return correctAnswer;
        }
    }
}
