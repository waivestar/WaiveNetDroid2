package au.com.waivestar.waivenetdroid;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;



public class WaiveNetWeb extends Activity {


    WebView wv;
    String user;
    String pass;
    public String domain = "www.waivenet.com.au";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
// Hide both the navigation bar and the status bar.
// SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
// a general rule, you should design your app to hide the status bar whenever you
// hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);



        setContentView(R.layout.activity_waive_net_wv);




        wv = (WebView)findViewById (R.id.webView);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new JavaScriptInterface(this), "Android");
        // Use subclassed WebViewClient to intercept hybrid native calls
        wv.setWebViewClient(new MyWebViewClient(this));
        Intent intent = getIntent();
        user = intent.getStringExtra("user");
        pass = intent.getStringExtra("pass");
        wv.loadUrl("https://" + domain + "/logon.aspx?user=" + user + "&pass=" + pass);
        
    }

    public void LoadLogin(boolean timeout){
        Intent activity2 = new Intent(this, WaiveNetWV.class);
        activity2.putExtra("timeout", timeout);
        startActivity(activity2);
    }

    public void LoadOther(String url){
        try{
            Uri uri = android.net.Uri.parse(url);
            Intent intent = new Intent (Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No application can handle this request. Please install a web browser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public class MyWebViewClient extends WebViewClient {
        WaiveNetWeb _parent;

        public MyWebViewClient(WaiveNetWeb context) {
            _parent = context;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //Console.WriteLine(url);
            if (url.contains("/logon.aspx")) {//session times out log back in
                _parent.LoadLogin(url.contains("?timeout"));
                return true;
            }
            if (!url.contains(_parent.domain) || url.contains(".pdf") || url.contains(".docx") || url.contains(".xls") || url.contains(".xlsx") || url.contains(".csv")) {
                _parent.LoadOther(url);
                return false;
            }
            if (url.contains("?")) url=url+"&android=true";
            else url=url+"?android=true";

            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        return;
    }
    public class JavaScriptInterface {
        Context mContext;

        /** Instantiate the interface and set the context */
        JavaScriptInterface(Context c) {
            mContext = c;
        }

        /** Show a toast from the web page */
        @android.webkit.JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

}
