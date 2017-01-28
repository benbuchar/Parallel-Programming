import java.lang.Math;

/**
 * Created by lightray on 12/10/16.
 */
public class Vector {

    private double magnitude, xcomponent, ycomponent;

    public Vector(double x, double y) {
        xcomponent = x;
        ycomponent = y;

        double xSquared = xcomponent * xcomponent;
        double ySquared = ycomponent * ycomponent;
        double sum = xSquared + ySquared;
        magnitude = Math.sqrt(sum);
    }

    public void setX(double x) {
        xcomponent = x;
    }

    public double getX() {
        return xcomponent;
    }

    public void setY(double y) {
        ycomponent = y;
    }

    public double getY() {
        return ycomponent;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void add(Vector v) {
        xcomponent = v.getX() + xcomponent;
        ycomponent = v.getY() + ycomponent;
        calcMag();
    }

    private void calcMag() {
        magnitude = Math.sqrt((xcomponent * xcomponent) + (ycomponent * ycomponent));
    }

    public void stop() {
        xcomponent = 0;
        ycomponent = 0;
        calcMag();
    }

    public Vector mirror() {
        return new Vector(-xcomponent, -ycomponent);
    }


    public Vector bounceX() {
        xcomponent = -xcomponent;
        return this;
    }

    public Vector bounceY() {
        ycomponent = -ycomponent;
        return this;
    }

    public Vector normalize() {
        double normalX = xcomponent / magnitude;
        double normalY = ycomponent / magnitude;
        return new Vector(normalX, normalY);
    }

    public double dot(Vector vec) {
        Vector u = this;
        Vector v = vec;
        double numerator = (u.getX() * v.getX()) + (u.getY() * v.getY());
        //System.out.println("Numerator: " + numerator);
        double denominator = Math.sqrt((u.getX() * u.getX()) + (u.getY() * u.getY()))
                * Math.sqrt((v.getX() * v.getX()) + (v.getY() * v.getY()));
        //System.out.println("Denominator: " + denominator);
        double alpha = numerator / denominator;
        //System.out.println("Alpha: " + alpha);
        double rad = Math.acos(alpha);
        return Math.toDegrees(rad);
    }

    public Vector mult(double m) {
        return new Vector(xcomponent * m, ycomponent * m);
    }

    public Vector plus(Vector v) {
        return new Vector(xcomponent + v.getX(), ycomponent + v.getY());
    }

    public Vector calcNormal() {
        return new Vector(ycomponent, -xcomponent);
    }


    public Vector collide(Vector wallNormal) {
        double wallNormalDx = wallNormal.getX() / wallNormal.getMagnitude();
        double wallNormalDy = wallNormal.getY() / wallNormal.getMagnitude();
        Vector wall = wallNormal.calcNormal();
        double wallDx = wall.getX() / wall.getMagnitude();
        double wallDy = wall.getY() / wall.getMagnitude();
        double dpA = this.getX() * wallDx + this.getY() * wallDy;

        double prA_vx = dpA * wallDx;
        double prA_vy = dpA * wallDy;

        double dpB = this.getX() * wallNormalDx + this.getY() * wallNormalDy;

        double prB_vx = dpB * wallNormalDx;
        double prB_vy = dpB * wallNormalDy;

        double newVx = prA_vx - prB_vx;
        double newVy = prA_vy - prB_vy;

        return new Vector(newVx, newVy);
    }
}

