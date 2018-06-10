package com.ksr.knnclassifier.ml.extraction;

import com.ksr.knnclassifier.model.TextEntity;
import com.ksr.knnclassifier.model.VectorEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountExtractor extends FeatureExtractor<TextEntity> {

    @Override
    public void buildDictionary(final TextEntity entity) {
        entity.getAllWords().stream().forEach(token -> dictionary.putIfAbsent(token, dictionary.size()));
    }

    @Override
    public VectorEntity extract(final TextEntity entity) {
        List<String> tokens = entity.getAllWords();
        Map<Integer, Double> features = new HashMap<>();
        for (String token : tokens) {
            if (dictionary.containsKey(token)) {
                int index = dictionary.get(token);
                if (!features.containsKey(index)) {
                    features.put(index, 1.0);
                } else {
                    double value = features.get(index);
                    features.put(index, value + 1.0);
                }
            }
        }
        return new VectorEntity(features, entity.getLabel());
    }

}
