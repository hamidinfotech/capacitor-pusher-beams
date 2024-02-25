package com.practera.capacitor.pusherbeams;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.pusher.pushnotifications.BeamsCallback;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PushNotificationsInstance;
import com.pusher.pushnotifications.PusherCallbackError;
import com.pusher.pushnotifications.auth.AuthData;
import com.pusher.pushnotifications.auth.AuthDataGetter;
import com.pusher.pushnotifications.auth.BeamsTokenProvider;
import com.pusher.pushnotifications.internal.InstanceDeviceStateStore;

import org.json.JSONException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@CapacitorPlugin(name = "PusherBeams")
public class PusherBeamsPlugin extends Plugin {
    private PusherBeams implementation = new PusherBeams();

    String packageName;

    @PluginMethod
    public void start(PluginCall call) {
        String instanceId = call.getString("instanceId");
        PushNotifications.start(getActivity().getApplicationContext(), instanceId);
        call.resolve();
    }

    @PluginMethod
    public void getDeviceId(PluginCall call) {
        String instanceId = call.getString("instanceId");
        InstanceDeviceStateStore instanceDeviceStateStore = new InstanceDeviceStateStore(getActivity().getApplicationContext(), instanceId);
        JSObject ret = new JSObject();
        ret.put("deviceId", instanceDeviceStateStore.getDeviceId());
        call.resolve(ret);
    }

    @PluginMethod
    public void addDeviceInterest(PluginCall call) {
        String interest = call.getString("interest");
        PushNotifications.addDeviceInterest(interest);
        JSObject ret = new JSObject();
        ret.put("message", "Interest Added");
        call.resolve(ret);
    }

    @PluginMethod
    public void removeDeviceInterest(PluginCall call) {
        String interest = call.getString("interest");
        PushNotifications.removeDeviceInterest(interest);
        call.resolve();
    }

    @PluginMethod
    public void getDeviceInterests(PluginCall call) {
        Set<String> interests = PushNotifications.getDeviceInterests();
        JSObject ret = new JSObject();
        ret.put("interests", interests);
        call.resolve(ret);
    }

    @PluginMethod
    public void setDeviceInterests(PluginCall call) throws JSONException {
        Set<String> interests = new HashSet<>(call.getArray("interests").toList());
        PushNotifications.setDeviceInterests(interests);

        JSObject ret = new JSObject();
        Set<String> registered = PushNotifications.getDeviceInterests();
        ret.put("interests", registered);
        ret.put("success", true);
        call.resolve(ret);
    }

    @PluginMethod
    public void clearDeviceInterests(PluginCall call) {
        PushNotifications.clearDeviceInterests();
        call.resolve();
    }

    @PluginMethod
    public void setUserID(final PluginCall call) {
        String beamsAuthURl = call.getString("beamsAuthURL", "");
        String userID = call.getString("userID");
        JSObject headers = call.getObject("headers", new JSObject());

        final HashMap headersHashMap = convertToHashMap(headers);

        BeamsTokenProvider beamsTokenProvider = new BeamsTokenProvider(
            beamsAuthURl,
            new AuthDataGetter() {
                @Override
                public AuthData getAuthData() {
                    HashMap<String, String> queryParams = new HashMap<>();
                    return new AuthData(
                        headersHashMap,
                        queryParams
                    );
                }
            }
        );

        PushNotifications.setUserId(userID, beamsTokenProvider, new BeamsCallback<Void, PusherCallbackError>() {
            @Override
            public void onSuccess(Void... values) {
                JSObject ret = new JSObject();
                Log.i("PusherBeams", "Successfully authenticated with Pusher Beams");

                ret.put("message", "Successfully authenticated with Pusher Beams");
                ret.put("success", true);
                ret.put("raw", values);
                call.resolve(ret);
            }

            @Override
            public void onFailure(PusherCallbackError error) {
                JSObject ret = new JSObject();
                Log.i("PusherBeamsError", String.valueOf(error));
                Log.i("PusherBeams", "Pusher Beams authentication failed: " + error.getMessage());

                ret.put("message", "Pusher Beams authentication failed: " + error.getMessage());
                ret.put("success", false);
                ret.put("raw", error);
                call.reject(error.getMessage());
            }
        });
    }

    private static HashMap<String, String> convertToHashMap(JSObject headers) {
        HashMap<String, String> result = new HashMap<String, String>();
        Iterator<String> keys = headers.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            String value = headers.getString(key);
            result.put(key, value);
        }
        return result;
    }

    @PluginMethod
    public void clearAllState(PluginCall call) {
        PushNotifications.clearAllState();
        JSObject ret = new JSObject();
        ret.put("success", false);
        call.resolve(ret);
    }

    @PluginMethod
    public void stop(PluginCall call) {
        PushNotifications.stop();
        JSObject ret = new JSObject();
        ret.put("success", false);
        call.resolve(ret);
    }

    @PluginMethod
    private void setPackageName(PluginCall call) {
        this.packageName = call.getString("packageName");
        call.resolve();
    }

    private String getPackageName() {
        if (this.packageName != null && !this.packageName.isEmpty()) {
            return this.packageName;
        }

        return "com.practera.appv2";
    }

    @Override
    protected void handleOnNewIntent(Intent data) {
        super.handleOnNewIntent(data);
        Bundle bundle = data.getExtras();
        if (bundle != null && bundle.containsKey("pusher_notification")) {
            JSObject notificationJson = new JSObject();
            JSObject dataObject = new JSObject();
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                String valueStr = (value != null) ? value.toString() : null;
                dataObject.put(key, valueStr);
            }
            notificationJson.put("data", dataObject);
            JSObject actionJson = new JSObject();
            actionJson.put("actionId", "tap");
            actionJson.put("notification", notificationJson);
            notifyListeners("pusherNotificationActionPerformed", actionJson, true);
        }
    }
}
