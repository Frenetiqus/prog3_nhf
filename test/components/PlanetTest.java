package components;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Test;

//import org.junit.*;

public class PlanetTest{
    static double G = 6.67430*Math.pow(10.0, -11.0);

    // @Test
    // public void testIsCollidingWith(){
    //     Planet p0 = new Planet(3.0, 1000.0, new Vector(0,0), new Vector(0,0), null);
    //     Planet p1NotCollP0 = new Planet(2.0, 1000.0, new Vector(6,0), new Vector(0,0), null);
    //     Planet p2CollP0 = new Planet(3.0, 1000.0, new Vector(0,6), new Vector(0,0), null);
    //     Planet p3CollP0 = new Planet(3.0, 1000.0, new Vector(6,0), new Vector(0,0), null);
    //     Planet p4CollP0P1 = new Planet(3.14, 1000.0, new Vector(3.14,2.72), new Vector(0,0), null);

    //     assertTrue(p0.isCollidingWith(p1NotCollP0) == false);
    //     assertTrue(p1NotCollP0.isCollidingWith(p0) == false);

    //     assertTrue(p2CollP0.isCollidingWith(p0) == true);
    //     assertTrue(p0.isCollidingWith(p2CollP0) == true);

    //     assertTrue(p0.isCollidingWith(p3CollP0) == true);
    //     assertTrue(p3CollP0.isCollidingWith(p0) == true);

    //     assertTrue(p0.isCollidingWith(p4CollP0P1) == true);
    //     assertTrue(p4CollP0P1.isCollidingWith(p0) == true);
    //     assertTrue(p1NotCollP0.isCollidingWith(p4CollP0P1) == true);
    //     assertTrue(p4CollP0P1.isCollidingWith(p1NotCollP0) == true);

    //     assertTrue(p0.isCollidingWith(p0) == true);
    // }

    @Test
    public void testCalculateForceTo(){
        Double m1 = 1000.0, m2 = 500.0;
        Planet p1 = new Planet(3.0, m1, new Vector(0.0,0.0), new Vector(0.0,0.0), null, null);
        Planet p2 = new Planet(3.0, m2, new Vector(10.0,0.0), new Vector(0.0,0.0), null, null);

        Double expectedForceLen = (G*m1*m2)/10*10;
        Vector f1 = p1.calculateForceTo(p2);
        Vector f2 = p2.calculateForceTo(p1);
        // calculateForceTo is to be expected accurate to 4 decimals
        assertEquals(expectedForceLen, f1.length(), 0.0001);
        // |f1| == |f2|
        assertEquals(f1.length(), f2.length(), 0);

        Vector f1Reversed = f1.multiply(-1.0);
        // f1 == -f2
        assertEquals(f1Reversed.getC1(), f2.getC1(), 0);
        assertEquals(f1Reversed.getC2(), f2.getC2(), 0);

        // 2 bodies with same mass and distance from third -> no force upon third, in equality
        Random r = new Random();
        Double m = 5000*r.nextDouble();
        Double distance = 15.0;
        Planet pUp = new Planet(3.0, m, new Vector(0.0,distance), new Vector(0.0,0.0), null, null);
        Planet pDown = new Planet(3.0, m, new Vector(0.0,-1*distance), new Vector(0.0,0.0), null, null);
        // Resultant force on p1 in space where there are p1, pUp, pDown bodies
        f1 = p1.calculateForceTo(pUp);
        f2 = p1.calculateForceTo(pDown);
        Vector resultantForce = f1.add(f2);
        assertEquals(0, resultantForce.length(), 0.0001);

        Vector f = p1.calculateForceTo(p1);
        assertEquals(new Vector(0,0), f);
        assertEquals(0, f.length(), 0);
    }

    @Test
    public void testIsInsideBoundary(){
        Planet p = new Planet(3.0, 10.0, new Vector(0.0,0.0), new Vector(0.0,0.0), null, null);
        assertTrue(p.isInsideBoundary(-4, 4, -4, 4));
        assertTrue(p.isInsideBoundary(-3, 3, -3, 3) == false);

        p = new Planet(2.99999999, 10.0, new Vector(0.0,0.0), new Vector(0.0,0.0), null, null);
        assertTrue(p.isInsideBoundary(-4, 4, -4, 4));
        assertTrue(p.isInsideBoundary(-3, 3, -3, 3));
        assertTrue(p.isInsideBoundary(-3, 3, -1, 3) == false);
    }

    @Test
    public void testCalculateWallCollision(){
        Planet p = new Planet(3.0, 10.0, new Vector(0.0,0.0), new Vector(0.0,0.0), null, null);
        assertTrue(p.isInsideBoundary(-4, 4, -4, 4));
        assertTrue(p.isInsideBoundary(-3, 3, -3, 3) == false);

        p = new Planet(2.99999999, 10.0, new Vector(0.0,0.0), new Vector(0.0,0.0), null, null);
        assertTrue(p.isInsideBoundary(-4, 4, -4, 4));
        assertTrue(p.isInsideBoundary(-3, 3, -3, 3));
        assertTrue(p.isInsideBoundary(-3, 3, -1, 3) == false);
    }

}