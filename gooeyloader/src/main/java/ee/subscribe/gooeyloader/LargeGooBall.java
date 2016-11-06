package ee.subscribe.gooeyloader;

import static ee.subscribe.gooeyloader.GooeyUtil.ACCELERATE_INTERPOLATOR;
import static ee.subscribe.gooeyloader.GooeyUtil.SMALLEST_BALL_RADIUS_PERCENTAGE;
import static ee.subscribe.gooeyloader.GooeyUtil.DECELERATE_INTERPOLATOR;

class LargeGooBall implements GooBall {

    private static final float DIFF_FROM_FULL_RADIUS = 1f - SMALLEST_BALL_RADIUS_PERCENTAGE;
    private static final float FIRST_QUARTER_END = 0.75f * 0.25f;
    private static final float LAST_QUARTER_START = 1 - FIRST_QUARTER_END;
    private static final float MIDDLE = 1 - (2 * FIRST_QUARTER_END);
    private static final float RADIUS_DIV = MIDDLE / 3;
    private static final float FIRST_RADIUS_DIV_END = FIRST_QUARTER_END + RADIUS_DIV;
    private static final float LAST_RADIUS_DIV_START = FIRST_QUARTER_END + (2 * RADIUS_DIV);

    private float xPercentage;
    private float radiusPercentage;

    LargeGooBall() {
        setCurrentInterpolation(0);
    }

    @Override
    public void setCurrentInterpolation(float interpolation) {
        if (interpolation < FIRST_QUARTER_END) {
            xPercentage = 0.5f * DECELERATE_INTERPOLATOR.getInterpolation(interpolation / FIRST_QUARTER_END);
            radiusPercentage = SMALLEST_BALL_RADIUS_PERCENTAGE;
        } else if (interpolation > LAST_QUARTER_START) {
            xPercentage = 0.5f + (0.5f * ACCELERATE_INTERPOLATOR.getInterpolation((interpolation - LAST_QUARTER_START) / FIRST_QUARTER_END));
            radiusPercentage = SMALLEST_BALL_RADIUS_PERCENTAGE;
        } else {
            xPercentage = 0.5f;
            float radiusFactor;
            if (interpolation < FIRST_RADIUS_DIV_END) {
                radiusFactor = (interpolation - FIRST_QUARTER_END) / RADIUS_DIV;
            } else if (interpolation > LAST_RADIUS_DIV_START) {
                radiusFactor = 1 - ((interpolation - LAST_RADIUS_DIV_START) / (RADIUS_DIV));
            } else {
                radiusFactor = 1;
            }
            radiusPercentage = SMALLEST_BALL_RADIUS_PERCENTAGE + Math.max(0, Math.min(DIFF_FROM_FULL_RADIUS, radiusFactor * DIFF_FROM_FULL_RADIUS));
        }
    }

    @Override
    public float getXPercentage() {
        return xPercentage;
    }

    @Override
    public float getRadiusPercentage() {
        return radiusPercentage;
    }

    @Override
    public boolean isMainBall() {
        return true;
    }
}
