package planetdave.me.cs4084project;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private enum InputStatus {
        OK(0),
        TOO_LONG(R.string.error_invalid_username_long),
        TOO_SHORT(R.string.error_invalid_username_short),
        UNREGISTERED(R.string.error_incorrect_username_unregistered),
        NETWORK_UNAVAILABLE(R.string.error_network_unavailable),
        WEBSITE_UNREACHABLE(R.string.error_website_unavailable),
        BLANK(R.string.error_field_required);

        private int code;

        InputStatus(int code){
            this.code = code;
        }

        public int getCode(){
            return  code;
        }
    }

    private static String userKey;
    private static String noUser;
    private static String firstRun;
    private static String databasePresent;
    private static String userSetKey;
    private static String alarmsSet;

    private static final int ID_MIN_LENGTH = 7;
    private static final int ID_MAX_LENGTH = 8;

    // UI references.
    private TextView mTextInputView;
    private ProgressBar mProgressBar;
    private Button mSignInButton;
    private SharedPreferences sPrefs;

    FetchStudentTimetableTask task = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().getBooleanExtra(getString(R.string.app_exit_key), false)){
            finish();
            System.out.println("should finish here");
            return;
        }
        setContentView(R.layout.activity_login);

        userKey = getString(R.string.current_user_key);
        noUser = getString(R.string.no_current_user);
        firstRun = getString(R.string.first_run);
        databasePresent = getString(R.string.database_present);
        userSetKey = getString(R.string.users_set_key);
        alarmsSet = getString(R.string.alarms_set_key);


        sPrefs = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE);

        System.out.println("current user: " + sPrefs.getString(userKey, "fail"));
        if(sPrefs.getBoolean(firstRun, true)) {
            initPrefs();
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    DatabaseHelper db = new DatabaseHelper(getApplicationContext());
                    db.getReadableDatabase();
                    return true;
                }
            };
            task.execute();
        } else if(!sPrefs.getString(userKey, noUser).equals(noUser)){
            launchMenuActivity();
            finish();
        }

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new SignInClickListener());

        mTextInputView = (TextView)findViewById(R.id.student_id_auto_text_view);
        mProgressBar = (ProgressBar)findViewById(R.id.login_progressBar);
        mProgressBar.setEnabled(false);
    }

    private void initPrefs() {
        sPrefs.edit().putString(userKey, noUser).apply();
        sPrefs.edit().putBoolean(firstRun, false).apply();
        sPrefs.edit().putBoolean(databasePresent, false).apply();
        sPrefs.edit().putBoolean(alarmsSet, false).apply();
        //sPrefs.edit().putStringSet()
    }

    private InputStatus checkIDStatus(String id) {
        String idRegex = "^[0-9]{"+ID_MIN_LENGTH+","+ID_MAX_LENGTH+"}$";

        InputStatus status = InputStatus.BLANK;

        if(id.matches(idRegex)){
            status = InputStatus.OK;
            task = new FetchStudentTimetableTask(id);
            task.execute();
            enableProgressBar();
        }
        else if(id.length() == 0){
            status = InputStatus.BLANK;
        }
        else if(id.length() < ID_MIN_LENGTH ){
            status = InputStatus.TOO_SHORT;
        }
        else if(id.length() > ID_MAX_LENGTH){
            status = InputStatus.TOO_LONG;
        }
        return status;
    }
/*
    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
*/
    private void enableProgressBar() {
        mProgressBar.setEnabled(true);
        mProgressBar.setVisibility(View.VISIBLE);
        mSignInButton.setClickable(false);
        mTextInputView.setEnabled(false);
    }

    private void disableProgressBar() {
        mProgressBar.setEnabled(false);
        mProgressBar.setVisibility(View.INVISIBLE);
        mSignInButton.setClickable(true);
        mTextInputView.setEnabled(true);
    }

    private void launchMenuActivity(String id, ArrayList<String> tableEntries){
        sPrefs.edit().putString(getString(R.string.current_user_key), id).apply();
        Intent intent = getMenuActivityIntent();
        String dataKey = getString(R.string.student_timetable_data_key);
        intent.putExtra(dataKey,tableEntries);
        startActivity(intent);
    }

    private Intent getMenuActivityIntent() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void launchMenuActivity(){
        Intent intent = getMenuActivityIntent();
        startActivity(intent);
    }

    private class SignInClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String id = mTextInputView.getText().toString();
            InputStatus iDStatus = checkIDStatus(id);
            System.out.println("button clicked, ID is: " + id + "\nValid ID?\t" + iDStatus);
            if(iDStatus != InputStatus.OK){
                mTextInputView.setError(getString(iDStatus.getCode()));
                mTextInputView.invalidate();
            }
        }
    }

    private class FetchStudentTimetableTask extends AsyncTask<Void, Void, Boolean>{
        private String studentID;
        private String url;
        private Document doc;
        private ArrayList<String> tableEntries;
        private InputStatus requestStatus;

        FetchStudentTimetableTask(String studentID){
            this.studentID = studentID;
            System.out.println("in task constructor: " + studentID);
            url = getResources().getString(R.string.timetable_url);
            requestStatus = InputStatus.OK;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!isNetworkAvailable()){
                requestStatus = InputStatus.NETWORK_UNAVAILABLE;
                cancel(true);
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                doc = Jsoup.connect(url)
                        .data("T1", studentID)
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
                requestStatus = InputStatus.WEBSITE_UNREACHABLE;
                cancel(true);
            }
            tableEntries = grabTimeTableEntries();
            //System.out.println(Arrays.toString(tableEntries.toArray(new String[0])));
            if(tableEntries.size() == 0){
                requestStatus = InputStatus.UNREGISTERED;
            }
            return tableEntries.size() > 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                finish();
                launchMenuActivity(studentID, tableEntries);

            }else{
                reportRequestFailure();
            }
            task = null;
        }

        private void reportRequestFailure(){
            mTextInputView.setError(getString(requestStatus.getCode()));
            disableProgressBar();
            mTextInputView.requestFocus();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(mTextInputView, InputMethodManager.SHOW_IMPLICIT);
        }

        @Override
        protected void onCancelled() {
            reportRequestFailure();
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        private ArrayList<String> grabTimeTableEntries(){
            ArrayList<String> tableEntries = new ArrayList<>();
            Element lastRow = doc.getElementsByTag("tr").last();
            Elements days = lastRow.getElementsByTag("td");
            for(int i = 0; i < 5; i++){
                Elements paragraphs = days.get(i).getElementsByTag("p");
                for(Element e : paragraphs){
                    String tableEntry = e.toString();
                    if(TimetableHTMLParser.isValidTimetableHTML(tableEntry)){
                        tableEntries.add(studentID + "," + i + ","
                                + TimetableHTMLParser.parseTimetableEntry(tableEntry));
                    }
                }
            }
            return tableEntries;
        }

    }
}

