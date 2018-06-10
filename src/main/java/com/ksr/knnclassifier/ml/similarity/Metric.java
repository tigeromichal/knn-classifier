package com.ksr.knnclassifier.ml.similarity;

public interface Metric<T> {

    double distance(final T content1, final T content2);

}
