package com.ksr.knnclassifier.ml.extraction;

import com.ksr.knnclassifier.model.Entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class FeatureExtractor<T extends Entity> {

    protected final Map<String, Integer> dictionary = new HashMap<>();

    public abstract void buildDictionary(final T entity);

    public void buildDictionary(final List<T> entities) {
        for (T entity : entities) {
            buildDictionary(entity);
        }
    }

    public abstract Entity extract(final T entity);

}