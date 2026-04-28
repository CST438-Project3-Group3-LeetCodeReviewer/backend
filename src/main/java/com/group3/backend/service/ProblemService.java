package com.group3.backend.service;

import com.group3.backend.entity.Problem;
import com.group3.backend.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProblemService {

    @Autowired
    private ProblemRepository problemRepository;

    public List<Problem> getAllProblems() {
        return problemRepository.findAll();
    }

    public Optional<Problem> getProblemById(Long id) {
        return problemRepository.findById(id);
    }
    
    public Problem saveProblem(Problem problem) {
        return problemRepository.save(problem);
    }
}
