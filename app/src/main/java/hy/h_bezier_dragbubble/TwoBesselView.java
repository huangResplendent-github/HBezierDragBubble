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
 * @desc 二阶贝塞尔曲线
 */
public class TwoBesselView extends View {
    private Path mPath;
    private Paint mPaint;
    private Paint pPaint;
    private float xC1, yC1;
    private float xEnd, yEnd;

    public TwoBesselView(Context context) {
        super(context);
        init();
    }

    public TwoBesselView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoBesselView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TwoBesselView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        mPath = new Path();
        xC1 = 300;
        yC1 = 60;

        xEnd = 500;
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
        mPath.moveTo(100, 100);
        mPath.quadTo(xC1, yC1, xEnd, yEnd);//二阶贝塞尔曲线
        canvas.drawPath(mPath, mPaint);

        canvas.drawCircle(100, 100, 10, pPaint);
        canvas.drawCircle(xEnd, yEnd, 10, pPaint);
        //控制点
        pPaint.setColor(Color.GREEN);
        canvas.drawCircle(xC1, yC1, 10, pPaint);

    }


}
