package com.tb.tanks.framework.input;

import android.view.View.OnTouchListener;

import com.tb.tanks.framework.Input;

import java.util.List;

public interface TouchHandler extends OnTouchListener {
	public boolean isTouchDown(int pointer);

	public int getTouchX(int pointer);

	public int getTouchY(int pointer);

	public List<Input.TouchEvent> getTouchEvents();
}