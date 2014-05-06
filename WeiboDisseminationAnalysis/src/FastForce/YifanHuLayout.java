package FastForce;

import java.io.IOException;

public class YifanHuLayout {
	private double optimalDistance;
    private double relativeStrength;
    private double step;
    private double initialStep;
    private int progress;
    private double stepRatio;
    private int quadTreeMaxLevel;
    private double barnesHutTheta;
    private double convergenceThreshold;
    private boolean adaptiveCooling;
    private boolean converged;
    private double energy0;
    private double energy;
    private double[] x; /* x coordinate */
    private double[] y; /* y coordinate */
    private int[] adjacency;
    private ForceVector[] fv;

    public YifanHuLayout(int nodeNum) {      
    	this.x = new double[nodeNum];
    	this.y = new double[nodeNum];
    	this.fv = new ForceVector[nodeNum];
    }

    protected void postAlgo() {
        updateStep();
        if (Math.abs((energy - energy0) / energy) < getConvergenceThreshold()) {
            setConverged(true);
        }
    }

    private SpringForce getEdgeForce() {
        return new SpringForce(getOptimalDistance());
    }

    private ElectricalForce getNodeForce() {
        return new ElectricalForce(getRelativeStrength(), getOptimalDistance());
    }

    private void updateStep() {
        if (isAdaptiveCooling()) {
            if (energy < energy0) {
                progress++;
                if (progress >= 5) {
                    progress = 0;
                    setStep(step / getStepRatio());
                }
            } else {
                progress = 0;
                setStep(step * getStepRatio());
            }
        } else {
            setStep(step * getStepRatio());
        }
    }

    public void resetPropertiesValues() {
        setStepRatio(0.95);
        setRelativeStrength(0.2);   
        setOptimalDistance(100.0);
        setInitialStep(optimalDistance / 5);
        setStep(initialStep);
        setQuadTreeMaxLevel(10);
        setBarnesHutTheta(1.2);
        setAdaptiveCooling(true);
        setConvergenceThreshold(1e-4);
        //setConvergenceThreshold(1e-5);
    }

    public boolean canAlgo() {
        return !isConverged() && x.length != 0 && y.length != 0;
    }
    
	
    
    public void initAlgo(int[] adjacency) throws IOException {
        if (x.length == 0 || y.length == 0) {
            return;
        }
        this.adjacency = adjacency;
        energy = Double.POSITIVE_INFINITY;
        for (int i = 0; i < x.length; i++) {
        	x[i] = ((0.01 + Math.random()) * 1000) - 500;
        	y[i] = ((0.01 + Math.random()) * 1000) - 500;
            fv[i] = new ForceVector();
		}
        progress = 0;
        setConverged(false);
        setStep(initialStep);
    }
    
    public void initAlgo(int[] adjacency, double[] xcoor, double[] ycoor) throws IOException {
        if (x.length == 0 || y.length == 0) {
            return;
        }
        this.adjacency = adjacency;
        energy = Double.POSITIVE_INFINITY;
        for (int i = 0; i < x.length; i++) {
        	x[i] = xcoor[i];
        	y[i] = ycoor[i];
            fv[i] = new ForceVector();
		}
        progress = 0;
        setConverged(false);
        setStep(initialStep);
    }

    public void endAlgo() {
    		
    }

    public void goAlgo() {       
        // Evaluates n^2 inter node forces using BarnesHut.
        QuadTree tree = QuadTree.buildTree(x, y, getQuadTreeMaxLevel());
        
        BarnesHut barnes = new BarnesHut(getNodeForce());
        barnes.setTheta(getBarnesHutTheta());
        for (int i = 0; i < x.length; i++) {       
            ForceVector f = barnes.calculateForce(x[i], y[i], tree);
            fv[i].add(f);
        }
        
        // Apply edge forces.
        for (int i = 0; i < adjacency.length / 2; i++) {
        	ForceVector f = getEdgeForce().calculateForce(x[adjacency[2 * i]], y[adjacency[2 * i]], 
            		x[adjacency[2 * i + 1]], y[adjacency[2 * i + 1]]);
            fv[adjacency[2 * i]].add(f);
            fv[adjacency[2 * i + 1]].subtract(f);
        }
        
        // Calculate energy and max force.
        energy0 = energy;
        energy = 0;
        double maxForce = 1;
        for (int i = 0; i < x.length; i++) {
        	ForceVector force = fv[i];
            energy += force.getNorm();
            maxForce = Math.max(maxForce, force.getNorm());
        }

        // Apply displacements on nodes.
        for (int i = 0; i < x.length; i++) {          
        	ForceVector force = fv[i];
            force.multiply((double) (1.0 / maxForce));
            force = force.normalize();
            force.multiply(step);
            x[i] = x[i] + force.x();
            y[i] = y[i] + force.y();   
        }
        postAlgo();
        //System.out.println("energy0 = " + energy0 + "   energy = " + energy);
    }
    
    public double[] getX() {
    	return x;
    }
    
    public double[] getY() {
    	return y;
    }

    /* Maximum level for Barnes-Hut's quadtree */
    public Integer getQuadTreeMaxLevel() {
        return quadTreeMaxLevel;
    }

    public void setQuadTreeMaxLevel(Integer quadTreeMaxLevel) {
        this.quadTreeMaxLevel = quadTreeMaxLevel;
    }

    /* theta is the parameter for Barnes-Hut opening criteria */
    public Double getBarnesHutTheta() {
        return barnesHutTheta;
    }

    public void setBarnesHutTheta(Double barnesHutTheta) {
        this.barnesHutTheta = barnesHutTheta;
    }

    /**
     * @return the optimalDistance
     */
    public Double getOptimalDistance() {
        return optimalDistance;
    }

    /**
     * @param optimalDistance the optimalDistance to set
     */
    public void setOptimalDistance(Double optimalDistance) {
        this.optimalDistance = optimalDistance;
    }

    /**
     * @return the relativeStrength
     */
    public Double getRelativeStrength() {
        return relativeStrength;
    }

    /**
     * @param relativeStrength the relativeStrength to set
     */
    public void setRelativeStrength(Double relativeStrength) {
        this.relativeStrength = relativeStrength;
    }

    /**
     * @param step the step to set
     */
    public void setStep(Double step) {
        this.step = step;
    }

    /**
     * @return the adaptiveCooling
     */
    public Boolean isAdaptiveCooling() {
        return adaptiveCooling;
    }

    /**
     * @param adaptiveCooling the adaptiveCooling to set
     */
    public void setAdaptiveCooling(Boolean adaptiveCooling) {
        this.adaptiveCooling = adaptiveCooling;
    }

    /**
     * @return the stepRatio
     */
    public Double getStepRatio() {
        return stepRatio;
    }

    /**
     * @param stepRatio the stepRatio to set
     */
    public void setStepRatio(Double stepRatio) {
        this.stepRatio = stepRatio;
    }

    /**
     * @return the convergenceThreshold
     */
    public Double getConvergenceThreshold() {
        return convergenceThreshold;
    }

    /**
     * @param convergenceThreshold the convergenceThreshold to set
     */
    public void setConvergenceThreshold(Double convergenceThreshold) {
        this.convergenceThreshold = convergenceThreshold;
    }

    /**
     * @return the initialStep
     */
    public Double getInitialStep() {
        return initialStep;
    }

    /**
     * @param initialStep the initialStep to set
     */
    public void setInitialStep(Double initialStep) {
        this.initialStep = initialStep;
    }
    
    public void setConverged(boolean converged) {
        this.converged = converged;
    }

    public boolean isConverged() {
        return converged;
    }

    /**
     * Fa = (n2 - n1) * ||n2 - n1|| / K
     */
    public class SpringForce extends AbstractForce {

        private double optimalDistance;

        public SpringForce(double optimalDistance) {
            this.optimalDistance = optimalDistance;
        }

        @Override
        public ForceVector calculateForce(double x1, double y1,
        		double x2, double y2, double distance) {
            ForceVector f = new ForceVector(x2 - x1, y2 - y1);
            f.multiply(distance / optimalDistance);
            return f;
        }

        public void setOptimalDistance(Double optimalDistance) {
            this.optimalDistance = optimalDistance;
        }

        public Double getOptimalDistance() {
            return optimalDistance;
        }
    }
        
    /**
     * Fr = -C*K*K*(n2-n1)/||n2-n1||
     */
    public class ElectricalForce extends AbstractForce {

        private double relativeStrength;
        private double optimalDistance;

        public ElectricalForce(double relativeStrength, double optimalDistance) {
            this.relativeStrength = relativeStrength;
            this.optimalDistance = optimalDistance;
        }

        @Override
        public ForceVector calculateForce(double x1, double y1,
        		double x2, double y2, double distance) {
            ForceVector f = new ForceVector(x2 - x1, y2 - y1);
            double scale = -relativeStrength * optimalDistance * optimalDistance / (distance * distance);
            if (Double.isNaN(scale) || Double.isInfinite(scale)) {
                scale = -1;
            }
            f.multiply(scale);
            return f;
        }
    }
}
