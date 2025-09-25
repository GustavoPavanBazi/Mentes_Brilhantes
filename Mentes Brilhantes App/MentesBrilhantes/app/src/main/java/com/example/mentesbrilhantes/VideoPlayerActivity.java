package com.example.mentesbrilhantes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
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
    private ImageButton btnSair, btnTentarNovamente, btnVoltarErro;
    private LinearLayout controlsContainer, noInternetScreen;

    private static boolean isAnyButtonProcessing = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String videoId = getIntent().getStringExtra("VIDEO_ID");
        String titulo = getIntent().getStringExtra("TITULO");

        Log.d("VideoPlayer", "VIDEO_ID recebido: " + videoId);
        Log.d("VideoPlayer", "TITULO recebido: " + titulo);

        configurarFullscreenImersivo();
        setContentView(R.layout.activity_video_player);

        inicializarComponentes();
        configurarBackButton();

        // ✅ NOVO: Verificar internet e definir orientação
        if (verificarConexaoInternet()) {
            Log.d("VideoPlayer", "Internet disponível - Modo landscape + vídeo");
            configurarOrientacaoLandscape();
            configurarWebView();
            carregarVideo(videoId);
            mostrarTelaVideo();
        } else {
            Log.d("VideoPlayer", "Sem internet - Modo portrait + tela de erro");
            configurarOrientacaoPortrait();
            mostrarTelaErro();
        }
    }

    // ✅ NOVO: Configurar orientação para landscape (vídeo)
    private void configurarOrientacaoLandscape() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Log.d("VideoPlayer", "Orientação definida: LANDSCAPE");
    }

    // ✅ NOVO: Configurar orientação para portrait (erro)
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

    // ✅ RESTAURADO: onConfigurationChanged para lidar com mudanças de orientação
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Só gerenciar orientação quando o vídeo estiver visível
        if (webView.getVisibility() == View.VISIBLE) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                controlsContainer.setVisibility(View.GONE);
                Log.d("VideoPlayer", "LANDSCAPE - Controles ocultos");
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                // Se usuário forçar portrait durante vídeo, manter controles ocultos
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
                if (webView != null && webView.canGoBack() && webView.getVisibility() == View.VISIBLE) {
                    webView.goBack();
                } else {
                    if (webView != null) {
                        webView.loadUrl("about:blank");
                    }
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void inicializarComponentes() {
        webView = findViewById(R.id.webview_video);
        btnSair = findViewById(R.id.btn_sair);
        controlsContainer = findViewById(R.id.controls_container);

        noInternetScreen = findViewById(R.id.no_internet_screen);
        btnTentarNovamente = findViewById(R.id.btn_tentar_novamente);
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

        // ✅ ATUALIZADO: Botão tentar novamente com mudança de orientação
        configurarBotaoProtegido(btnTentarNovamente, () -> {
            Log.d("VideoPlayer", "Tentando conectar novamente...");

            if (verificarConexaoInternet()) {
                String videoId = getIntent().getStringExtra("VIDEO_ID");

                // ✅ MUDANÇA DE ORIENTAÇÃO: Portrait → Landscape
                configurarOrientacaoLandscape();

                configurarWebView();
                carregarVideo(videoId);
                mostrarTelaVideo();
                Log.d("VideoPlayer", "Conexão restaurada - Mudando para landscape + carregando vídeo");
            } else {
                Log.d("VideoPlayer", "Ainda sem internet - Mantendo portrait");
                // Manter na tela de erro em portrait
            }
        });

        // Botão voltar (tela de erro)
        configurarBotaoProtegido(btnVoltarErro, () -> {
            Log.d("VideoPlayer", "Voltando sem carregar vídeo");
            finish();
        });

        // Double tap no vídeo (só se estiver visível)
        configurarDoubleTapVideo();
    }

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
                        }, 3000);
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

                if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT) {
                    runOnUiThread(() -> {
                        configurarOrientacaoPortrait(); // ✅ Voltar para portrait em erro
                        mostrarTelaErro();
                    });
                }
            }
        });

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

                controlsContainer.setVisibility(View.GONE);
                Log.d("VideoPlayer", "Fullscreen YouTube ativado");
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
                Log.d("VideoPlayer", "Fullscreen YouTube desativado - Controles mantidos ocultos");
            }
        });
    }

    private void carregarVideo(String videoId) {
        String videoHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { background-color: #004bff; overflow: hidden; }\n" +
                "        .video-container {\n" +
                "            position: relative;\n" +
                "            width: 100vw;\n" +
                "            height: 100vh;\n" +
                "            display: flex;\n" +
                "            align-items: center;\n" +
                "            justify-content: center;\n" +
                "            background-color: #004bff;\n" +
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
}
