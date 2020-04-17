package com.example.jonas.areafoliar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DadosOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_ALTER_FOLHA_1 = "ALTER TABLE FOLHA ADD COLUMN TIPO INTEGER NOT NULL;";


    public DadosOpenHelper(Context context) {
        super(context, "Dados", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ScriptDLL.getCreateTableCliente());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(DATABASE_ALTER_FOLHA_1);
        }   
    }
}
