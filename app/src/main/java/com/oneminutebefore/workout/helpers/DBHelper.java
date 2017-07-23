package com.oneminutebefore.workout.helpers;

/**
 * Created by husain707 on 07/06/17.
 */


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.oneminutebefore.workout.WorkoutApplication;
import com.oneminutebefore.workout.models.CompletedWorkout;
import com.oneminutebefore.workout.models.SelectedWorkout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "WorkoutDatabase.db";

    public static final String SELECTED_WORKOUT = "selected_workout";
    public static final String USER_TRACK = "user_track";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table if not exists "
                + SELECTED_WORKOUT
                + "("
                + "_id varchar(50) primary key not null,"
                + "workout_time varchar(10),"
                + "category varchar(50),"
                + "workout varchar(50),"
                + "user_id varchar(50),"
                + "uid varchar(50),"
                + "created_at bigint,"
                + "updated bigint,"
                + "updated_at bigint,"
                + "active integer)");

        db.execSQL("create table if not exists "
                + USER_TRACK
                + "("
                + "user_track_id integer primary key autoincrement not null,"
                + "workout_id varchar(50),"
                + "rep numeric,"
                + "date bigint)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


//        db.execSQL("PRAGMA writable_schema = 1");
//        db.execSQL("delete from sqlite_master where type in ('table', 'index', 'trigger')");
//        db.execSQL("PRAGMA writable_schema = 0");
        // db.execSQL("vacuum");
        // db.execSQL("PRAGMA INTEGRITY_CHECK");
        onCreate(db);

    }

    public void insertSelectedWorkout(JSONObject jsonObject){

        insertSelectedWorkout(jsonObject, false, false);
    }

    public void insertSelectedWorkout(JSONObject jsonObject, boolean insertTrack, boolean completeData){

        ContentValues contentValues = new ContentValues();
        contentValues.put("_id", jsonObject.optString("_id"));
        contentValues.put("workout_time", jsonObject.optString("workout_time"));
        if(completeData){
            contentValues.put("category", jsonObject.optJSONObject("category") != null ? jsonObject.optJSONObject("category").optString("_id") : jsonObject.optString("category"));
            contentValues.put("workout", jsonObject.optJSONObject("workout") != null ? jsonObject.optJSONObject("workout").optString("_id") : jsonObject.optString("workout"));
            contentValues.put("user_id", jsonObject.optJSONObject("user_id") != null ? jsonObject.optJSONObject("user_id").optString("_id") : jsonObject.optString("user_id"));
        }else{
            contentValues.put("category", jsonObject.optString("category"));
            contentValues.put("workout", jsonObject.optString("workout"));
            contentValues.put("user_id", jsonObject.optString("user_id"));
        }
        contentValues.put("uid", jsonObject.optString("uid"));
        contentValues.put("created_at", SelectedWorkout.getDateTimeLong(jsonObject.optString("created_at")));
        contentValues.put("updated_at", SelectedWorkout.getDateTimeLong(jsonObject.optString("updated_at")));
        contentValues.put("updated", SelectedWorkout.getDateTimeLong(jsonObject.optString("updated")));
        contentValues.put("active", jsonObject.optString("active"));

        SQLiteDatabase db = getWritableDatabase();

//        String sql = "delete from " + SELECTED_WORKOUT + " where workout_time = '" + jsonObject.optString("workout_time") + "'";
//        db.execSQL(sql);

        db.insert(SELECTED_WORKOUT, null, contentValues);

        if(insertTrack){
            JSONArray track = jsonObject.optJSONArray("user_track");
            if(track != null && track.length() > 0){
                for(int i = 0 ; i < track.length() ; i++){
                    ContentValues values = new ContentValues();
                    values.put("workout_id", jsonObject.optString("_id"));
                    JSONObject trackItem = track.optJSONObject(i);
                    values.put("date", CompletedWorkout.getDateLong(trackItem.optString("date")));
                    values.put("rep", trackItem.optInt("rep"));

                    db.insert(USER_TRACK,null,values);
                }
            }
        }
    }

    public void insertUserTrack(CompletedWorkout completedWorkout){

        ContentValues values = new ContentValues();
        values.put("workout_id", completedWorkout.getSelectedWorkoutId());
        values.put("date", completedWorkout.getDate());
        values.put("rep", completedWorkout.getRepsCount());

        SQLiteDatabase db = getWritableDatabase();
        db.insert(USER_TRACK,null,values);

    }

    public String getSelectedWorkoutIdByTime(String workoutTime){

        String sql = "select _id from " + SELECTED_WORKOUT + " where workout_time = '" + workoutTime + "' order by created_at desc";
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery(sql,null);
        if(result.moveToFirst()){
            return result.getString(0);
        }
        return null;
    }



    public SelectedWorkout getSelectedWorkoutByTime(String workoutTime){

        String sql = "select * from " + SELECTED_WORKOUT + " where workout_time = '" + workoutTime + "' order by created_at desc";
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery(sql,null);
        if(result.moveToFirst()){
            String workoutId = result.getString(result.getColumnIndex("workout"));
            WorkoutApplication application = WorkoutApplication.getmInstance();
            String timeMeridian = result.getString(result.getColumnIndex("workout_time"));
            SelectedWorkout selectedWorkout = new SelectedWorkout(application.getWorkouts().get(workoutId),Utils.getTimeKey(timeMeridian));
            selectedWorkout.setSelectedWorkoutId(result.getString(result.getColumnIndex("_id")));
            return selectedWorkout;
        }
        return null;
    }

    public boolean deleteSelectedWorkoutByTime(String workoutTime){
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(SELECTED_WORKOUT,"workout_time = '" + workoutTime + "'",null) > 0;
    }

    public HashMap<String, SelectedWorkout> getSelectedWorkouts(){

        String sql = "select * from " + SELECTED_WORKOUT + " order by created_at desc";
        SQLiteDatabase db = getReadableDatabase();
        Cursor result = db.rawQuery(sql,null);
        if(result.moveToFirst()){
            HashMap<String, SelectedWorkout> selectedWorkouts = new HashMap<>();
            do{
                String workoutId = result.getString(result.getColumnIndex("workout"));
                WorkoutApplication application = WorkoutApplication.getmInstance();
                String timeMeridian = result.getString(result.getColumnIndex("workout_time"));
                SelectedWorkout selectedWorkout = new SelectedWorkout(application.getWorkouts().get(workoutId),Utils.getTimeKey(timeMeridian));
                selectedWorkout.setSelectedWorkoutId(result.getString(result.getColumnIndex("_id")));
                selectedWorkout.setCreatedAt(result.getLong(result.getColumnIndex("created_at")));
                if(!selectedWorkouts.containsKey(selectedWorkout.getTimeKey())){
                    selectedWorkouts.put(selectedWorkout.getTimeKey(), selectedWorkout);
                }
            }while (result.moveToNext());
            return selectedWorkouts;
        }
        return null;
    }

    public ArrayList<CompletedWorkout> getTodayCompletedWorkouts(){

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND, 1);

        String sql = "SELECT " + USER_TRACK + ".rep, " + SELECTED_WORKOUT + ".*" +
                " FROM " + USER_TRACK + " JOIN " + SELECTED_WORKOUT + " ON " + USER_TRACK + ".workout_id = " + SELECTED_WORKOUT + "._id " +
                " WHERE " + USER_TRACK + ".date > " + calendar.getTimeInMillis();

        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor result = sqLiteDatabase.rawQuery(sql, null);
        if(result.moveToFirst()){
            ArrayList<CompletedWorkout> workoutsDone = new ArrayList<>();
            WorkoutApplication application = WorkoutApplication.getmInstance();
            do{
                String workoutId = result.getString(result.getColumnIndex("workout"));
                String time = result.getString(result.getColumnIndex("workout_time"));
                int rep = result.getInt(result.getColumnIndex("rep"));
                CompletedWorkout completedWorkout = new CompletedWorkout(application.getWorkouts().get(workoutId),Utils.getTimeKey(time),rep);
                workoutsDone.add(completedWorkout);
            }while (result.moveToNext());
            return workoutsDone;
        }
        return null;
    }

    public void clearData() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(SELECTED_WORKOUT,null,null);
        db.delete(USER_TRACK,null,null);
    }

}