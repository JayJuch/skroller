package com.torusworks.android.shoutcast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class StreamMetaDataReader implements Runnable{
	
	private static final int STREAM_INFO_ARTIST_SONG = 6;
	private StringBuilder rawHtmlResponse;
	private String responseBody;
	private String[] streamInfo;
	private OnFetchComplete onFetchComplete;
	
	private String streamUrl;

	public StreamMetaDataReader(String url) {
		this.streamUrl = url;
	}
	
	public String getStreamUrl() {
		return streamUrl;
	}

	public void setStreamUrl(String streamUrl) {
		this.streamUrl = streamUrl;
	}

	public String fetchSevenHtml() {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(streamUrl + "/7.html");
		request.setHeader("User-Agent", "Mozilla/5.0");
		HttpResponse response;
		try {
			response = client.execute(request);
			String html = "";
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			rawHtmlResponse = new StringBuilder();
			String line = null;
			while((line = reader.readLine()) != null)
			{
				rawHtmlResponse.append(line);
			}
			in.close();
			html = rawHtmlResponse.toString();		
			
			Pattern p = Pattern.compile("<body>(.*?)</body>");
			Matcher m = p.matcher(html);
			if(m.find()) {
				responseBody = m.group(1);
				streamInfo = m.group(1).split(",");
			}

			if (this.onFetchComplete != null) {
				this.onFetchComplete.handleFetchComplete(this);
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return responseBody;

	}
	
	public String getArtistAndSong() {
		String ret = null;
		if (streamInfo.length > STREAM_INFO_ARTIST_SONG) {
			ret = streamInfo[STREAM_INFO_ARTIST_SONG];
		}
		return ret;
	}
	
	public void setOnFetchComplete(OnFetchComplete onFetchComplete) {
		this.onFetchComplete  = onFetchComplete;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		fetchSevenHtml();
	}
	
	
}
