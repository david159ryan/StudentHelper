package planetdave.me.cs4084project;


import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PDFReaderActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfreader);

        String pdfSource = getIntent().getStringExtra(getString(R.string.pdf_key));
        System.out.println("In pdf activity:\n\t" + pdfSource + "\n");

        WebView webview=(WebView)findViewById(R.id.pdf_webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);

        try {
            webview.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" +
                URLEncoder.encode(pdfSource, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
