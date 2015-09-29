package au.com.waivestar.waivenetdroid;

import au.com.waivestar.waivenetdroid.util.SystemUiHider;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
//import au.com.waivestar.waivenetdroid.VideoEnabledWebView.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class WaiveNetWV extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 30000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = false;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private SharedPreferences prefs;
    private EditText u;
    private EditText p;
    private CheckBox cb;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        prefs = this.getSharedPreferences("WNMyPrefs", MODE_PRIVATE);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (TOGGLE_ON_CLICK) {
                //    mSystemUiHider.toggle();
                //} else {
                    mSystemUiHider.show();
                //}
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        u = (EditText) findViewById(R.id.txtUser);
        p = (EditText) findViewById(R.id.txtPass);
        cb = (CheckBox) findViewById(R.id.checkBox1);
        btn = (Button) findViewById(R.id.btn_login);

        //btn.setOnTouchListener(mDelayHideTouchListener);
        //u.setOnTouchListener(mDelayHideTouchListener);
        //p.setOnTouchListener(mDelayHideTouchListener);
        //cb.setOnTouchListener(mDelayHideTouchListener);
        String usrn = prefs.getString("username",null);
        String passw = prefs.getString("password",null);
        Boolean save = prefs.getBoolean("SaveLogin",false);
        if (save) {
            u.setText(usrn);
            p.setText(passw);
            cb.setChecked(save);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (u.getText().length() == 0 || p.getText().length() == 0) {
                    Toast.makeText(v.getContext().getApplicationContext(), "Please enter a username and password.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (cb.isChecked()) {
                    Editor edit = prefs.edit();
                    edit.clear();
                    edit.putString("username", u.getText().toString().trim());
                    edit.putString("password", p.getText().toString().trim());
                    edit.putBoolean("SaveLogin", cb.isChecked());
                    edit.commit();
                }

                Intent intnt = new Intent(v.getContext(), WaiveNetWeb.class);
                intnt.putExtra("user", u.getText().toString().trim());
                intnt.putExtra("pass", p.getText().toString().trim());
                startActivity(intnt);
                finish();

            }
        });


        //final WebView webView = (WebView)findViewById(R.id.webView2);
        //webView.getSettings().setJavaScriptEnabled(true);
        ///webView.loadUrl("https://www.waivenet.com.au/RGA/Video/WaivestarVideo.html");

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
