package planetdave.me.cs4084project;

import android.content.Context;
import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David on 26/04/2017.
 * Interprets a room code and extracts the building, floor and room number
 */
class Room {
    private String code;
    private String building;
    private String buildingCode;
    private String floor;
    private String room;

    /**
     * Constructor
     * @param roomCode room code
     */
    Room(String roomCode, Context context){
        this.code = roomCode;
        readFloorAndBuilding(context);
    }

    /**
     * Extracts useful data from room code and stores them in data members
     * @param context context for database initialisation
     */
    private void readFloorAndBuilding(Context context){
        DatabaseHelper db = new DatabaseHelper(context.getApplicationContext());
        String buildingCodePatternString = "^(KB)|([A-Z]{1})|([A-Z]{2})|([A-Z]{3})";
        String floorPatternString = "[G0BM123]{1}";
        String roomNumberPatternString = "[0-9]{1,3}[A-Z]?$";

        Pattern buildingCodePattern = Pattern.compile("("+buildingCodePatternString+")" +
                "(?=("+floorPatternString+"))");
        Pattern floorNumPattern = Pattern.compile("(?<=(" + buildingCodePatternString + "))" +
                floorPatternString);
        Pattern roomNumPattern = Pattern.compile("(?<=(" + floorPatternString + "))" +
                roomNumberPatternString);

        Matcher m = buildingCodePattern.matcher(code);
        if(m.find()){
            buildingCode = m.group();
            Cursor c = db.getReadableDatabase().rawQuery("SELECT " +
                    db.getContext().getString(R.string.db_buildings_name) + " FROM " +
                    db.getContext().getString(R.string.db_table_buildings) + " WHERE " +
                    db.getContext().getString(R.string.db_buildings_id) + " = '" + buildingCode +
                            "';",
                    null);
            c.moveToFirst();
            building = c.getString(0);
            c.close();
        }

        m = floorNumPattern.matcher(code);
        if(m.find()){
            floor = m.group();
        }

        m = roomNumPattern.matcher(code);
        if(m.find()){
            room = m.group();
        }
        db.close();
    }

    String getCode() {
        return code;
    }


    String getBuilding() {
        return building;
    }


    String getFloor() {
        return floor;
    }

    String getBuildingCode() {
        return buildingCode;
    }

    String getRoom() {
        return room;
    }

}
