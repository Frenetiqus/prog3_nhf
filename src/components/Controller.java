package components;


import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


enum PlanetSpec{
    Static,
    Orbit
}

public class Controller{
    private Integer MAPSIZE_X_MAX, MAPSIZE_Y_MAX;
    static private Double timeFrame;
    private List<Planet> planets;
    private PlanetSpec currPlanetSpec;
    

    static{
        timeFrame = 1.0/50000.0;
    }

    public Controller(Integer mapsizeXMax, Integer mapsizeYMax){
        this.MAPSIZE_X_MAX = mapsizeXMax;
        this.MAPSIZE_Y_MAX = mapsizeYMax;
        currPlanetSpec = PlanetSpec.Orbit;
        planets = Collections.synchronizedList(new ArrayList<>());
    }

    public void setMaxMapsize(Integer newXMax, Integer newYMax){
        this.MAPSIZE_X_MAX = newXMax;
        this.MAPSIZE_Y_MAX = newYMax;
    }

    public void saveSimulationToFile(String file_path) throws IOException{
        try{
            ObjectOutputStream writeStream = new ObjectOutputStream(new FileOutputStream(file_path));  
            writeStream.writeObject(planets);
            writeStream.flush();
            writeStream.close();     
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void loadSimulationFile(String file_path) throws IOException{
        try{
            ObjectInputStream readStream = new ObjectInputStream(new FileInputStream(file_path));
            synchronized(planets){
                planets = (List<Planet>) readStream.readObject();
            }
            readStream.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void placePlanet(Double radius, Double mass, Vector position, Color color, String spec) {
        PlanetSpec pSpec = PlanetSpec.Orbit;
        switch(spec){
            case "Orbit":
                pSpec = PlanetSpec.Orbit;
                break;
            case "Static":
                pSpec = PlanetSpec.Static;
                break;
        }
        placePlanet(radius, mass, position, color, pSpec);      
    }

    public void placePlanet(Double radius, Double mass, Vector position, Color color, PlanetSpec spec) {
        PerformanceCounter c = new PerformanceCounter("placePlanet()(PlanetSpec)");
        c.countStart();
        Planet newPlanet = new Planet(radius, mass, position, new Vector(), color, spec);
        if(newPlanet.isInsideBoundary(0, MAPSIZE_X_MAX, 0, MAPSIZE_Y_MAX) && getPlanetAt(position)==null){
            synchronized(planets){
                planets.add(newPlanet);
            }
        }
        c.countStop();         
    }

    public void removePlanet(Integer x, Integer y) {
        Vector clickedPosition = new Vector(x, y);
        Planet planetToRemove = getPlanetAt(clickedPosition);
        if(planetToRemove != null){
            synchronized(planets){
                planets.remove(planetToRemove);
            }
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
        PerformanceCounter c = new PerformanceCounter("calculateNewPlanetPositions()");
        c.countStart();
        synchronized(planets){
            planets.parallelStream().forEach(currPlanet -> {
                if(currPlanet.getSpec().equals(PlanetSpec.Static)) {
                    currPlanet.getVelocity().setC1(0.0);
                    currPlanet.getVelocity().setC2(0.0);
                    return;
                }

                Vector resultantForce = new Vector();
                for (Planet otherPlanet : planets) {
                    if(currPlanet.equals(otherPlanet)) continue;
                    resultantForce = resultantForce.add(currPlanet.calculateForceTo(otherPlanet));
                }
                Planet previousState = new Planet(currPlanet);
                Vector acceleration = resultantForce.multiply(1/currPlanet.getMass());
                // Vector oldVelocity = new Vector(currPlanet.getVelocity());
                // Vector oldPosition = new Vector(currPlanet.getPosition());
                Vector newVelocity = currPlanet.getVelocity().add(acceleration.multiply(timeFrame));
                Vector newPosition = currPlanet.getPosition().add(newVelocity.multiply(timeFrame));
                currPlanet.setVelocity(newVelocity);
                currPlanet.setPosition(newPosition);

                handlePlanetAnomalies(currPlanet, previousState);
                //System.out.println(currPlanet + "   v=" + currPlanet.getVelocity() + "; x" + currPlanet.getPosition());
            });
        }
        c.countStop();
    }

    /**
    * Returns an Image object that can then be painted on the screen. 
    * The url argument must specify an absolute <a href="#{@link}">{@link URL}</a>. The name
    * argument is a specifier that is relative to the url argument. 
    * <p>
    * This method always returns immediately, whether or not the 
    * image exists. When this applet attempts to draw the image on
    * the screen, the data will be loaded. The graphics primitives 
    * that draw the image will incrementally paint on the screen. 
    *
    * @param  url  an absolute URL giving the base location of the image
    * @param  name the location of the image, relative to the url argument
    * @return      the image at the specified URL
    * @see         Image
    */
    private void handlePlanetAnomalies(Planet planet, Planet previousState){
        // check if planet collides with wall, if yes keep old position and bounce
        PerformanceCounter c = new PerformanceCounter("handlePlanetAnomalies()");
        c.countStart();
        handleWallCollision(planet, previousState);
        //handlePlanetCollision(planet);

        c.countStop();
    }


    private void handleWallCollision(Planet planet, Planet previousState){
        PerformanceCounter c = new PerformanceCounter("handleWallCollision()");
        c.countStart();
        Vector leftPoint = planet.getCirclePointAt(Math.PI);
        Vector rightPoint = planet.getCirclePointAt(0.0);
        Vector topPoint = planet.getCirclePointAt(Math.PI/2.0);
        Vector bottomPoint = planet.getCirclePointAt(-Math.PI/2.0);
        WallCollision coll = null;
        if(leftPoint.getC1() <= Double.valueOf(0)){
            c.countStop();
            coll = WallCollision.left;
        }
        if(rightPoint.getC1() >= Double.valueOf(MAPSIZE_X_MAX)){
            c.countStop();
            coll = WallCollision.right;
        }
        if(bottomPoint.getC2() <= Double.valueOf(0)){
            c.countStop();
            coll = WallCollision.bottom;
        }
        if(topPoint.getC2() >= Double.valueOf(MAPSIZE_Y_MAX)){
            c.countStop();
            coll = WallCollision.top;
        }

        if(coll != null){
            planet.setPosition(previousState.getPosition());
            Vector oldVelocity = previousState.getVelocity();
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
        c.countStop();
    }


    public void drawPlanets(Graphics2D g2d){
        PerformanceCounter c = new PerformanceCounter("drawPlanets()");
        c.countStart();
        // for (Planet planet : planets) {
        //     planet.drawPlanet(g2d);
        // }      
        planets.stream().forEach(planet -> {
            planet.drawPlanet(g2d);
        }); 
        c.countStop();
    }

}