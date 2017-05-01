package planetdave.me.cs4084project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import java.io.File;

/**
 * This class loads the SULIS webstie in
 */
public class SulisActivity extends AppCompatActivity {
    private long mDownloadedFileID;
    private DownloadManager dm;
    private boolean bSulisDirectoryExists;
    private WebView webview;

    //// TODO: 16/04/2017 handle more than just PDFs

    @SuppressLint("SetJavascriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sulis);

        Toolbar tBar = (Toolbar)findViewById(R.id.sulis_activity_toolbar);
        setSupportActionBar(tBar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        /* check if we have write permission and if sulis directory exists .
         * Sulis directory should be /Documents/StudentHelper/Sulis
         * */
        if(checkAndRequestExternalPermissions()){
            File folder = new File(getSulisDirectory() );
            bSulisDirectoryExists = folder.exists();
            if(!bSulisDirectoryExists){
                bSulisDirectoryExists = folder.mkdirs();
            }
        }

        webview = (WebView)findViewById(R.id.sulis_webview);
        WebSettings webSettings = webview.getSettings();
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new SulisWebViewClient(this));

        webview.loadUrl(getString(R.string.sulis_url));

        webview.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, final String mimeType,
                                        long contentLength) {

                handleDownload(url, userAgent, contentDisposition, mimeType, contentLength);
                BroadcastReceiver onComplete=new BroadcastReceiver() {
                    public void onReceive(Context ctxt, Intent intent) {
                        Uri mostRecentDownload =
                                dm.getUriForDownloadedFile(mDownloadedFileID);
                        if(MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
                                .equals("pdf")){
                            launchPdfActivity(mostRecentDownload.toString());
                        }
                    }
                };
                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        });
    }

    /**
     * Get filepath to Sulis Directory
     * @return file path for Sulis Folder
     */
    String getSulisDirectory(){
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS) + getString(R.string.sulis_directory);
    }

    /**
     * Lunches PDF activity and passes the path to a PDF file to open
     * @param target path to PDF to read
     */
    void launchPdfActivity(String target) {
        Intent i = new Intent(SulisActivity.this, PDFReaderActivity.class);
        i.putExtra(getString(R.string.pdf_key), target);
        startActivity(i);
    }

    /* Modified code found on StackOverflow */
    private void handleDownload(String url, String userAgent, String contentDisposition,
                                String mimeType, long contentLength) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        /* got most of this from StackOverflow */
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
        if(bSulisDirectoryExists){
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOCUMENTS,    /* Documents Folder */
                    getString(R.string.sulis_directory) +           /* Sulis Sub Dir */
                            URLUtil.guessFileName(url, contentDisposition, mimeType));
        }
        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        mDownloadedFileID = dm.enqueue(request);
        Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
    }

    /**
     * Checks if the application has External Write permission, if not requests it from
     * the user.
     * @return true if permission exists or is granted. False is user denies access
     */
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

    @Override
    public void onBackPressed() {
        if(webview.canGoBack()){
            webview.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
class SulisWebViewClient extends WebViewClient {

    private SulisActivity parent;
    SulisWebViewClient(SulisActivity parent) {
        this.parent = parent;
    }

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
        if( uri.getHost().equals(parent.getBaseContext().getString(R.string.sulis_domain))){
            shouldOverride = false;
            if (uri.getLastPathSegment().endsWith(".pdf")){
                File fileCheck = new File(parent.getSulisDirectory(), uri.getLastPathSegment());
                /* Check if file exists, if so launch it in PDFReaderActivity */
                if(fileCheck.exists()){
                    parent.launchPdfActivity(fileCheck.getAbsolutePath());
                    shouldOverride = true;
                }
            }
        }
        return shouldOverride;
    }
}
