package planetdave.me.cs4084project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        String dataKey = getString(R.string.student_timetable_data_key);
        System.out.println("data key" + dataKey);


        ArrayList<String> studentData;
        studentData = getIntent().getExtras().getStringArrayList(dataKey);
        assert studentData != null;
        System.out.println(Arrays.toString(studentData.toArray(new String[0])));

        System.out.println("IN Menu Activity");
    }

    @Override
    public void onBackPressed() {
        //this.moveTaskToBack(true);
        Intent i = new Intent(this, SulisActivity.class);
        startActivity(i);
    }
}
