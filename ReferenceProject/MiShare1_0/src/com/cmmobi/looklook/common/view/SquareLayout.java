package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.view
 * @filename SquareLayout.java
 * @summary 导入界面正方形的gridview item布局
 * @author Lanhai
 * @date 2013-11-20
 * @version 1.0
 */
public class SquareLayout extends FrameLayout {
    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public SquareLayout(Context context) {
        super(context);
    }
 
    @SuppressWarnings("unused")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // For simple implementation, or internal size is always 0.
        // We depend on the container to specify the layout size of
        // our view. We can't really know what it is since we will be
        // adding and removing different arbitrary views and do not
        // want the layout to change as this happens.
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
 
        // Children are just made to fill our space.
        int childWidthSize = getMeasuredWidth();
        int childHeightSize = getMeasuredHeight();
        //高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}