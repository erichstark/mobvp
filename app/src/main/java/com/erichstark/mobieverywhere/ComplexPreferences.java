package com.erichstark.mobieverywhere;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by Erich on 02/12/15.
 */
public class ComplexPreferences {
    public static final String NAME = "mobvp_pref";
    private static ComplexPreferences complexPreferences;
    private static Gson GSON = new Gson();
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    private ComplexPreferences(Context context, int mode) {
        preferences = context.getSharedPreferences(NAME, mode);
        editor = preferences.edit();
    }

    public static ComplexPreferences getComplexPreferences(Context context, int mode) {

        if (complexPreferences == null) {
            complexPreferences = new ComplexPreferences(context, mode);
        }

        return complexPreferences;
    }

    public void putObject(String key, Object object) {
        if (object == null) {
            throw new IllegalArgumentException("object is null");
        }

        if ("".equals(key)) {
            throw new IllegalArgumentException("key is empty or null");
        }

        editor.putString(key, GSON.toJson(object));
    }

    public void remove(String key) {
        editor.remove(key);
    }

    public void commit() {
        editor.commit();
    }

    public <T> T getObject(String key, Class<T> a) {

        String gson = preferences.getString(key, null);
        if (gson == null) {
            return null;
        } else {
            try {
                return GSON.fromJson(gson, a);
            } catch (Exception e) {
                throw new IllegalArgumentException("Object storaged with key " + key + " is instanceof other class");
            }
        }
    }


}
