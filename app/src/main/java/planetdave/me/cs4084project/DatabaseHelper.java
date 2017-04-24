package planetdave.me.cs4084project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by David on 24/04/2017.
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    DatabaseHelper(Context context){
        super(context, context.getString(R.string.database_name), null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE timetable " +
                "(_id TEXT," +
                "_day TEXT," +
                "_start TEXT," +
                "_end TEXT," +
                "_module TEXT," +
                "_type TEXT," +
                "_group TEXT," +
                "_room TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getDatabaseName(){
        return context.getString(R.string.database_name);
    }
}
