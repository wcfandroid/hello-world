package com.example.weichangfa.myapplication;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;

public class DrawableUtils {

    public static GradientDrawable createDrawable(int contentColor, int strokeColor, int radius) {
        GradientDrawable drawable = new GradientDrawable(); // 生成Shape
        drawable.setGradientType(GradientDrawable.RECTANGLE); // 设置矩形
        drawable.setColor(contentColor);// 内容区域的颜色
        drawable.setStroke(1, strokeColor); // 四周描边,描边后四角真正为圆角，不会出现黑色阴影。如果父窗体是可以滑动的，需要把父View设置setScrollCache(false)
        drawable.setCornerRadius(radius); // 设置四角都为圆角
        return drawable;
    }

    public static StateListDrawable createSelector(Drawable normalState, Drawable pressedState) {
        StateListDrawable bg = new StateListDrawable();
        bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressedState);
        bg.addState(new int[] { android.R.attr.state_enabled }, normalState);
        bg.addState(new int[] {}, normalState);
        return bg;
    }
}
