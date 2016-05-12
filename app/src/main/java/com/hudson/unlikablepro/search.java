package com.hudson.unlikablepro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class search {
    List<String> list;
    ListView lstview;
    final WebView searcher;
    Context context;
    Activity activity;
    ProgressDialog progress;
    public search(Activity act, Context cont, final WebView webView) {
        activity = act;
        context = cont;
        searcher = new WebView(context);
        String newUA= "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.4) Gecko/20100101 Firefox/4.0";
        searcher.getSettings().setUserAgentString(newUA);
        searcher.getSettings().setJavaScriptEnabled(true);
        searcher.addJavascriptInterface(new MyJavaScriptInterface(context), "HtmlViewer");
        searcher.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Toast.makeText(context, "Unable to perform search. Please try again.", Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // TODO Auto-generated method stub
                Log.d("Hudson", "Finished Loading Page");
                searcher.loadUrl("javascript:window.HtmlViewer.showHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
        list = new ArrayList<String>();

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final SearchView userInput = (SearchView) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        userInput.setQueryHint("Enter a Username");
        userInput.setIconified(false);
        userInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {
                //Log.e("onQueryTextChange", "called");
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                progress = new ProgressDialog(context);
                progress.setTitle("Loading");
                progress.setMessage("Wait while loading...");
                progress.show();
                progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        searcher.stopLoading();
                    }
                });
                searcher.loadUrl("http://websta.me/search/" + query);
                return false;
            }

        });
        lstview = (ListView) promptsView.findViewById(R.id.list);


        // set dialog message
        alertDialogBuilder.setCancelable(true);

        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();
        lstview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = ((TextView)view).getText().toString();
                webView.loadUrl("http://instagram.com/" + item.substring(0, item.indexOf("\n")));
                alertDialog.dismiss();
            }
        });
        // show it
        alertDialog.show();


    }
    class MyJavaScriptInterface {

        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }
        @JavascriptInterface
        public void showHTML(String html) {
            Document doc = Jsoup.parse(html);
            List<String> matches = new ArrayList<String>();
            for(int i = 0; i < doc.getElementsByClass("fullname").size(); i = i + 1){
                String str = doc.getElementsByClass("fullname").get(i).attr("href").substring(3) + "\n" + doc.getElementsByClass("fullname").get(i).html();
                String pattern = "(<span).*(span>)";
                str = str.replaceAll(pattern, "");
                Log.d("Hudson", str);
                matches.add(str);
            }
            final ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1, matches);
            activity.runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            lstview.setAdapter(arrayAdapter);
                            progress.dismiss();
                        }
                    }


            );


        }

    }
}
