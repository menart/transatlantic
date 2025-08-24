package express.atc.backend.metrics.service;

class AtomicDouble extends Number {
    private double value;

    public AtomicDouble(double value) {
        this.value = value;
    }

    public void set(double value) {
        this.value = value;
    }

    @Override
    public int intValue() {
        return (int) value;
    }

    @Override
    public long longValue() {
        return (long) value;
    }

    @Override
    public float floatValue() {
        return (float) value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
}