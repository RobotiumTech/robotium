package com.robotium.solo;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerProperties;
import android.view.MotionEvent.PointerCoords;
import android.graphics.PointF;


class Zoomer {
	
	private final Instrumentation _instrument;
	public static final int GESTURE_DURATION_MS = 1000;
    public static final int EVENT_TIME_INTERVAL_MS = 10;
	
	public Zoomer(Instrumentation inst)
	{
		this._instrument = inst;
	}

	public void generateZoomGesture(PointF startPoint1, PointF startPoint2, PointF endPoint1, PointF endPoint2) 
	{

		 long downTime = SystemClock.uptimeMillis();
         long eventTime = SystemClock.uptimeMillis();

         float startX1 = startPoint1.x;
         float startY1 = startPoint1.y;
         float startX2 = startPoint2.x;
         float startY2 = startPoint2.y;

         float endX1 = endPoint1.x;
         float endY1 = endPoint1.y;
         float endX2 = endPoint2.x;
         float endY2 = endPoint2.y;

         //pointer 1
         float x1 = startX1;
         float y1 = startY1;

         //pointer 2
         float x2 = startX2;
         float y2 = startY2; 

         PointerCoords[] pointerCoords = new PointerCoords[2];
         PointerCoords pc1 = new PointerCoords();
         PointerCoords pc2 = new PointerCoords();
         pc1.x = x1;
         pc1.y = y1;
         pc1.pressure = 1;
         pc1.size = 1;
         pc2.x = x2;
         pc2.y = y2;
         pc2.pressure = 1;
         pc2.size = 1;
         pointerCoords[0] = pc1;
         pointerCoords[1] = pc2;

         PointerProperties[] pointerProperties = new PointerProperties[2];
         PointerProperties pp1 = new PointerProperties();
         PointerProperties pp2 = new PointerProperties();
         pp1.id = 0;
         pp1.toolType = MotionEvent.TOOL_TYPE_FINGER;
         pp2.id = 1;
         pp2.toolType = MotionEvent.TOOL_TYPE_FINGER;
         pointerProperties[0] = pp1;
         pointerProperties[1] = pp2;

         MotionEvent event;
         // send the initial touches
         event = MotionEvent.obtain( downTime,
                                     eventTime,
                                     MotionEvent.ACTION_DOWN,
                                     1,
                                     pointerProperties,
                                     pointerCoords,
                                     0, 0, // metaState, buttonState
                                     1, // x precision
                                     1, // y precision
                                     0, 0, 0, 0 ); // deviceId, edgeFlags, source, flags
         _instrument.sendPointerSync(event);

         event = MotionEvent.obtain( downTime,
                                     eventTime,
                                     MotionEvent.ACTION_POINTER_DOWN + (pp2.id << MotionEvent.ACTION_POINTER_INDEX_SHIFT),
                                     2,
                                     pointerProperties,
                                     pointerCoords,
                                     0, 0,
                                     1,
                                     1,
                                     0, 0, 0, 0 );
         _instrument.sendPointerSync(event);

         int numMoves = GESTURE_DURATION_MS / EVENT_TIME_INTERVAL_MS;

         float stepX1 = (endX1 - startX1) / numMoves;
         float stepY1 = (endY1 - startY1) / numMoves;
         float stepX2 = (endX2 - startX2) / numMoves;
         float stepY2 = (endY2 - startY2) / numMoves;

         // send the zoom
         for (int i = 0; i < numMoves; i++)
         {
             eventTime += EVENT_TIME_INTERVAL_MS;
             pointerCoords[0].x += stepX1;
             pointerCoords[0].y += stepY1;
             pointerCoords[1].x += stepX2;
             pointerCoords[1].y += stepY2;

             event = MotionEvent.obtain( downTime,
                                         eventTime,
                                         MotionEvent.ACTION_MOVE,
                                         2,
                                         pointerProperties,
                                         pointerCoords,
                                         0, 0,
                                         1,
                                         1,
                                         0, 0, 0, 0 );
             _instrument.sendPointerSync(event);
         }
	}
}
