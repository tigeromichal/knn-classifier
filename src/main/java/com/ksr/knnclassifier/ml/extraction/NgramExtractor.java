package com.ksr.knnclassifier.ml.extraction;

import com.ksr.knnclassifier.model.Entity;
import com.ksr.knnclassifier.model.FrequencyMatrixEntity;
import com.ksr.knnclassifier.model.TextEntity;

import java.util.HashMap;
import java.util.Map;

public class NgramExtractor extends FeatureExtractor<TextEntity> {
    private final int nGramSize;

    public NgramExtractor(int nGramSize) {
        this.nGramSize = nGramSize;
    }

    @Override
    public void buildDictionary(TextEntity entity) {

    }

    @Override
    public Entity extract(TextEntity entity) {
        String text = entity.getContent().replaceAll(" ", "_");
        Map<String, Long> nGramCounts = new HashMap<>();
        for (int i = 0; i < text.length() - nGramSize + 1; i++) {
            nGramCounts.merge(text.substring(i, i + nGramSize), 1L, Long::sum);
        }
        return new FrequencyMatrixEntity(nGramCounts, entity.getLabel());
    }
}
