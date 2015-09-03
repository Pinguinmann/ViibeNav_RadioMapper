package de.beacon.tom.viibenav_radiomapper.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import de.beacon.tom.viibenav_radiomapper.model.dbmodels.AnchorPointDBModel;
import de.beacon.tom.viibenav_radiomapper.model.dbmodels.BeaconMedianToAnchorDBModel;
import de.beacon.tom.viibenav_radiomapper.model.dbmodels.InfoDBModel;
import de.beacon.tom.viibenav_radiomapper.model.dbmodels.MedianDBModel;
import de.beacon.tom.viibenav_radiomapper.model.dbmodels.OnyxBeaconDBModel;
import de.beacon.tom.viibenav_radiomapper.model.position.MacToMedian;
import de.beacon.tom.viibenav_radiomapper.model.position.neighbor.DeviationToCoord;


/**
 * Created by TomTheBomb on 14.07.2015.
 */
public class DBHandler extends SQLiteOpenHelper {

    private static final String TAG = "DBHandler";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "radiomap.db";

    // ANCHORS TABLE
    public static final String TABLE_ANCHORS = "anchorpoints";
    public static final String ANCHORS_COLUMN_ID = "_id";
    // POSITION OF ANCHOR
    public static final String COLUMN_X = "x";
    public static final String COLUMN_Y = "y";
    public static final String COLUMN_FLOOR = "floor";
    public static final String COLUMN_FRONT = "front";
    public static final String COLUMN_BACK = "back";
    public static final String COLUMN_INFO_ID = "infoid";

    // MAXIMUM AMOUNT OF BEACONS IN ONE TABLE
    public static final String TABLE_BEACON_MEDIAN_TO_ANCHOR = "beaconmediantoanchor";
    public static final String BEACON_MEDIAN_TO_ANCHOR_ID = "id";
    public static final String COLUMN_BEACON_1 = "beacon1";
    public static final String COLUMN_BEACON_2 = "beacon2";
    public static final String COLUMN_BEACON_3 = "beacon3";
    public static final String COLUMN_BEACON_4 = "beacon4";
    public static final String COLUMN_BEACON_5 = "beacon5";
    public static final String COLUMN_BEACON_6 = "beacon6";

    // MEDIANS TABLE
    public static final String TABLE_MEDIANS = "medians";
    public static final String MEDIANS_COLUMN_ID = "_id";
    public static final String COLUMN_MEDIAN_VALUE = "median";
    public static final String COLUMN_MACADDRESS = "macaddress";

    // BEACONS TABLE
    public static final String TABLE_BEACONS = "beacons";
    public static final String BEACONS_COLUMN_ID = "id";
    public static final String COLUMN_MAJOR = "major";
    public static final String COLUMN_MINOR = "minor";

    // INFO TABLE
    public static final String TABLE_INFO = "info";
    public static final String INFO_COLUMN_ID = "id";
    public static final String COLUMN_PERSON_NAME = "personname";
    public static final String COLUMN_ROOM_NAME = "roomname";
    public static final String COLUMN_ENVIRONMENT = "environment";
    public static final String COLUMN_CATEGORY = "category";
    private static DBHandler singleton;

    private Context context;

    private DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);

        this.context = context;
    }

    public static DBHandler createDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        // Avoid possible errors with multiple threads accessing this method -> synchronized
        synchronized(DBHandler.class) {
            if (singleton == null) {
                singleton = new DBHandler(context, name, factory, version);
            }
        }
        return singleton;
    }

    public static DBHandler getDB(){
        return singleton;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {


        // CREATE BEACONS TABLE
        String query1 = "CREATE TABLE "+ TABLE_BEACONS + "(" +
                "'"+ BEACONS_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_MACADDRESS +"'"+ " TEXT UNIQUE, " +
                "'"+ COLUMN_MAJOR+"'"+ " INTEGER, "+
                "'"+ COLUMN_MINOR +"'"+ " INTEGER "+
                ");";
        db.execSQL(query1);

        // CREATE MEDIANS TABLE - laut: http://www.w3schools.com/sql/sql_foreignkey.asp
        String query2 = "CREATE TABLE "+ TABLE_MEDIANS + "(" +
                "'"+ MEDIANS_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_MEDIAN_VALUE+"'"+ " INTEGER, "+
                "'"+ COLUMN_MACADDRESS +"'"+ " TEXT "/*FOREIGN KEY REFERENCES "+TABLE_BEACONS+"("+BEACONS_COLUMN_ID+")"*/+
                ");";
        db.execSQL(query2);

        // CREATE ANCHORS TABLE
        String query3 = "CREATE TABLE "+ TABLE_ANCHORS + "(" +
                "'"+ ANCHORS_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_X +"'"+ " INTEGER, "+
                "'"+ COLUMN_Y +"'"+ " INTEGER, "+
                "'"+ COLUMN_FLOOR +"'"+ " INTEGER, "+
                // 90 degrees
                "'"+ COLUMN_FRONT +"'"+ " INTEGER,"+
                // 270 degrees
                "'"+ COLUMN_BACK +"'"+ " INTEGER,"+
                "'"+ COLUMN_INFO_ID +"'"+ " INTEGER  " +
                ");";
        db.execSQL(query3);

        // CREATE INFO TABLE
        String query4 = "CREATE TABLE "+ TABLE_INFO + "(" +
                "'"+ INFO_COLUMN_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_PERSON_NAME +"'"+ " TEXT, "+
                "'"+ COLUMN_ROOM_NAME +"'"+ " TEXT, "+
                "'"+ COLUMN_ENVIRONMENT +"'"+ " TEXT, "+
                "'"+ COLUMN_CATEGORY+"'"+ " TEXT "+
                ");";
        db.execSQL(query4);

        // CREATE INFO TABLE
        String query5 = "CREATE TABLE "+ TABLE_BEACON_MEDIAN_TO_ANCHOR + "(" +
                "'"+ BEACON_MEDIAN_TO_ANCHOR_ID +"'"+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "'"+ COLUMN_BEACON_1 +"'"+ " INTEGER, "+
                "'"+ COLUMN_BEACON_2 +"'"+ " INTEGER, "+
                "'"+ COLUMN_BEACON_3 +"'"+ " INTEGER, "+
                "'"+ COLUMN_BEACON_4 +"'"+ " INTEGER, "+
                "'"+ COLUMN_BEACON_5 +"'"+ " INTEGER, "+
                "'"+ COLUMN_BEACON_6 +"'"+ " INTEGER "+
                ");";
        db.execSQL(query5);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ANCHORS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_MEDIANS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_BEACONS);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_INFO);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_BEACON_MEDIAN_TO_ANCHOR);
        onCreate(db);
    }

    // Add a new row to the database
    public void addAnchor(AnchorPoint a, AddInfo addInfo){
        SQLiteDatabase db = getWritableDatabase();

        int anchor_COLUMN_ID_FRONT = getBeaconMedianToAnchorID(db,a.getFront());
        int anchor_COLUMN_ID_BACK = getBeaconMedianToAnchorID(db,a.getBack());

        //INSERT REALTED-MEDIANS IN ANCHORTABLE
        ContentValues valuesAnchor= new ContentValues();
        // 1.param: COLUMN_NAME     2.param VALUE
        valuesAnchor.put(COLUMN_X, a.getCoordinate().getX());
        valuesAnchor.put(COLUMN_Y, a.getCoordinate().getY());
        valuesAnchor.put(COLUMN_FLOOR, a.getCoordinate().getFloor());
        valuesAnchor.put(COLUMN_FRONT, anchor_COLUMN_ID_FRONT);
        valuesAnchor.put(COLUMN_BACK, anchor_COLUMN_ID_BACK);

        // Add Additional Info
        if(addInfo.hasAddInfo()){
            Log.d(TAG, "NAME " + addInfo.getPerson_name() + " Room " + addInfo.getRoom_name() +
                    " ENV: " + addInfo.getEnvironment());

            ContentValues valuesAddInfo = new ContentValues();
            if(addInfo.hasPersonInfo())
                valuesAddInfo.put(COLUMN_PERSON_NAME, addInfo.getPerson_name());
            if(addInfo.hasRoomInfo())
                valuesAddInfo.put(COLUMN_ROOM_NAME, addInfo.getRoom_name());
            if(addInfo.hasEnvironmentInfo())
                valuesAddInfo.put(COLUMN_ENVIRONMENT, addInfo.getEnvironment());
            if(addInfo.hasCategoryInfo())
                valuesAddInfo.put(COLUMN_CATEGORY, addInfo.getCategory());

            Log.d(TAG, " Insert table info  "+db.insertOrThrow(TABLE_INFO, null, valuesAddInfo));

            int infoID = getLastID(db,TABLE_INFO, INFO_COLUMN_ID);
            Log.d(TAG, "ID " + infoID);
            valuesAnchor.put(COLUMN_INFO_ID, infoID);
        }

        db.insertOrThrow(TABLE_ANCHORS, null, valuesAnchor);

        db.close();
    }

    public int getBeaconMedianToAnchorID(SQLiteDatabase db, BeaconsToOrient input){
        ArrayList<Integer> relatedMedians = new ArrayList<>();
        for(OnyxBeacon tmp : input.getBeaconArray()){

            // INSERT INTO BEACONS TABLE IF NOT EXISTS
            ContentValues values = new ContentValues();
            // 1.param: COLUMN_NAME     2.param VALUE
            values.put(COLUMN_MACADDRESS, tmp.getMacAddressStr());
            values.put(COLUMN_MAJOR, tmp.getMajor());
            values.put(COLUMN_MINOR, tmp.getMinor());

            //  laut: http://stackoverflow.com/questions/6918206/android-sqlite-insert-or-ignore-doesnt-work
            db.insertWithOnConflict(TABLE_BEACONS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
//            db.insert(TABLE_BEACONS, null, values);

            // INSERT INTO MEDIANSTABLE
            ContentValues valuesMedian = new ContentValues();
            valuesMedian.put(COLUMN_MEDIAN_VALUE, tmp.getMedianRSSI());
            valuesMedian.put(COLUMN_MACADDRESS, tmp.getMacAddressStr());
            db.insert(TABLE_MEDIANS, null, valuesMedian);


            relatedMedians.add(getLastID(db,TABLE_MEDIANS, MEDIANS_COLUMN_ID));
        }


//        Log.d(TAG,"TEST SAVING: "+relatedMedians.get(0));

        // Insert Beacons Medians
        ContentValues valuesBeaconMedianToAnchor = new ContentValues();
        valuesBeaconMedianToAnchor.put(COLUMN_BEACON_1,relatedMedians.get(0));
        valuesBeaconMedianToAnchor.put(COLUMN_BEACON_2,relatedMedians.get(1));
        valuesBeaconMedianToAnchor.put(COLUMN_BEACON_3,relatedMedians.get(2));
        valuesBeaconMedianToAnchor.put(COLUMN_BEACON_4,relatedMedians.get(3));
        if(relatedMedians.size()>4)
            valuesBeaconMedianToAnchor.put(COLUMN_BEACON_5,relatedMedians.get(4));
        else
            valuesBeaconMedianToAnchor.put(COLUMN_BEACON_5,0);
        if(relatedMedians.size()>5)
            valuesBeaconMedianToAnchor.put(COLUMN_BEACON_6, relatedMedians.get(5));
        else
            valuesBeaconMedianToAnchor.put(COLUMN_BEACON_6, 0);

        db.insert(TABLE_BEACON_MEDIAN_TO_ANCHOR, null, valuesBeaconMedianToAnchor);

        return getLastID(db,TABLE_BEACON_MEDIAN_TO_ANCHOR, BEACON_MEDIAN_TO_ANCHOR_ID);
    }

    /**
     * Gets the most recent ID, which is the latest inserted entry for a specific TABLE
     * @param db
     * @param TABLE
     * @param id
     * @return
     */
    public int getLastID(SQLiteDatabase db, final String TABLE, final String id) {
        final String query = "SELECT MAX("+id+") FROM '" + TABLE+ "'";
        Cursor cur = db.rawQuery(query, null);
        cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();
        return ID;
    }


    public void deleteAllTables(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_ANCHORS+"'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_MEDIANS+"'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_BEACONS+"'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_INFO+"'");
        db.execSQL("DROP TABLE IF EXISTS '" + TABLE_BEACON_MEDIAN_TO_ANCHOR + "'");
        onCreate(db);
    }


    /*
    Uses euclidean distance
     */
    private String calcEuclideanDB_Cmd(double median){
        return "SQRT(ABS(MAX(POWER("+COLUMN_MEDIAN_VALUE+" - "+median+"))))";
    }

    /*
    Uses Manhatten distrance for DB
     */
    private String calcManhattenDB_Cmd(double median){
       return "ABS(MIN("+COLUMN_MEDIAN_VALUE+" - "+median+"))";
    }


    public ArrayList<DeviationToCoord> getAllDistancesFromMedians(MacToMedian[] map, int maxResults, int threshold){
        SQLiteDatabase db = getWritableDatabase();

        ArrayList<DeviationToCoord> devsToCoords = new ArrayList<>();
        final String LOCAL_COLUMN_DEVIATION = "deviation";

        for(int i=0;i<map.length;i++) {
            final String macAddress = map[i].getMacAddressStr();
            final double median = map[i].getMedian();
//            Log.d(TAG, "MEDIAN IN LOOP "+median);

            String query = "SELECT " + TABLE_ANCHORS + "." + COLUMN_FLOOR + "," + TABLE_ANCHORS + "." + COLUMN_X + ", " + TABLE_ANCHORS + "." + COLUMN_Y + "," + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + ", " + calcManhattenDB_Cmd(median) + " AS " + LOCAL_COLUMN_DEVIATION + " " +
                    " FROM '" + TABLE_MEDIANS + "' JOIN '" + TABLE_ANCHORS + "' JOIN '" + TABLE_BEACON_MEDIAN_TO_ANCHOR + "' WHERE macAddress = '" + macAddress  + "' AND " +
                    " ( "    + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_1 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + " " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_2 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_3 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_4 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_5 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "   OR " + TABLE_BEACON_MEDIAN_TO_ANCHOR + "." + COLUMN_BEACON_6 + " = " + TABLE_MEDIANS + "." + MEDIANS_COLUMN_ID + "  " +
                    "  ) " +
                    " GROUP BY " + COLUMN_MEDIAN_VALUE + " HAVING deviation <=" + threshold + " ORDER BY " + LOCAL_COLUMN_DEVIATION + " ASC LIMIT " + maxResults + ";";

            // IMPORTANT - NOTE:
            // IT MAKES SENSE TO SET UP THE LIMIT TO 5 WHEN MULTIPLE ANCHORS ARE SET IN THE RADIO MAP
            // BUT NOW FOR TESTING PURPOSES IT DOES NOT MAKE SENSE -> TESTCASE: I ONLY HAVE 2 ANCHORS IN MY MAP IF I SET LIMIT TO >1
            // I WILL GET BOTH ANCHORS AS POSSIBLE, BUT I ONLY WANT THE ONE MOST LIKELIEST ANCHORPOINT !

            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();

            while (!c.isAfterLast()) {
                Coordinate coordinate = new Coordinate(c.getInt(c.getColumnIndex(COLUMN_FLOOR)), c.getInt(c.getColumnIndex(COLUMN_X)), c.getInt(c.getColumnIndex(COLUMN_Y)));

                double deviation = c.getInt(c.getColumnIndex(LOCAL_COLUMN_DEVIATION));
                devsToCoords.add(new DeviationToCoord(deviation, coordinate));

                Log.d(TAG, "Deviation-Median" + c.getInt(c.getColumnIndex(MEDIANS_COLUMN_ID)) +
                        " deviation: " + deviation +
                        " -> Coord: " + coordinate + " macAddress " + macAddress);
                c.moveToNext();
            }
        }
        db.close();
//        return devsToCoords.toArray(new DeviationToCoord[devsToCoords.size()]);
        return devsToCoords;
    }




    public Coordinate getCoordFromAnchorId(int id){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT "+COLUMN_X+", "+COLUMN_Y+", "+COLUMN_FLOOR+" FROM '"+TABLE_ANCHORS + "' WHERE "+ANCHORS_COLUMN_ID+" = '"+id+"';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query,null);
        // Move to the first row in your results
        c.moveToFirst();

        int x = -1;
        int y = -1;
        int floor = -1;

        while(!c.isAfterLast()){
            x = c.getInt(c.getColumnIndex(COLUMN_X));
            y = c.getInt(c.getColumnIndex(COLUMN_Y));
            floor = c.getInt(c.getColumnIndex(COLUMN_FLOOR));
            c.moveToNext();
        }

        c.close();
        db.close();
        Coordinate coord = new Coordinate(floor,x,y);
        return coord;
    }


    public void getAllAnchors(){
        ArrayList<AnchorPointDBModel> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '"+TABLE_ANCHORS + "';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query,null);
        // Move to the first row in your results
        c.moveToFirst();

        int _id = 0;
        int x = 0;
        int y = 0;
        int floor = 0;
        int addInfoID = 0;
        int front_id = 0;
        int back_id = 0;

        while(!c.isAfterLast()){
            _id = c.getInt(c.getColumnIndex(ANCHORS_COLUMN_ID));
            x = c.getInt(c.getColumnIndex(COLUMN_X));
            y = c.getInt(c.getColumnIndex(COLUMN_Y));
            floor = c.getInt(c.getColumnIndex(COLUMN_FLOOR));

            front_id = c.getInt(c.getColumnIndex(COLUMN_FRONT));
            back_id = c.getInt(c.getColumnIndex(COLUMN_BACK));

            addInfoID = c.getInt(c.getColumnIndex(COLUMN_INFO_ID));

            res.add(new AnchorPointDBModel(_id,new Coordinate(floor,x,y),front_id, back_id,addInfoID));
            c.moveToNext();
        }

        Log.d(TAG, "DONE FETCHING ANCHORLIST, SIZE:" + res.size());
        c.close();
        db.close();
        AnchorPointDBModel.setAllAnchors(res);
    }
//
    public void getAllBeacons(){
        ArrayList<OnyxBeaconDBModel> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '"+TABLE_BEACONS + "';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query,null);
        // Move to the first row in your results
        c.moveToFirst();

        int _id = 0;
        int major = 0;
        int minor = 0;
        String macAddress = "";

        while(!c.isAfterLast()){
            _id = c.getInt(c.getColumnIndex(BEACONS_COLUMN_ID));
            major = c.getInt(c.getColumnIndex(COLUMN_MAJOR));
            minor = c.getInt(c.getColumnIndex(COLUMN_MINOR));
            macAddress = c.getString(c.getColumnIndex(COLUMN_MACADDRESS));
            res.add(new OnyxBeaconDBModel(_id,major,minor,macAddress));
            c.moveToNext();
        }

        Log.d(TAG, "DONE FETCHING BEACONSLIST SIZE:" + res.size());
        c.close();
        db.close();
        OnyxBeaconDBModel.setAllBeacons(res);
    }

    public void getAllMedians(){
        ArrayList<MedianDBModel> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '"+TABLE_MEDIANS + "';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query,null);
        // Move to the first row in your results
        c.moveToFirst();

        int _id = 0;
        double median = 0;
        String macAddress = "";

        while(!c.isAfterLast()){
            _id = c.getInt(c.getColumnIndex(MEDIANS_COLUMN_ID));
            median = c.getDouble(c.getColumnIndex(COLUMN_MEDIAN_VALUE));
            macAddress = c.getString(c.getColumnIndex(COLUMN_MACADDRESS));
            res.add(new MedianDBModel(_id,median,macAddress));
            c.moveToNext();
        }

        Log.d(TAG, "DONE FETCHING MEDIANSLIST SIZE:" + res.size());
        c.close();
        db.close();
        MedianDBModel.setAllMedians(res);
    }

    public void getAllInfo(){
        ArrayList<InfoDBModel> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '"+TABLE_INFO + "';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query,null);
        // Move to the first row in your results
        c.moveToFirst();

        int id = 0;
        String person_name = "";
        String room_name = "";
        String environment = "";
        String category = "";

        while(!c.isAfterLast()){
            id = c.getInt(c.getColumnIndex(INFO_COLUMN_ID));
            person_name = c.getString(c.getColumnIndex(COLUMN_PERSON_NAME));
            room_name = c.getString(c.getColumnIndex(COLUMN_ROOM_NAME));
            environment = c.getString(c.getColumnIndex(COLUMN_ENVIRONMENT));
            category = c.getString(c.getColumnIndex(COLUMN_CATEGORY));
            res.add(new InfoDBModel(id,person_name,room_name,environment,category));
            c.moveToNext();
        }

        Log.d(TAG,"DONE FETCHING INFOLIST SIZE:"+res.size());
        c.close();
        db.close();
        InfoDBModel.setAllInfo(res);
    }

    public void getAllBeaconMedianToAnchor(){
        ArrayList<BeaconMedianToAnchorDBModel> res = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM '"+TABLE_BEACON_MEDIAN_TO_ANCHOR + "';";

        // Cursor point to a location in your results
        Cursor c = db.rawQuery(query,null);
        // Move to the first row in your results
        c.moveToFirst();

        int id = 0;
        int beacon_1 = 0;
        int beacon_2 = 0;
        int beacon_3 = 0;
        int beacon_4 = 0;
        int beacon_5 = 0;
        int beacon_6 = 0;

        while(!c.isAfterLast()){
            id = c.getInt(c.getColumnIndex(BEACON_MEDIAN_TO_ANCHOR_ID));
            beacon_1 = c.getInt(c.getColumnIndex(COLUMN_BEACON_1));
            beacon_2 = c.getInt(c.getColumnIndex(COLUMN_BEACON_2));
            beacon_3 = c.getInt(c.getColumnIndex(COLUMN_BEACON_3));
            beacon_4 = c.getInt(c.getColumnIndex(COLUMN_BEACON_4));
            beacon_5 = c.getInt(c.getColumnIndex(COLUMN_BEACON_5));
            beacon_6 = c.getInt(c.getColumnIndex(COLUMN_BEACON_6));
            res.add(new BeaconMedianToAnchorDBModel(id,beacon_1,beacon_2,beacon_3,beacon_4,beacon_5,beacon_6));
            c.moveToNext();
        }

        Log.d(TAG, "DONE FETCHING BEACMEDTOANCH SIZE:"+res.size());
        c.close();
        db.close();
        BeaconMedianToAnchorDBModel.setAllBeaconMedianToAnchor(res.toArray(new BeaconMedianToAnchorDBModel[res.size()]));
    }



    public String getDBPath(){
        return context.getDatabasePath(DBHandler.DATABASE_NAME).toString();
    }


}
