package components;

import java.awt.Graphics2D;
import java.awt.Color;

public class PlanetNeptun extends Planet{
    public PlanetNeptun(Vector position, Vector velocity){
        super();
        this.color = new Color(63,84,186);
        this.radius = 69.911;
        this.mass = 1.89e27;
        this.position = position;
        this.velocity = velocity;
    }
    @Override
    public void drawPlanet(Graphics2D g2d) {
        // TODO Auto-generated method stub
        super.drawPlanet(g2d);
    }
}
