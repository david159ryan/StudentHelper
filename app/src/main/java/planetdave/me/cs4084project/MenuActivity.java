package planetdave.me.cs4084project;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

public class MenuActivity extends AppCompatActivity implements ListView.OnItemClickListener{
    private enum MenuItems {
        SULIS("Sulis", SulisActivity.class),
        TIMETABLE("Timetable", TimetableActivity.class);

        private String text;


        private Class activity;
        MenuItems(String text, Class activity){
            this.text = text;
            this.activity = activity;
        }

        public Class getActivity() {
            return activity;
        }

        public String getText(){
            return text;
        }
    }

    private SharedPreferences sPrefs;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sPrefs = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE);

        db = new DatabaseHelper(getApplicationContext());

        if(!sPrefs.getBoolean(getString(R.string.database_present), false)){
            populateDatabase();
        }

        ArrayList<String> menuItemsList;
        ArrayAdapter<String> mMenuListAdapter;
        ListView mMenuListView;

        menuItemsList = new ArrayList<>();

        for(MenuItems m : MenuItems.values()){
            menuItemsList.add(m.getText());
        }
        //dataStuff();

        mMenuListView = (ListView)findViewById(R.id.menu_list_view);
        mMenuListView.setClickable(true);
        mMenuListAdapter = new ArrayAdapter<>(this,
                R.layout.layout_list_element,
                R.id.menu_selection,
                menuItemsList);
        mMenuListView.setAdapter(mMenuListAdapter);
        mMenuListView.setOnItemClickListener(this);
    }

    //TODO this may be better handled somewhere else. leaving it here for now
    private void populateDatabase() {
        String dataKey = getString(R.string.student_timetable_data_key);
        SQLiteDatabase database = db.getWritableDatabase();
        ArrayList<String> studentData = getIntent().getExtras().getStringArrayList(dataKey);
        assert studentData != null;
        ContentValues cv = new ContentValues();

        String titles[] = {"_id","_day", "_start", "_end", "_module", "_type", "_group", "_room"};

        for(String s : studentData){
            String values[] = s.split(",");
            // cv.put("id", sPrefs.getString(getString(R.string.current_user_key), ""));
            for(int i = 0; i < titles.length; i++){
                cv.put(titles[i], values[i]);
            }
            database.insert("timetable", "_id", cv);
        }
        sPrefs.edit().putBoolean(getString(R.string.database_present), true).apply();
        System.out.println(Arrays.toString(studentData.toArray(new String[0])));
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_options, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(this, MenuItems.values()[position].getActivity());
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            sPrefs.edit().putString(getString(R.string.current_user_key),
                    getString(R.string.no_current_user)).apply();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}
