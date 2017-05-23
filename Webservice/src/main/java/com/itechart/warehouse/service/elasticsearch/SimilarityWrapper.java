package com.itechart.warehouse.service.elasticsearch;

/**
 * Created by Lenovo on 19.05.2017.
 */
public class SimilarityWrapper <T> {
    private T ojbect;
    private Float similarity;

    public SimilarityWrapper() {
    }

    public SimilarityWrapper(T ojbect, Float similarity) {
        this.ojbect = ojbect;
        this.similarity = similarity;
    }

    public T getOjbect() {
        return ojbect;
    }

    public void setOjbect(T ojbect) {
        this.ojbect = ojbect;
    }

    public Float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Float similarity) {
        this.similarity = similarity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimilarityWrapper<?> that = (SimilarityWrapper<?>) o;

        if (ojbect != null ? !ojbect.equals(that.ojbect) : that.ojbect != null) return false;
        return similarity != null ? similarity.equals(that.similarity) : that.similarity == null;
    }

    @Override
    public int hashCode() {
        int result = ojbect != null ? ojbect.hashCode() : 0;
        result = 31 * result + (similarity != null ? similarity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimilarityWrapper{" +
                "ojbect=" + ojbect +
                ", similarity=" + similarity +
                '}';
    }
}
