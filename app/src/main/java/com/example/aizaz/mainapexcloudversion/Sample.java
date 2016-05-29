package com.example.aizaz.mainapexcloudversion;




/**
 * Created by mudassir on 3/2/2016.
 */
public class Sample {
    public double x;
    public  double y;
    public double z;



    public Sample(double x,double y,double z){
        setX(x);
        setY(y);
        setZ(z);


    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
