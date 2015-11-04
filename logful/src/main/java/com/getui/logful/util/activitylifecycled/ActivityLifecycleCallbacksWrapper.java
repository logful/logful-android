/*
 * This source is part of the _____ ___ ____ __ / / _ \/ _ | / __/___ _______ _ / // / , _/ __ |/
 * _/_/ _ \/ __/ _ `/ \___/_/|_/_/ |_/_/ (_)___/_/ \_, / /___/ repository.
 * 
 * Copyright (C) 2013 Benoit 'BoD' Lubek (BoD@JRAF.org)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.getui.logful.util.activitylifecycled;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;

/**
 * Wraps an {@link ActivityLifecycleCallbacksCompat} into an {@link ActivityLifecycleCallbacks}.
 */
/* package */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class ActivityLifecycleCallbacksWrapper implements ActivityLifecycleCallbacks {

    private final ActivityLifecycleCallbacksCompat mCallback;

    public ActivityLifecycleCallbacksWrapper(ActivityLifecycleCallbacksCompat callback) {
        mCallback = callback;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mCallback.onActivityCreated(activity, savedInstanceState);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mCallback.onActivityStarted(activity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        mCallback.onActivityResumed(activity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        mCallback.onActivityPaused(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        mCallback.onActivityStopped(activity);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        mCallback.onActivitySaveInstanceState(activity, outState);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mCallback.onActivityDestroyed(activity);
    }

    /**
     * Compare the current wrapped callback with another object wrapped callback.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ActivityLifecycleCallbacksWrapper)) {
            return false;
        }
        final ActivityLifecycleCallbacksWrapper that = (ActivityLifecycleCallbacksWrapper) object;
        return null == mCallback ? null == that.mCallback : mCallback.equals(that.mCallback);
    }

    /**
     * return wrapped callback object hashCode.
     */
    @Override
    public int hashCode() {
        return null != mCallback ? mCallback.hashCode() : 0;
    }
}
