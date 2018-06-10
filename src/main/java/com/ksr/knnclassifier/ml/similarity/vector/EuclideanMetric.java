package com.ksr.knnclassifier.ml.similarity.vector;

import com.ksr.knnclassifier.ml.similarity.Metric;
import com.ksr.knnclassifier.model.VectorEntity;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EuclideanMetric implements Metric<VectorEntity> {

    @Override
    public double distance(final VectorEntity content1, final VectorEntity content2) {
        Map<Integer, Double> point1 = content1.getContent();
        Map<Integer, Double> point2 = content2.getContent();
        double d = 0;
        Set<Integer> keySet = new HashSet<>(point1.keySet());
        keySet.addAll(point2.keySet());
        for (int index : keySet) {
            if (!point1.containsKey(index)) {
                d += Math.pow(point2.get(index), 2);
            } else if (!point2.containsKey(index)) {
                d += Math.pow(point1.get(index), 2);
            } else {
                d += Math.pow(point1.get(index) - point2.get(index), 2);
            }
        }
        return Math.sqrt(d);
    }

}
