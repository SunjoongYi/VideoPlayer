package com.example.videoplayer;

import com.example.videoplayer.aidl.*;

import java.io.IOException;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

public class PlayService extends Service{
	private MediaPlayer mediaplayer;
	private int seekTime = 0;
	private String folder = null;
	private final IBinder binder = new LocalBinder();
	private ServiceCallbaks serviceCallbaks;

	IVideoService.Stub mService = new IVideoService.Stub() {
		
		@Override
		public void setAudio(String path, int currentTime) throws RemoteException {
			// TODO Auto-generated method stub
			folder = path;
			seekTime = currentTime;
		}
		
		@Override
		public String getString() throws RemoteException {
			// TODO Auto-generated method stub
			return folder;
		}
		
		@Override
		public int getCurrentTime() throws RemoteException {
			// TODO Auto-generated method stub
			seekTime = mediaplayer.getCurrentPosition();
			return seekTime;
		}
	};


	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		//folder = Environment.getExternalStorageDirectory().getAbsolutePath();
		//frameLayout.addView(videoview);
		//videoview.requestFocus();
		//videoview.setVideoPath(folder + "/trailer.mp4");

		
		mediaplayer = new MediaPlayer();
		try {
			//mediaplayer.setDataSource(folder + "/NGC_Clip.mp4");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			;
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
				
		String folder = intent.getStringExtra("path");
		int seekTime = intent.getIntExtra("currentTime", 1);
		
		//folder = Environment.getExternalStorageDirectory().getAbsolutePath();
		
		try {
			mediaplayer.setDataSource(folder);
			mediaplayer.prepare();
			mediaplayer.seekTo(seekTime);
			mediaplayer.start();
			
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		//videoview.stopPlayback();
		mediaplayer.stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}
	
	public class LocalBinder extends Binder{
		PlayService getService(){
			return PlayService.this;
		}
	}
	
	public interface ServiceCallbaks{
		void setAudio(String path, int time);
	}
	
	public int getSeekTime(){
		this.seekTime = mediaplayer.getCurrentPosition();
		return seekTime;
	}
	
	public String getFilePath(){
		return this.folder;
	}
}
