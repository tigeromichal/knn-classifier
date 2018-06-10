package com.ksr.knnclassifier.ml.extraction;

import com.ksr.knnclassifier.model.Entity;
import com.ksr.knnclassifier.model.FrequencyMatrixEntity;
import com.ksr.knnclassifier.model.TextEntity;
import com.ksr.knnclassifier.model.VectorEntity;

import java.util.*;
import java.util.Map.Entry;

public class CustomExtractor extends FeatureExtractor<TextEntity> {

    private final int nGramSize;
    private final double topFrequentPercentage;
    private Map<String, Integer> dictionary = new LinkedHashMap<>();

    public CustomExtractor(int nGramSize, double topFrequentPercentage) {
        this.nGramSize = nGramSize;
        this.topFrequentPercentage = topFrequentPercentage;
    }

    @Override
    public void buildDictionary(List<TextEntity> entities) {
        super.buildDictionary(entities);
        dictionary = sortByComparator(dictionary);
    }

    @Override
    public void buildDictionary(TextEntity entity) {
        String text = entity.getContent().replaceAll(" ", "_");
        for (int i = 0; i < text.length() - nGramSize + 1; i++) {
            dictionary.merge(text.substring(i, i + nGramSize), 1, Integer::sum);
        }
    }

    @Override
    public Entity extract(TextEntity entity) {
        String text = entity.getContent().replaceAll(" ", "_");
        Map<String, Long> nGramCounts = new HashMap<>();
        for (int i = 0; i < text.length() - nGramSize + 1; i++) {
            String ngram = text.substring(i, i + nGramSize);
            if(dictionary.containsKey(ngram))
                nGramCounts.merge(trimUnderscores(text.substring(i, i + nGramSize)), 1L, Long::sum);
        }
        return new FrequencyMatrixEntity(nGramCounts, entity.getLabel());
    }

    private Map<String, Integer> sortByComparator(Map<String, Integer> unsortedMap) {

        List<Entry<String, Integer>> list = new LinkedList<>(unsortedMap.entrySet());

        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        Map<String, Integer> sortedMap = new LinkedHashMap<>();

        list = list.subList(0, (int)(list.size() * topFrequentPercentage));

        for (Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    private String trimUnderscores(String input) {
        return input.replaceAll("_$|^_", "");
    }
}
