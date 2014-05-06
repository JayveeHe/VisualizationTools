package FastForce;

public class BarnesHut {
	/* theta is the parameter for Barnes-Hut opening criteria */
    private double theta = 1.2;
    private AbstractForce force;

    public BarnesHut(AbstractForce force) {
        this.force = force;
    }

    /* Calculates the ForceVector on node against every other node represented
     * in the tree with respect to force.
     */
    public ForceVector calculateForce(double x, double y, QuadTree tree) {
        if (tree.mass() <= 0) {
            return null;
        }

        double distance = (double)Math.hypot(x - tree.x(), y - tree.y());

        if (tree.isIsLeaf() || tree.mass() == 1) {
            // this is probably the case where tree has only the node.
            if (distance < 1e-8) {
                return null;
            }
            return force.calculateForce(x, y, tree.x(), tree.y());
        }

        if (distance * theta > tree.size()) {
            ForceVector f = force.calculateForce(x, y, tree.x(), tree.y(), distance);
            f.multiply(tree.mass());
            return f;
        }

        ForceVector f = new ForceVector();
        for (QuadTree child : tree.getChildren()) {
            f.add(calculateForce(x, y, child));
        }
        return f;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public double getTheta() {
        return theta;
    }
}
