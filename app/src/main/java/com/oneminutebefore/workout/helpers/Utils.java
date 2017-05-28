package com.oneminutebefore.workout.helpers;

/**
 * Created by tahir on 13/5/17.
 */

public class Utils {






    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }



}
