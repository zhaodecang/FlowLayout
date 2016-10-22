package com.zdc.flowlayoutdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 描述：
 * Created by zhaodecang on 2016-10-19 0019 14:06
 * 邮箱：zhaodecang@gmail.com
 */

public class AnotherFlowLayout extends ViewGroup {

    ArrayList<ArrayList<View>> allLinesView = new ArrayList<>();
    /** 指定每一个子view之前的padding值 **/
    private static final int PADDING = 6;

    public AnotherFlowLayout(Context context) {
        super(context);
    }

    public AnotherFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnotherFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        allLinesView.clear();
        int containerWidth = MeasureSpec.getSize(widthMeasureSpec);
        ArrayList<View> oneLineView = null;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            child.measure(0, 0);
            if (i == 0 || child.getMeasuredWidth() > getUsableWidth(containerWidth, oneLineView)) {
                oneLineView = new ArrayList<>();
                allLinesView.add(oneLineView);
            }
            oneLineView.add(child);
        }
        // 计算容器的宽高
        // 容器的高度等于每一行的元素高度乘以行数
        int allLinesHeight = oneLineView.get(0).getMeasuredHeight() * allLinesView.size();
        int veritialPadding = getPaddingTop() + getPaddingBottom();
        int containerHeight = allLinesHeight + veritialPadding + PADDING * (allLinesView.size() - 1);
        // 手动调用方法保存一下测量之后的值
        setMeasuredDimension(containerWidth, containerHeight);

//      super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int getUsableWidth(int containerWidth, ArrayList<View> oneLineView) {
        int oneLineViewWidth = 0;
        for (View view : oneLineView) {
            oneLineViewWidth += view.getMeasuredWidth();
        }
        int horizontalPadding = getPaddingLeft() + getPaddingRight() + PADDING * (oneLineView.size() - 1);
        return containerWidth - oneLineViewWidth - horizontalPadding;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int tempBottom = 0;// 用于保存上一行view底部位置的临时数据
        for (int rowIndex = 0; rowIndex < allLinesView.size(); rowIndex++) {
            int tempRight = 0;
            ArrayList<View> onelineViews = allLinesView.get(rowIndex);
            int availableSpace = getUsableWidth(getMeasuredWidth(), onelineViews) / onelineViews.size();
            for (int columnIndex = 0; columnIndex < onelineViews.size(); columnIndex++) {
                View child = onelineViews.get(columnIndex);


                // 获取子view测量之后的宽度
                int childMeasuredWidth = child.getMeasuredWidth();
                int childMeasuredHeight = child.getMeasuredHeight();

                int childLeft = columnIndex == 0 ? getPaddingLeft() : tempRight + PADDING;
                // 如果是当前行最后一个子view就到最后边
                int childRight = columnIndex == onelineViews.size() - 1 ?
                        getMeasuredWidth() - getPaddingRight()
                        : childLeft + childMeasuredWidth + availableSpace;
                int childTop = rowIndex == 0 ? getPaddingTop() : tempBottom + PADDING;
                int childBottom = childTop + childMeasuredHeight;

                child.layout(childLeft, childTop, childRight, childBottom);
                tempRight = childRight;

                // 由于子view的宽度与测量好的可能有差距  所以需要重新手动测量一下
                int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childRight - childLeft, MeasureSpec.EXACTLY);
                int heightMeasureSpec = MeasureSpec.makeMeasureSpec(childBottom - childTop, MeasureSpec.EXACTLY);
                child.measure(widthMeasureSpec, heightMeasureSpec);
            }
            tempBottom = onelineViews.get(0).getBottom();
        }
    }
}
