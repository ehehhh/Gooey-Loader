package ee.subscribe.gooeyloader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import java.util.ArrayList;
import java.util.List;

import static ee.subscribe.gooeyloader.GooeyUtil.BALLS_IN_ANIMATION;
import static ee.subscribe.gooeyloader.GooeyUtil.SMALLEST_BALL_RADIUS_PERCENTAGE;

public class GooeyLoaderView extends View implements CallbackAnimation.TransformationListener {

    private static final int MIN_DURATION_IN_MS = 1000;
    private static final int DEFAULT_DURATION_IN_MS = 3500;
    private static final int MAX_DURATION_IN_MS = 7000;
    private static final int DEFAULT_CIRCLE_COLOR = Color.parseColor("#9C27B0");

    private int minimumDesiredHeight;
    private int durationMs;

    private Paint circlePaint;
    private CallbackAnimation loaderAnimation;

    private List<GooBall> gooBalls;
    private GooBall mainBall;

    public GooeyLoaderView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public GooeyLoaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public GooeyLoaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        minimumDesiredHeight = GooeyUtil.dp2px(context, 80);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.GooeyLoaderView);
        int circleColor = typedArray.getColor(R.styleable.GooeyLoaderView_glv_color, DEFAULT_CIRCLE_COLOR);
        durationMs = Math.max(MIN_DURATION_IN_MS, Math.min(MAX_DURATION_IN_MS,
                typedArray.getInteger(R.styleable.GooeyLoaderView_glv_duration_in_ms, DEFAULT_DURATION_IN_MS)));
        typedArray.recycle();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);

        gooBalls = new ArrayList<>(BALLS_IN_ANIMATION);
        for (int i = 1; i < BALLS_IN_ANIMATION; i++) {
            gooBalls.add(new SmallGooBall(i));
        }
        gooBalls.add(mainBall = new LargeGooBall());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int minimumDesiredWidth = widthSize;
        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(minimumDesiredWidth, widthSize);
        } else {
            width = minimumDesiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(minimumDesiredHeight, heightSize);
        } else {
            height = minimumDesiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) {
            startAnimation();
        } else {
            stopAnimation();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        stopAnimation();
        super.onDetachedFromWindow();
    }

    private void startAnimation() {
        if (loaderAnimation == null) {
            loaderAnimation = new CallbackAnimation(this);
            loaderAnimation.setDuration(durationMs);
            loaderAnimation.setInterpolator(new LinearInterpolator());
            loaderAnimation.setRepeatCount(Animation.INFINITE);
        }
        startAnimation(loaderAnimation);
    }

    private void stopAnimation() {
        clearAnimation();
    }

    @Override
    public void onApplyTransformation(float interpolatedTime) {
        for (GooBall ball : gooBalls) {
            ball.setCurrentInterpolation(interpolatedTime);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();
        final int cy = measuredHeight / 2;
        int maxBallRadius = measuredHeight / 2;
        int fullWidth = measuredWidth + (2 * maxBallRadius);
        // Draw the balls
        for (GooBall ball : gooBalls) {
            final float cx = getCxFor(ball, maxBallRadius, fullWidth);
            final float radius = maxBallRadius * ball.getRadiusPercentage();
            canvas.drawCircle(cx, cy, radius, circlePaint);
        }
        // Draw the arcs
        for (GooBall ball : gooBalls) {
            if (shouldDrawArcFor(ball)) {
                if (ball.getXPercentage() < 0.5f) {
                    drawArcsFor(ball, canvas, maxBallRadius, fullWidth, cy, true);
                } else if (ball.getXPercentage() > 0.5f) {
                    drawArcsFor(ball, canvas, maxBallRadius, fullWidth, cy, false);
                }
            }
        }
    }

    private float getCxFor(GooBall ball, int maxBallRadius, int fullWidth) {
        return (fullWidth * ball.getXPercentage()) - maxBallRadius;
    }

    private boolean shouldDrawArcFor(GooBall ball) {
        if (!ball.isMainBall()) {
            final float mainBallXPercentage = mainBall.getXPercentage();
            final float smallBallXPercentage = ball.getXPercentage();
            return mainBallXPercentage > 0.45f &&
                    mainBallXPercentage < 0.55f &&
                    mainBall.getRadiusPercentage() > SMALLEST_BALL_RADIUS_PERCENTAGE &&
                    smallBallXPercentage > 0.4f &&
                    smallBallXPercentage < 0.6f;
        } else {
            return false;
        }
    }

    private void drawArcsFor(GooBall ball, Canvas canvas, int maxBallRadius, int fullWidth, int cy, boolean entering) {
        float mainBallRadius = maxBallRadius * mainBall.getRadiusPercentage();
        float mainBallCx = getCxFor(mainBall, maxBallRadius, fullWidth);
        float smallBallRadius = maxBallRadius * ball.getRadiusPercentage();
        float smallBallCx = getCxFor(ball, maxBallRadius, fullWidth);
        float mainBallPosModifier = (mainBall.getRadiusPercentage() - SMALLEST_BALL_RADIUS_PERCENTAGE) / (1 - SMALLEST_BALL_RADIUS_PERCENTAGE);
        int mainAngleModifier = (int) ((float) 45 * mainBallPosModifier);

        drawPath(canvas,
                entering,
                mainAngleModifier,
                mainBallCx,
                smallBallCx,
                cy,
                mainBallRadius,
                smallBallRadius);
    }

    private void drawPath(Canvas canvas,
                          boolean entering,
                          int mainAngleModifier,
                          float mainBallCx,
                          float smallBallCx,
                          float cy,
                          float mainBallRadius,
                          float smallBallRadius) {
        float arcMidPointXModifier = smallBallRadius / 10;
        float arcMidPointYModifier = smallBallRadius / 8;

        float mainBallUpperX = mainBallRadius * (float) Math.cos(Math.toRadians(270 + (entering ? -mainAngleModifier : mainAngleModifier))) + mainBallCx;
        float mainBallUpperY = mainBallRadius * (float) Math.sin(Math.toRadians(270 + (entering ? -mainAngleModifier : mainAngleModifier))) + cy;
        float smallBallUpperX = smallBallRadius * (float) Math.cos(Math.toRadians(270)) + smallBallCx;
        float smallBallUpperY = smallBallRadius * (float) Math.sin(Math.toRadians(270)) + cy;

        float mainBallLowerX = mainBallRadius * (float) Math.cos(Math.toRadians(90 + (entering ? mainAngleModifier : -mainAngleModifier))) + mainBallCx;
        float mainBallLowerY = mainBallRadius * (float) Math.sin(Math.toRadians(90 + (entering ? mainAngleModifier : -mainAngleModifier))) + cy;
        float smallBallLowerX = smallBallRadius * (float) Math.cos(Math.toRadians(90)) + smallBallCx;
        float smallBallLowerY = smallBallRadius * (float) Math.sin(Math.toRadians(90)) + cy;

        Path path = new Path();

        path.moveTo(mainBallUpperX, mainBallUpperY);

        float upperMidPointX = (mainBallUpperX + smallBallUpperX) / 2;
        float upperMidPointY = (mainBallUpperY + smallBallUpperY) / 2;
        float firstUpperMidPointX = ((mainBallUpperX + upperMidPointX) / 2) + (entering ? -arcMidPointXModifier : arcMidPointXModifier);
        float firstUpperMidPointY = ((mainBallUpperY + upperMidPointY) / 2) + arcMidPointYModifier;
        float secondUpperMidPointX = ((upperMidPointX + smallBallUpperX) / 2) + (entering ? arcMidPointXModifier : -arcMidPointXModifier);
        float secondUpperMidPointY = ((upperMidPointY + smallBallUpperY) / 2) + arcMidPointYModifier;
        path.cubicTo(firstUpperMidPointX, firstUpperMidPointY, secondUpperMidPointX, secondUpperMidPointY, smallBallUpperX, smallBallUpperY);

        path.lineTo(smallBallLowerX, smallBallLowerY);

        float lowerMidPointX = (mainBallLowerX + smallBallLowerX) / 2;
        float lowerMidPointY = (mainBallLowerY + smallBallLowerY) / 2;
        float firstLowerMidPointX = ((smallBallLowerX + lowerMidPointX) / 2) + (entering ? arcMidPointXModifier : -arcMidPointXModifier);
        float firstLowerMidPointY = ((smallBallLowerY + lowerMidPointY) / 2) - arcMidPointYModifier;
        float secondLowerMidPointX = ((lowerMidPointX + mainBallLowerX) / 2) + (entering ? -arcMidPointXModifier : arcMidPointXModifier);
        float secondLowerMidPointY = ((lowerMidPointY + mainBallLowerY) / 2) - arcMidPointYModifier;
        path.cubicTo(firstLowerMidPointX, firstLowerMidPointY, secondLowerMidPointX, secondLowerMidPointY, mainBallLowerX, mainBallLowerY);

        path.lineTo(mainBallUpperX, mainBallUpperY);

        canvas.drawPath(path, circlePaint);
    }
}
