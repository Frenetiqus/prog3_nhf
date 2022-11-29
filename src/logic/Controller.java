package logic;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Controller{
    private Integer MAPSIZE_X_MAX, MAPSIZE_Y_MAX;
    static private Double timeFrame;
    private List<Planet> planets;

    static{
        timeFrame = 1.0/10000.0;
    }

    public Controller(Integer newXMax, Integer newYMax){
        this.MAPSIZE_X_MAX = newXMax;
        this.MAPSIZE_Y_MAX = newYMax;
        planets = new ArrayList<>();
    }

    public void setMaxMapSize(Integer newXMax, Integer newYMax){
        this.MAPSIZE_X_MAX = newXMax;
        this.MAPSIZE_Y_MAX = newYMax;
    }

    public void placePlanet(Double radius, Double mass, Vector position, Color color) {
        Planet newPlanet = new Planet(radius, mass, position, new Vector(), color);
        if(newPlanet.isInsideBoundary(0, MAPSIZE_X_MAX, 0, MAPSIZE_Y_MAX) && getPlanetAt(position)==null){
            planets.add(newPlanet);
        }         
    }

    public void removePlanet(Integer x, Integer y) {
        Vector clickedPosition = new Vector(x.doubleValue(), y.doubleValue());
        Planet planetToRemove = getPlanetAt(clickedPosition);
        if(planetToRemove != null){
            planets.remove(planetToRemove);
        }
    }

    public void resetGame() {
        planets.clear();
        //drawPlanets();            
    }


    private Planet getPlanetAt(Vector coordinates){
        for (int i = 0; i < planets.size(); i++) {
            Planet currPlanet = planets.get(i);
            if(currPlanet.getPosition().distance(coordinates) <= currPlanet.getRadius()){
                return currPlanet;
            }   
        }
        return null;
    }

    public void calculateNewPlanetPositions(){
        for (int i = 0; i < planets.size(); i++) {
            Vector resultantForce = new Vector();
            Planet currPlanet = planets.get(i);
            for (int j = 0; j < planets.size(); j++) {
                if(j==i) continue;
                resultantForce = resultantForce.add(currPlanet.calculateForceTo(planets.get(j)));
            }
            Vector acceleration = resultantForce.multiply(1/currPlanet.getMass());
            Vector oldVelocity = new Vector(currPlanet.getVelocity());
            Vector oldPosition = new Vector(currPlanet.getPosition());
            Vector newVelocity = currPlanet.getVelocity().add(acceleration.multiply(timeFrame));
            Vector newPosition = currPlanet.getPosition().add(newVelocity.multiply(timeFrame));
            currPlanet.setVelocity(newVelocity);
            currPlanet.setPosition(newPosition);

            handlePlanetAnomalies(currPlanet, oldPosition, oldVelocity);
            System.out.println(currPlanet + "   v=" + currPlanet.getVelocity() + "; x" + currPlanet.getPosition());
        }
    }

    private void handlePlanetAnomalies(Planet planet, Vector oldPosition, Vector oldVelocity){
        // check if planet collides with wall, if yes keep old position and bounce
        WallCollision coll = planet.calculateWallCollision(0, MAPSIZE_X_MAX, 0, MAPSIZE_Y_MAX);
        if(coll != null){
            planet.setPosition(oldPosition);
            Vector newVelocity = oldVelocity;
            switch(coll){
                case left:
                    newVelocity = new Vector(-1.0*oldVelocity.getC1(), oldVelocity.getC2());
                    break;
                case right:
                    newVelocity = new Vector(-1.0*oldVelocity.getC1(), oldVelocity.getC2());
                    break;
                case top:
                    newVelocity = new Vector(oldVelocity.getC1(), -1.0*oldVelocity.getC2());    
                    break;
                case bottom:
                    newVelocity = new Vector(oldVelocity.getC1(), -1.0*oldVelocity.getC2());    
                    break;
            }
            planet.setVelocity(newVelocity.multiply(0.5));
        }
        // // check if planet collides with othet planet, if yes keep old pos and bounce
        // for (Planet p : planets) {
        //     if(planet == p) continue;
        //     if(planet.isCollidingWith(p)){
        //         planet.setPosition(oldPosition);
        //         planet.collideWith(p);
        //         //planet.setVelocity(new Vector());
        //     }
        // }
    }   


    public void drawPlanets(Graphics2D g2d){
        for (Planet planet : planets) {
            planet.drawPlanet(g2d);
        }       
    }

}