package com.irccloud.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class AddChannelFragment extends DialogFragment {
    ArrayList<ServersDataSource.Server> servers;
    Spinner spinner;
    TextView channels;
    int defaultCid = -1;
	
    class DoneClickListener implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			int cid = servers.get(spinner.getSelectedItemPosition()).cid;
			String[] splitchannels = channels.getText().toString().split(",");
			for(int i = 0; i < splitchannels.length; i++) {
				String[] channelandkey = splitchannels[i].split(" ");
				if(channelandkey.length > 1)
					NetworkConnection.getInstance().join(cid, channelandkey[0].trim(), channelandkey[1]);
				else
					NetworkConnection.getInstance().join(cid, channelandkey[0].trim(), "");
			}
			dismiss();
		}
    }

    public void setDefaultCid(int cid) {
    	defaultCid = cid;
    }
    
    @Override
    public void onResume() {
    	int pos = 0;
    	super.onResume();
    	servers = ServersDataSource.getInstance().getServers();
    	
    	ArrayList<String> servernames = new ArrayList<String>();
    	for(int i = 0; i < servers.size(); i++) {
    		servernames.add(servers.get(i).name);
    		if(servers.get(i).cid == defaultCid)
    			pos = i;
    	}
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, android.R.id.text1, servernames.toArray(new String[servernames.size()]));
    	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    	spinner.setAdapter(adapter);
    	spinner.setSelection(pos);
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
		Context ctx = getActivity();
		if(Build.VERSION.SDK_INT < 11)
			ctx = new ContextThemeWrapper(ctx, android.R.style.Theme_Dialog);
		LayoutInflater inflater = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    	
    	View v = inflater.inflate(R.layout.dialog_add_channel,null);
    	spinner = (Spinner)v.findViewById(R.id.networkSpinner);
    	channels = (TextView)v.findViewById(R.id.channels);
    	Button b = (Button)v.findViewById(R.id.addBtn);
    	b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
	        	EditConnectionFragment newFragment = new EditConnectionFragment();
	            newFragment.show(getActivity().getSupportFragmentManager(), "editconnection");
			}
    	});
    	
    	return new AlertDialog.Builder(ctx)
                .setTitle("Join A Channel")
                .setView(v)
                .setPositiveButton("Join", new DoneClickListener())
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
                })
                .create();
    }
}
