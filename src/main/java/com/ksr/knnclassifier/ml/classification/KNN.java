package com.ksr.knnclassifier.ml.classification;

import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.model.Entity;
import javafx.util.Pair;

import java.util.*;

public class KNN implements Classifier {

    private final Metric metric;
    private final int k;

    public KNN(final Metric metric, final int k) {
        this.metric = metric;
        this.k = k;
    }

    @Override
    public String classify(List<Entity> trainEntities, Entity testEntity) {

        int n = trainEntities.size();
        List<Pair<Integer, Double>> distances = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Entity trainEntity = trainEntities.get(i);
            distances.add(new Pair(i, metric.distance(trainEntity, testEntity)));
        }

        Map<String, Integer> labelsCount = new HashMap<>();
        for (int i = 0; i < k; i++) {
            Pair<Integer, Double> distance = Collections.min(distances, Comparator.comparing(Pair::getValue));
            String label = trainEntities.get(distance.getKey()).getLabel();
            if (!labelsCount.containsKey(label)) {
                labelsCount.put(label, 1);
            } else {
                int count = labelsCount.get(label);
                labelsCount.put(label, ++count);
            }
            distances.remove(distance);
        }

        int maxCount = 0;
        String maxLabel = null;
        for (String label : labelsCount.keySet()) {
            int count = labelsCount.get(label);
            if (count > maxCount) {
                maxCount = count;
                maxLabel = label;
            }
        }

        return maxLabel;
    }
}
