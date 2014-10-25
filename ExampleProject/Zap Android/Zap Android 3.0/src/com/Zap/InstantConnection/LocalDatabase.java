package com.Zap.InstantConnection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class LocalDatabase {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_USERNAME = "user_name";
	public static final String KEY_PASSWORD = "pass_word";
	public static final String KEY_USERID = "user_id";
	
	
	private static final String DATABASE_NAME = "UserLogin";
	private static final String DATABASE_TABLE = "UserTable";
	private static final int DATABASE_VERSION = 1;
	
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	
	private static class DbHelper extends SQLiteOpenHelper{

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
			
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
					//KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
					KEY_ROWID + " TEXT NOT NULL, " +
					KEY_USERNAME +" TEXT NOT NULL, "+
					KEY_PASSWORD +" TEXT NOT NULL, "+
					KEY_USERID + " INT NOT NULL);"
			);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			//db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			onCreate(db);
		}
	}
	
	public LocalDatabase(Context c){
		ourContext = c;
	}
	
	
	public LocalDatabase open(){
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		ourHelper.close();	
	}


	public long createEntry(String username, String password, int userId) {
		// TODO Auto-generated method stub
		
		ContentValues cv = new ContentValues();
		cv.put(KEY_ROWID, "1");
		cv.put(KEY_USERNAME, username);
		cv.put(KEY_PASSWORD, password);
		cv.put(KEY_USERID, userId);
		return ourDatabase.insert(DATABASE_TABLE, null, cv);
		
	}
	
	public int getData() {
		// TODO Auto-generated method stub
		String[] columns = new String[]{KEY_ROWID,KEY_USERNAME, KEY_PASSWORD, KEY_USERID};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, null);
		int result = 0;
		
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iUsername = c.getColumnIndex(KEY_USERNAME);
		int iPassword = c.getColumnIndex(KEY_PASSWORD);
		int iUserId = c.getColumnIndex(KEY_USERID);
		
		
		for(c.moveToFirst(); !c.isAfterLast();c.moveToNext()){
			//result = result + c.getString(iRow) + " "+ c.getString(iUsername)+ " " + c.getString(iPassword) + " " + c.getString(iUserId)+ "\n";
			result  = c.getInt(iUserId);
		}
		c.close();
		
		return result;
	}


	public void deleteEntry() {
		// TODO Auto-generated method stub
		ourDatabase.delete(DATABASE_TABLE, KEY_ROWID+"=1", null);
	}
	
	public boolean ifExists() throws SQLException{
		String[] columns = new String[]{KEY_ROWID, KEY_USERNAME,KEY_PASSWORD, KEY_USERID};
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns,null, null, null, null, null);

		if (c.getCount()!=0){
			c.close();
			return true;
		}else{
			c.close();
			return false;
		}
	}

}
