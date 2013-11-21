package com.adev.abmp3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ads.*;
import com.actionbarsherlock.app.SherlockListFragment;
import com.codeimage.music.dowloader.R;

public class SearchFragment extends SherlockListFragment {	
	public String searchstring;
	private RetrieveSearch retrievesearch;
	public MyAdapter adapter;
	public int fail = 0, ia;
	public View inflate;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.search, container, false);
       
		
		
		final EditText searchEdit = (EditText)inflate.findViewById(R.id.searchEdit);  
		
		
		
		
        searchEdit.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            		if(searchEdit.getText().toString().equals("")){
            			toastmake("Nothing Inputed");
            		}else{
            			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            			imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
            			
            			searchstring = searchEdit.getText().toString();
            			startsearch(inflate);            			
            		}
                    return true;
                }
                return false;
			}
        });
        
        final ImageButton searchbtn = (ImageButton)inflate.findViewById(R.id.search_ib);
        searchbtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
        		if(searchEdit.getText().toString().equals("")){
        			toastmake("Nothing Inputed");
        		}else{
        			InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        			imm.hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
        			
        			searchstring = searchEdit.getText().toString();
        			startsearch(inflate);            			
        		}
			}	
        });

        return inflate;
    }
	
    public void startsearch(View v){
		if(ia > 0){
			adapter = new MyAdapter(getActivity(), 0);
			setListAdapter(adapter);
		}
		
		TextView failresults = (TextView)v.findViewById(R.id.failresults);
		failresults.setText("");
		
		ia = 0;
		
        final EditText searchEdit = (EditText)v.findViewById(R.id.searchEdit);  
        searchEdit.setText(searchstring);
        
		toastmake("Searching For: "+searchstring);
			
		adapter = new MyAdapter(getActivity(), 0);
		setListAdapter(adapter);
			
		retrievesearch = new RetrieveSearch();
		retrievesearch.execute();
    }
    
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        AdapterItem data = adapter.items.get(position);

        String title = data.first;
        String url = data.second;
        
		Intent intent = new Intent(getActivity(), ViewSongDialog.class);
		
		intent.putExtra("title", title);
		intent.putExtra("url", url);
		intent.putExtra("source", data.third);
		
		startActivity(intent);
	}
	
	private class RetrieveSearch extends AsyncTask<Void, Void, Void>{
		private ProgressDialog cancelDialog = null;
	
		@Override
		protected Void doInBackground(Void... params) {
			
			adapter = new MyAdapter(getActivity(), 0);
			ia = 0;

			try{
				Log.d("ASE", "Getting Dilandau");
				getDilandau(searchstring);
			} catch(IOException e) {
				e.printStackTrace();
				fail++;
			} catch(StringIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			} catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			}
			
			try{
				Log.d("ASE", "Getting Hulkshare");
				getHulkshare(searchstring);
			} catch(IOException e) {
				e.printStackTrace();
				fail++;
			} catch(StringIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			} catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			}
			
			try{
				Log.d("ASE", "Getting SoundCloud");
				getSoundCloud(searchstring);
			} catch(IOException e) {
				e.printStackTrace();
				fail++;
			} catch(StringIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			} catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			}
			
			try{
				Log.d("ASE", "Getting Soundcat");
				getSoundcat(searchstring);
			} catch(IOException e) {
				e.printStackTrace();
				fail++;
			} catch(StringIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			} catch(ArrayIndexOutOfBoundsException e){
				e.printStackTrace();
				fail++;
			}
			
			return null;
		}
	
		@Override
		protected void onCancelled() {		
			super.onCancelled();
			adapter = new MyAdapter(getActivity(), 0);
			setListAdapter(adapter);

			TextView failresults = (TextView)inflate.findViewById(R.id.failresults);
			failresults.setText("Searching has been cancelled");
			
	        try{
				cancelDialog.dismiss();
		        cancelDialog = null;
	        }catch (Exception e){
	        	//nothing
	        }
		}
	        
		@Override
		protected void onPreExecute() {
			cancelDialog = new ProgressDialog(getActivity());
			cancelDialog.setMessage("Searching For Songs...");
			cancelDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.btn_cancel_name), new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int which) {
					retrievesearch.cancel(true);
			    }
			});
			cancelDialog.show();

			super.onPreExecute();
		} 
	        
		@Override
		protected void onPostExecute(Void result) {
			if(ia == 0 && fail == 0){
				fail = 0;
				adapter = new MyAdapter(getActivity(), 0);
				TextView failresults = (TextView)inflate.findViewById(R.id.failresults);
		        if(!isInternetConnectionActive(getActivity())) {
					failresults.setText("It appears to be you do not have an active Internet Connection or have a Weak Signal. Please turn on your wifi or data and try again.");
		        }else{
					failresults.setText("No Results Found");
		        }
			}
			
			Collections.sort(adapter.items, new Comparator<AdapterItem>() {                
		        @Override
		        public int compare(AdapterItem item1, AdapterItem item2) {
		               Integer x1 = item1.lid;
		               Integer x2 = item2.lid;
		               return x1.compareTo(x2);
		        }
		    });
		    
			setListAdapter(adapter);
			try{
				cancelDialog.dismiss();
				cancelDialog = null;
		    } catch (Exception e) {
		        // nothing
		    }
			
			super.onPostExecute(result);
		}
		
		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
		
		public void getHulkshare(String search) throws IOException, MalformedURLException, NullPointerException{
			search = URLEncoder.encode(search, "UTF-8");			
			search = search.replace("%20", "+");
			String link = "http://www.hulkshare.com/search.php?q=" + search + "&p=1&per_page=20";
		    // Send data
		    URL url = new URL(link);
		    URLConnection conn = url.openConnection();
		    conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; .NET CLR 1.0.3705; .NET CLR 1.1.4322; .NET CLR 1.2.30703)");
		    conn.setDoOutput(true);
		    conn.setConnectTimeout(3000);

		    // Get the response
		    StringBuffer sb = new StringBuffer();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    
		    String line;
		    while ((line = rd.readLine()) != null) {
		    	sb.append(line);
		    }
		    rd.close();
		    
		    String[] searchRI = sb.toString().split("<div class=\"searchResultsItem\">");
		    for(int i = 1; i < searchRI.length ; i++){
		    	try {
			    	String content = searchRI[i];
			    	
			    	String songData = content.substring(content.indexOf("<div class=\"resTP\">"));
			    	songData = songData.substring(0, songData.indexOf("</div>"));
			    	
			    	String songURL = songData.substring(songData.indexOf("<a href=\"/"));
			    	songURL = songURL.substring(0, songURL.indexOf("\"><b>"));
			    	songURL = songURL.replace("<a href=\"/", "");
			    	songURL = "http://www.hulkshare.com/ap-" + songURL +"/&ref=.mp3";
			    	
			    	String songTitle = songData.substring(songData.indexOf("<b>"));
			    	songTitle = songTitle.substring(0, songTitle.indexOf("</a>"));
			    	songTitle = android.text.Html.fromHtml(songTitle).toString();
			    	songTitle = songTitle.replace("by", "-");
			    	songTitle = songTitle.replace(".mp3", "");
			    	
			    	String duration = "";
			    	if(content.contains("<span class=\"vidDuration\">")){
			    		String dur = content.substring(content.indexOf("<span class=\"vidDuration\">"));
			    		dur = dur.substring(0, dur.indexOf("</span>"));
			    		duration = dur.replace("<span class=\"vidDuration\">", "");
			    	}
			    	
			    	if(songURL != "" || songTitle != ""){
						adapter.addAdapterItem(new AdapterItem(songTitle, songURL, "hulkshare", duration,  i));
						ia++;
			    	}
		    	} catch(StringIndexOutOfBoundsException e){
					e.printStackTrace();
				} catch(ArrayIndexOutOfBoundsException e){
					e.printStackTrace();
				}
		    }
		}
		
		public void getSoundCloud(String search) throws IOException, MalformedURLException, NullPointerException{			
			search = URLEncoder.encode(search, "ISO-8859-1");
			search = search.replace("+", "%20");
			
			String link = "https://api.sndcdn.com/search?q=" + search + "&facet=model&limit=10&offset=0&linked_partitioning=1&client_id=b45b1aa10f1ac2941910a7f0d10f8e28";
		    // Send data
		    URL url = new URL(link);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    conn.setConnectTimeout(3000);

		    // Get the response
		    StringBuffer sb = new StringBuffer();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    
		    String line;
		    while ((line = rd.readLine()) != null) {
		    	sb.append(line);
		    }
		    rd.close();
		    

			try {
				JSONObject jso = new JSONObject(sb.toString());
				JSONArray ja = jso.getJSONArray("collection");
	
				for (int i = 1; i < ja.length(); i++) {
					JSONObject jsonSection = ja.getJSONObject(i);
					
					String title, dur, dllink;
					try{
						title = jsonSection.getString("title");		
						dur = jsonSection.getString("duration");
						dllink = jsonSection.getString("stream_url");
						int seconds = (int) (Integer.parseInt(dur) / 1000) % 60 ;
						int minutes = (int) ((Integer.parseInt(dur) / (1000*60)) % 60);
						dur = minutes + ":" + seconds;
						if(title != "" || dllink != ""){
							adapter.addAdapterItem(new AdapterItem(title, dllink + "?client_id=b45b1aa10f1ac2941910a7f0d10f8e28", "SoundCloud", dur,  i));
							ia++;
						}
					} catch (JSONException e) {
						//e.printStackTrace();
					}
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		public void getSoundcat(String search) throws IOException, MalformedURLException, NullPointerException{
			search = URLEncoder.encode(search, "ISO-8859-1");
			search = search.replace("+", "%20");
			
			String link = "http://www.soundcat.ch/ajaxbrowse.php?q=" + search  +"&sortby=&page=1";
		    // Send data
		    URL url = new URL(link);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    conn.setConnectTimeout(3000);

		    // Get the response
		    StringBuffer sb = new StringBuffer();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    
		    String line;
		    while ((line = rd.readLine()) != null) {
		    	sb.append(line);
		    }
		    rd.close();

			String[] a = sb.toString().split("<div class=\"row_container");
			
			for(int i = 1; i <= (a.length - 1); i++){
				try {
					String content = a[i];
						
					String songtitle = content.substring(content.indexOf("<div class=\"listing_song_text\">"));
					songtitle = songtitle.substring(0, songtitle.indexOf("</div>"));
					songtitle = songtitle.replace("<div class=\"listing_song_text\">", "");
						
					String songartist = content.substring(content.indexOf("<div class=\"listing_artist\">"));
					songartist = songartist.substring(0, songartist.indexOf("</div>"));
					songartist = songartist.replace("<div class=\"listing_artist\">", "");
					
					String filename = "";
					if(!songtitle.contains(songartist)){
						filename = songtitle + " - " + songartist;
					}else{
						filename = songtitle;
					}
					
					String fileurl = content.substring(content.indexOf("target=\"_blank\" href=\"download.php"));
					fileurl = fileurl.substring(0, fileurl.indexOf("\"></a>"));
					fileurl = fileurl.replace("target=\"_blank\" href=\"", "");
					
					String duration = "";
					if(content.contains("<div class=\"listing_duration\">")){
						String dur = content.substring(content.indexOf("<div class=\"listing_duration\">"));
						dur = dur.substring(0, dur.indexOf("</div>"));
						duration = dur.replace("<div class=\"listing_duration\">", "");
					}
					
					if(filename != "" || fileurl != ""){
						adapter.addAdapterItem(new AdapterItem(filename, "http://www.soundcat.ch/"+fileurl, "soundcat", duration,  i));
						ia++;
					}
		    	} catch(StringIndexOutOfBoundsException e){
					e.printStackTrace();
				} catch(ArrayIndexOutOfBoundsException e){
					e.printStackTrace();
				}
			}
			
		}
		
		public void getDilandau(String search) throws IOException, MalformedURLException, NullPointerException{
			search = URLEncoder.encode(search, "ISO-8859-1");
			search = search.replace("+", "-");
			
			String link = "http://en.dilandau.eu/download-mp3/" + search + "-1.html";
		    // Send data
		    URL url = new URL(link);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    conn.setConnectTimeout(3000);

		    // Get the response
		    StringBuffer sb = new StringBuffer();
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    
		    String line;
		    while ((line = rd.readLine()) != null) {
		    	sb.append(line);
		    }
		    rd.close();

			String[] a = sb.toString().split("<a class=\"dilandau_download\"");
			
			for(int i = 1; i <= (a.length - 1); i++){
				try {
					String content = a[i];
						
					String songtitle = content.substring(content.indexOf("title=\"\" >"));
					songtitle = songtitle.substring(0, songtitle.indexOf("</a>"));
					songtitle = songtitle.replace("title=\"\" >", "");
					
					String fileurl = content.substring(content.indexOf("href=\""));
					fileurl = fileurl.substring(0, fileurl.indexOf("\" title"));
					fileurl = fileurl.replace("href=\"", "");
					
					String duration = "Unknown";
					
					if(songtitle != "" || fileurl != ""){
						adapter.addAdapterItem(new AdapterItem(songtitle, fileurl, "dilandau", duration,  i));
						ia++;
					}
		    	} catch(StringIndexOutOfBoundsException e){
					e.printStackTrace();
				} catch(ArrayIndexOutOfBoundsException e){
					e.printStackTrace();
				}
			}
		}
	}	 
	
	public class MyAdapter extends ArrayAdapter<AdapterItem> {
		private List<AdapterItem> items = new ArrayList<AdapterItem>();
        
		public MyAdapter(Context context, int textviewid) {
			super(context, textviewid);
		}
		
	    public void addAdapterItem(AdapterItem item) {
	    	items.add(item);
	    }

	    @Override
		public int getCount() {
	    	return items.size();
	    }

        @Override
        public AdapterItem getItem(int position) {
        	return ((null != items) ? items.get(position) : null);
        }
        
	    @Override
		public long getItemId(int position) {
	    	return position;
	    }

	    @Override
		public View getView(int position, View convertView, ViewGroup parent){
	    	View rowView;
	    	if(convertView == null) {
	    		rowView = getActivity().getLayoutInflater().inflate(R.layout.searchitemview, null);
	    	} else {
	    		rowView = convertView;
	    	}
	    	
	    	TextView firstTextView = (TextView) rowView.findViewById(R.id.txtTitle);
	    	firstTextView.setText(items.get(position).first);	      

	    	TextView thirdTextView = (TextView) rowView.findViewById(R.id.txtSize);
	    	if(items.get(position).fourth != "00:00" || items.get(position).fourth != ""){
	    		thirdTextView.setText("Duration: " + items.get(position).fourth);
	    	}else{
	    		thirdTextView.setVisibility(View.GONE);
	    	}
		  
	    	return rowView;
	    }
	}	
	
	class AdapterItem {
		public String first;
		public String second;
		public String third;
		public String fourth;
		public int lid;

		public AdapterItem(String first, String second, String third, String fourth, int lid) {
			this.first = first;
			this.second = second;
			this.third = third;
			this.fourth = fourth;
			this.lid = lid;
		}
	}	
	
	private boolean isInternetConnectionActive(Context context) {
	   	NetworkInfo networkInfo = ((ConnectivityManager) getActivity()
		   	    .getSystemService(Context.CONNECTIVITY_SERVICE))
		   	    .getActiveNetworkInfo();
		
		   	if(networkInfo == null || !networkInfo.isConnected()) {
		   		return false;
		   	}
			return true;
		}
	
	public void toastmake(String title){
		Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT).show();
	}	
	

	//somewhere nested in the class:
	static class GSON{
	    String title;
	    String stream_url;

	    public String gettitle()
	    {
	        return title;
	    }

	    public String getstream_url()
	    {
	        return stream_url;
	    }

	    public void settitle(String name)
	    {
	        this.title = name;
	    }

	    public void setstream_url(String stream_url)
	    {
	        this.stream_url = stream_url;
	    }
	}
}
