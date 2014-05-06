package FastForce;

public abstract class AbstractForce {
    public ForceVector calculateForce(double x1, double y1, double x2, double y2) {
        return calculateForce(x1, y1, x2, y2, Math.hypot(x1 - x2, y1 - y2));
    }

    public abstract ForceVector calculateForce(double x1, double y1,
    		double x2, double y2, double distance);
}
