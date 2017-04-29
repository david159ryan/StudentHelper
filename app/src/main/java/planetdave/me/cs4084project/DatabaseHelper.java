package planetdave.me.cs4084project;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by David on 24/04/2017.
 * Uses SQLiteAssetHelper to properly copy a pre-made, pre-populated database into the apps
 * internal database.
 */
class DatabaseHelper extends SQLiteAssetHelper {

    private Context context;

    public Context getContext() {
        return context;
    }

    /**
     * DatabaseHelper constructor.
     * Look here: https://github.com/jgilfelt/android-sqlite-asset-helper
     * @param context context to use
     */
    DatabaseHelper(Context context){
        super(context, context.getString(R.string.database_name), null, 1);
        this.context = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String getDatabaseName(){
        return context.getString(R.string.database_name);
    }

    boolean containsUser(String userID){
        Cursor c = this.getReadableDatabase().rawQuery(
                "SELECT `_id` FROM users WHERE `_id` = '" + userID + "';",
                null
        );
        if(c.getCount() > 0){
            c.moveToFirst();
            System.out.println(c.getString(0));
        }
        int count = c.getCount();
        c.close();
        return count == 1;
    }
}
