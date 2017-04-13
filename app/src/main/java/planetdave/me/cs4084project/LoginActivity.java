package planetdave.me.cs4084project;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final int ID_MIN_LENGTH = 7;
    private static final int ID_MAX_LENGTH = 8;

    private List<String> timetableData;

    enum InputStatus {
        OK(0),
        TOO_LONG(R.string.error_invalid_username_long),
        TOO_SHORT(R.string.error_invalid_username_short),
        UNREGISTERED(R.string.error_incorrect_username_unregistered),
        BLANK(R.string.error_field_required);

        private int code;

        InputStatus(int code){
            this.code = code;
        }

        public int getCode(){
            return  code;
        }
    }
    // UI references.
    private TextView mTextInputView;

    FetchStudentTimetableTask task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button mSignInButton = (Button) findViewById(R.id.sign_in_button);
        mSignInButton.setOnClickListener(new SignInClickListener());

        mTextInputView = (TextView)findViewById(R.id.student_id_auto_text_view);
    }

    private InputStatus checkIDStatus(String id) {
        String idRegex = "^[0-9]{"+ID_MIN_LENGTH+","+ID_MAX_LENGTH+"}$";

        InputStatus status = InputStatus.BLANK;

        if(id.matches(idRegex)){
            task = new FetchStudentTimetableTask(id, this);
            task.execute();

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

    public void receiveTimeTableData(List<String> data){
        timetableData = data;
    }

    private class SignInClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String id = mTextInputView.getText().toString();
            InputStatus iDStatus = checkIDStatus(id);
            System.out.println("button clicked, ID is: " + id + "\nValid ID?\t" + iDStatus);
            if(iDStatus == InputStatus.OK){
               ; //TODO load next activity or populate database
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
        private LoginActivity parent;

        FetchStudentTimetableTask(String studentID, LoginActivity parent){
            this.studentID = studentID;
            this.parent = parent;
            System.out.println("in task constructor: " + studentID);
            url = getResources().getString(R.string.timetable_url);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                doc = Jsoup.connect(url)
                        .data("T1", studentID)
                        .post();
            } catch (IOException e) {
                e.printStackTrace();
                return Boolean.FALSE;
            }
            List<String> tableEntries = grabTimeTableEntries();
            parent.receiveTimeTableData(tableEntries);
            System.out.println(Arrays.toString(tableEntries.toArray(new String[0])));
            return Boolean.TRUE;
        }

        private List<String> grabTimeTableEntries(){
            List<String> tableEntries = new ArrayList<>();
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

