package com.zdc.flowlayoutdemo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 描述：
 * Created by zhaodecang on 2016-10-18 0018 13:42
 * 邮箱：zhaodecang@gmail.com
 */

public class FlowLayout extends ViewGroup {

    /** 用于存储所有的view **/
    private ArrayList<ArrayList<View>> allLines = new ArrayList<>();
    /** 指定每一个子view之前的padding值 **/
    private static final int PADDING = 6;

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 测量之前清空原来的view集合
        allLines.clear();
        int containerMeasureWidth = MeasureSpec.getSize(widthMeasureSpec);
        // 创建一个用于存储一行view的集合
        ArrayList<View> oneLine = null;
        // 创建一个未指定规格且大小为0的模式
        int noModeMesureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            // 使用无规格模式测量一下每一个子view
            child.measure(noModeMesureSpec, noModeMesureSpec);
            // 如果是第一个或者这一行容不下了  就重新创建一行
            if (i == 0 || child.getMeasuredWidth() > getUsableWidth(containerMeasureWidth, oneLine)) {
                oneLine = new ArrayList<>();
                allLines.add(oneLine);
            }
            oneLine.add(child);
        }
        // 计算容器的高度 = 其中一行(第一行)的元素高度 * 行数 - 上下的padding值
        int totalVerticalPadding = getPaddingTop() + getPaddingBottom() + PADDING * (allLines.size() - 1);
        int oneLineHeight = getChildAt(0).getMeasuredHeight();
        int containerMeasureHeight = oneLineHeight * allLines.size() + totalVerticalPadding;
        // 设置FlowLayout的宽和高  宽就用父容器传的宽，高用子view的高  setMeasuredDimension通过该方法保存测量的值
        setMeasuredDimension(containerMeasureWidth, containerMeasureHeight);
    }

    /** 获取一行的可用宽度大小 **/
    private int getUsableWidth(int containerMeasureWidth, ArrayList<View> oneLine) {
        int onelineWidth = 0;
        for (View view : oneLine) {
            onelineWidth += view.getMeasuredWidth();
        }
        //一行的可用宽度 = 容器的总宽度 - 一行所有view的宽度 - 水平方向的padding值
        int totalHorizontalPadding = getPaddingLeft() + getPaddingRight() + PADDING * (oneLine.size() - 1);
        return containerMeasureWidth - onelineWidth - totalHorizontalPadding;
    }

    /** 排版子view **/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int tempRight = 0;   //用于临时保存当前列的坐标
        int tempBottom = 0;    // 用于临时保存当前行的底部坐标
        // 遍历所有行的view集合
        for (int rowIndex = 0; rowIndex < allLines.size(); rowIndex++) {
            // 获取一行的view集合
            ArrayList<View> oneLines = allLines.get(rowIndex);
            // 将剩余的可用空间平分给当前行所有view
            int oneLineAvailableSpace = getUsableWidth(getMeasuredWidth(), oneLines) / oneLines.size();
            for (int columnIndex = 0; columnIndex < oneLines.size(); columnIndex++) {
                View child = oneLines.get(columnIndex);
                // 测量完之后就可以获取每一个子view的宽和高了
                int childMeasuredWidth = child.getMeasuredWidth();
                int childMeasuredHeight = child.getMeasuredHeight();
                // 设置子View在容器中的位置
                int childLeft = columnIndex == 0 ? getPaddingLeft() : tempRight + PADDING;// 当前view的左边位置就是上一个view的右边位置
                int childTop = rowIndex == 0 ? getPaddingTop() : tempBottom + PADDING;
                // 将分得的剩余水平空间大小加到view的右边位置上
                int childRight = columnIndex == oneLines.size() - 1 ? getMeasuredWidth() - getPaddingRight() :
                        childLeft + childMeasuredWidth + oneLineAvailableSpace;
                int childBottom = childTop + childMeasuredHeight;
                childMeasuredHeight = MeasureSpec.makeMeasureSpec(childBottom - childTop, MeasureSpec.EXACTLY);
                childMeasuredWidth = MeasureSpec.makeMeasureSpec(childRight - childLeft, MeasureSpec.EXACTLY);
                child.measure(childMeasuredWidth, childMeasuredHeight);
                // 重新赋值上一个view的右边、下边位置
                child.layout(childLeft, childTop, childRight, childBottom);
                tempRight = childRight;
            }
            // 重新赋值当前行的底部位置
            tempBottom = oneLines.get(0).getBottom();
        }
    }
}
