package com.xzl.project.minizhihu.utils;

import android.graphics.Color;

import com.xzl.project.minizhihu.Constants;

import java.util.Random;

public class CommonUtils {
    /**
     * 获取随机tgb颜色
     */
    public static int randomColor(){
        Random random = new Random();
        //0-190,如果颜色额值过大，就越接近白色，就看不清了，所以需要限定范围
        int red = random.nextInt(150);
        //0-190
        int green = random.nextInt(150);
        //0-190
        int blue = random.nextInt(150);
        //使用tgb混合生成一种新的颜色，Color.rgb生成的额是一个int数
        return Color.rgb(red,green,blue);
    }

    public static int randomTagColor() {
        int randomNum = new Random().nextInt();
        int position = randomNum % Constants.TAB_COLORS.length;
        if (position < 0) {
            position = -position;
        }
        return Constants.TAB_COLORS[position];
    }
}
