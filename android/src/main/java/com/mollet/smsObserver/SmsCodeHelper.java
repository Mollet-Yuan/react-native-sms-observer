package com.mollet.smsObserver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.Callable;

final class SmsCodeHelper {

    private static final String TASK_FAILURE_ERROR_TYPE = "SmsRetriever task err:";
    private static final String PERMISSIONS_ERRO = "Permissions request err!";

    private final ReactApplicationContext mContext;

    private BroadcastReceiver mReceiver;
    private Promise mPromise;

    SmsCodeHelper(@NonNull final ReactApplicationContext context) {
        mContext = context;
    }

    void startRetriever(final Promise promise) {
        mPromise = promise;
        String[]  permissions = new String[]{
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
        };

        if (!SmsGPSHelper.isAvailable(mContext)) {
            promiseReject(SmsGPSHelper.UNAVAILABLE_ERROR_TYPE, SmsGPSHelper.UNAVAILABLE_ERROR_MESSAGE);
            return;
        }

        if (!SmsGPSHelper.hasSupportedVersion(mContext)) {
            promiseReject(SmsGPSHelper.UNSUPORTED_VERSION_ERROR_TYPE, SmsGPSHelper.UNSUPORTED_VERSION_ERROR_MESSAGE);
            return;
        }

        PermissionHelper.permissionsCheck( mContext.getCurrentActivity(), permissions, new Callback() {
            @Override
            public void invoke(Object... args) {
                String res = (String) args[0];
                if (res == PermissionHelper.PERMISSIONS_MISSING ) {
                    promiseReject(PermissionHelper.PERMISSIONS_MISSING,PERMISSIONS_ERRO);
                } else  if (res == PermissionHelper.PERMISSION_GRANTED){

                    final SmsRetrieverClient client = SmsRetriever.getClient(mContext);
                    final Task<Void> task = client.startSmsRetriever();
                    task.addOnSuccessListener(mOnSuccessListener);
                    task.addOnFailureListener(mOnFailureListener);
                }
            }
        });



    }

    //region - Listeners
    private final OnSuccessListener<Void> mOnSuccessListener = new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            final boolean registered = tryToRegisterReceiver();
            promiseResolve(registered);
        }
    };

    private final OnFailureListener mOnFailureListener = new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            unregisterReceiverIfNeeded();
            promiseReject(TASK_FAILURE_ERROR_TYPE,  e.toString());
        }
    };

    private boolean tryToRegisterReceiver() {
        mReceiver = new SmsCodeReceiver(mContext);

        final IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);

        try {
            mContext.registerReceiver(mReceiver, intentFilter);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void unregisterReceiverIfNeeded() {
        if (mReceiver == null) {
            return;
        }
        try {
            mContext.unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void promiseResolve(@NonNull final Object value) {
        if (mPromise != null) {
            mPromise.resolve(value);
            mPromise = null;
        }
    }

    private void promiseReject(@NonNull final String type, @NonNull final String  msg) {
        if (mPromise != null) {
            mPromise.reject(type, msg);
            mPromise = null;
        }
    }

}
