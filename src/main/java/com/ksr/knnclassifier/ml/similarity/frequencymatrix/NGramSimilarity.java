package com.ksr.knnclassifier.ml.similarity.frequencymatrix;

import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.model.FrequencyMatrixEntity;

import java.util.Map;

public class NGramSimilarity implements Metric<FrequencyMatrixEntity> {
    @Override
    public double distance(FrequencyMatrixEntity content1, FrequencyMatrixEntity content2) {
        Map<String, Long> ngrams1 = content1.getContent();
        Map<String, Long> ngrams2 = content2.getContent();
        int ngramSize = ngrams1.keySet().stream().findFirst().get().length();
        int possibleNElementSubstrings = ngrams1.size() - ngramSize + 1;
        double fraction = 1.0 / possibleNElementSubstrings;
        int sum = 0;
        for(Map.Entry<String, Long> ngram : ngrams1.entrySet()) {
            if(ngrams2.containsKey(ngram.getKey())) {
                sum += ngram.getValue();
            }
        }
        double distance = 1 - fraction * sum;
        return distance;
    }
}
