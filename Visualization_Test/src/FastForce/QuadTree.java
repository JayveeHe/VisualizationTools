package FastForce;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private double posX;
    private double posY;
    private double size;
    private double centerMassX;  // X and Y position of the center of mass
    private double centerMassY;
    private int mass;  // Mass of this tree (the number of nodes it contains)
    private int maxLevel;
    private AddBehaviour add;
    private List<QuadTree> children;
    private boolean isLeaf;
    public static final double eps = 1e-6;

    public static QuadTree buildTree(double[] x, double[] y, int maxLevel) {
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (double xcoor : x) {
            minX = Math.min(minX, xcoor);
            maxX = Math.max(maxX, xcoor);
        }
        for (double ycoor : y) {
            minY = Math.min(minY, ycoor);
            maxY = Math.max(maxY, ycoor);
        }
        double size = Math.max(maxY - minY, maxX - minX);
        QuadTree tree = new QuadTree(minX, minY, size, maxLevel);
        for (int i = 0; i < x.length; i++) {
            tree.addNode(x[i], y[i]);
        }
        return tree;
    }

    public QuadTree(double posX, double posY, double size, int maxLevel) {
    	this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.maxLevel = maxLevel;
        this.isLeaf = true;
        mass = 0;
        add = new FirstAdd();
    }

    public double size() {
        return size;
    }

    private void divideTree() {
        double childSize = size / 2;
        children = new ArrayList<QuadTree>();
        children.add(new QuadTree(posX + childSize, posY + childSize,
                                  childSize, maxLevel - 1));
        children.add(new QuadTree(posX, posY + childSize,
                                  childSize, maxLevel - 1));
        children.add(new QuadTree(posX, posY, childSize, maxLevel - 1));
        children.add(new QuadTree(posX + childSize, posY,
                                  childSize, maxLevel - 1));

        isLeaf = false;
    }

    private boolean addToChildren(double x, double y) {
    	for (QuadTree q : children) {
            if (q.addNode(x, y)) {
                return true;
            }
        }
        return false;
    }

    private void assimilateNode(double x, double y) {
    	centerMassX = (mass * centerMassX + x) / (mass + 1);
        centerMassY = (mass * centerMassY + y) / (mass + 1);
        mass++;
    }

    public Iterable<QuadTree> getChildren() {
        return children;
    }

    public double x() {
        return centerMassX;
    }

    public double y() {
        return centerMassY;
    }

    public int mass() {
        return mass;
    }

    public double z() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean addNode(double x, double y) {
    	if (posX <= x && x <= posX + size &&
            posY <= y && y <= posY + size) {
            return add.addNode(x, y);
        } else {
            return false;
        }
    }

    /**
     * @return the isLeaf
     */
    public boolean isIsLeaf() {
        return isLeaf;
    }

    class FirstAdd implements AddBehaviour {

        public boolean addNode(double x, double y) {
        	mass = 1;
            centerMassX = x;
            centerMassY = y;

            if (maxLevel == 0) {
                add = new LeafAdd();
            } else {
                add = new SecondAdd();
            }

            return true;
        }
    }

    class SecondAdd implements AddBehaviour {
        public boolean addNode(double x, double y) {
        	divideTree();
            add = new RootAdd();
            /* This QuadTree represents one node, add it to a child accordingly
             */
            addToChildren(QuadTree.this.centerMassX, QuadTree.this.centerMassY);
            return add.addNode(x, y);
        }
    }

    class LeafAdd implements AddBehaviour {
        public boolean addNode(double x, double y) {
        	assimilateNode(x, y);
            return true;
        }
    }

    class RootAdd implements AddBehaviour {
        public boolean addNode(double x, double y) {
        	assimilateNode(x, y);
            return addToChildren(x, y);
        }
    }
}

interface AddBehaviour {
    public boolean addNode(double x, double y);
}
