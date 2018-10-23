package hy.h_bezier_dragbubble;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.PointFEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * @author huanghaha
 * @desc 仿QQ炸裂气泡
 */
public class DragBubbleView extends View {
    //气泡的状态-静止
    private final int BUBBLE_STATE_STATIC = 0;
    //气泡的状态-消失
    private final int BUBBLE_STATE_DISMISS = 1;

    //气泡的状态-连接
    private final int BUBBLE_STATE_CONNECTION = 2;

    //气泡的状态-分离
    private final int BUBBLE_STATE_APART = 3;


    //气泡的状态
    private int mBubbleState = BUBBLE_STATE_STATIC;
    private Path bezierPath;//气泡贝塞尔曲线
    private Paint bezierPaint;//贝塞尔曲线样式
    //气泡相关绘制样式
    private Paint bPaint;
    //气泡文字画笔
    private Paint textPaint;

    //文字绘制区域
    private Rect textRect;
    //爆炸画笔
    private Paint bombPaint;
    //炸裂区域
    private Rect bombRect;
    //气泡半径
    private float bubbleRadius = 20;
    //小圆的半径
    private float bubbleStillRadius;

    //气泡颜色
    private int bubbleColor;
    //气泡文字
    private String bubbleText;
    //气泡文字大小
    private float bubbleTextSize = 12;
    //气泡文字颜色
    private int bubbleTextColor;

    //气泡不动时圆心的位置
    private PointF stillBubbleCenter;
    //气泡动时的圆心
    private PointF moveBubbleCenter;

    //俩气泡距离-圆心两点之间的
    private float dist;
    //两个圆最大距离
    private float maxDist;
    //手指偏移量
    private float moveOffsize;

    /**
     * 炸裂图片
     */
    private int[] BOOM_ARRAY = {R.drawable.burst_1, R.drawable.burst_2
            , R.drawable.burst_3, R.drawable.burst_4, R.drawable.burst_5};
    //炸裂图片Bitmap
    private Bitmap[] bomb_bitmaps;


    public DragBubbleView(Context context) {
        this(context, null);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public DragBubbleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        //获取属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DragBubbleView, defStyleAttr, defStyleRes);
        bubbleRadius = typedArray.getDimension(R.styleable.DragBubbleView_bubble_radius, bubbleRadius);
        bubbleColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_color, Color.RED);
        bubbleText = typedArray.getString(R.styleable.DragBubbleView_bubble_text);
        bubbleTextSize = typedArray.getDimension(R.styleable.DragBubbleView_bubble_textSize, bubbleTextSize);
        bubbleTextColor = typedArray.getColor(R.styleable.DragBubbleView_bubble_textColor, Color.WHITE);

        bezierPath = new Path();
        bezierPaint = new Paint();
        bezierPaint.setAntiAlias(true);
//        bezierPaint.setStyle(Paint.Style.STROKE);

//        bezierPaint.setColor(Color.GREEN);
        bezierPaint.setStyle(Paint.Style.FILL);
        bezierPaint.setColor(bubbleColor);


        //设置气泡画笔
        bPaint = new Paint();
        bPaint.setStyle(Paint.Style.FILL);
        bPaint.setAntiAlias(true);
        bPaint.setColor(bubbleColor);

        //设置文字画笔
        textPaint = new Paint();
        textPaint.setColor(bubbleTextColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(bubbleTextSize);
        textRect = new Rect();

        //设置爆炸画笔
        bombPaint = new Paint();
        bombPaint.setAntiAlias(true);
        bombPaint.setFilterBitmap(true);
        bombRect = new Rect();
        bomb_bitmaps = new Bitmap[BOOM_ARRAY.length];
        for (int i = 0; i < BOOM_ARRAY.length; i++) {
            bomb_bitmaps[i] = BitmapFactory.decodeResource(getResources(), BOOM_ARRAY[i]);

        }


        //滑动距离为5个自身的距离
        maxDist = bubbleRadius * 10;
        //移动的偏移量为一个圆的距离
        moveOffsize = maxDist / 5;

        //小圆的半径为一半
        bubbleStillRadius = bubbleRadius / 2;

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initBubble(w, h);
    }

    /**
     * 默认一开始显示的时候气泡是静止状态并且气泡显示在中心位置
     *
     * @param w
     * @param h
     */
    private void initBubble(int w, int h) {

        mBubbleState = BUBBLE_STATE_STATIC;
        if (stillBubbleCenter == null) {
            stillBubbleCenter = new PointF(w / 2, h / 2);
        } else {
            stillBubbleCenter.set(w / 2, h / 2);
        }
        if (moveBubbleCenter == null) {
            moveBubbleCenter = new PointF(w / 2, h / 2);
        } else {
            moveBubbleCenter.set(w / 2, h / 2);
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.在气泡非消失绘制出气泡以及文字
        if (mBubbleState != BUBBLE_STATE_DISMISS) {
            canvas.drawCircle(moveBubbleCenter.x, moveBubbleCenter.y, bubbleRadius, bPaint);
            textPaint.getTextBounds(bubbleText, 0, bubbleText.length(), textRect);
            canvas.drawText(bubbleText, moveBubbleCenter.x - textRect.width() / 2, moveBubbleCenter.y + textRect.height() / 2, textPaint);
        }

        //2.绘制连接状态
        /**
         * 连接状态下为两个二阶贝塞尔曲线，在这个贝塞尔曲线中我们需要知道五个点
         * 两个在小圆上的点-作为两条贝塞尔曲线的起点
         * 两个在大圆上的点-作为两条贝塞尔曲线的终点
         * 大圆和小圆圆心相连的中点-作为两条贝塞尔曲线的控制点
         */
        if (mBubbleState == BUBBLE_STATE_CONNECTION) {
            //绘制不动点的圆
            canvas.drawCircle(stillBubbleCenter.x, stillBubbleCenter.y, bubbleStillRadius, bPaint);
            //控制点
            int anchorx = (int) (stillBubbleCenter.x + moveBubbleCenter.x) / 2;
            int anchorY = (int) (stillBubbleCenter.y + moveBubbleCenter.y) / 2;

            //
            /**
             * 确定与圆相交的其他四个点ABCD
             * 连接两个圆心其线段称为l，并在两段做这条直线的两条垂线，垂线与两个圆的交点即为我们需要找的ABCD四个点
             * 这四个点我们可以通过数学定律："角度相同那么它们的三角函数值也相同"求出来
             * 首先1.分别过这两个圆心做平行于X轴的平行线，这两条线段与l的任意夹角称为Theta
             * 2.求出Theta的sin，cos值 sin=对边/斜边  cos=邻边/斜边
             * 3.根据定量求出ABCD四个点的坐标
             *
             */

            float sinTheta = (moveBubbleCenter.y - stillBubbleCenter.y) / dist;
            float cosTheta = (moveBubbleCenter.x - stillBubbleCenter.x) / dist;

            //小圆上的点
            float aX = stillBubbleCenter.x - sinTheta * bubbleStillRadius;
            float aY = stillBubbleCenter.y + cosTheta * bubbleStillRadius;

            float bX = stillBubbleCenter.x + sinTheta * bubbleStillRadius;
            float bY = stillBubbleCenter.y - cosTheta * bubbleStillRadius;

            //大圆上的点
            float cX = moveBubbleCenter.x - sinTheta * bubbleRadius;
            float cY = moveBubbleCenter.y + cosTheta * bubbleRadius;

            float dX = moveBubbleCenter.x + sinTheta * bubbleRadius;
            float dY = moveBubbleCenter.y - cosTheta * bubbleRadius;

            bezierPath.reset();
            //上
            bezierPath.moveTo(aX, aY);
            bezierPath.quadTo(anchorx, anchorY, cX, cY);
            //下
            bezierPath.lineTo(dX, dY);
            bezierPath.quadTo(anchorx, anchorY, bX, bY);

            bezierPath.close();//进行path的闭合
            canvas.drawPath(bezierPath, bezierPaint);


        }

        //炸裂动画的展示
        if (isBombAnimStarting) {
            bombRect.set((int) (moveBubbleCenter.x - bubbleRadius),
                    (int) (moveBubbleCenter.y - bubbleRadius),
                    (int) (moveBubbleCenter.x + bubbleRadius),
                    (int) (moveBubbleCenter.y + bubbleRadius));
            canvas.drawBitmap(bomb_bitmaps[bombDrawableIndex], null, bombRect, bombPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {//按下时
            dist = (float) Math.hypot(event.getX() - stillBubbleCenter.x, event.getY() - stillBubbleCenter.y);
            if (dist <= bubbleRadius + moveOffsize) {//偏移出去了半个圆
                mBubbleState = BUBBLE_STATE_CONNECTION;
            } else {
                mBubbleState = BUBBLE_STATE_STATIC;
            }

        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (mBubbleState != BUBBLE_STATE_STATIC) {
                moveBubbleCenter.x = event.getX();
                moveBubbleCenter.y = event.getY();
                dist = (float) Math.hypot(event.getX() - stillBubbleCenter.x,
                        event.getY() - stillBubbleCenter.y);
                if (mBubbleState == BUBBLE_STATE_CONNECTION) {
                    // 减去MOVE_OFFSET是为了让不动气泡半径到一个较小值时就直接消失
                    // 或者说是进入分离状态
                    if (dist < maxDist - moveOffsize) {

                        bubbleStillRadius = bubbleRadius - dist / 10;
                    } else {
                        mBubbleState = BUBBLE_STATE_APART;//消失的状态
                    }
                }
                invalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mBubbleState == BUBBLE_STATE_CONNECTION) {
                bubbleRestAnim();
            } else if (mBubbleState == BUBBLE_STATE_APART) {
                bubblebombAnim();
            }

        }
        return true;
    }

    /**
     * 动画的实现思路
     * 由于还原动画是一步一步回退回去的，动画的展示需要进行一步一步的数值计算，所以这里采用了ValueAnimator
     */

    //炸裂动画
    //当前炸裂动画播放第几张图片
    private int bombDrawableIndex = 0;
    //动画是否在播放
    private boolean isBombAnimStarting = false;

    private void bubblebombAnim() {
        mBubbleState = BUBBLE_STATE_DISMISS;//设置为消失的状态
        isBombAnimStarting = true;
        ValueAnimator valueAnimator = ValueAnimator.ofInt(0, BOOM_ARRAY.length);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                bombDrawableIndex = (int) animation.getAnimatedValue();
                invalidate();

            }
        });

        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isBombAnimStarting = false;
            }
        });
        valueAnimator.start();


    }

    //还原动画
    private void bubbleRestAnim() {
        ValueAnimator vAnimator = ValueAnimator.ofObject(new PointFEvaluator(), new PointF(moveBubbleCenter.x, moveBubbleCenter.y), new PointF(stillBubbleCenter.x, stillBubbleCenter.y));
        vAnimator.setDuration(200);
        vAnimator.setInterpolator(new OvershootInterpolator(5f));
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //不断移动大圆的位置
                moveBubbleCenter = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        vAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBubbleState = BUBBLE_STATE_STATIC;//静止状态
            }
        });
        vAnimator.start();
    }

    public void reset() {
        initBubble(getWidth(), getHeight());

        invalidate();
    }
}
