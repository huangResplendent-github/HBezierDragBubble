package hy.h_bezier_dragbubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author huanghaha
 * @desc 多阶贝塞尔曲线
 */
public class OtherBesselView extends View {

    private Path mPath;
    private Paint mPaint;//点线之间的画笔
    private Paint pPaint;//贝塞尔曲线的画笔
    private float xStart, yStart;
    private float xEnd, yEnd;
    private List<PointF> cPoints = new ArrayList<>();

    public OtherBesselView(Context context) {
        super(context);
        init();
    }

    public OtherBesselView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OtherBesselView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public OtherBesselView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }


    private void init() {
        mPath = new Path();

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(5);
        mPaint.setColor(Color.RED);

        pPaint = new Paint();
        pPaint.setStyle(Paint.Style.STROKE);
        pPaint.setStrokeWidth(5);
        pPaint.setColor(Color.GREEN);


        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int cX = random.nextInt(600) + 100;
            int cY = random.nextInt(600) + 100;
            PointF p = new PointF(cX, cY);
            cPoints.add(p);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.画出控制点以及他们之间的线段-仅仅为了对照
        for (int i = 0; i < cPoints.size(); i++) {
            PointF pointF = cPoints.get(i);
            if (i > 0) {
                canvas.drawLine(cPoints.get(i - 1).x, cPoints.get(i - 1).y, pointF.x, pointF.y, mPaint);
            }
            canvas.drawCircle(pointF.x, pointF.y, 10, mPaint);
        }

        //2.根据德卡斯特利奥算法画出贝塞尔曲线
        buildBezierPoints(canvas);


    }

    /**
     * 绘制贝塞尔曲线
     */
    private void buildBezierPoints(Canvas canvas) {
        int order = cPoints.size() - 1; //当前贝塞尔曲线的阶数
        float delta = 1.0f / 100;// 绘制的密度，（如果密度太小可能不能绘制完所有的曲线）
        for (float t = 0.0f; t <= 1.0f; t += delta) {
            PointF pointF = new PointF(deCusterLeoX(order, 0, t), deCusterLeoY(order, 0, t));//贝塞尔曲线上的点

            //使用path连接点
            if (t == 0.0f) {//移动画笔到第一个点
                mPath.moveTo(pointF.x, pointF.y);

            } else {//连接点
                mPath.lineTo(pointF.x, pointF.y);
            }


        }
        canvas.drawPath(mPath, pPaint);

    }

    /**
     * 利用递归思想求出在t-比例不断变动的情况先贝塞尔曲线上的点
     * 由于递归的计算有压栈的特点，所以这儿的j进入的时候传入的0，为方便理解可以打开注释
     *
     * @param order 阶数
     * @param j     当前控制点
     * @param t     比例
     * @return
     */
    private float deCusterLeoX(int order, int j, float t) {
//        Log.e("msg", "开始计算的条件" + " order=" + order + "  j=" + j + "  t=" + t);
        float reuslt = 0.0f;
        if (order == 1) {//一阶段
            reuslt = (1 - t) * cPoints.get(j).x + t * cPoints.get(j + 1).x;
        } else {

            float val1 = (1 - t) * deCusterLeoX(order - 1, j, t);
            float val2 = t * deCusterLeoX(order - 1, j + 1, t);

            reuslt = val1 + val2;

        }
//        Log.e("msg", "开始计算的结果" + reuslt);
        return reuslt;

    }

    private float deCusterLeoY(int order, int j, float t) {
        if (order == 1) {//一阶段
            return (1 - t) * cPoints.get(j).y + t * cPoints.get(j + 1).y;
        } else {


            return (1 - t) * deCusterLeoY(order - 1, j, t) + t * deCusterLeoY(order - 1, j + 1, t);

        }

    }
}
