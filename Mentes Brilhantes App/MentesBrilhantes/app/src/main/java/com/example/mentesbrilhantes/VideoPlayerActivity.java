package com.example.mentesbrilhantes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {
    private WebView webView;
    private ImageButton btnSair, btnVoltarErro;
    private LinearLayout controlsContainer, noInternetScreen;
    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private String currentVideoId;
    private boolean isMonitoringNetwork = false;
    private static boolean isAnyButtonProcessing = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String videoId = getIntent().getStringExtra("VIDEO_ID");
        String titulo = getIntent().getStringExtra("TITULO");
        this.currentVideoId = videoId;

        Log.d("VideoPlayer", "VIDEO_ID recebido: " + videoId);
        Log.d("VideoPlayer", "TITULO recebido: " + titulo);

        configurarFullscreenImersivo();
        setContentView(R.layout.activity_video_player);

        inicializarComponentes();
        configurarBackButton();
        inicializarMonitoramentoRede();

        if (verificarConexaoInternet()) {
            Log.d("VideoPlayer", "Internet disponível");
            configurarOrientacaoLandscape();
            configurarWebView();
            carregarVideo(videoId);
            mostrarTelaVideo();
        } else {
            Log.d("VideoPlayer", "Sem internet");
            configurarOrientacaoPortrait();
            mostrarTelaErro();
            iniciarMonitoramentoRede();
        }
    }

    private void inicializarComponentes() {
        webView = findViewById(R.id.webview_video);
        btnSair = findViewById(R.id.btn_sair);
        controlsContainer = findViewById(R.id.controls_container);
        noInternetScreen = findViewById(R.id.no_internet_screen);
        btnVoltarErro = findViewById(R.id.btn_voltar_erro);
        configurarBotoes();
    }

    private void configurarFullscreenImersivo() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    private void configurarOrientacaoLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void configurarOrientacaoPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private boolean verificarConexaoInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    private void mostrarTelaVideo() {
        webView.setVisibility(View.VISIBLE);
        noInternetScreen.setVisibility(View.GONE);
        esconderControlesAutomaticamente();
    }

    private void mostrarTelaErro() {
        webView.setVisibility(View.GONE);
        noInternetScreen.setVisibility(View.VISIBLE);
        controlsContainer.setVisibility(View.GONE);
    }

    private void esconderControlesAutomaticamente() {
        if (controlsContainer != null) {
            controlsContainer.setVisibility(View.GONE);
        }
    }

    private void inicializarMonitoramentoRede() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    runOnUiThread(() -> {
                        if (noInternetScreen.getVisibility() == View.VISIBLE) {
                            tentarCarregarVideoAutomaticamente();
                        }
                    });
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    runOnUiThread(() -> {
                        if (webView.getVisibility() == View.VISIBLE) {
                            configurarOrientacaoPortrait();
                            mostrarTelaErro();
                            iniciarMonitoramentoRede();
                        }
                    });
                }

                @Override
                public void onCapabilitiesChanged(Network network, android.net.NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }
            };
        }
    }

    private void iniciarMonitoramentoRede() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null && networkCallback != null && !isMonitoringNetwork) {
            try {
                NetworkRequest request = new NetworkRequest.Builder().build();
                connectivityManager.registerNetworkCallback(request, networkCallback);
                isMonitoringNetwork = true;
            } catch (Exception e) {
                Log.e("VideoPlayer", "Erro ao registrar NetworkCallback: " + e.getMessage());
            }
        }
    }

    private void pararMonitoramentoRede() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null && networkCallback != null && isMonitoringNetwork) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                isMonitoringNetwork = false;
            } catch (Exception e) {
                Log.e("VideoPlayer", "Erro ao desregistrar NetworkCallback: " + e.getMessage());
            }
        }
    }

    private void tentarCarregarVideoAutomaticamente() {
        if (verificarConexaoInternet()) {
            String videoId = getIntent().getStringExtra("VIDEO_ID");
            pararMonitoramentoRede();
            configurarOrientacaoLandscape();
            configurarWebView();
            carregarVideo(videoId);
            mostrarTelaVideo();
        }
    }

    private void configurarBotoes() {
        configurarBotaoProtegido(btnSair, () -> {
            if (webView != null) {
                webView.loadUrl("about:blank");
            }
            finish();
        });

        configurarBotaoProtegido(btnVoltarErro, this::finish);
        configurarDoubleTapVideo();
    }

    private void configurarDoubleTapVideo() {
        webView.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                if (webView.getVisibility() != View.VISIBLE) return;

                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 300) {
                    if (controlsContainer.getVisibility() == View.VISIBLE) {
                        controlsContainer.setVisibility(View.GONE);
                    } else {
                        controlsContainer.setVisibility(View.VISIBLE);
                        controlsContainer.postDelayed(() -> {
                            if (controlsContainer.getVisibility() == View.VISIBLE) {
                                controlsContainer.setVisibility(View.GONE);
                            }
                        }, 4000);
                    }
                }
                lastClickTime = currentTime;
            }
        });
    }

    private void configurarBotaoProtegido(View botao, Runnable acao) {
        botao.setOnTouchListener(new View.OnTouchListener() {
            private boolean isThisButtonActive = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (!isAnyButtonProcessing) {
                            isAnyButtonProcessing = true;
                            isThisButtonActive = true;
                            v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
                            return true;
                        } else {
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        if (isThisButtonActive) {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80)
                                    .withEndAction(() -> {
                                        acao.run();
                                        isAnyButtonProcessing = false;
                                        isThisButtonActive = false;
                                    }).start();
                            v.performClick();
                        } else {
                            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(80).start();
                        if (isThisButtonActive) {
                            isAnyButtonProcessing = false;
                            isThisButtonActive = false;
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configurarWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new SelectiveWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
    }

    private void carregarVideo(String videoId) {
        String embedUrl = "https://www.youtube-nocookie.com/embed/" + videoId
                + "?autoplay=1&playsinline=1&controls=1&modestbranding=1&rel=0&showinfo=0&fs=1&enablejsapi=1";

        String html = "<!DOCTYPE html><html><head><meta charset='utf-8'><meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<style>body { margin: 0; padding: 0; background: #000; } iframe { width: 100%; height: 100vh; border: none; }</style>"
                + "</head><body><iframe src='" + embedUrl + "' allow='autoplay'></iframe></body></html>";

        Log.d("VideoPlayer", "Carregando vídeo: " + videoId);
        webView.loadDataWithBaseURL("https://www.youtube-nocookie.com", html, "text/html", "UTF-8", null);
    }

    private class SelectiveWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // ⭐ BLOQUEIA: Todos os cliques fora do vídeo embed
            if (url.contains("/channel/") ||
                    url.contains("/user/") ||
                    url.contains("/c/") ||
                    url.contains("/playlist") ||
                    url.contains("/watch?") ||
                    url.contains("youtube.com/subscription_center") ||
                    url.contains("youtube.com/account") ||
                    url.contains("youtube.com/results") ||
                    url.contains("youtube.com/navigate") ||
                    url.contains("youtube.com/interact") ||
                    url.contains("youtube.com/s/player") ||
                    (url.contains("youtube.com") && !url.contains("embed") && !url.contains("googlevideo.com") && !url.contains("youtube.com/api") && !url.contains("youtube.com/youtubei"))) {

                Log.d("VideoPlayer", "URL BLOQUEADA: " + url);
                return true; // BLOQUEIA
            }

            // ✅ PERMITE: Apenas URLs necessárias para o vídeo
            if (url.contains("youtube.com/embed/") ||
                    url.contains("youtube-nocookie.com/embed/") ||
                    url.contains("youtube-nocookie.com/s/") ||
                    url.contains("googlevideo.com") ||
                    url.contains("youtube.com/api/") ||
                    url.contains("youtube.com/youtubei/") ||
                    url.startsWith("about:blank")) {

                Log.d("VideoPlayer", "URL PERMITIDA: " + url);
                return false; // PERMITE
            }

            Log.d("VideoPlayer", "URL BLOQUEADA (padrão): " + url);
            return true; // BLOQUEIA por padrão
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
            return false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("VideoPlayer", "Página carregada: " + url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                runOnUiThread(() -> {
                    configurarOrientacaoPortrait();
                    mostrarTelaErro();
                    iniciarMonitoramentoRede();
                });
            }
        }
    }

    private class CustomWebChromeClient extends WebChromeClient {
        private View customView;
        private CustomViewCallback customViewCallback;

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
            controlsContainer.setVisibility(View.GONE);
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
            controlsContainer.setVisibility(View.GONE);
        }
    }

    private void configurarBackButton() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null) {
                    webView.loadUrl("about:blank");
                }
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pararMonitoramentoRede();
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.clearCache(true);
            webView.clearHistory();
            webView.removeAllViews();
            webView.destroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreenImersivo();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        configurarFullscreenImersivo();
    }
}
