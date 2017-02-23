package com.example.mmkrell.timesviatext;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

class CTAHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "cta.db";
    private static final int DATABASE_VERSION = 1;

    CTAHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}