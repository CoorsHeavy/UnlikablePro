package com.hudson.unlikablepro;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import java.net.CookieManager;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity{
    WebView webview;
    Activity activity;
    ImageButton refresh;
    ImageButton hearts;
    ImageButton search;
    Button pro;
    ProgressBar Pbar;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        context = this;
        setContentView(R.layout.browser);
        CookieManager cm = new CookieManager();
        Pbar = (ProgressBar) findViewById(R.id.pB1);
        refresh = (ImageButton) findViewById(R.id.Refresh);
        hearts = (ImageButton) findViewById(R.id.hearts);
        search = (ImageButton) findViewById(R.id.search);
        webview = (WebView) findViewById(R.id.webView);
        webview.setWebChromeClient(new HelloWebViewClient());
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                Log.d("Hudson", "started loading");


            }
            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                Log.d("Hudson", "Finished Loading Page");
                injectJavaScript(webview);
                injectJavaScriptLog(webview);

            }
        });
        webview.getSettings().setJavaScriptEnabled(true);
        if (android.os.Build.VERSION.SDK_INT>=21)
        webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        //webview.setAcceptThirdPartyCookies(webview, true);
        webview.loadUrl("https://instagram.com/accounts/login/");
        webview.addJavascriptInterface(new WebAppInterface(this), "Android");
        new AlertDialog.Builder(this)
                .setTitle("Instructions")
                .setMessage("Log into Instagram and browse knowing that doubling clicking is disabled. If there is a heart button on a post then something went wrong and the post is likable. \n Notice the buttons at the bottom of the screen.\n The middle button with the upside down heart will make sure that the ability to like posts has been removed from the loaded posts. You probably will not need this as the operation is run twice a second but please press it if you see any like buttons that aren't suppose to be there. \n The left button will refresh the page. \n Known Bugs: The top right menu button may not be as responsive as it should be.")
                .setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
        refresh.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.reload();
            }
        });
        hearts.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                injectJavaScriptHearts(webview);
            }
        });
        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                new search(MainActivity.this, MainActivity.this, webview);
            }
        });
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class HelloWebViewClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int progress)
        {
            if(progress < 100 && Pbar.getVisibility() == ProgressBar.GONE){
                Pbar.setVisibility(ProgressBar.VISIBLE);
            }
            Pbar.setProgress(progress);
            if(progress == 100) {
                Pbar.setVisibility(ProgressBar.GONE);
            }
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {

            return super.onConsoleMessage(consoleMessage);
        }

        public void onReceivedError(WebView view, int errorCod,String description, String failingUrl) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setTitle("Make your selection");
            builder.setMessage("Your Internet Connection May not be active Or " + description);
            builder.show();
            }
        }
    public void injectJavaScript(WebView view) {

                String javascript =
                        "function remover() {\n" +
                                "    if(document.getElementsByClassName('mhPhotos').length == 1){\n" +
                                "Android.login();\n" +
                                "console.log('hudson login');\n" +
                                "}\n" +
                                "document.getElementsByClassName(\"top-bar-right account-state\")[0].addEventListener('click', function () {\n" +
                                "        Android.openDialog('<html>' + document.getElementsByClassName(\"Dropdown dOpen\")[0].innerHTML + '</html>');\n" +
                                "    }, false);" +
                                "" +
                                "        var elements = document.getElementsByClassName(\"mediaPhoto\");\n" +
                                "        var len = elements.length;\n" +
                                "        //console.log(len)\n" +
                                "        for (var i = 0; i < len; i++) {\n" +
                                "            elements[i].removeAttribute(\"data-reactid\");\n" +
                                "            var inner = elements[i].innerHTML;\n" +
                                "            var image = inner.substring(inner.indexOf('src=\"') + 5, inner.indexOf('\" class='));\n" +
                                "            if (image.indexOf('jpg') != -1) {\n" +
                                "                elements[i].innerHTML = '<img src=' + image + ' style=\"width:100%;height:100%\"></img>'\n" +
                                "                //console.log(elements[i].innerHTML);\n" +
                                "                console.log(image);\n" +
                                "            }\n" +
                                "        }\n" +
                                "        var elements = document.getElementsByClassName(\"timelineLikeButton\");\n" +
                                "        while (elements.length > 0) {\n" +
                                "            elements[0].parentNode.removeChild(elements[0]);\n" +
                                "        }\n" +

                                "}" +
                                "remover();" +
                                "var intervalID = window.setInterval(remover, 500);"
                        ;
        if (javascript == null || javascript.isEmpty()) {
            return;
        }

        // As of KitKat, evaluateJavascript(String javascript) should be used
        // over loadAd("javascript:(javaScriptMethod())")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            String js = "javascript:" + javascript;
            view.loadUrl(js);
        } else {
            // Returns the value of the executed JavaScript as a JSON string.
            view.evaluateJavascript(javascript, new ValueCallback<String>() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveValue(String stringCallback) {
                    // If you'd like any response from the JS to confirm the
                    // execution
                    Log.d("Hudson", "onRecievedValue");
                }
            });
        }
    }
    public void injectJavaScriptLog(WebView view) {

        String javascript =""

                ;
        if (javascript == null || javascript.isEmpty()) {
            return;
        }
    }
    public void injectJavaScriptHearts(WebView view) {

        String javascript =
                "function remover() {\n" +
                        "        var elements = document.getElementsByClassName(\"mediaPhoto\");\n" +
                        "        var len = elements.length;\n" +
                        "        //console.log(len)\n" +
                        "        for (var i = 0; i < len; i++) {\n" +
                        "            elements[i].removeAttribute(\"data-reactid\");\n" +
                        "            var inner = elements[i].innerHTML;\n" +
                        "            var image = inner.substring(inner.indexOf('src=\"') + 5, inner.indexOf('\" class='));\n" +
                        "            if (image.indexOf('jpg') != -1) {\n" +
                        "                elements[i].innerHTML = '<img src=' + image + ' style=\"width:100%;height:100%\"></img>'\n" +
                        "                //console.log(elements[i].innerHTML);\n" +
                        "                console.log(image);\n" +
                        "            }\n" +
                        "        }\n" +
                        "        var elements = document.getElementsByClassName(\"timelineLikeButton\");\n" +
                        "        while (elements.length > 0) {\n" +
                        "            elements[0].parentNode.removeChild(elements[0]);\n" +
                        "        }\n" +
                        "    }\n" +
                        "remover();"
                ;
        if (javascript == null || javascript.isEmpty()) {
            return;
        }

        // As of KitKat, evaluateJavascript(String javascript) should be used
        // over loadAd("javascript:(javaScriptMethod())")
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {

            String js = "javascript:" + javascript;
            view.loadUrl(js);
        } else {
            // Returns the value of the executed JavaScript as a JSON string.
            view.evaluateJavascript(javascript, new ValueCallback<String>() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onReceiveValue(String stringCallback) {
                    // If you'd like any response from the JS to confirm the
                    // execution
                    Log.d("Hudson", "onRecievedValue");
                }
            });
        }
    }
    AlertDialog dialog;
    public class WebAppInterface {

        /** Instantiate the interface and set the context */
        Context mContext;
        WebAppInterface(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void openDialog(String html) {
            String pre = html.substring(html.indexOf("href=\"")+6);
            String username = pre.substring(0,pre.indexOf("\""));
            Log.d("hudson",username);
            Log.d("hudson","called");
            final CharSequence[] items = {
                    "View Profile", "Edit Profile", "Badges", "Logout"
            };
            final CharSequence[] urls = {
                    "https://instagram.com"+username, "https://instagram.com/accounts/edit/", "https://instagram.com/accounts/badges/", "https://instagram.com/accounts/logout/"
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle("Make your selection");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, final int item) {
                    // Do something with the selection
                    webview.post(new Runnable() {
                        @Override
                        public void run() {
                            webview.loadUrl(urls[item].toString());
                        }
                    });
                    dialog.dismiss();
                }
            });
            if(dialog == null) {
                dialog = builder.create();
                dialog.show();
            }else {
                if (!dialog.isShowing()) {
                    dialog = builder.create();
                    dialog.show();
                }
            }
        }
        @JavascriptInterface
        public void login() {
            webview.post( new Runnable() {
                              @Override
                              public void run() {
                                  webview.loadUrl("https://instagram.com/accounts/login/");
                              }
                          });
    }}

}
class ImageDownloader extends AsyncTask<String, Integer, ArrayList> {

    @Override
    protected void onPreExecute(){
        //Setup is done here
    }

    @Override
    protected ArrayList doInBackground(String... params) {
        ArrayList<String> array = new ArrayList();
        array.add("Billy");
        return array;
    }

    @Override
    protected void onProgressUpdate(Integer... params){
        //Update a progress bar here, or ignore it, it's up to you
    }

    @Override
    protected void onPostExecute(ArrayList arrayList){

    }

    @Override
    protected void onCancelled(){
        // Handle what you want to do if you cancel this task
    }
}
