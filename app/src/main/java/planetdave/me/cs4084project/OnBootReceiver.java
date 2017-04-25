package planetdave.me.cs4084project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class OnBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DatabaseHelper db = new DatabaseHelper(context.getApplicationContext());

        Cursor c = db.getReadableDatabase().rawQuery("SELECT * FROM timetable", null);
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        System.out.println("OnBootReceiver Cursor: " + c.getCount());

        c.close();
    }
}
