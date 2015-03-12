package com.robotium.solo;

import java.util.ArrayList;
import android.view.MotionEvent;

public class Illustration {

  private int toolType = MotionEvent.TOOL_TYPE_STYLUS;
  private ArrayList<PressurePoint> points = new ArrayList<PressurePoint>();

  private Illustration(Builder builder) {
      this.toolType = builder.builderToolType;
      this.points = builder.builderPoints;
  }

  public static class Builder {

      private int builderToolType;
      private ArrayList<PressurePoint> builderPoints = new ArrayList<PressurePoint>();

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

  public ArrayList<PressurePoint> getPoints()
  {
    return points;
  }

  public int getToolType()
  {
    return toolType;
  }
}
