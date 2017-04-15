package planetdave.me.cs4084project;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

    private static final int ID_MIN_LENGTH = 7;
    private static final int ID_MAX_LENGTH = 8;

    private List<String> timetableData;
    // UI references.
    private TextView mTextInputView;
    private ProgressBar mProgressBar;
    private Button mSignInButton;

    FetchStudentTimetableTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new SignInClickListener());

        mTextInputView = (EditText)findViewById(R.id.student_id_auto_text_view);
        mProgressBar = (ProgressBar)findViewById(R.id.login_progressBar);
        mProgressBar.setEnabled(false);
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

    private class SignInClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String id = mTextInputView.getText().toString();
            InputStatus iDStatus = checkIDStatus(id);
            System.out.println("button clicked, ID is: " + id + "\nValid ID?\t" + iDStatus);
            if(iDStatus == InputStatus.OK){
                //TODO load next activity or populate database
                System.out.println("Might need to do something here");
            }else{
                mTextInputView.setError(getString(iDStatus.getCode()));
                mTextInputView.invalidate();
            }
        }
    }

    private class FetchStudentTimetableTask extends AsyncTask<Void, String, Boolean>{
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
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                String dataKey = getString(R.string.student_timetable_data_key);
                intent.putExtra(dataKey,tableEntries);
                startActivity(intent);

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
            Elements paragraphs = doc.getElementsByTag("p");
            for(Element e : paragraphs){
                String tableEntry = e.toString();
                if(TimetableHTMLParser.isValidTimetableHTML(tableEntry)){
                    tableEntries.add(TimetableHTMLParser.parseTimetableEntry(tableEntry));
                }
            }
            return tableEntries;
        }

    }
}

