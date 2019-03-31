package com.example.yiska.project_part2.model.backend;

import android.content.Context;

import com.example.yiska.project_part2.model.datasource.DatabaseFb;

public final class BackendFactory {
    static Backend instance = null;

    public static String mode ="fb";
    static public final Backend getInstance(Context context) {

       /* if (mode == "lists") {
            if (instance == null)
              //  instance = new DatabaseList(); ;
            return instance;
        }*/
        if (mode == "fb") {
            if (instance == null)
                instance = new DatabaseFb(context);
            return instance;

        }
        else
            return null;
    }
}
