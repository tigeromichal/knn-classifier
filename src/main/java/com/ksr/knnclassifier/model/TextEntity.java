package com.ksr.knnclassifier.model;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TextEntity extends Entity<String> {
    public TextEntity(String content, String label) {
        super(content, label);
    }

    public List<String> getAllWords() {
        return Arrays.asList(getContent().split("\\W+"));
    }

    public Set<String> getUniqueWords() {
        return new HashSet<>(getAllWords());
    }

    public Map<String, Long> getWordCounts() {
        return getAllWords().stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

}
