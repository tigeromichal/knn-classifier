package com.ksr.knnclassifier.ml.similarity.frequencymatrix;

import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.model.FrequencyMatrixEntity;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity implements Metric<FrequencyMatrixEntity> {

    @Override
    public double distance(FrequencyMatrixEntity content1, FrequencyMatrixEntity content2) {
        Map<String, Long> map1 = content1.getContent();
        Map<String, Long> map2 = content2.getContent();
        long productsSum = 0L;
        long squaresSum1 = 0L;
        long squaresSum2 = 0L;
        Long value1, value2;
        Set<String> keys = new LinkedHashSet<>();
        keys.addAll(map1.keySet());
        keys.addAll(map2.keySet());
        for (String key : keys) {
            value1 = map1.getOrDefault(key, 0L);
            value2 = map2.getOrDefault(key, 0L);
            productsSum += value1 * value2;
            squaresSum1 += value1 * value1;
            squaresSum2 += value2 * value2;
        }
        double cosineValue = productsSum * 1.0 / (Math.sqrt(squaresSum1 * squaresSum2) * 1.0);
        double distance = 1 - cosineValue;
        return distance;
    }

}
