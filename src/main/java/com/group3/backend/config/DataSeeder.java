package com.group3.backend.config;

import com.group3.backend.entity.Problem;
import com.group3.backend.repository.ProblemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private ProblemRepository problemRepository;

    @Override
    public void run(String... args) throws Exception {
        if (problemRepository.count() == 0) {
            Problem p1 = new Problem(
                    "Two Sum",
                    "Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.",
                    "Easy",
                    Arrays.asList("Array", "Hash Map"),
                    "def two_sum(nums, target):\n    # Write your solution here\n    pass"
            );

            Problem p2 = new Problem(
                    "Valid Parentheses",
                    "Given a string s containing just the characters ()[]{} determine if the input string is valid.",
                    "Easy",
                    Arrays.asList("Stack", "String"),
                    "def is_valid(s):\n    # Write your solution here\n    pass"
            );

            Problem p3 = new Problem(
                    "Binary Search",
                    "Given a sorted array of integers and a target value, return the index if the target is found. Otherwise return -1.",
                    "Easy",
                    Arrays.asList("Array", "Binary Search"),
                    "def search(nums, target):\n    # Write your solution here\n    pass"
            );

            problemRepository.saveAll(Arrays.asList(p1, p2, p3));
            System.out.println("Data Seeding Complete: 3 problems added.");
        }
    }
}
