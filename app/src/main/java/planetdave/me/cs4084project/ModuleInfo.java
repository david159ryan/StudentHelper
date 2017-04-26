package planetdave.me.cs4084project;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by David on 26/04/2017.
 *
 */

public class ModuleInfo {

    private String code;
    private String title;
    private String description;
    private String lecturer;
    private Context context;
    private DatabaseHelper db;

    public ModuleInfo(String code, Context context) {
        this.code = code;
        this.context = context;
        db = new DatabaseHelper(context);
        parseCode();
    }

    private void parseCode() {
        String query =  "SELECT * FROM " + context.getString(R.string.db_table_module_details) +
                " WHERE " + context.getString(R.string.db_module_id) + " = '" + code + "';";

        Cursor c = db.getReadableDatabase().rawQuery(query, null);
        c.moveToFirst();
        title = c.getString(c.getColumnIndex(context.getString(R.string.db_module_title)));
        description = c.getString(c.getColumnIndex(context.getString(
                R.string.db_module_description)));
        lecturer = c.getString(c.getColumnIndex(context.getString(R.string.db_module_lecturer)));
        c.close();
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLecturer() {
        return lecturer;
    }

    public String getCode() {
        return code;
    }
}
