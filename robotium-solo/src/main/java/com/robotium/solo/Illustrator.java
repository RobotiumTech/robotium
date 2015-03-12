package com.robotium.solo;

import java.util.ArrayList;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerProperties;
import android.view.MotionEvent.PointerCoords;
import android.view.InputDevice;
import android.app.Instrumentation;
import android.os.SystemClock;

class Illustrator {

    private Instrumentation inst;

    public Illustrator(Instrumentation inst) {
        this.inst = inst;
    }

    public void illustrate(Illustration illustration) {
        MotionEvent event;
        int currentAction;
        PointerCoords[] coords = new PointerCoords[1];
        PointerProperties[] properties = new PointerProperties[1];
        PointerProperties prop = new PointerProperties();
        prop.id = 0;
        prop.toolType = illustration.getToolType();
        properties[0] = prop;
        ArrayList<PressurePoint> points = illustration.getPoints();
        if (points.size() > 0) {
            for (int i = 0; i < points.size(); i++) {
                coords[0] = new PointerCoords();
                coords[0].x = points.get(i).x;
                coords[0].y = points.get(i).y;
                coords[0].pressure = points.get(i).pressure;
                coords[0].size = 1;

                long downTime = SystemClock.uptimeMillis();
                long eventTime = SystemClock.uptimeMillis();

                if (i == 0) currentAction = MotionEvent.ACTION_DOWN;
                else currentAction = MotionEvent.ACTION_MOVE;

                event = MotionEvent.obtain(downTime,
                    eventTime,
                    currentAction,
                    1,
                    properties,
                    coords,
                    0, 0, 1, 1, 0, 0,
                    InputDevice.SOURCE_TOUCHSCREEN,
                    0);
                inst.sendPointerSync(event);
            }
            currentAction = MotionEvent.ACTION_UP;
            coords[0] = new PointerCoords();
            coords[0].x = points.get(points.size() - 1).x;
            coords[0].y = points.get(points.size() - 1).y;
            coords[0].pressure = points.get(points.size() - 1).pressure;
            coords[0].size = 1;
            long downTime = SystemClock.uptimeMillis();
            long eventTime = SystemClock.uptimeMillis();
            event = MotionEvent.obtain(downTime,
                eventTime,
                currentAction,
                1,
                properties,
                coords,
                0, 0, 1, 1, 0, 0,
                InputDevice.SOURCE_TOUCHSCREEN,
                0);
            inst.sendPointerSync(event);
        }
        else {
            throw new RuntimeException("Illustration requires at least one point.");
        }
    }
}
