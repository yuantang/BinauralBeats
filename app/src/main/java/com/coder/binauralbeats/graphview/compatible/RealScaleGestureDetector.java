package com.coder.binauralbeats.graphview.compatible;

import android.content.Context;
/**
 * Copyright (C) 2011 Jonas Gehring
 * Licensed under the GNU Lesser General Public License (LGPL)
 * http://www.gnu.org/licenses/lgpl.html
 */
public class RealScaleGestureDetector extends ScaleGestureDetector {
	public RealScaleGestureDetector(Context context, final ScaleGestureDetector fakeScaleGestureDetector, final  SimpleOnScaleGestureListener fakeListener) {
		super(context, new SimpleOnScaleGestureListener() {
			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				return fakeListener.onScale(fakeScaleGestureDetector);
			}
		});
	}
}
