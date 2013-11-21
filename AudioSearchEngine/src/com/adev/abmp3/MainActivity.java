package com.adev.abmp3;

import java.io.File;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar;
import com.codeimage.music.dowloader.R;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.pad.android.iappad.AdController;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

public class MainActivity extends SherlockFragmentActivity implements ActionBar.OnNavigationListener {

	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private AdController myController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		myController = new AdController(this, getString(R.string.leadbolt_id));
		myController.loadAd();
		
		
		
		
		startService(new Intent(this, DownloadService.class));
		
		rateapp();
		
		//Prepare Pre-Requisites
		String path=Environment.getExternalStorageDirectory()+"/music/"+getString(R.string.app_name);  
		boolean exists = (new File(path)).exists();
		if (!exists){
			new File(path).mkdirs();
		}
		
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		actionBar.setListNavigationCallbacks(
				new ArrayAdapter<String>(getSupportActionBar().getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.fragment_one),
								getString(R.string.fragment_two),
								getString(R.string.fragment_three), }), this);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getSupportActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Fragment fragment = null;
		
		switch(position){
			case 0:
				fragment = new SearchFragment();
				break;
			case 1:
				fragment = new DownloadFragment();
				break;
			case 2:
				fragment = new AppWallFragment();
				break;
		}
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		
		return true;
	}
	
	public void onDestroy(){
		myController.destroyAd();
		super.onDestroy();
	}
	
	public void rateapp(){		
		final String ekey = "voted";
		final String test = "voter";
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean hasBeenShown = prefs.getBoolean(ekey, false);
		final int testr = prefs.getInt(test, 0);
		if(hasBeenShown == false && testr >= 2){
			String title = "Rate me in market";
			
			String message = "Dear users, If you like our app, please give us 5 stars. Your sustained support is the source of our improvement.";
 
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setTitle(title)
					.setMessage(message)
					.setPositiveButton("Vote", new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialogInterface, int i) {
							MainActivity.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getString(R.string.package_url))));
							SharedPreferences.Editor editor = prefs.edit();
    						editor.putBoolean(ekey, true);
							editor.commit();
							dialogInterface.dismiss();
						}
					})
					.setNegativeButton("Cancel", new Dialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if(testr >= 4){
								SharedPreferences.Editor editor = prefs.edit();
								editor.putBoolean(ekey, true);
								editor.commit();
							}else{
								SharedPreferences.Editor editor = prefs.edit();
								editor.putInt(test, testr+1);
								editor.commit();
							}
							dialog.dismiss();
						}
					});
			builder.create().show();
		}else{
			SharedPreferences.Editor editor = prefs.edit();
			editor.putInt(test, testr+1);
			editor.commit();
		}
	}
	
}
