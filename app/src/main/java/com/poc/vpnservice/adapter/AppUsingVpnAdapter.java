package com.poc.vpnservice.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.poc.vpnservice.R;

import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.poc.vpnservice.common.Constants.MY_SHARED_PREFS_NAME;
import static com.poc.vpnservice.common.Constants.VPN_ALLOWED_APPS;
import static com.poc.vpnservice.common.Constants.VPN_NOT_ALLOWED_APPS;

public class AppUsingVpnAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<AppList> listStorage;
    private List<String> packageNames;
    private Context mContext;

    public AppUsingVpnAdapter(Context context, List<AppList> customizedListView, List<String> packageNameList, RemoveAllowedAppEventListener listener) {
        layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
        packageNames = packageNameList;
        mContext = context;
        removeAppEventListener = listener;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder listViewHolder;
        final AppList app;
        final String packageName;

        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.installed_app_list, parent, false);

            listViewHolder.textInListView = convertView.findViewById(R.id.list_app_name);
            listViewHolder.imageInListView = convertView.findViewById(R.id.app_icon);
            listViewHolder.switchInListView = convertView.findViewById(R.id._switch);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }

        if(null != listStorage && listStorage.size() > 0) {
            app = listStorage.get(position);
            listViewHolder.textInListView.setText(app.getName());
            listViewHolder.imageInListView.setImageDrawable(app.getIcon());
            packageName = app.getPackageName();

            listViewHolder.switchInListView.setChecked(true);

            listViewHolder.switchInListView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // do something, the isChecked will be
                    // true if the switch is in the On position
                    if(!isChecked) {
                        removeAppEventListener.onRemoveAllowedAppEvent(app, packageName);
                    }
                }
            });
        }

        return convertView;
    }

    static class ViewHolder{
        TextView textInListView;
        ImageView imageInListView;
        Switch switchInListView;
    }


    private RemoveAllowedAppEventListener removeAppEventListener;

    public interface RemoveAllowedAppEventListener {
        void onRemoveAllowedAppEvent(AppList app, String packageName);
    }
}