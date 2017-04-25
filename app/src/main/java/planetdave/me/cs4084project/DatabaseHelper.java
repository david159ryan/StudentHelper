package planetdave.me.cs4084project;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Created by David on 24/04/2017.
 *
 */

class DatabaseHelper extends SQLiteAssetHelper {

    private Context context;

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
