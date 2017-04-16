package planetdave.me.cs4084project;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SulisActivity extends AppCompatActivity {
    private long mDownloadedFileID;
    DownloadManager dm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sulis);
        checkAndRequestExternalPermissions();
        System.out.println(Environment.getExternalStorageState());
        File folder = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) +
                getString(R.string.sulis_directory) );

        System.out.println(folder.getAbsolutePath());
        System.out.println("folder made successfully: " + folder.mkdirs());
        //if(checkAndRequestExternalPermissions()){
        //}


        final WebView webview = (WebView)findViewById(R.id.sulis_webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new SulisWebViewClient());

        webview.loadUrl(getString(R.string.sulis_url));

        webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                handleDownload(url, userAgent, contentDisposition, mimeType, contentLength);
                BroadcastReceiver onComplete=new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        Uri mostRecentDownload =
                                dm.getUriForDownloadedFile(mDownloadedFileID);


                        launchPdfActivity(mostRecentDownload.toString());
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });
    }

    void launchPdfActivity(String target) {
        Intent i = new Intent(SulisActivity.this, PDFReaderActivity.class);
        i.putExtra(getString(R.string.pdf_key), target);
        startActivity(i);
    }

    private void handleDownload(String url, String userAgent, String contentDisposition,
                                String mimeType, long contentLength) {
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
        request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOCUMENTS,
                getString(R.string.sulis_directory) +
                       URLUtil.guessFileName(url, contentDisposition, mimeType));
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mDownloadedFileID = dm.enqueue(request);
        Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
    }

    private boolean checkAndRequestExternalPermissions() {
        int permissionCheck = ContextCompat.checkSelfPermission(SulisActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionCheck == PermissionChecker.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
        permissionCheck = ContextCompat.checkSelfPermission(SulisActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionCheck == PermissionChecker.PERMISSION_GRANTED;
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
            }
            if (uri.toString().endsWith(".pdf")){
                //   DownloadFileTask task = new DownloadFileTask();
                //  task.execute(uri.toString());
            }
            return shouldOverride;
        }



    }
}
