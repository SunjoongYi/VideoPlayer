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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController.MediaPlayerControl;

public class PlayService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener{
	private MediaPlayer mediaplayer;
	private int seekTime = 0;
	private String folder = null;
	private final IBinder binder = new LocalBinder();
	private ServiceCallbaks serviceCallbaks;
	private VideoView videoview;
	boolean mMediaPlayerIsPrepared = false;
	private MediaPlayer.OnSeekCompleteListener mOnSeekComplete;
	private int isVideoPlaying = 0;
	private int isMusicPlaying = 0;
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

		mOnSeekComplete = new MediaPlayer.OnSeekCompleteListener() {
			
			@Override
			public void onSeekComplete(MediaPlayer mp) {
				// TODO Auto-generated method stub
				if(isVideoPlaying==1){
					mediaplayer.start();
					isMusicPlaying = 1;
				} else{
					mediaplayer.pause();
					isMusicPlaying = 0;
				}
			}
		};

		try {
			//mediaplayer.setDataSource(folder + "/NGC_Clip.mp4");
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		Log.i("superdroid", "onStartCommand");
		seekTime = intent.getIntExtra("currentTime", 1);
		String folder = intent.getStringExtra("path");
		isVideoPlaying = intent.getIntExtra("isVideoPlaying", 0);
				
		Log.i("superdroid", folder);
		Log.i("superdroid", Integer.toString(seekTime));
		Log.i("superdroid", Integer.toString(isVideoPlaying));
				
		
		//folder = Environment.getExternalStorageDirectory().getAbsolutePath();
		try {
			
			mediaplayer.setDataSource(folder);
			//mediaplayer.prepare();
			mediaplayer.setOnPreparedListener(this);
			mediaplayer.setOnSeekCompleteListener(mOnSeekComplete);
			mediaplayer.prepare();
		
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}
	
	@Override
	public void onPrepared(MediaPlayer player){
		mMediaPlayerIsPrepared = true;
		if(mediaplayer.getDuration() > 0)
			mediaplayer.seekTo(seekTime);
		else{
			onPrepared(mediaplayer);
		}
		Log.i("getduration", Integer.toString(mediaplayer.getDuration()));
		//player.start();
		Log.i("superdroid", "onPrepared Ok");
	}
	
	@Override
	public void onCompletion(MediaPlayer mp){
		mediaplayer.stop();
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mediaplayer.stop();
		mediaplayer.release();
		//mediaplayer = null;
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
	
	public int getMusicIsPlaying(){
		return isMusicPlaying;
	}
}
