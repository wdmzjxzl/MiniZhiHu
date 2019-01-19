package com.xzl.project.minizhihu.widget;

import android.view.animation.Interpolator;

public class ElasticOutInterpolator implements Interpolator{


    @Override
    public float getInterpolation(float input) {
        if (input == 0){
            return 0;
        }

        if (input >= 1){
            return 1;
        }

        float p = .3f;
        float s = p/4;
        return ((float) Math.pow(2, -10 * input) * (float)Math.sin((input - s) * (2 * (float)Math.PI) / p) + 1);
    }
}
