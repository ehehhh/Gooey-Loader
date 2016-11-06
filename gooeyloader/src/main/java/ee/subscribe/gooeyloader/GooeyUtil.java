package ee.subscribe.gooeyloader;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

class GooeyUtil {

    static final int BALLS_IN_ANIMATION = 8;
    static final float SMALLEST_BALL_RADIUS_PERCENTAGE = 0.4f;
    static final Interpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    static final Interpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    private GooeyUtil() {
    }

    static int dp2px(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
}
