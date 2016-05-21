package com.dhaval.main;

/**
 * Created by Dhaval on 5/16/2016.
 */
public class Button {

    private float x1;
    private float x2;

//    public boolean isEnabled() {
//        return isEnabled;
//    }
//
//    public void setEnabled(boolean enabled) {
//        isEnabled = enabled;
//    }
//
//    public boolean isEnabled = false;
    public static final int BUTTON_WIDTH = 100;
    public static final int BUTTON_HEIGHT = 15;


    public float getX1() {
        return x1;
    }

    public Button(float x1, float x2) {
        this.x1 = x1;
        this.x2 = x2;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }
}
