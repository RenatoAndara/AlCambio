/*
 * Calculadora BCV — aplicación Android
 * Autor: Renato Andara | Desarrollado por R.A. 2026
 *
 * Muestra la calculadora (assets/index.html) dentro de un WebView. La página
 * vive dentro del APK, así que abre sin conexión; las tasas se piden a
 * DolarApi mediante un puente nativo (evita restricciones CORS del WebView).
 */
package ve.renatoandara.calculadorabcv;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.webkit.WebViewAssetLoader;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private static final String PERMITIDO = "https://ve.dolarapi.com/";
    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);      // localStorage: guarda la tasa del día
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }
        });

        web.addJavascriptInterface(new Puente(), "AndroidBCV");
        web.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    /** Puente JavaScript → Android para descargar JSON de DolarApi. */
    private class Puente {
        @JavascriptInterface
        public void get(final String url, final String id) {
            new Thread(() -> {
                int code = 0;
                String body;
                try {
                    if (url == null || !url.startsWith(PERMITIDO)) {
                        throw new IOException("URL no permitida");
                    }
                    HttpURLConnection c = (HttpURLConnection) new URL(url).openConnection();
                    c.setConnectTimeout(15000);
                    c.setReadTimeout(15000);
                    c.setRequestProperty("Accept", "application/json");
                    code = c.getResponseCode();
                    InputStream in = code < 400 ? c.getInputStream() : c.getErrorStream();
                    body = in != null ? leer(in) : "";
                    c.disconnect();
                } catch (Exception e) {
                    code = 0;
                    body = String.valueOf(e.getMessage());
                }
                final String js = "window.__respuestaNativa(" + JSONObject.quote(id) + ","
                        + code + "," + JSONObject.quote(body) + ")";
                web.post(() -> web.evaluateJavascript(js, null));
            }).start();
        }
    }

    private static String leer(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        in.close();
        return new String(out.toByteArray(), StandardCharsets.UTF_8);
    }
}
