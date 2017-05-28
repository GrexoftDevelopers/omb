package com.oneminutebefore.workout;

import android.support.v7.app.AppCompatActivity;

import com.oneminutebefore.workout.helpers.HttpTask;
import com.oneminutebefore.workout.helpers.VolleyHelper;

import java.util.ArrayList;

/**
 * Created by tahir on 13/5/17.
 */

public class BaseRequestActivity extends AppCompatActivity {

    private ArrayList<VolleyHelper> volleyHelpers;
    private ArrayList<HttpTask> httpTasks;

    @Override
    protected void onDestroy() {
        if(volleyHelpers != null && volleyHelpers.isEmpty()){
            for(VolleyHelper volleyHelper : volleyHelpers){
                volleyHelper.setCancelled(true);
            }
        }
        if(httpTasks != null && httpTasks.isEmpty()){
            for(HttpTask httpTask : this.httpTasks){
                httpTask.cancel(true);
            }
        }
        super.onDestroy();
    }

    public void addHelper(VolleyHelper volleyHelper){
        if(volleyHelpers == null){
            volleyHelpers = new ArrayList<>();
        }
        volleyHelpers.add(volleyHelper);
    }

    public void addTask(HttpTask task){
        if(httpTasks == null){
            httpTasks = new ArrayList<>();
        }
        httpTasks.add(task);
    }
}
