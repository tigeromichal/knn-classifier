package com.ksr.knnclassifier.ml.extraction;

import com.ksr.knnclassifier.model.Entity;
import com.ksr.knnclassifier.model.FrequencyMatrixEntity;
import com.ksr.knnclassifier.model.TextEntity;

public class FrequencyMatrixExtractor extends FeatureExtractor<TextEntity> {

    @Override
    public void buildDictionary(TextEntity entity) {
    }

    @Override
    public Entity extract(TextEntity entity) {
        return new FrequencyMatrixEntity(entity.getWordCounts(), entity.getLabel());
    }

}
