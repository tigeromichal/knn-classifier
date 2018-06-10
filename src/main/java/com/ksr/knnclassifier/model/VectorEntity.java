package com.ksr.knnclassifier.model;

import java.util.Map;

public class VectorEntity extends Entity<Map<Integer, Double>> {
    public VectorEntity(Map<Integer, Double> content, String label) {
        super(content, label);
    }
}
