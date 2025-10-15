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

    // Inicializa componentes da interface
    private void inicializarComponentes() {
        webView = findViewById(R.id.webview_video);
        btnSair = findViewById(R.id.btn_sair);
        controlsContainer = findViewById(R.id.controls_container);
        noInternetScreen = findViewById(R.id.no_internet_screen);
        btnVoltarErro = findViewById(R.id.btn_voltar_erro);
        configurarBotoes();
    }

    // Gerenciamento de orientação da tela
    private void configurarOrientacaoLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void configurarOrientacaoPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // Verifica se tem internet disponivel
    private boolean verificarConexaoInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    // Controle de visibilidade das telas
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

    // Configura callback para monitorar conexao em tempo real
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null &&
                networkCallback != null && !isMonitoringNetwork) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null &&
                networkCallback != null && isMonitoringNetwork) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                isMonitoringNetwork = false;
            } catch (Exception e) {
                Log.e("VideoPlayer", "Erro ao desregistrar NetworkCallback: " + e.getMessage());
            }
        }
    }

    // Tenta carregar video quando internet voltar
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

    // Configura botoes com animacao e protecao contra multiplos cliques
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

    // Duplo toque na tela do video mostra/esconde controles
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

    // Previne multiplos cliques simultaneos e adiciona animacao de toque
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

    // Configura WebView para reproducao de video do YouTube
    @SuppressLint("SetJavaScriptEnabled")
    private void configurarWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; SM-A505FN) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.120 Mobile Safari/537.36");
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webView.setWebViewClient(new SelectiveWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
    }

    // Bloqueia navegacao indesejada mantendo apenas o video atual
    private class SelectiveWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("/channel/") || url.contains("/user/") || url.contains("/c/") ||
                    url.contains("/playlist") || url.contains("youtube.com/subscription_center") ||
                    url.contains("youtube.com/account") ||
                    (url.contains("/watch?v=") && !url.contains(currentVideoId))) {
                return true;
            }

            if (url.contains("youtube.com/embed/" + currentVideoId) ||
                    url.contains("googlevideo.com") ||
                    url.contains("youtube.com/youtubei/") ||
                    url.contains("youtube.com/api/") ||
                    url.startsWith("about:blank")) {
                return false;
            }
            return false;
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
            injetarJavaScriptSeletivo(view);
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

    // Injeta JavaScript para remover elementos de navegacao mantendo controles essenciais
    private void injetarJavaScriptSeletivo(WebView view) {
        String javascript =
                "javascript:(function() {" +
                        "function removeNavigationElements() {" +
                        "const navigationSelectors = [" +
                        "'.ytp-title-channel','.ytp-title-expanded-overlay','.ytp-youtube-button'," +
                        "'.ytp-watch-later-button','.ytp-share-button','.ytp-more-button'," +
                        "'.ytp-cards-button','.ytp-watermark','.videowall-endscreen'," +
                        "'.ytp-ce-element','.ytp-ce-video','.ytp-ce-playlist','.ytp-ce-channel'," +
                        "'[class*=\"ytp-ce\"]','[class*=\"endscreen\"]','.annotation','.iv-promo','.iv-promo-contents'];" +
                        "navigationSelectors.forEach(function(selector) {" +
                        "const elements = document.querySelectorAll(selector);" +
                        "for(let i = 0; i < elements.length; i++) {" +
                        "const el = elements[i];" +
                        "if(el) {el.style.display = 'none'; el.style.visibility = 'hidden';}" +
                        "}" +
                        "});" +
                        "const essentialControls = [" +
                        "'.ytp-play-button','.ytp-pause-button','.ytp-progress-bar-container'," +
                        "'.ytp-time-display','.ytp-volume-area','.ytp-settings-button'," +
                        "'.ytp-subtitles-button','.ytp-fullscreen-button','.ytp-chrome-controls','.ytp-control-bar'];" +
                        "essentialControls.forEach(function(selector) {" +
                        "const elements = document.querySelectorAll(selector);" +
                        "for(let i = 0; i < elements.length; i++) {" +
                        "const el = elements[i];" +
                        "if(el) {el.style.display = ''; el.style.visibility = 'visible';}" +
                        "}" +
                        "});" +
                        "document.addEventListener('click', function(e) {" +
                        "const target = e.target; const className = target.className || '';" +
                        "if (className.includes('ytp-title') || className.includes('ytp-youtube-button') ||" +
                        "className.includes('ytp-watermark') || className.includes('ytp-ce-') ||" +
                        "className.includes('ytp-cards-button') || className.includes('ytp-more-button')) {" +
                        "e.preventDefault(); e.stopPropagation(); return false;}" +
                        "}, true);}" +
                        "removeNavigationElements();" +
                        "setTimeout(removeNavigationElements, 500);" +
                        "setTimeout(removeNavigationElements, 1500);" +
                        "setTimeout(removeNavigationElements, 3000);" +
                        "setInterval(removeNavigationElements, 5000);" +
                        "})();";

        view.loadUrl(javascript);
        view.postDelayed(() -> view.loadUrl(javascript), 3000);
    }

    // Gerencia modo fullscreen do player
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

    // Carrega video do YouTube no WebView
    private void carregarVideo(String videoId) {
        String videoHtml = "<!DOCTYPE html>\n<html>\n<head>\n" +
                "<meta charset=\"UTF-8\">\n<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n<style>\n" +
                "* { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "body { background-color: #000000; overflow: hidden; }\n" +
                ".video-container { position: relative; width: 100vw; height: 100vh; display: flex; align-items: center; justify-content: center; background-color: #000000; }\n" +
                "iframe { width: 100%; height: 100%; border: none; }\n" +
                ".ytp-title-channel, .ytp-title-expanded-overlay, .ytp-youtube-button, .ytp-watch-later-button, " +
                ".ytp-share-button, .ytp-more-button, .ytp-cards-button, .ytp-watermark, .videowall-endscreen, " +
                ".ytp-ce-element, .annotation, .iv-promo { display: none !important; visibility: hidden !important; }\n" +
                ".ytp-play-button, .ytp-pause-button, .ytp-progress-bar-container, .ytp-time-display, " +
                ".ytp-volume-area, .ytp-settings-button, .ytp-subtitles-button, .ytp-fullscreen-button, " +
                ".ytp-chrome-controls { display: block !important; visibility: visible !important; }\n" +
                "</style>\n</head>\n<body>\n<div class=\"video-container\">\n<iframe src=\"https://www.youtube.com/embed/" + videoId + "?" +
                "autoplay=1&controls=1&showinfo=0&rel=0&iv_load_policy=3&modestbranding=1&playsinline=1&fs=1&disablekb=0&" +
                "enablejsapi=1&cc_load_policy=1&loop=0&start=0&widget_referrer=" + getPackageName() + "\" " +
                "frameborder=\"0\" scrolling=\"no\" allowfullscreen sandbox=\"allow-scripts allow-same-origin allow-presentation\" " +
                "allow=\"autoplay; encrypted-media; accelerometer; gyroscope; picture-in-picture\"></iframe>\n</div>\n" +
                "<script>document.addEventListener('contextmenu', function(e) {e.preventDefault(); return false;});</script>\n</body>\n</html>";

        webView.loadDataWithBaseURL("https://www.youtube.com", videoHtml, "text/html", "UTF-8", null);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (webView.getVisibility() == View.VISIBLE) {
            controlsContainer.setVisibility(View.GONE);
        }
        configurarFullscreenImersivo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        configurarFullscreenImersivo();
        isAnyButtonProcessing = false;

        if (webView.getVisibility() == View.VISIBLE) {
            esconderControlesAutomaticamente();
        }

        if (noInternetScreen.getVisibility() == View.VISIBLE && !isMonitoringNetwork) {
            iniciarMonitoramentoRede();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pararMonitoramentoRede();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            configurarFullscreenImersivo();
        }
    }

    // Ativa modo fullscreen imersivo escondendo barras do sistema
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

    // Configura botao de voltar para limpar WebView antes de sair
    private void configurarBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null) {
                    webView.loadUrl("about:blank");
                }
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    protected void onDestroy() {
        pararMonitoramentoRede();
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}