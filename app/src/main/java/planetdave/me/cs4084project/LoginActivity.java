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
 * LoginActivity.java - Login Screen and entry point for Application
 * @author David Ryan
 * Requests a valid University of Limerick Student ID. Displays a number
 * of error messages if an invalid ID is entered.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Enum containing various responses from ID submission
     */
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

    private DatabaseHelper db;
    private String currentUser;

    /** Shared Preference keys **/
    private static String userKey;
    private static String noUser;
    private static String firstRun;
    private static String alarmsSet;
    //private static String databasePresent;

    /** Min and Max ID lengths **/
    private static final int ID_MIN_LENGTH = 7;
    private static final int ID_MAX_LENGTH = 8;

    /** UI References **/
    private TextView mTextInputView;
    private ProgressBar mProgressBar;
    private Button mSignInButton;
    private SharedPreferences sPrefs;

    /** Task variable a Asynchronously grab user's timetable data **/
    private FetchStudentTimetableTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Does another activity wish for us to quit the application? */
        if (getIntent().getBooleanExtra(getString(R.string.app_exit_key), false)) {
            finish();
            System.out.println("should finish here");
            return;
        }
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(getApplicationContext());

        /* Initialise key values */
        userKey = getString(R.string.current_user_key);
        noUser = getString(R.string.no_current_user);
        firstRun = getString(R.string.first_run);
        alarmsSet = getString(R.string.alarms_set_key);
        //databasePresent = getString(R.string.database_present);


        sPrefs = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE);
        currentUser = sPrefs.getString(userKey, noUser);

        /* If first run */
        if (sPrefs.getBoolean(firstRun, true)) {
            initPrefs();
            /* Initialise database asynchronously */
            AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    db.getReadableDatabase();
                    return true;
                }
            };
            task.execute();
        } else if (!currentUser.equals(noUser)) {
            /* user has logged in previously */
            launchMenuActivity(currentUser);
            return;
        }
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new SignInClickListener());
        mTextInputView = (TextView) findViewById(R.id.student_id_auto_text_view);
        mProgressBar = (ProgressBar) findViewById(R.id.login_progressBar);
        mProgressBar.setEnabled(false);

    }

    /**
     * Initialise Preferences
     */
    private void initPrefs() {
        sPrefs.edit()
                .putString(userKey, noUser)
                .putBoolean(firstRun, false)
                .putBoolean(alarmsSet, false)
                //.putBoolean(databasePresent, false)
                .apply();
    }

    /**
     * Checks if student ID is valid
     * @param id ID to validate
     * @return Status indicating result of validation
     */
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


    /**
     * Disable UI and display spinning circle while validating ID
     */
    private void enableProgressBar() {
        mProgressBar.setEnabled(true);
        mProgressBar.setVisibility(View.VISIBLE);
        mSignInButton.setClickable(false);
        mTextInputView.setEnabled(false);
    }
    /**
     * Hide spinning circle and enable UI
     */
    private void disableProgressBar() {
        mProgressBar.setEnabled(false);
        mProgressBar.setVisibility(View.INVISIBLE);
        mSignInButton.setClickable(true);
        mTextInputView.setEnabled(true);
    }

    /**
     * Launch Menu and pass new user ID and their timetable data
     * @param id validated student ID
     * @param tableEntries retrieved timetable data
     */
    private void launchMenuActivity(String id, ArrayList<String> tableEntries){
        if(mTextInputView != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTextInputView.getWindowToken(), 0);
        }
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mTextInputView.getWindowToken(), 0);
        sPrefs.edit()
                .putString(getString(R.string.current_user_key), id)
                //.putBoolean(databasePresent, false)
                .apply();
        Intent intent = getMenuActivityIntent();
        String dataKey = getString(R.string.student_timetable_data_key);
        intent.putExtra(dataKey,tableEntries);
        finish();
        startActivity(intent);
    }

    /**
     * Launch Menu without passing data. Used when a user is still logged in
     */
    private void launchMenuActivity(String id){
        if(mTextInputView != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mTextInputView.getWindowToken(), 0);
        }
        sPrefs.edit()
                //.putBoolean(databasePresent, true)
                .putString(userKey, id)
                .apply();
        Intent intent = getMenuActivityIntent();
        finish();
        startActivity(intent);
    }

    /**
     * Returns an intent to launch Menu activity
     * @return intent prepared for launching MenuActivity
     */
    private Intent getMenuActivityIntent() {
        Intent intent = new Intent(this, MenuActivity.class);
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * Sign in button click listener
     */
    private class SignInClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String id = mTextInputView.getText().toString();

            if(db.containsUser(id)){
                launchMenuActivity(id);
            }else{
                InputStatus iDStatus = checkIDStatus(id);
                if(iDStatus != InputStatus.OK){
                    mTextInputView.setError(getString(iDStatus.getCode()));
                    mTextInputView.invalidate(); /* invalidate to redraw view with error message */
                }
            }
        }
    }

    /**
     * Attempts to connect to UL timetable site and retrieve user's timetable
     */
    private class FetchStudentTimetableTask extends AsyncTask<Void, Void, Boolean>{
        private String studentID;
        private String url;
        private Document doc;
        private ArrayList<String> tableEntries;
        private InputStatus requestStatus;

        /**
         * Task constructor
         * @param studentID ID of student to retrieve timetable for
         */
        FetchStudentTimetableTask(String studentID){
            this.studentID = studentID;
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
                /* Uses JSoup library */
                doc = Jsoup.connect(url)
                        .data("T1", studentID)
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
                /* set error message */
                requestStatus = InputStatus.WEBSITE_UNREACHABLE;
                cancel(true);
                return false;
            }

            /* no will return empty list if ID isn't registered */
            tableEntries = grabTimeTableEntries();
            if(tableEntries.size() == 0){
                requestStatus = InputStatus.UNREGISTERED;
            }
            return tableEntries.size() > 0;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                launchMenuActivity(studentID, tableEntries);
            }else{
                reportRequestFailure();
            }
            task = null;
        }

        /**
         * Displays detected failure and returns UI control
         */
        private void reportRequestFailure(){
            mTextInputView.setError(getString(requestStatus.getCode()));
            mTextInputView.requestFocus();
            disableProgressBar();
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(mTextInputView, InputMethodManager.SHOW_IMPLICIT);
        }

        @Override
        protected void onCancelled() {
            reportRequestFailure();
            task = null;
        }

        /**
         * Checks if an active internet connection is available
         * @return true if connection available, otherwise false
         */
        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        /**
         * Turns retrieved HTML into something useful
         * @return timetable entries in CSV list
         */
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

