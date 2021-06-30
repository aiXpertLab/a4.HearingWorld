package com.seeingvoice.www.svhearing.bluetooth.util;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Date:2019/3/27
 * Time:9:37
 * auther:zyy
 */
public class BluetoothClsUtils {
    /**
     * 与设备配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean createBond(Class btClass,BluetoothDevice btDevice) throws Exception {
        Method createBondMethod = btClass.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    /**
     * 与设备解除配对 参考源码：platform/packages/apps/Settings.git
     * /Settings/src/com/android/settings/bluetooth/CachedBluetoothDevice.java
     */
    static public boolean removeBond(Class btClass,BluetoothDevice btDevice) throws Exception {
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }



    //A2DP 与  HeadSet连接
    static public boolean connect(Class btClass, BluetoothProfile proxy, BluetoothDevice btDevice) throws Exception {
        Method connectMethod = btClass.getDeclaredMethod("connect", BluetoothDevice.class);
        connectMethod.setAccessible(true);
        Boolean returnValue = (Boolean) connectMethod.invoke(proxy,btDevice);
        return returnValue.booleanValue();
    }


    //A2DP 与  HeadSet 断开连接
    static public boolean disconnect(Class btClass,BluetoothProfile proxy,BluetoothDevice btDevice) throws Exception {
        Method disconnectMethod = btClass.getDeclaredMethod("disconnect", BluetoothDevice.class);
        disconnectMethod.setAccessible(true);
        Boolean returnValue = (Boolean) disconnectMethod.invoke(proxy,btDevice);
        return returnValue.booleanValue();
    }


    /**
     *
     * @param clsShow
     */
    static public void printAllInform(Class clsShow) {
        try {
            // 取得所有方法
            Method[] hideMethod = clsShow.getMethods();
            int i = 0;
            for (; i < hideMethod.length; i++) {
                Log.e("method name", hideMethod[i].getName());
            }
            // 取得所有常量
            Field[] allFields = clsShow.getFields();
            for (i = 0; i < allFields.length; i++) {
                Log.e("Field name", allFields[i].getName());
            }
        } catch (SecurityException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // throw new RuntimeException(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
