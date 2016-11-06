package ee.subscribe.gooeyloader;

import android.view.animation.Animation;
import android.view.animation.Transformation;

class CallbackAnimation extends Animation {

    private TransformationListener listener;

    CallbackAnimation(TransformationListener listener) {
        this.listener = listener;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        listener.onApplyTransformation(interpolatedTime);
    }

    interface TransformationListener {
        void onApplyTransformation(float interpolatedTime);
    }
}