package components;

public class Vector{
    private Double c1;
    private Double c2;

    public Vector(){
        this.c1 = 0.0;
        this.c2 = 0.0;
    }
    public Vector(double c1, double c2){
        this.c1 = c1;
        this.c2 = c2;
    }
    public Vector(Vector init){
        this.c1 = init.getC1();
        this.c2 = init.getC2();
    }
    @Override
    public String toString() {
        return "("+c1+","+c2+")";
    }
    public void setC1(Double newVal){
        c1 = newVal;
    }
    public void setC2(Double newVal){
        c2 = newVal;
    }
    public Double getC1(){
        return c1;
    }
    public Double getC2(){
        return c2;
    }
    public Vector add(Vector other){
        return new Vector(c1 + other.getC1(), c2 + other.getC2());
    }
    public Vector multiply(Double num){
        return new Vector(c1 * num, c2 * num);
    }
    public Double multiply(Vector other){
        return (this.getC1()*other.getC1())+(this.getC2()*other.getC2());
    }
    public Double length(){
        return Math.sqrt(Math.pow(c1, 2) + Math.pow(c2, 2));
    }
    public Double distance(Vector other){
        Double a = this.getC1()-other.getC1();
        Double b = this.getC2()-other.getC2();
        return Math.sqrt(a*a + b*b);
    }
    @Override
    public boolean equals(Object o) {
        Vector other = (Vector) o;
        if(this.getC1().equals(other.getC1()) && this.getC2().equals(other.getC2())){
            return true;
        }
        return false;
    }
}