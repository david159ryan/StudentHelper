package planetdave.me.cs4084project;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by David on 26/04/2017.
 *
 * Looks up a module code from the pre-populated module_details table
 * and stores the information in a useful format
 */
class ModuleInfo {

    private String code;
    private String title;
    private String description;
    private String lecturer;
    private Context context;
    private DatabaseHelper db;


    /**
     * Constructor
     * @param code module
     * @param context context
     */
    ModuleInfo(String code, Context context) {
        this.code = code;
        this.context = context;
        parseCode();
    }

    /**
     * Queries the database for the details of the provided module and stores result
     * in the classes data members
     */
    private void parseCode() {
        String query =  "SELECT * FROM " + context.getString(R.string.db_table_module_details) +
                " WHERE " + context.getString(R.string.db_module_id) + " = '" + code + "';";

        db = new DatabaseHelper(context);
        Cursor c = db.getReadableDatabase().rawQuery(query, null);
        c.moveToFirst();
        title = c.getString(c.getColumnIndex(context.getString(R.string.db_module_title)));
        description = c.getString(c.getColumnIndex(context.getString(
                R.string.db_module_description)));
        lecturer = c.getString(c.getColumnIndex(context.getString(R.string.db_module_lecturer)));
        c.close();
        db.close();
    }

    /**
     * Returns module title
     * @return module title
     */
    String getTitle() {
        return title;
    }

    /**
     * Returns module description
     * @return module description
     */
    String getDescription() {
        return description;
    }

    /**
     * Returns modules Lecturer
     * @return module lecturer, if known
     */
    String getLecturer() {
        return lecturer;
    }

    /**
     * Returns module code
     * @return module code
     */
    String getCode() {
        return code;
    }
}
