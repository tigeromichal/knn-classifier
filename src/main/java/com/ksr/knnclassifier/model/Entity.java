package com.ksr.knnclassifier.model;

public abstract class Entity<T> {

    private final T content;

    private final String label;

    public Entity(T content, String label) {
        this.content = content;
        this.label = label;
    }

    public T getContent() {
        return content;
    }

    public String getLabel() {
        return label;
    }
}
