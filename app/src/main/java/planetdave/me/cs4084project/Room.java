package planetdave.me.cs4084project;

import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by David on 26/04/2017.
 */

public class Room {
    private String code;
    private String building;
    private String buildingCode;
    private String floor;
    private String room;
    private DatabaseHelper db;

    public Room(String roomCode, DatabaseHelper db){
        this.code = roomCode;
        this.db = db;
        readFloorAndBuilding();
    }

    private void readFloorAndBuilding(){
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
        }

        m = floorNumPattern.matcher(code);
        if(m.find()){
            floor = m.group();
        }

        m = roomNumPattern.matcher(code);
        if(m.find()){
            room = m.group();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getBuildingCode() {
        return buildingCode;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
