package com.seeingvoice.www.svhearing.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

public class SaveObjectSPUtils {
    private Context context;
    private String name;

    public SaveObjectSPUtils(Context context) {
        this.context = context;
    }

    /**
     * 根据key和预期的value类型获取value的值
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T getValue(String key, Class<T> clazz){
        if (context == null){
            throw new RuntimeException("请先调用带有context，name参数的构造！");}
        SharedPreferences sp = this.context.getSharedPreferences(this.name,Context.MODE_PRIVATE);
        return getValue(key,clazz,sp);
    }

    /**
     * 对于外部不可见的过渡方法
     * @param key
     * @param clazz
     * @param sp
     * @param <T>
     * @return
     */
    private <T> T getValue(String key, Class<T> clazz, SharedPreferences sp) {
        T t;
        try {
            t = clazz.newInstance();
            if (t instanceof  Integer){
                return (T) Integer.valueOf(sp.getInt(key, 0));
            }else if (t instanceof String) {
                return (T) sp.getString(key, "");
            }else if (t instanceof Boolean) {
                return (T) Boolean.valueOf(sp.getBoolean(key, false));
            }else if (t instanceof Long) {
                return (T) Long.valueOf(sp.getLong(key, 0L));
            }else if (t instanceof Float) {
                return (T) Float.valueOf(sp.getFloat(key, 0L));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.e("system", "类型输入错误或者复杂类型无法解析[" + e.getMessage() + "]");
        }
        Log.e("system", "无法找到" + key + "对应的值");
        return null;
    }

    /**
     * 针对复杂类型存储<对象>
     * @param key
     * @param object
     */
    public void setObject(String key, Object object) {
        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        //创建字节输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //创建字节对象输出流
        ObjectOutputStream out = null;
        //然后通过将字对象进行64转码，写入key值为key的sp中
        try {
            out = new ObjectOutputStream(baos);
            out.writeObject(object);
            String objectVal = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(key, objectVal);
            editor.commit();

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (out != null){
                    out.close();
                }
                if (baos != null){
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @SuppressWarnings("unchecked")
    public <T> T getObject(String key) {
        SharedPreferences sp = this.context.getSharedPreferences(this.name, Context.MODE_PRIVATE);
        if (sp.contains(key)) {
            String objectVal = sp.getString(key, null);
            byte[] buffer = Base64.decode(objectVal, Base64.DEFAULT);
            //一样通过读取字节流，创建字节流输入流，写入对象并作强制转换
            ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
            ObjectInputStream ois = null;

            try {
                ois = new ObjectInputStream(bais);
                T t = (T) ois.readObject();
                return t;
            } catch (StreamCorruptedException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (bais != null){
                        bais.close();
                    }

                    if (ois != null){
                        ois.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}