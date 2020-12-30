package com.mollet.smsObserver;

import android.app.Activity;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;

import java.util.ArrayList;

public class SmsObserverModule extends ReactContextBaseJavaModule {
    private final SmsTelHelper mSmsTelHelper = new SmsTelHelper();
    private final SmsCodeHelper mSmsCodeHelper;
    private final ReactApplicationContext mContext;

    public SmsObserverModule(ReactApplicationContext reactContext) {
        super(reactContext);
        mContext = reactContext;
        mSmsCodeHelper = new SmsCodeHelper(reactContext);
    }

    @Override
    public String getName() {
        return "SmsObserver";
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void getHash(Promise promise) {
        try {
            SmsHashHelper helper = new SmsHashHelper(mContext);
            ArrayList<String> signatures = helper.getAppSignatures();
            WritableArray arr = Arguments.createArray();
            for (String s : signatures) {
                arr.pushString(s);
            }
            promise.resolve(arr);
        } catch (Exception e) {
            promise.reject(e);
        }
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void getPhoneNumber(final Promise promise) {
        final ReactApplicationContext context = getReactApplicationContext();
        final Activity activity = getCurrentActivity();
        final ActivityEventListener eventListener = mSmsTelHelper.getActivityEventListener();

        context.addActivityEventListener(eventListener);

        mSmsTelHelper.setListener(new SmsTelHelper.Listener() {
            @Override
            public void phoneNumberResultReceived() {
                context.removeActivityEventListener(eventListener);
            }
        });

        mSmsTelHelper.requestPhoneNumber(context, activity, promise);
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void startSmsRetriever(final Promise promise) {
        mSmsCodeHelper.startRetriever(promise);
    }

    @SuppressWarnings("unused")
    @ReactMethod
    public void getServiceSupport(final Promise promise) {
        if (!SmsGPSHelper.isAvailable(mContext)||!SmsGPSHelper.hasSupportedVersion(mContext)) {
            promise.reject(SmsGPSHelper.UNAVAILABLE_ERROR_TYPE, SmsGPSHelper.UNAVAILABLE_ERROR_MESSAGE);
        }else {
            promise.resolve(true);
        }
    }

}

