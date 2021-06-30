package com.seeingvoice.www.svhearing.heartests.hearAgeTest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Date:2019/7/17
 * Time:14:38
 * auther:zyy
 */
public class DBService {
    private SQLiteDatabase db;
    //在构造函数中打开指定数据库，并把它的引用指向db
    DBService(){
        db = SQLiteDatabase.openDatabase("/data/data/com.seeingvoice.www.svhearing/databases/question.db" +
                "",null,SQLiteDatabase.OPEN_READWRITE);
    }

    //获取数据库中的问题
    List<Question> getQuestion(){

        List<Question> list=new ArrayList<Question>();
        /*
               Cursor是结果集游标，用于对结果集进行随机访问,其实Cursor与JDBC中的ResultSet作用很相似。
             rawQuery()方法的第一个参数为select语句；第二个参数为select语句中占位符参数的值，如果select语句没有使用占位符，该参数可以设置为null。*/
        Cursor cursor =db.rawQuery("select * from question",null);
        if(cursor.getCount()>0){
            cursor.moveToFirst();//将cursor移动到第一个光标上
            int count = cursor.getCount();
            //将cursor中的每一条记录生成一个question对象，并将该question对象添加到list中
            for(int i=0;i<count;i++){
                cursor.moveToPosition(i);
                Question question = new Question();
                question.ID = cursor.getInt(cursor.getColumnIndex("ID"));
                question.question = cursor.getString(cursor.getColumnIndex("question"));
                question.answerA=cursor.getString(cursor.getColumnIndex("answerA"));
                question.answerB=cursor.getString(cursor.getColumnIndex("answerB"));
                question.answer=cursor.getInt(cursor.getColumnIndex("answer"));
                question.frequency_band =cursor.getInt(cursor.getColumnIndex("frequency_band"));
                question.explaination=cursor.getString(cursor.getColumnIndex("explaination"));
                question.hearage =cursor.getString(cursor.getColumnIndex("hearage"));
                //表示没有选择任何选项
                question.selectedAnswer = -1;
                list.add(question);
            }
        }
        return list;
    }
}
