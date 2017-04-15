package planetdave.me.cs4084project;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SulisActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sulis);
        WebView webview = (WebView)findViewById(R.id.sulis_webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new SulisWebViewClient());
        webview.loadUrl(getString(R.string.sulis_url));
    }

    private class SulisWebViewClient extends WebViewClient {

        @Override @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            return Uri.parse(url).getHost().equals(getString(R.string.sulis_domain));
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return request.getUrl().getHost().equals(getString(R.string.sulis_domain));
        }
    }
}
