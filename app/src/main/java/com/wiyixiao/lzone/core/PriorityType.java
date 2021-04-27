package com.wiyixiao.lzone.core;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @Desc: 线程优先级
 * @Author: Aries.hu
 * @Date: 2021/4/27 22:23
 */

@IntDef({PriorityType.HIGH, PriorityType.NORMAL,PriorityType.LOW})
@Retention(RetentionPolicy.SOURCE)
public @interface PriorityType {

    /**
     * @Desc: 不设置优先级，默认正常
     */
    int NONE = -1;

    /**
     * @Desc: 优先级高
     */
    int HIGH = 0;

    /**
     * @Desc: 优先级正常
     */
    int NORMAL = 1;

    /**
     * @Desc: 优先级低
     */
    int LOW = 2;

}
