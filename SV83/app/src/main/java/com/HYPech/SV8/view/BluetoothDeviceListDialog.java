package com.HYPech.SV8.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.HYPech.SV8.App;
import com.HYPech.SV8.adapter.BluetoothDeviceListItemAdapter;
import com.HYPech.SV8.util.CommonUtil;

import  java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Dialog for selecting a remote Bluetooth device themed with the Material Design style.
 *
 * @author Macro Yau
 */
public class BluetoothDeviceListDialog {

    /**
     * Listener for the
     */
    public interface OnDeviceSelectedListener {

        /**
         * A remote Bluetooth device is selected from the dialog.
         *
         * @param device The selected device.
         */
        void onBluetoothDeviceSelected(BluetoothDevice device);

    }

    private Context mContext;
    private OnDeviceSelectedListener mListener;
    private Set<BluetoothDevice> mDevices =new HashSet<>();
    private ArrayList<String> mNames = new ArrayList<String>() ;
    private ArrayList<String> mAddresses = new ArrayList<String>();
    private String mTitle;
    private boolean mShowAddress = true;
    private boolean mUseDarkTheme;
    BluetoothDeviceListItemAdapter mAdapter;
    SelfDialogListview dialog;

    /**
     * Constructor.
     *
     * @param context The {@link Context} to use.
     */
    public BluetoothDeviceListDialog(Context context) {
        mContext = context;
    }

    /**
     * Set a listener to be invoked when a remote Bluetooth device is selected.
     *
     * @param listener The
     */
    public void setOnDeviceSelectedListener(OnDeviceSelectedListener listener) {
        mListener = listener;
    }

    /**
     * Set the title of the dialog.
     *
     * @param title The title string.
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * Set the title of the dialog.
     *
     * @param resId The resource ID of the title string.
     */
    public void setTitle(int resId) {
        mTitle = mContext.getString(resId);
    }

    /**
     * Set the remote Bluetooth devices to be shown on the dialog for selection.
     *
     * @param devices The remote Bluetooth devices.
     */
    public void setDevices(Set<BluetoothDevice> devices) {
        mDevices = devices;

        if (devices != null) {
//            mNames.clear();
//            mAddresses.clear();
            int i = 0;
            for (BluetoothDevice d : devices) {
                mNames.add(d.getName());
                mAddresses.add(d.getAddress());
                i++;
            }
        }
    }
    public void addDevices(BluetoothDevice devices){
       boolean addDevice =  mDevices.add(devices);
        if (devices != null && devices.getName() != null && devices.getAddress() != null) {
//            mNames = new String[mDevices.size()];
//            mAddresses = new String[mDevices.size()];
//            int i = 0;
//            for (BluetoothDevice d : mDevices) {
                mNames.add(devices.getName());
                mAddresses.add(devices.getAddress());
//                i++;
//            }
            if(mAdapter!=null) {
                mAdapter.notifyDataSetChanged();
            }
        }
//        if(mAdapter!=null) {
//            mAdapter.notifyDataSetChanged();
//        }
    }
    /**
     * Show the devices' MAC addresses on the dialog.
     *
     * @param showAddress Set to true to show the MAC addresses.
     */
    public void showAddress(boolean showAddress) {
        mShowAddress = showAddress;
    }

    /**
     * Force to use the dark version of Material theme on the dialog.
     *
     * @deprecated As of version 0.1.3, the library uses the AppCompat AlertDialog. Styling of the dialog should be done in styles.xml.
     * @param useDarkTheme Set to true to use the dark theme.
     */
    @Deprecated
    public void useDarkTheme(boolean useDarkTheme) {
        mUseDarkTheme = useDarkTheme;
    }

    /**
     * Show the dialog. This must be called after setting the dialog's listener, title and devices.
     */
    public void show() {
//        if(mNames!= null && mAddresses!=null && isclear){
//            if(App.isDebug){Log.d("ljp","清楚Name 和address");}
//            mNames.clear();
//            mAddresses.clear();
//        }
        if(mAdapter == null){
            mAdapter = new BluetoothDeviceListItemAdapter(mContext, mNames, mAddresses, mShowAddress);
        }
//        if(dialog == null) {
//            dialog = new AlertDialog.Builder(mContext)
//                    .setTitle(mTitle)
//                    .setAdapter(mAdapter, null)
//                    .setView()
//                    .create();
//;           dialog.setCanceledOnTouchOutside(false);
//
//            final ListView listView = dialog.getListView();
//            if (listView != null) {
//                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        if(App.isDebug) {Log.d("ljplist", mAddresses.get(position));}
////                        App.myApplication.setBindMacDevice(mAddresses.get(position));
//                        mListener.onBluetoothDeviceSelected(CommonUtil.getAdapter(mContext).getRemoteDevice(mAddresses.get(position)));
//                        dialog.cancel();
//                    }
//                });
//            }
        if(dialog == null) {
            dialog = new SelfDialogListview(mContext);
            dialog.initListView(mAdapter);
            dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (App.isDebug) {
                        Log.d("ljplist", mAddresses.get(position));
                    }
//                        App.myApplication.setBindMacDevice(mAddresses.get(position));
                    mListener.onBluetoothDeviceSelected(CommonUtil.getAdapter(mContext).getRemoteDevice(mAddresses.get(position)));
                    dialog.cancel();
                }
            });
        }

        if(!dialog.isShowing()) {
            dialog.show();
        }

    }

}
