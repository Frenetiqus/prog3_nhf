package components;

import java.awt.Graphics2D;
import java.awt.Color;

public class PlanetMars extends Planet{
    public PlanetMars(Double radius, Double mass, Vector position, Vector velocity, Color color){
        super(radius, mass, position, velocity, color);
    }
    @Override
    public void drawPlanet(Graphics2D g2d) {
        // TODO Auto-generated method stub
        super.drawPlanet(g2d);
    }
}
