package com.tb.tanks.framework.gfx;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.view.DisplayCutout;

public class CutoutHelper {

    private DisplayCutout m_cutout = null;

    public CutoutHelper(DisplayCutout cutout)
    {
        m_cutout = cutout;
    }

    public DisplayCutout getM_cutout() {
        return m_cutout;
    }

    public boolean isSafe(int x0, int y0)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            for(Rect unsafeArea : m_cutout.getBoundingRects())
            {
                if(unsafeArea.contains(x0, y0))
                    return false;
            }
        }
        return true;
    }

    public Point standardizeToSafeX(int x0, int y0){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            for(Rect unsafeArea : m_cutout.getBoundingRects())
            {
                if(unsafeArea.contains(x0, y0)){
                    x0 = unsafeArea.right;
                    y0 += unsafeArea.top;
                    return new Point(x0, y0);
                }

            }
        }
        return new Point(x0, y0);
    }
}
