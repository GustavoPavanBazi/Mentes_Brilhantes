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
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayInputStream;

public class VideoPlayerActivity extends AppCompatActivity {

    private WebView webView;
    private ImageButton btnSair, btnVoltarErro;
    private LinearLayout controlsContainer, noInternetScreen;

    private static boolean isAnyButtonProcessing = false;

    private ConnectivityManager connectivityManager;
    private ConnectivityManager.NetworkCallback networkCallback;
    private boolean isMonitoringNetwork = false;

    // ✅ Controle do vídeo atual
    private String currentVideoId;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String videoId = getIntent().getStringExtra("VIDEO_ID");
        String titulo = getIntent().getStringExtra("TITULO");

        // ✅ Armazenar ID do vídeo atual
        this.currentVideoId = videoId;

        Log.d("VideoPlayer", "VIDEO_ID recebido: " + videoId);
        Log.d("VideoPlayer", "TITULO recebido: " + titulo);

        configurarFullscreenImersivo();
        setContentView(R.layout.activity_video_player);

        inicializarComponentes();
        configurarBackButton();
        inicializarMonitoramentoRede();

        if (verificarConexaoInternet()) {
            Log.d("VideoPlayer", "Internet disponível - Modo landscape + vídeo");
            configurarOrientacaoLandscape();
            configurarWebView();
            carregarVideo(videoId);
            mostrarTelaVideo();
        } else {
            Log.d("VideoPlayer", "Sem internet - Modo portrait + tela de erro + monitoramento automático");
            configurarOrientacaoPortrait();
            mostrarTelaErro();
            iniciarMonitoramentoRede();
        }
    }

    // ... [TODOS os métodos de monitoramento de rede PERMANECEM IGUAIS] ...

    private void inicializarMonitoramentoRede() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && connectivityManager != null) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    Log.d("VideoPlayer", "🌐 Internet voltou automaticamente!");

                    runOnUiThread(() -> {
                        if (noInternetScreen.getVisibility() == View.VISIBLE) {
                            Log.d("VideoPlayer", "Carregando vídeo automaticamente...");
                            tentarCarregarVideoAutomaticamente();
                        }
                    });
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    Log.d("VideoPlayer", "📶 Internet perdida");

                    runOnUiThread(() -> {
                        if (webView.getVisibility() == View.VISIBLE) {
                            Log.d("VideoPlayer", "Mostrando tela de erro - conexão perdida");
                            configurarOrientacaoPortrait();
                            mostrarTelaErro();
                            iniciarMonitoramentoRede();
                        }
                    });
                }

                @Override
                public void onCapabilitiesChanged(Network network, android.net.NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    Log.d("VideoPlayer", "Capacidades da rede mudaram");
                }
            };
        } else {
            Log.w("VideoPlayer", "NetworkCallback não disponível nesta versão do Android");
        }
    }

    private void iniciarMonitoramentoRede() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                connectivityManager != null &&
                networkCallback != null &&
                !isMonitoringNetwork) {

            try {
                NetworkRequest request = new NetworkRequest.Builder().build();
                connectivityManager.registerNetworkCallback(request, networkCallback);
                isMonitoringNetwork = true;
                Log.d("VideoPlayer", "✅ Monitoramento automático de rede iniciado");
            } catch (Exception e) {
                Log.e("VideoPlayer", "Erro ao registrar NetworkCallback: " + e.getMessage());
            }
        }
    }

    private void pararMonitoramentoRede() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N &&
                connectivityManager != null &&
                networkCallback != null &&
                isMonitoringNetwork) {

            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
                isMonitoringNetwork = false;
                Log.d("VideoPlayer", "❌ Monitoramento de rede parado");
            } catch (Exception e) {
                Log.e("VideoPlayer", "Erro ao desregistrar NetworkCallback: " + e.getMessage());
            }
        }
    }

    private void tentarCarregarVideoAutomaticamente() {
        if (verificarConexaoInternet()) {
            String videoId = getIntent().getStringExtra("VIDEO_ID");

            Log.d("VideoPlayer", "🎬 Carregando vídeo automaticamente - Internet restaurada");

            pararMonitoramentoRede();
            configurarOrientacaoLandscape();
            configurarWebView();
            carregarVideo(videoId);
            mostrarTelaVideo();
        } else {
            Log.d("VideoPlayer", "Falso positivo - ainda sem internet estável");
        }
    }

    // ... [TODOS os métodos de orientação e conexão PERMANECEM IGUAIS] ...

    private void configurarOrientacaoLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Log.d("VideoPlayer", "Orientação definida: LANDSCAPE");
    }

    private void configurarOrientacaoPortrait() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d("VideoPlayer", "Orientação definida: PORTRAIT");
    }

    private boolean verificarConexaoInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

            Log.d("VideoPlayer", "Status da internet: " + (isConnected ? "Conectada" : "Desconectada"));
            return isConnected;
        }

        Log.e("VideoPlayer", "ConnectivityManager não disponível");
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
            Log.d("VideoPlayer", "Controles ocultos automaticamente - Modo tela cheia");
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (webView.getVisibility() == View.VISIBLE) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                controlsContainer.setVisibility(View.GONE);
                Log.d("VideoPlayer", "LANDSCAPE - Controles ocultos");
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                controlsContainer.setVisibility(View.GONE);
                Log.d("VideoPlayer", "PORTRAIT - Controles mantidos ocultos");
            }
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

    private void configurarFullscreenImersivo() {
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
    }

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

    private void inicializarComponentes() {
        webView = findViewById(R.id.webview_video);
        btnSair = findViewById(R.id.btn_sair);
        controlsContainer = findViewById(R.id.controls_container);

        noInternetScreen = findViewById(R.id.no_internet_screen);
        btnVoltarErro = findViewById(R.id.btn_voltar_erro);

        configurarBotoes();
    }

    private void configurarBotoes() {
        // Botão sair (original)
        configurarBotaoProtegido(btnSair, () -> {
            if (webView != null) {
                webView.loadUrl("about:blank");
            }
            finish();
        });

        // Botão voltar (único botão na tela de erro)
        configurarBotaoProtegido(btnVoltarErro, () -> {
            Log.d("VideoPlayer", "Voltando - usuário cancelou espera pela internet");
            finish();
        });

        // ✅ MANTER double-tap para mostrar/ocultar controles
        configurarDoubleTapVideo();
    }

    // ✅ RESTAURADO: Double-tap para controles
    private void configurarDoubleTapVideo() {
        webView.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;

            @Override
            public void onClick(View v) {
                if (webView.getVisibility() != View.VISIBLE) return;

                long currentTime = System.currentTimeMillis();

                if (currentTime - lastClickTime < 300) { // Double tap
                    if (controlsContainer.getVisibility() == View.VISIBLE) {
                        controlsContainer.setVisibility(View.GONE);
                        Log.d("VideoPlayer", "Controles ocultos - Double tap");
                    } else {
                        controlsContainer.setVisibility(View.VISIBLE);
                        Log.d("VideoPlayer", "Controles visíveis - Double tap");

                        controlsContainer.postDelayed(() -> {
                            if (controlsContainer.getVisibility() == View.VISIBLE) {
                                controlsContainer.setVisibility(View.GONE);
                                Log.d("VideoPlayer", "Controles ocultos automaticamente");
                            }
                        }, 4000); // 4 segundos para ocultar
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

                            v.animate()
                                    .scaleX(0.95f)
                                    .scaleY(0.95f)
                                    .setDuration(80)
                                    .start();
                            return true;
                        } else {
                            isThisButtonActive = false;
                            return false;
                        }

                    case MotionEvent.ACTION_UP:
                        if (isThisButtonActive) {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .withEndAction(() -> {
                                        acao.run();
                                        isAnyButtonProcessing = false;
                                        isThisButtonActive = false;
                                    })
                                    .start();

                            v.performClick();
                        } else {
                            v.animate()
                                    .scaleX(1.0f)
                                    .scaleY(1.0f)
                                    .setDuration(80)
                                    .start();
                        }
                        return true;

                    case MotionEvent.ACTION_CANCEL:
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(80)
                                .start();

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

    // ✅ WebView com controle seletivo
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

        // ✅ WebViewClient com controle seletivo
        webView.setWebViewClient(new SelectiveWebViewClient());
        webView.setWebChromeClient(new CustomWebChromeClient());
    }

    // ✅ WebViewClient que remove APENAS elementos de navegação
    private class SelectiveWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d("VideoPlayer", "🔗 Tentativa de navegação para: " + url);

            // ✅ Bloquear apenas navegação para canais, playlists, inscrições
            if (url.contains("/channel/") ||
                    url.contains("/user/") ||
                    url.contains("/c/") ||
                    url.contains("/playlist") ||
                    url.contains("/watch?v=") && !url.contains(currentVideoId) ||
                    url.contains("youtube.com/subscription_center") ||
                    url.contains("youtube.com/account")) {

                Log.d("VideoPlayer", "❌ URL de navegação bloqueada: " + url);
                return true; // Bloquear navegação
            }

            // ✅ Permitir recursos necessários do YouTube
            if (url.contains("youtube.com/embed/" + currentVideoId) ||
                    url.contains("googlevideo.com") ||
                    url.contains("youtube.com/youtubei/") ||
                    url.contains("youtube.com/api/") ||
                    url.startsWith("about:blank")) {

                Log.d("VideoPlayer", "✅ URL permitida: " + url);
                return false; // Permitir carregamento
            }

            return false; // Permitir outros recursos do YouTube
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("VideoPlayer", "Página iniciada: " + url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d("VideoPlayer", "Página carregada: " + url);

            // ✅ Injetar JavaScript para remover APENAS elementos de navegação
            injetarJavaScriptSeletivo(view);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e("VideoPlayer", "Erro ao carregar: " + description + " - URL: " + failingUrl);

            if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                runOnUiThread(() -> {
                    configurarOrientacaoPortrait();
                    mostrarTelaErro();
                    iniciarMonitoramentoRede();
                });
            }
        }
    }

    // ✅ JavaScript que remove APENAS elementos de navegação
    private void injetarJavaScriptSeletivo(WebView view) {
        String javascript =
                "javascript:(function() {" +
                        "console.log('🔧 Removendo APENAS elementos de navegação...');" +

                        // ✅ Função para remover elementos de navegação específicos
                        "function removeNavigationElements() {" +
                        "const navigationSelectors = [" +
                        "'.ytp-title-channel'," +           // Nome do canal
                        "'.ytp-title-expanded-overlay'," +  // Overlay de título expandido
                        "'.ytp-youtube-button'," +          // Botão "Assistir no YouTube"
                        "'.ytp-watch-later-button'," +      // Botão "Assistir mais tarde"
                        "'.ytp-share-button'," +            // Botão compartilhar
                        "'.ytp-more-button'," +             // Botão "Mais opções"
                        "'.ytp-cards-button'," +            // Botão de cards
                        "'.ytp-watermark'," +               // Marca d'água do canal
                        "'.videowall-endscreen'," +         // Tela final com vídeos relacionados
                        "'.ytp-ce-element'," +              // Elementos de call-to-action
                        "'.ytp-ce-video'," +                // Vídeos relacionados
                        "'.ytp-ce-playlist'," +             // Playlists relacionadas
                        "'.ytp-ce-channel'," +              // Canais relacionados
                        "'[class*=\"ytp-ce\"]'," +          // Qualquer elemento CE
                        "'[class*=\"endscreen\"]'," +       // Elementos de tela final
                        "'.annotation'," +                   // Anotações
                        "'.iv-promo'," +                     // Promoções
                        "'.iv-promo-contents'" +             // Conteúdo de promoções
                        "];" +

                        "navigationSelectors.forEach(function(selector) {" +
                        "const elements = document.querySelectorAll(selector);" +
                        "for(let i = 0; i < elements.length; i++) {" +
                        "const el = elements[i];" +
                        "if(el) {" +
                        "el.style.display = 'none';" +
                        "el.style.visibility = 'hidden';" +
                        "console.log('🗑️ Elemento de navegação removido:', selector);" +
                        "}" +
                        "}" +
                        "});" +

                        // ✅ MANTER controles essenciais visíveis
                        "const essentialControls = [" +
                        "'.ytp-play-button'," +             // Botão play
                        "'.ytp-pause-button'," +            // Botão pause
                        "'.ytp-progress-bar-container'," +  // Barra de progresso
                        "'.ytp-time-display'," +            // Display de tempo
                        "'.ytp-volume-area'," +             // Área de volume
                        "'.ytp-settings-button'," +         // Botão configurações
                        "'.ytp-subtitles-button'," +        // Botão legendas
                        "'.ytp-fullscreen-button'," +       // Botão fullscreen
                        "'.ytp-chrome-controls'," +         // Container de controles
                        "'.ytp-control-bar'" +              // Barra de controle
                        "];" +

                        "essentialControls.forEach(function(selector) {" +
                        "const elements = document.querySelectorAll(selector);" +
                        "for(let i = 0; i < elements.length; i++) {" +
                        "const el = elements[i];" +
                        "if(el) {" +
                        "el.style.display = '';" +
                        "el.style.visibility = 'visible';" +
                        "console.log('✅ Controle essencial mantido:', selector);" +
                        "}" +
                        "}" +
                        "});" +

                        // ✅ Bloquear cliques apenas em elementos de navegação
                        "document.addEventListener('click', function(e) {" +
                        "const target = e.target;" +
                        "const classList = target.classList || [];" +
                        "const className = target.className || '';" +

                        "// Bloquear cliques em elementos de navegação" +
                        "if (className.includes('ytp-title') ||" +
                        "className.includes('ytp-youtube-button') ||" +
                        "className.includes('ytp-watermark') ||" +
                        "className.includes('ytp-ce-') ||" +
                        "className.includes('ytp-cards-button') ||" +
                        "className.includes('ytp-more-button')) {" +
                        "e.preventDefault();" +
                        "e.stopPropagation();" +
                        "console.log('🚫 Clique de navegação bloqueado');" +
                        "return false;" +
                        "}" +

                        "// PERMITIR cliques em controles essenciais" +
                        "if (className.includes('ytp-play-button') ||" +
                        "className.includes('ytp-pause-button') ||" +
                        "className.includes('ytp-progress-bar') ||" +
                        "className.includes('ytp-settings-button') ||" +
                        "className.includes('ytp-subtitles-button') ||" +
                        "className.includes('ytp-fullscreen-button') ||" +
                        "className.includes('ytp-volume')) {" +
                        "console.log('✅ Clique em controle essencial permitido');" +
                        "return true;" +
                        "}" +
                        "}, true);" +
                        "}" +

                        // ✅ EXECUTAR várias vezes para garantir
                        "removeNavigationElements();" + // Imediato
                        "setTimeout(removeNavigationElements, 500);" +  // 0.5s
                        "setTimeout(removeNavigationElements, 1500);" + // 1.5s
                        "setTimeout(removeNavigationElements, 3000);" + // 3s

                        // ✅ Monitoramento periódico mais suave
                        "setInterval(removeNavigationElements, 5000);" + // A cada 5 segundos

                        "})();";

        view.loadUrl(javascript);

        // ✅ Reforço após carregamento completo
        view.postDelayed(() -> {
            view.loadUrl(javascript);
            Log.d("VideoPlayer", "🔄 Reforço de remoção seletiva aplicado");
        }, 3000);
    }

    // ✅ WebChromeClient personalizado
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
            Log.d("VideoPlayer", "🎬 Fullscreen YouTube ativado");
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
            Log.d("VideoPlayer", "Fullscreen YouTube desativado");
        }
    }

    // ✅ HTML com controles ESSENCIAIS habilitados
    private void carregarVideo(String videoId) {
        String videoHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { \n" +
                "            background-color: #000000; \n" +
                "            overflow: hidden; \n" +
                "        }\n" +
                "        .video-container {\n" +
                "            position: relative;\n" +
                "            width: 100vw;\n" +
                "            height: 100vh;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            background-color: #000000;\n" +
                "        }\n" +
                "        iframe {\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            border: none;\n" +
                "        }\n" +
                "        \n" +
                "        /* ✅ CSS para ocultar APENAS elementos de navegação */\n" +
                "        .ytp-title-channel,\n" +
                "        .ytp-title-expanded-overlay,\n" +
                "        .ytp-youtube-button,\n" +
                "        .ytp-watch-later-button,\n" +
                "        .ytp-share-button,\n" +
                "        .ytp-more-button,\n" +
                "        .ytp-cards-button,\n" +
                "        .ytp-watermark,\n" +
                "        .videowall-endscreen,\n" +
                "        .ytp-ce-element,\n" +
                "        .annotation,\n" +
                "        .iv-promo {\n" +
                "            display: none !important;\n" +
                "            visibility: hidden !important;\n" +
                "        }\n" +
                "        \n" +
                "        /* ✅ MANTER controles essenciais visíveis */\n" +
                "        .ytp-play-button,\n" +
                "        .ytp-pause-button,\n" +
                "        .ytp-progress-bar-container,\n" +
                "        .ytp-time-display,\n" +
                "        .ytp-volume-area,\n" +
                "        .ytp-settings-button,\n" +
                "        .ytp-subtitles-button,\n" +
                "        .ytp-fullscreen-button,\n" +
                "        .ytp-chrome-controls {\n" +
                "            display: block !important;\n" +
                "            visibility: visible !important;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"video-container\">\n" +
                "        <iframe\n" +
                "            src=\"https://www.youtube.com/embed/" + videoId + "?" +
                // ✅ Parâmetros que MANTÊM controles essenciais
                "autoplay=1&" +                    // Reprodução automática
                "controls=1&" +                    // ✅ MANTER controles (1 = habilitado)
                "showinfo=0&" +                    // SEM informações do canal
                "rel=0&" +                         // SEM vídeos relacionados
                "iv_load_policy=3&" +              // SEM anotações
                "modestbranding=1&" +              // SEM logo YouTube
                "playsinline=1&" +                 // Reprodução inline
                "fs=1&" +                          // ✅ PERMITIR fullscreen
                "disablekb=0&" +                   // ✅ PERMITIR controles teclado
                "enablejsapi=1&" +                 // ✅ PERMITIR API JavaScript
                "cc_load_policy=1&" +              // ✅ PERMITIR legendas
                "loop=0&" +                        // SEM loop automático
                "start=0&" +                       // Começar do início
                "widget_referrer=" + getPackageName() + "\"\n" +
                "            frameborder=\"0\"\n" +
                "            scrolling=\"no\"\n" +
                "            allowfullscreen\n" +
                "            sandbox=\"allow-scripts allow-same-origin allow-presentation\"\n" +
                "            allow=\"autoplay; encrypted-media; accelerometer; gyroscope; picture-in-picture\">\n" +
                "        </iframe>\n" +
                "    </div>\n" +
                "    \n" +
                "    <script>\n" +
                "        // ✅ Prevenir apenas clique direito\n" +
                "        document.addEventListener('contextmenu', function(e) {\n" +
                "            e.preventDefault();\n" +
                "            return false;\n" +
                "        });\n" +
                "        \n" +
                "        console.log('🛡️ YouTube com controles essenciais carregado');\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";

        Log.d("VideoPlayer", "🎬 Carregando vídeo com controles essenciais mantidos");
        webView.loadDataWithBaseURL("https://www.youtube.com", videoHtml, "text/html", "UTF-8", null);
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
