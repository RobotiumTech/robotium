package com.robotium.solo;

/**
 * A class to describe points and respective pressures in an Illustration
 *
 * @author Jake Kuli, 3kajjak3@gmail.com
 */
class PressurePoint {
    public final float x;
    public final float y;
    public final float pressure;

    public PressurePoint(float x, float y, float pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }
}
