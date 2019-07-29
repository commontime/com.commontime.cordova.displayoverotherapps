package com.commontime.plugin;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayOverAppsEnableActivity extends Activity
{
    private final String ENABLE_MESSAGE = "For the %s app to function correctly, please enable the <b><i>Display over other apps</i></b> setting for this app.";
    private final String ENABLE_BTN_TXT = "Enable Setting";

    private boolean checkEnabledOnResume = false;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        actionBar.setDisplayHomeAsUpEnabled(false);

        Bundle extras = getIntent().getExtras();

        setContentView(generateView(
                extras.getString(DisplayOverOtherApps.BACKGROUND_COLOR_KEY),
                extras.getString(DisplayOverOtherApps.TEXT_COLOR_KEY),
                extras.getString(DisplayOverOtherApps.BUTTON_BACKGROUND_COLOR_KEY),
                extras.getString(DisplayOverOtherApps.BUTTON_TEXT_COLOR_KEY)));

        setResult(1);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if (!checkEnabledOnResume) return;

        if (isCanDrawOverlaysEnabled()) {
            restartApp();
        }

        checkEnabledOnResume = false;
    }

    private View generateView(String bgColor, String txtColor, String buttonBgColor, String buttonTxtColor)
    {
        LinearLayout ll = new LinearLayout(getApplicationContext());

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setGravity(Gravity.CENTER_HORIZONTAL);
        ll.setPadding(60,80,60,80);
        if (!TextUtils.isEmpty(bgColor))
        {
            try {
                ll.setBackgroundColor(Color.parseColor(bgColor));
            } catch (Exception e) { e.printStackTrace(); }
        }

        ImageView logoImageView = new ImageView(getApplicationContext());

        LinearLayout.LayoutParams lp_iv = new LinearLayout.LayoutParams(180, 180);
        lp_iv.setMargins(0, 60, 0, 50);
        logoImageView.setLayoutParams(lp_iv);

        try {
            Drawable icon = getPackageManager().getApplicationIcon(getPackageName());
            logoImageView.setImageDrawable(icon);
        } catch (Exception e) { e.printStackTrace(); }

        ll.addView(logoImageView);

        TextView descriptionTextView = new TextView(getApplicationContext());

        descriptionTextView.setText(Html.fromHtml(String.format(ENABLE_MESSAGE, getApplicationName())));
        descriptionTextView.setTextSize(19);
        descriptionTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        if (!TextUtils.isEmpty(txtColor))
        {
            try {
                descriptionTextView.setTextColor(Color.parseColor(txtColor));
            } catch (Exception e) { e.printStackTrace(); }
        }

        LinearLayout.LayoutParams lp_tv = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp_tv.setMargins(0,0,0,50);
        descriptionTextView.setLayoutParams(lp_tv);

        ll.addView(descriptionTextView);

        LinearLayout llButtonContainer = new LinearLayout(getApplicationContext());

        llButtonContainer.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);

        LinearLayout.LayoutParams lp_buttonContainer = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        llButtonContainer.setLayoutParams(lp_buttonContainer);

        Button enableButton = new Button(getApplicationContext());

        LinearLayout.LayoutParams lp_button = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                180
        );
        lp_button.setMargins(50,0,50,0);

        enableButton.setLayoutParams(lp_button);

        enableButton.setText(ENABLE_BTN_TXT);
        enableButton.setTextSize(18);

        try {
            enableButton.setTextColor(Color.parseColor(buttonTxtColor));
        } catch (Exception e) { e.printStackTrace(); }

        try {
            Drawable buttonDrawable = enableButton.getBackground();
            buttonDrawable = DrawableCompat.wrap(buttonDrawable);
            //the color is a direct color int and not a color resource
            DrawableCompat.setTint(buttonDrawable, Color.parseColor(buttonBgColor));
            enableButton.setBackground(buttonDrawable);
        } catch (Exception e) { e.printStackTrace(); }

        enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingsPage();
            }
        });

        llButtonContainer.addView(enableButton);

        ll.addView(llButtonContainer);

        return ll;
    }

    private void openSettingsPage()
    {
        if (!isCanDrawOverlaysEnabled()) {
            Intent permissionIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(permissionIntent, 1);
            checkEnabledOnResume = true;
        }
    }

    @TargetApi(23)
    private boolean isCanDrawOverlaysEnabled() {
        return Settings.canDrawOverlays(getApplicationContext());
    }

    private void restartApp()
    {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        startActivity(mainIntent);
        finish();
    }

    public String getApplicationName() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : getString(stringId);
    }
}