package com.example.dksush0828.livebusking.wallet;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class KeyDBHelper extends SQLiteOpenHelper {

    Context context;
    private SQLiteDatabase db = null;

    public KeyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;

    }

    // KeyDBHelper 가 없을때 딱 한번 실행된다.
    // 디비 만드는 역할.
    @Override
    public void onCreate(SQLiteDatabase db) {


         //String 보다 StringBuffer가 Query 만들기 편하다.
         StringBuffer sb = new StringBuffer();
         sb.append("CREATE TABLE key_list (");
         sb.append(" no INTEGER PRIMARY KEY AUTOINCREMENT, ");
         sb.append(" wallet_name TEXT, ");
         sb.append(" wallet_address TEXT ) ");


        // SQLite Database로 쿼리 실행
        db.execSQL(sb.toString());

        Log.v("wallet_sqlite ; ", "잘 맨들어짐.");


    }

    public void keyDB(){
        if(db == null){
            db = getWritableDatabase();
        }
    }


    // 키값 inset.
    public boolean insertNewKey(String wallet_name, String wallet_address) {
        SQLiteDatabase keyDB = getWritableDatabase(); // 사용할 / 사용할 수 있는 디비를 가져온다.

        try {
            StringBuffer sb = new StringBuffer();
            sb.append(" INSERT INTO key_list ( ");
            sb.append(" wallet_name, wallet_address ) ");
            sb.append(" VALUES ( ?, ? ) ");

            keyDB.execSQL(sb.toString(), new Object[]{
                    wallet_name,
                    wallet_address
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }




    // Application의 버전이 올라가서 Table 구조가 변경되었을 때 실행된다.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
