package planetdave.me.cs4084project;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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
        webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimeType);
                //------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                //------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class SulisWebViewClient extends WebViewClient {

        @Override @SuppressWarnings("deprecation")
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Uri uri = Uri.parse(url);
            return handleUrlLoading(uri);
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Uri uri = request.getUrl();
            return handleUrlLoading(uri);
        }

        private boolean handleUrlLoading(Uri uri) {
            boolean shouldOverride = true;

            /* Stops internal SULIS links being opened in external app */
            if( uri.getHost().equals(getString(R.string.sulis_domain))){
                shouldOverride = false;
                /* Open new PDF Activity if file is PDF */
               // if (uri.toString().endsWith(".pdf")){
                 //   DownloadFileTask task = new DownloadFileTask();
                  //  task.execute(uri.toString());
               // }
            }
            return shouldOverride;
        }

        private void launchPdfActivity(String target) {
            Intent i = new Intent(SulisActivity.this, PDFReaderActivity.class);
            i.putExtra(getString(R.string.pdf_key), target);
            startActivity(i);
        }

        private class DownloadFileTask extends AsyncTask<String, Integer, String> {
           // private String url = "";
            private String filename = "";

            @Override
            protected String doInBackground(String... params) {

                return filename;
            }

            private void saveFile(Document doc) throws IOException {
                System.out.println("Saving file");
                FileOutputStream out;
               // String filenameParts[] = url.split("/");
                //filename = filenameParts[filenameParts.length - 1];
               // out = openFileOutput(filename, Context.MODE_PRIVATE);
                //out.write(doc.toString().getBytes());
            }

            @Override
            protected void onPostExecute(final String file) {
                launchPdfActivity(file);
            }
        }
    }
}
