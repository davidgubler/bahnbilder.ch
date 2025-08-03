package entities.tmp;

public class Confidence<T> implements Comparable<Confidence> {
    private final T entity;
    private final double confidence;

    public Confidence(T entity, double confidence) {
        this.entity = entity;
        this.confidence = confidence;
    }

    public T getEntity() {
        return entity;
    }

    public double getConfidence() {
        return confidence;
    }

    @Override
    public int compareTo(Confidence o) {
        if (o.confidence == confidence) {
            return entity.toString().compareTo(o.entity.toString());
        }
        return Double.compare(o.confidence, confidence); // sort from highest to lowest
    }

    @Override
    public String toString() {
        return entity.toString() + " " + String.format("%.2f", confidence);
    }
}
