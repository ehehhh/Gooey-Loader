package ee.subscribe.gooeyloader;

import static ee.subscribe.gooeyloader.GooeyUtil.BALLS_IN_ANIMATION;
import static ee.subscribe.gooeyloader.GooeyUtil.ACCELERATE_INTERPOLATOR;
import static ee.subscribe.gooeyloader.GooeyUtil.SMALLEST_BALL_RADIUS_PERCENTAGE;
import static ee.subscribe.gooeyloader.GooeyUtil.DECELERATE_INTERPOLATOR;

class SmallGooBall implements GooBall {

    private static final float ANIMATION_DURATION_PERCENTAGE = 0.75f;
    private static final float ANIMATION_START_DELAY = 1f - ANIMATION_DURATION_PERCENTAGE;
    private static final float FIRST_QUARTER = ANIMATION_DURATION_PERCENTAGE * 0.25f;
    private static final float LAST_QUARTER = ANIMATION_DURATION_PERCENTAGE * 0.75f;

    private final float startDelay;

    private float xPercentage;

    SmallGooBall(int numberInQueue) {
        startDelay = ANIMATION_START_DELAY * ((float) numberInQueue / BALLS_IN_ANIMATION);
        setCurrentInterpolation(0);
    }

    @Override
    public void setCurrentInterpolation(float interpolation) {
        interpolation -= startDelay;
        if (interpolation >= 0 && interpolation <= ANIMATION_DURATION_PERCENTAGE) {
            if (interpolation < FIRST_QUARTER) {
                xPercentage = 0.46f * DECELERATE_INTERPOLATOR.getInterpolation(interpolation / FIRST_QUARTER);
            } else if (interpolation > LAST_QUARTER) {
                xPercentage = 0.54f + (0.46f * ACCELERATE_INTERPOLATOR.getInterpolation((interpolation - LAST_QUARTER) / FIRST_QUARTER));
            } else {
                xPercentage = 0.46f + (0.08f * ((interpolation - FIRST_QUARTER) / (LAST_QUARTER - FIRST_QUARTER)));
            }
        } else {
            xPercentage = 0;
        }
    }

    @Override
    public float getXPercentage() {
        return xPercentage;
    }

    @Override
    public float getRadiusPercentage() {
        return SMALLEST_BALL_RADIUS_PERCENTAGE;
    }

    @Override
    public boolean isMainBall() {
        return false;
    }
}
