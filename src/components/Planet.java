package components;


import java.awt.Graphics2D;
import java.io.Serializable;
import java.awt.Color;

enum WallCollision{
    left,
    right,
    top,
    bottom
}

public class Planet implements Serializable{
    private static final Double G;
    protected Double radius;
    protected Double mass;
    protected Vector position;
    protected Vector velocity;
    protected Color color;

    static{
        G = 6.67430*Math.pow(10.0, -11.0); // (m^3)/(kg*s^2) so distance should be in meters, time in seconds, mass in kg
    }

    public Planet(){

    }

    public Planet(Double radius, Double mass, Vector position, Vector velocity, Color color){
        this.color = color;
        this.radius = radius;
        this.mass = mass;
        this.position = position;
        this.velocity = velocity;
    }

    public Double getMass() {
        return mass;
    }
    public void setPosition(Vector position) {
        this.position = position;
    }
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
    public Vector getPosition() {
        return position;
    }
    public Vector getVelocity() {
        return velocity;
    }
    public Double getRadius() {
        return radius;
    }

    // getCirclePointAt returns the Planet's circle point at fi radian
    private Vector getCirclePointAt(Double fi){
        PerformanceCounter c = new PerformanceCounter("getCirclePointAt()");
        c.countStart();
        Double baseX = this.getRadius();
        Double newX = baseX*Math.cos(fi);
        Double newY = baseX*Math.sin(fi);
        c.countStop();
        return new Vector(newX+position.getC1(), newY+position.getC2());
    }

    public WallCollision calculateWallCollision(Integer minX, Integer maxX, Integer minY, Integer maxY){
        PerformanceCounter c = new PerformanceCounter("calculateWallCollision()");
        c.countStart();
        int precision = 2;
        for (int i = 0; i < 360*precision; i++) {
            Double fi = Math.toRadians(Double.valueOf(i)/Double.valueOf(precision));
            Vector currPoint = this.getCirclePointAt(fi);
            if(currPoint.getC1().compareTo(Double.valueOf(minX)) <= 0){
                return WallCollision.left;
            }
            if(currPoint.getC1().compareTo(Double.valueOf(maxX)) >= 0){
                return WallCollision.right;
            }
            if(currPoint.getC2().compareTo(Double.valueOf(minY)) <= 0){
                return WallCollision.top;
            }
            if(currPoint.getC2().compareTo(Double.valueOf(maxY)) >= 0){
                return WallCollision.bottom;
            }
        }
        c.countStop();
        return null;
    }

    public boolean isInsideBoundary(Integer minX, Integer maxX, Integer minY, Integer maxY){
        PerformanceCounter c = new PerformanceCounter("isInsideBoundary()");
        c.countStart();
        int precision = 2;
        for (int i = 0; i < 360*precision; i++) {
            Double fi = Math.toRadians(Double.valueOf(i)/Double.valueOf(precision));
            Vector currPoint = this.getCirclePointAt(fi);
            // if(currPoint.getC1().compareTo(Double.valueOf(minX)) == -1 || currPoint.getC1().compareTo(Double.valueOf(minX)) == 0  ||
            //    currPoint.getC1().compareTo(Double.valueOf(maxX)) == 1 || currPoint.getC1().compareTo(Double.valueOf(maxX)) == 0 ||
            //    currPoint.getC2().compareTo(Double.valueOf(minY)) == -1 || currPoint.getC2().compareTo(Double.valueOf(minY)) == 0 ||
            //    currPoint.getC2().compareTo(Double.valueOf(maxY)) == 1 || currPoint.getC2().compareTo(Double.valueOf(maxY)) == 0){
            //         return false;
            // }
            Double x = currPoint.getC1();
            Double y = currPoint.getC2();
            if(!(Double.valueOf(minX) < x && x < Double.valueOf(maxX) && Double.valueOf(minY) < y && y < Double.valueOf(maxY))){
                    return false;
            }
        }
        c.countStop();
        return true;
    }

    public Vector calculateForceTo(Planet other){
        PerformanceCounter c = new PerformanceCounter("calculateForceTo()");
        c.countStart();
        if(this.getPosition().equals(other.getPosition())){
            return new Vector();
        }
        Vector distanceVec = other.position.add(this.position.multiply(-1.0));
        Double distance = distanceVec.length();
        Vector normVector = distanceVec.multiply(1/distanceVec.length());
        c.countStop();
        return normVector.multiply((G*this.getMass()*other.getMass())/(distance*distance));
    }

    // public boolean isCollidingWith(Planet other){
    //     int precision = 2;
    //     for (int i = 0; i < 360*precision; i++) {
    //         Double fi = Math.toRadians(Double.valueOf(i)/Double.valueOf(precision));
    //         Vector otherCirclePoint = other.getCirclePointAt(fi);
    //         if(this.position.distance(otherCirclePoint) <= this.radius){
    //             return true;
    //         }
    //     }
    //     return false;
    // }

    // public void collideWith(Planet other){
    //     Vector distanceVec = this.position.add(other.position.multiply(-1.0));
    //     Vector normVec = distanceVec.multiply(1/distanceVec.length());
    //     Vector newVelocity = this.getVelocity().add(normVec.multiply(normVec.multiply(other.getVelocity().add(this.getVelocity().multiply(-1.0)))).multiply(-2*this.getMass()/(this.getMass() + other.getMass()))) ;
    //     this.setVelocity(newVelocity.multiply(0.25));
    // }

    // this how different planets can draw different colors
    public void drawPlanet(Graphics2D g2d){
        PerformanceCounter c = new PerformanceCounter("drawPlanet()");
        c.countStart();
        g2d.setPaint(this.color);

        int x = (int)Math.round(position.getC1()-radius);
        int y = (int)Math.round(position.getC2()-radius);
        int size = (int)Math.round(2*radius);
        g2d.fillOval(x, y, size, size);
        c.countStop();
    }
}