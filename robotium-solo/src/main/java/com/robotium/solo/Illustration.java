package com.robotium.solo;

import java.util.ArrayList;
import android.view.MotionEvent;

/**
 * A class used to pass Illustrations to an Illustrator.
 * Compatible with specific MotionEvent.TOOL_TYPEs
 *
 * @author Jake Kuli, 3kajjak3@gmail.com
 */
public class Illustration {

  private final int toolType;
  private final ArrayList<PressurePoint> points;

  private Illustration(Builder builder) {
      this.toolType = builder.builderToolType;
      this.points = builder.builderPoints;
  }

  /**
   * Builder class to build illustrations
   */
  public static class Builder {

      private int builderToolType = MotionEvent.TOOL_TYPE_FINGER;
      private ArrayList<PressurePoint> builderPoints = new ArrayList<PressurePoint>();

      /**
        * Sets the tool type to use when illustrating.
        * By default this is set to MotionEvent.TOOL_TYPE_FINGER
        * @param toolType an int from MotionEvent's static int TOOL_TYPEs
        */
      public Builder setToolType(int toolType) {
          builderToolType = toolType;
          return this;
      }

      public Builder addPoint(float x, float y, float pressure) {
          builderPoints.add(new PressurePoint(x, y, pressure));
          return this;
      }

      public Illustration build() {
          return new Illustration(this);
      }
  }

  public ArrayList<PressurePoint> getPoints() {
    return points;
  }

  public int getToolType() {
    return toolType;
  }
}
