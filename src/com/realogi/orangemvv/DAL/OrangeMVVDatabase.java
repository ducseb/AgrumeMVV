package com.realogi.orangemvv.DAL;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrangeMVVDatabase extends SQLiteOpenHelper {

	 private static String DATABASE_NAME = "orangeMVV";
     private static int DATABASE_VERSION = 1;
     
     
     public OrangeMVVDatabase(Context context)
	 {
    	 super(context,DATABASE_NAME,null,DATABASE_VERSION);
	 }
     
     
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		
		
		 db.execSQL("CREATE TABLE T_MESSAGES ("+
                 "MES_I_ID  INTEGER PRIMARY KEY AUTOINCREMENT,"+
				 "MES_I_ID_MAIL INTEGER,"+
                 "MES_DT_DATE  datetime,"+
                 "MES_S_NUMERO  TEXT,"+
                 "MES_S_CONTACT  TEXT,"+
                 "MES_S_FICHIER  TEXT,"+
                 "MES_I_LU  INTEGER,"+
                 "MES_I_NBSECONDE INTEGER)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 db.execSQL("DROP TABLE IF EXISTS T_MESSAGES");
		 onCreate(db);  
		
	}

}
