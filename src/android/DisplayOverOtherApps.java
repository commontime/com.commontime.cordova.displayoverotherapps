package com.commontime.plugin;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class DisplayOverOtherApps extends CordovaPlugin {

    private final int REQUEST_CODE = 6745;

    private static final String BACKGROUND_COLOR_PLUGIN_KEY = "displayOverOtherAppsBackgroundColor";
    private static final String TEXT_COLOR_PLUGIN_KEY = "displayOverOtherAppsTextColor";
    private static final String BUTTON_TEXT_COLOR_PLUGIN_KEY = "displayOverOtherAppsButtonTextColor";
    private static final String BUTTON_BACKGROUND_COLOR_PLUGIN_KEY = "displayOverOtherAppsButtonBackgroundColor";

    public static final String BACKGROUND_COLOR_KEY = "bgColor";
    public static final String TEXT_COLOR_KEY = "txtColor";
    public static final String BUTTON_BACKGROUND_COLOR_KEY = "buttonBgColor";
    public static final String BUTTON_TEXT_COLOR_KEY = "btnTxtColor";

    @Override
    protected void pluginInitialize()
    {
        super.pluginInitialize();
    }

    @Override
    public boolean execute(String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException
    {
        if (action.equals("enabled"))
        {
            PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, false);

            if (Build.VERSION.SDK_INT >= 23) {
                pluginResult = new PluginResult(PluginResult.Status.OK, Settings.canDrawOverlays(cordova.getActivity()));
            }
            callbackContext.sendPluginResult(pluginResult);
        }
        return true;
    }

    @Override
    public void onResume(boolean multitasking)
    {
        super.onResume(multitasking);

        if (Build.VERSION.SDK_INT >= 29)
        {
            try
            {
                ApplicationInfo ai = cordova.getActivity().getPackageManager().getApplicationInfo(cordova.getActivity().getPackageName(), PackageManager.GET_META_DATA);
                Bundle aBundle = ai.metaData;

                String bgColor = String.format("#%s", aBundle.getString(BACKGROUND_COLOR_PLUGIN_KEY));
                String txtColor = String.format("#%s", aBundle.getString(TEXT_COLOR_PLUGIN_KEY));
                String buttonBgColor = String.format("#%s", aBundle.getString(BUTTON_BACKGROUND_COLOR_PLUGIN_KEY));
                String buttonTxtColor = String.format("#%s", aBundle.getString(BUTTON_TEXT_COLOR_PLUGIN_KEY));

                if (!Settings.canDrawOverlays(cordova.getActivity())) {
                    Intent i = new Intent(cordova.getActivity(), DisplayOverAppsEnableActivity.class);
                    i.putExtra(BACKGROUND_COLOR_KEY, bgColor);
                    i.putExtra(TEXT_COLOR_KEY, txtColor);
                    i.putExtra(BUTTON_BACKGROUND_COLOR_KEY, buttonBgColor);
                    i.putExtra(BUTTON_TEXT_COLOR_KEY, buttonTxtColor);
                    cordova.startActivityForResult(this, i, REQUEST_CODE);
                }
            }
            catch (PackageManager.NameNotFoundException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == 1) cordova.getActivity().finish();
        }
    }
}