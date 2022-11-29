package logic;


import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Test;

// import logic.Vector;

public class VectorTest{
    
    @Test
    public void testAdd(){
        Double v1c1=3.14, v1c2=342.1;
        Double v2c1=2.71828, v2c2=221.53;
        Vector v1 = new Vector(v1c1, v1c2);
        Vector v2 = new Vector(v2c1, v2c2);

        Vector result = v1.add(v2);
        assertEquals(v1c1+v2c1, result.getC1(), 0);
        assertEquals(v1c2+v2c2, result.getC2(), 0);
    }

    @Test
    public void testMultiply(){
        Double vc1=3.14, vc2=342.1;
        Vector v = new Vector(vc1, vc2);
        Double multiplier = 2.71828;

        Vector result = v.multiply(1.0);
        assertEquals(vc1, result.getC1(), 0);
        assertEquals(vc2, result.getC2(), 0);

        result = v.multiply(multiplier);
        assertEquals(multiplier*vc1, result.getC1(), 0);
        assertEquals(multiplier*vc2, result.getC2(), 0);

        result = v.multiply(0.0);
        assertEquals(0, result.getC1(), 0);
        assertEquals(0, result.getC2(), 0);
    }

    @Test
    public void testLength(){
        Vector v;

        v = new Vector(1, 1);
        assertEquals(Math.sqrt(2), v.length(), 0);

        v = new Vector(3, 4);
        assertEquals(5, v.length(), 0);

        v = new Vector(-3, -4);
        assertEquals(5, v.length(), 0);

        v = new Vector(0, 1);
        assertEquals(1, v.length(), 0);

        v = new Vector();
        assertEquals(0, v.length(), 0);
    }

    @Test
    public void testDistance(){
        Vector v1 = new Vector();
        Vector v2 = new Vector();
        assertEquals(0, v1.distance(v2), 0);

        v1 = new Vector(100, 0);
        v2 = new Vector();
        assertEquals(100, v1.distance(v2), 0);

        v1 = new Vector(3.14, 52);
        v2 = new Vector(13, 2.74);
        assertEquals(50.237109, v1.distance(v2), 0.000001);

        assertEquals(v2.distance(v1), v1.distance(v2), 0);

        Random r = new Random();
        v1 = new Vector(100000 * r.nextDouble(), 100000 * r.nextDouble());
        v2 = new Vector(100000 * r.nextDouble(), 100000 * r.nextDouble());
        Vector v3 = new Vector(100000 * r.nextDouble(), 100000 * r.nextDouble());
        assertTrue(v1.distance(v3) <= v1.distance(v2) + v2.distance(v3));
    }

    @Test
    public void testEquals(){
        Random r = new Random();
        Double val1 = 1000*r.nextDouble();
        Double val2 = 1000*r.nextDouble();

        Vector v1 = new Vector(val1, val2);
        Vector v2 = new Vector(val2, val1);

        assertTrue(v1.equals(v1) == true);
        assertTrue(v2.equals(v2) == true);
        assertTrue(v1.equals(v2) == false);
        assertTrue(v2.equals(v1) == false);
    }

    @Test
    public void testInit(){
        Vector v = new Vector(11.0, 13.0);
        Vector v2 = new Vector(v);

        v.setC1(20.0);
        v.setC2(53.1);
        assertEquals(11.0, v2.getC1(), 0);
        assertEquals(13.0, v2.getC2(), 0);
    }

}