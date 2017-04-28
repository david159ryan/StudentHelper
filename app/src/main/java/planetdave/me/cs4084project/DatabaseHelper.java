package planetdave.me.cs4084project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by David on 24/04/2017.
 *
 */

class DatabaseHelper extends SQLiteAssetHelper {

    private Context context;

    public Context getContext() {
        return context;
    }

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
}
