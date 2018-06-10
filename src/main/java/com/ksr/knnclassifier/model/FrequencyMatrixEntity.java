package com.ksr.knnclassifier.model;

import java.util.Map;

public class FrequencyMatrixEntity extends Entity<Map<String, Long>> {

    public FrequencyMatrixEntity(Map<String, Long> content, String label) {
        super(content, label);
    }

}
