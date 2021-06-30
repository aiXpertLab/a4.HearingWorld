package seeingvoice.jskj.com.seeingvoice;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MySP {

    public static MySP mySP;
    private Object mObject;
    
    private SharedPreferences        sp = null;
    private SharedPreferences.Editor editor = null;

    public static MySP getInstance() {
        if (mySP == null) {
            synchronized (MySP.class) {if (mySP == null) {mySP = new MySP();}}
        }
        return mySP;
    }

    public void init(Context context) {
        sp = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }
    /**
     * 问题在于，这个Context哪来的我们不能确定，很大的可能性，你在某个Activity里面为了方便，直接传了个this;
     * 这样问题就来了，我们的这个类中的sInstance是一个static且强引用的，在其内部引用了一个Activity作为Context，也就是说，
     * 我们的这个Activity只要我们的项目活着，就没有办法进行内存回收。而我们的Activity的生命周期肯定没这么长，造成了内存泄漏。
     * 所以这里使用context.getApplicationContext()
     */
    private MySP() {    }

    public synchronized void setSP(String key, Object object) {
        if (editor == null) editor = sp.edit();

        String type = object.getClass().getSimpleName();
        if ("String".equals(type)) {            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {    editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {    editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {      editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {       editor.putLong(key, (Long) object);
        } else {
            if (!(object instanceof Serializable)) {
                throw new IllegalArgumentException(object.getClass().getName() + " 必须实现Serializable接口!");
            }

            // 不是基本类型则是保存对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                String productBase64 = Base64.encodeToString(
                        baos.toByteArray(), Base64.DEFAULT);
                editor.putString(key, productBase64);
                Log.d(this.getClass().getSimpleName(), "save object success");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(this.getClass().getSimpleName(), "save object error");
            }
        }
        editor.apply();
    }

    public synchronized void removeSP(String key) {
        if (editor == null) editor = sp.edit();
        editor.remove(key);
        editor.apply();
    }

    public Object getSP(String key, Object defaultObject) {
        if (defaultObject == null) {            return getObject(key);        }

        String type = defaultObject.getClass().getSimpleName();

        if ("String".equals(type)) {        return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {return sp.getInt(key,    (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {return sp.getBoolean(key,(Boolean) defaultObject);
        } else if ("Float".equals(type)) {  return sp.getFloat(key,  (Float) defaultObject);
        } else if ("Long".equals(type)) {   return sp.getLong(key,   (Long) defaultObject);
        } return getObject(key);
    }

    public void setCurrentTime(long currentTime) {  setSP("CurrentTime", currentTime);    }
    public long getCurrentTime() {return (long)     getSP("CurrentTime", "");    }

// user in String

    public void   setUPhone(String uPhone) {    setSP("uPhone", uPhone);    }
    public String getUPhone() {return (String)  getSP("uPhone", "");    }

    public void   setUPassword(String uPassword) {    setSP("uPassword", uPassword);    }
    public String getUPassword() {return (String)  getSP("uPassword", "");    }

    public void   setUAvatar(String uAvatar) {    setSP("uAvatar", uAvatar);    }
    public String getUAvatar() {return (String)  getSP("uAvatar", "");    }

    public void   setUWxId(String uWxId) {    setSP("uWxId", uWxId);    }
    public String getUWxId() {return (String)  getSP("uWxId", "");    }

    public void   setUNickName(String uNickName) {    setSP("uNickName", uNickName);    }
    public String getUNickName() {return (String)  getSP("uNickName", "");    }

    public void   setUSignature(String uSignature) {    setSP("uSignature", uSignature);    }
    public String getUSignature() {return (String)  getSP("uSignature", "");    }
    public void   setURegion(String uRegion) {    setSP("uRegion", uRegion);    }
    public String getURegion() {return (String)  getSP("uRegion", "");    }
    public void   setUGender(String uGender) {    setSP("uGender", uGender);    }
    public String getUGender() {return (String)  getSP("uGender", "");    }
    /**
     * Whether to use for the first time
     *
     * return
     */
    public boolean isFirst() {
        return (Boolean) getSP("isFirst", true);
    }

    /**
     * set user first use is false
     *
     * return
     */
    public void setFirst(Boolean isFirst) {
        setSP("isFirst", isFirst);
    }

    /**
     * Set up the first time login
     *
     * return
     */
    public boolean isLogin() {
        return (Boolean) getSP("isLogin", false);
    }

    /**
     * return
     */
    public void setLogin(Boolean isLogin) {
        setSP("isLogin", isLogin);
    }

    public void setNewMsgsUnreadNumber(int newMsgsUnreadNumber) {
        setSP("newMsgsUnreadNumber", newMsgsUnreadNumber);
    }


    public void setNewFriendsUnreadNumber(int newFriendsUnreadNumber) {
        setSP("newFriendsUnreadNumber", newFriendsUnreadNumber);
    }

    public Integer getNewFriendsUnreadNumber() {
        return (Integer) getSP("newFriendsUnreadNumber", 0);
    }

    public Object getObject(String key) {
        String wordBase64 = sp.getString(key, "");
        byte[] base64 = Base64.decode(wordBase64.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            ObjectInputStream bis = new ObjectInputStream(bais);
            mObject = bis.readObject();
            Log.d(this.getClass().getSimpleName(), "Get object success");
            return mObject;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.toString());
        }
        Log.e(this.getClass().getSimpleName(), "Get object is error");
        return null;
    }

    public void setDisclaimer(String Disclaimer) {
        setSP("Disclaimer", Disclaimer);    }
    public String getDisclaimer() {
        return (String) getSP("Disclaimer", "");
    }

    public void setWelcome(String welcome) {
        setSP("Welcome", welcome);    }
    public String getWelcome() {
        return (String) getSP("Welcome", "");
    }

    public void setPickedCity(String cityName) {
        setSP("pickedCity", cityName);
    }

    public String getPickedCity() {
        return (String) getSP("pickedCity", "");
    }

    public void setPickedDistrict(String districtName) {
        setSP("pickedDistrict", districtName);
    }

    public String getPickedDistrict() {
        return (String) getSP("pickedDistrict", "");
    }

    public void setPickedPostCode(String postCode) {
        setSP("pickedPostCode", postCode);
    }
    public String getPickedPostCode() {
        return (String) getSP("pickedPostCode", "");
    }

    /**
     * 是否开启"附近的人"
     *
     * return true:是  false:否
     */
    public boolean isOpenPeopleNearby() {
        return (Boolean) getSP("isOpenPeopleNearby", false);
    }

    /**
     * 设置是否开启附近的人
     *
     * @param isOpenPeopleNearby 是否开启附近的人
     */
    public void setOpenPeopleNearby(Boolean isOpenPeopleNearby) {
        setSP("isOpenPeopleNearby", isOpenPeopleNearby);
    }

}
