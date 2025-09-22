package com.example.mentesbrilhantes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private WebView webView;
    private Button btnFechar;
    private String videoId;
    private String titulo;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // **CONFIGURAÇÃO FULLSCREEN**
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );

        setContentView(R.layout.activity_video_player);

        // Receber dados do Intent
        videoId = getIntent().getStringExtra("VIDEO_ID");
        titulo = getIntent().getStringExtra("TITULO");

        Log.d("VideoPlayer", "VIDEO_ID recebido: " + videoId);
        Log.d("VideoPlayer", "TITULO recebido: " + titulo);

        inicializarComponentes();
        configurarWebView();
        carregarVideo();
    }

    private void inicializarComponentes() {
        webView = findViewById(R.id.webview_video);
        btnFechar = findViewById(R.id.btn_fechar);

        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configurarWebView() {
        WebSettings webSettings = webView.getSettings();

        // ✅ CONFIGURAÇÕES ESSENCIAIS PARA YOUTUBE (API 33+ COMPATÍVEL)
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);

        // ✅ CACHE MODERNO (SUBSTITUI setAppCacheEnabled)
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // ✅ CONFIGURAÇÕES DE LAYOUT
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);

        // ✅ CONFIGURAÇÕES DE MÍDIA
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        // ✅ USER AGENT PARA YOUTUBE
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; SM-A505FN) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36");

        // ✅ MIXED CONTENT (API 21+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        // ✅ WEBVIEW CLIENT
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d("VideoPlayer", "Página iniciada: " + url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("VideoPlayer", "Página carregada: " + url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("VideoPlayer", "Erro ao carregar: " + description + " - URL: " + failingUrl);
            }
        });

        // ✅ WEBCHROME CLIENT PARA FULLSCREEN
        webView.setWebChromeClient(new WebChromeClient() {
            private View customView;
            private WebChromeClient.CustomViewCallback customViewCallback;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (customView != null) {
                    onHideCustomView();
                    return;
                }

                customView = view;
                customViewCallback = callback;

                ViewGroup parent = findViewById(R.id.main_container);
                parent.addView(customView, new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

                findViewById(R.id.controls_container).setVisibility(View.GONE);
                Log.d("VideoPlayer", "Fullscreen ativado");
            }

            @Override
            public void onHideCustomView() {
                ViewGroup parent = findViewById(R.id.main_container);
                if (customView != null) {
                    parent.removeView(customView);
                    customView = null;
                }

                if (customViewCallback != null) {
                    customViewCallback.onCustomViewHidden();
                    customViewCallback = null;
                }

                findViewById(R.id.controls_container).setVisibility(View.VISIBLE);
                Log.d("VideoPlayer", "Fullscreen desativado");
            }
        });
    }

    private void carregarVideo() {
        // ✅ HTML OTIMIZADO PARA YOUTUBE
        String videoHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { background-color: #000; overflow: hidden; }\n" +
                "        .video-container {\n" +
                "            position: relative;\n" +
                "            width: 100vw;\n" +
                "            height: 100vh;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "        }\n" +
                "        iframe {\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            border: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"video-container\">\n" +
                "        <iframe\n" +
                "            src=\"https://www.youtube.com/embed/" + videoId + "?" +
                "autoplay=1&" +
                "controls=1&" +
                "showinfo=0&" +
                "rel=0&" +
                "iv_load_policy=3&" +
                "modestbranding=1&" +
                "playsinline=1&" +
                "fs=1&" +
                "enablejsapi=1&" +
                "origin=" + getPackageName() + "\"\n" +
                "            frameborder=\"0\"\n" +
                "            allowfullscreen\n" +
                "            allow=\"autoplay; encrypted-media; gyroscope; picture-in-picture; accelerometer\">\n" +
                "        </iframe>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";

        Log.d("VideoPlayer", "Carregando HTML do vídeo...");
        webView.loadDataWithBaseURL("https://www.youtube.com", videoHtml, "text/html", "UTF-8", null);
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
