package com.hypech.SV3.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hypech.SV3.R;

import java.util.ArrayList;

/**
 * Adapter for
 *
 * @author Macro Yau
 */
public class BluetoothDeviceListItemAdapter extends BaseAdapter implements View.OnClickListener {

    private  Context mContext;
    private ArrayList<String> mNames, mAddresses;
    private  boolean mShowAddress;

    public BluetoothDeviceListItemAdapter(Context context, ArrayList names, ArrayList addresses, boolean showAddress) {
        mContext = context;
        mNames = names;
        mAddresses = addresses;
        mShowAddress = showAddress;
    }
    public void update(ArrayList name,ArrayList address){
        mAddresses = address;
        mNames = name;
    }
    @Override
    public int getCount() {
        if(mAddresses == null || mAddresses.size() ==0){
            return 0;
        }else {
            return mAddresses.size();
        }
    }

    @Override
    public String getItem(int position) {
        return mAddresses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.dialog_devicelistitem, null);
        ((TextView) view.findViewById(R.id.device_name)).setText(mNames.get(position));
        if (mShowAddress)
            ((TextView) view.findViewById(R.id.device_address)).setText(mAddresses.get(position));
        return view;
    }

    @Override
    public void onClick(View v) {

    }

}
