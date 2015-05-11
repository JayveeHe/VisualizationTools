package FastForce;

public class ForceVector {
    private double x;
    private double y;

    public ForceVector(ForceVector vector) {
        this.x = vector.x;
        this.y = vector.y;
    }

    public ForceVector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public ForceVector() {
        this.x = 0;
        this.y = 0;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public double z() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void add(ForceVector f) {
        if (f != null) {
            x += f.x;
            y += f.y;
        }
    }

    public void multiply(double s) {
        x *= s;
        y *= s;
    }

    public void subtract(ForceVector f) {
        if (f != null) {
            x -= f.x;
            y -= f.y;
        }
    }

    public double getEnergy() {
        return x * x + y * y;
    }

    public double getNorm() {
        return (double) Math.sqrt(getEnergy());
    }

    public ForceVector normalize() {
        double norm = getNorm();
        return new ForceVector(x / norm, y / norm);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }	
}
