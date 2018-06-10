package com.ksr.knnclassifier.ml.extraction;

import com.ksr.knnclassifier.model.TextEntity;
import com.ksr.knnclassifier.model.VectorEntity;

import java.util.*;

public class TfidfExtractor extends FeatureExtractor<TextEntity> {

    private final Map<Integer, Integer> dictionaryCount = new HashMap<>();
    private int documentsNumber = 0;

    @Override
    public void buildDictionary(final TextEntity entity) {
        List<String> tokens = entity.getAllWords();
        Set<String> tokensUsed = new HashSet<>();
        for (String token : tokens) {
            if (!dictionary.containsKey(token)) {
                int index = dictionary.size();
                dictionary.put(token, index);
                dictionaryCount.put(index, 1);
                tokensUsed.add(token);
            } else if (!tokensUsed.contains(token)) {
                int index = dictionary.get(token);
                int count = dictionaryCount.get(index);
                dictionaryCount.put(index, ++count);
                tokensUsed.add(token);
            }
        }
        documentsNumber++;
    }

    @Override
    public VectorEntity extract(TextEntity entity) {
        Map<String, Long> wordCounts = entity.getWordCounts();
        Map<Integer, Double> features = new HashMap<>();
        for (String token : wordCounts.keySet()) {
            if (dictionary.containsKey(token)) {
                double tf = wordCounts.get(token);
                int index = dictionary.get(token);
                int count = dictionaryCount.get(index);
                double idf = Math.log(documentsNumber * 1.0 / count);
                double tfidf = tf * idf;
                features.put(index, tfidf);
            }
        }
        return new VectorEntity(features, entity.getLabel());
    }

}
