package planetdave.me.cs4084project;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Menu activity. App opens directly here if user is logged in.
 */
public class MenuActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    /**
     * Enum that defines Menu items. Provides the button label and Activity to launch
     */
    private enum MenuItems {
        SULIS("Sulis", SulisActivity.class),
        TIMETABLE("Timetable", TimetableActivity.class);

        private String text;
        private Class activity;

        /**
         * MenuItems constructor
         * @param text Menu item label
         * @param activity Activity to launch
         */
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

    private Toolbar toolbar;
    private ArrayList<String> menuItemsList;
    private ArrayAdapter<String> mMenuListAdapter;
    private ListView mMenuListView;

    private SharedPreferences sPrefs;
    private DatabaseHelper db;
    private PopulateDatabaseTask dbTask;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        sPrefs = getSharedPreferences(getString(R.string.shared_preferences),
                MODE_PRIVATE);

        currentUser = sPrefs.getString(getString(R.string.current_user_key),
                getString(R.string.no_current_user));

        db = new DatabaseHelper(getApplicationContext());

        /* Inserts database entries into database if not present */
        if(!db.containsUser(currentUser)){
            dbTask = new PopulateDatabaseTask();
            dbTask.execute();
        }



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

    private void populateDatabase() {
        String dataKey = getString(R.string.student_timetable_data_key);
        SQLiteDatabase database = db.getWritableDatabase();
        ArrayList<String> studentData = getIntent().getExtras().getStringArrayList(dataKey);
        assert studentData != null;
        ContentValues cv = new ContentValues();

        String titles[] = {"_id","_day", "_start", "_end", "_module", "_type", "_group", "_room"};

        database.execSQL("INSERT INTO users VALUES ('" + currentUser + "')");
        for(String s : studentData){
            String values[] = s.split(",");
            // cv.put("id", sPrefs.getString(getString(R.string.current_user_key), ""));
            for(int i = 0; i < titles.length; i++){
                cv.put(titles[i], values[i]);
            }
            database.insert("timetable", "_id", cv);
        }
        db.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_options, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Vibrator vb = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(100);
        Intent i = new Intent(this, MenuItems.values()[position].getActivity());
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            closeOptionsMenu();

            sPrefs.edit()
                    .putString(getString(R.string.current_user_key), getString(R.string.no_current_user))
                    .putBoolean(getString(R.string.alarms_set_key), false)
                    .apply();

            /* Cancel current user's alarms */
            List<TimetableEntry> entries[] = TimetableEntryRetriever
                    .getTimetableEntries(getApplicationContext());
            for(List<TimetableEntry> day : entries){
                for(TimetableEntry entry : day){
                    if(entry != null){
                        entry.cancelAlarm();
                    }
                }
            }

            finish();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        menuItemsList = null;
        mMenuListAdapter = null;
        mMenuListView = null;
        toolbar.dismissPopupMenus();
        toolbar = null;

        super.onDestroy();
    }

    private class PopulateDatabaseTask extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            populateDatabase();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            dbTask = null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dbTask = null;
        }
    }
}
