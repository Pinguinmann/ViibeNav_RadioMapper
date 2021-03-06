package de.beacon.tom.viibenav_radiomapper.model.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import de.beacon.tom.viibenav_radiomapper.R;
import de.beacon.tom.viibenav_radiomapper.model.dbmodels.OnyxBeaconView;


/**
 * Created by TomTheBomb on 24.07.2015.
 */
public class CustomListBeaconAdapter extends ArrayAdapter<OnyxBeaconView> {

    public CustomListBeaconAdapter(Context context){
        super(context,0);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater tomsInflater = LayoutInflater.from(getContext());
        View customView = tomsInflater.inflate(R.layout.testarea_beacons_custom_row, parent, false);

        OnyxBeaconView o = getItem(position);
        TextView _id = (TextView) customView.findViewById(R.id.id_beacons);
        TextView major = (TextView) customView.findViewById(R.id.major);
        TextView minor = (TextView) customView.findViewById(R.id.minor);
        TextView macAddress = (TextView) customView.findViewById(R.id.macAddress);

        _id.setText(""+ o.getId());
        major.setText("" + o.getMajor()+" |");
        minor.setText(""+o.getMinor()+" |");
        macAddress.setText(""+o.getMacAddress());


        return customView;
    }
}
