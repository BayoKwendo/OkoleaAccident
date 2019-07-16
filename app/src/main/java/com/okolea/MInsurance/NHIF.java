package com.okolea.MInsurance;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.okolea.R;

import java.lang.reflect.Method;


public class NHIF extends AppCompatActivity implements View.OnClickListener  {
    WebView mWebView;
    private LinearLayout container;
    Animation animSideDown;
    private Button nextButton, closeButton;
    private EditText findBox;
    /** Called when the activity is first created. */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cervical);
        Toolbar toolbar = findViewById(R.id.toolbar);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mWebView.setVisibility(View.VISIBLE);
                mWebView.startAnimation(animSideDown);
            }
        }, 00);

        // set animation listener
        TextView tool = (TextView) toolbar.findViewById(R.id.title);
        tool.setText("NHIF (THE SAVIOUR)");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
        mWebView = findViewById(R.id.webview);
        nextButton = findViewById(R.id.nextButto);
        closeButton = findViewById(R.id.closeButto);
        findBox = findViewById(R.id.findBo);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls( true );
        mWebView.getSettings().setDisplayZoomControls( false);
        mWebView.loadUrl("file:///android_asset/cervical/medical.php");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(i);
                return true;
            }

        });
        findBox.setSingleLine(true);
        findBox.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER))) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mWebView.findAllAsync(findBox.getText().toString());
                    mWebView.setFindListener(new WebView.FindListener() {
                        @Override
                        public void onFindResultReceived(int activeMatchOrdinal, int numberOfMatches, boolean isDoneCounting) {
                            if (numberOfMatches == 0) {
                                // Creating alert Dialog with one Button+
                                AlertDialog alertDialog = new AlertDialog.Builder(
                                        NHIF.this).create();
                                // Setting Dialog Message
                                alertDialog.setTitle(Html.fromHtml("<font color='#e20719'> No result found</font>"));
                                // Setting Icon to Dialog
                                // Setting OK Button
                                alertDialog.setButton(Html.fromHtml("<font color='#1d7a10'> OK</font>"),
                                        new DialogInterface.OnClickListener() {

                                            public void onClick(DialogInterface dialog,
                                                                int which) {
                                                // Write your code here to execute after dialog
                                                // closed
                                                dialog.dismiss();
                                            }
                                        });
                                // Showing Alert Message
                                alertDialog.show();
                                mWebView.clearMatches();
                                findBox.setText("");
                                hideSoftKeyboard();
                                container.setVisibility(RelativeLayout.GONE);
                            }
                        }

                    });
                }
                else
                {
                    // Creating alert Dialog with one Button
                    AlertDialog alertDialog = new AlertDialog.Builder(
                            NHIF.this).create();
                    // Setting Dialog Message
                    alertDialog.setMessage(Html.fromHtml("<font color='#e20719'>Search can not be performed!! kindly upgrade your android version/font>"));
                    // Setting Icon to Dialog
                    // Setting OK Button
                    alertDialog.setButton(Html.fromHtml("<font color='#1d7a10'> OK</font>"),
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Write your code here to execute after dialog
                                    // closed
                                    dialog.dismiss();
                                }
                            });
                    // Showing Alert Message
                    alertDialog.show();
                    mWebView.clearMatches();
                    findBox.setText("");
                    container.setVisibility(RelativeLayout.GONE);
                }
                try
                {
                    // Can't use getMethod() as it's a private method
                    for (Method m : WebView.class.getDeclaredMethods())
                    {
                        if (m.getName().equals("setFindIsUp"))
                        {
                            m.setAccessible(true);
                            m.invoke(mWebView, true);
                            break;
                        }
                    }
                }
                catch (Exception ignored)
                {
                }
                finally
                {
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    // check if no view has focus:
                    View vv = getCurrentFocus();
                    if (vv != null)
                    {
                        assert inputManager != null;
                        inputManager.hideSoftInputFromWindow(v.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                    }
                }
            }
            return false;
        });
        nextButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.main, menu);
//        return true;
//    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        return true;
    }
//    public boolean onOptionsItemSelected(MenuItem item)
//    {
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_shar) {
//            search();
//        }
//        if (id == android.R.id.home){
//            onBackPressed();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
    public void search()
    {
        container = findViewById(R.id.layoutId);
        if (container.getVisibility() == RelativeLayout.GONE)
        {
            container.setVisibility(RelativeLayout.VISIBLE);
            findBox.requestFocus();
            showKeyboard();
        }

    }
    public  void showKeyboard() {
        findBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public  void hideSoftKeyboard() {
        findBox.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(findBox.getWindowToken(), 0);

    }

    @Override
    public void onClick(View v)
    {
        if (v == nextButton)
        {
            mWebView.findNext(true);
        }
        else if (v == closeButton)
        {
            mWebView.clearMatches();
            findBox.setText("");
            hideSoftKeyboard();
            container.setVisibility(RelativeLayout.GONE);
        }
    }


}

