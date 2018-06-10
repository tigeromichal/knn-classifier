package com.ksr.knnclassifier.ml.classification;

import com.ksr.knnclassifier.model.Entity;

import java.util.List;

public interface Classifier {

    String classify(List<Entity> trainEntities, Entity testEntity);

}
