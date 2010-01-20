
/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jayway.android.robotium.solo;

import android.app.Instrumentation;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * Reusable methods for generating touch events. These methods can be used with
 * InstrumentationTestCase or ActivityTestCases to simulate user interaction with
 * the application through a touch screen.
 */
public class TouchUtils {
    
    /**
     * Simulate touching a specific location and dragging to a new location.
     * 
     * param inst the instrumentation object. 
     * @param fromX X coordinate of the initial touch, in screen coordinates
     * @param toX Xcoordinate of the drag destination, in screen coordinates
     * @param fromY X coordinate of the initial touch, in screen coordinates
     * @param toY Y coordinate of the drag destination, in screen coordinates
     * @param stepCount How many move steps to include in the drag
     */
	protected static void drag(Instrumentation inst, float fromX, float toX, float fromY, float toY,
			int stepCount) {

		long downTime = SystemClock.uptimeMillis();
		long eventTime = SystemClock.uptimeMillis();
		float y = fromY;
		float x = fromX;
		float yStep = (toY - fromY) / stepCount;
		float xStep = (toX - fromX) / stepCount;
		MotionEvent event = MotionEvent.obtain(downTime, eventTime,
				MotionEvent.ACTION_DOWN, fromX, y, 0);
		inst.sendPointerSync(event);
		
		for (int i = 0; i < stepCount; ++i) {
			y += yStep;
			x += xStep;
			eventTime = SystemClock.uptimeMillis();
			event = MotionEvent.obtain(downTime, eventTime,
					MotionEvent.ACTION_MOVE, x, y, 0);
			inst.sendPointerSync(event);
			inst.waitForIdleSync();
		}

		eventTime = SystemClock.uptimeMillis();
		event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP,
				fromX, y, 0);
		inst.sendPointerSync(event);

	}

}
