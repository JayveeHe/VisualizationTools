package SQLiteUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

@SuppressLint("NewApi")
public class DBhelper extends SQLiteOpenHelper {

	public DBhelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	// 第一次创建数据库时会调用的函数
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table user_data(_id integer primary key autoincrement, userName varchar(14),userID varchar(20) unique,Vtexin INTEGER,Vtexout INTEGER,woman INTEGER,man INTEGER,ClusterCoefficient DOUBLE,truefollRatio DOUBLE,BilateralRatio DOUBLE,recentweibolist nvarchar)");
		db.execSQL("create table status_data(_id integer primary key autoincrement, wid varchar(20) unique,repost_timeline nvarchar)");
		Log.d(getDatabaseName(), "数据库创建成功");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}