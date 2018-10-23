package hy.h_bezier_dragbubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * @author huanghaha
 * @desc 三阶贝塞尔曲线
 */
public class ThreeBesselView extends View {
    private Path mPath;
    private Paint mPaint;
    private Paint pPaint;
    private float xStart, yStart;
    private float xC1, yC1;
    private float xC2, yC2;
    private float xEnd, yEnd;

    public ThreeBesselView(Context context) {
        super(context);
        init();
    }

    public ThreeBesselView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ThreeBesselView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ThreeBesselView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        mPath = new Path();
        xStart = 100;
        yStart = 100;

        xC1 = 300;
        yC1 = 60;

        xC2 = 500;
        yC2 = 500;

        xEnd = 800;
        yEnd = 300;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);


        pPaint = new Paint();
        pPaint.setStyle(Paint.Style.FILL);
        pPaint.setColor(Color.BLACK);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.moveTo(xStart, yStart);
        mPath.cubicTo(xC1, yC1, xC2, yC2, xEnd, yEnd);//三阶贝塞尔曲线
        canvas.drawPath(mPath, mPaint);

        //控制点
        canvas.drawCircle(xC1, yC1, 10, pPaint);
        canvas.drawCircle(xC2, yC2, 10, pPaint);
        //起始点
        pPaint.setColor(Color.GREEN);
        canvas.drawCircle(xStart, yStart, 10, pPaint);
        canvas.drawCircle(xEnd, yEnd, 10, pPaint);

    }
}
