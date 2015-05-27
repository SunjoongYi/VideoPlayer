package com.example.videoplayer;

import java.util.concurrent.Semaphore;

import com.example.videoplayer.PlayService.LocalBinder;
//import com.example.videoplayer.PlayService.MyBinder;
import com.example.videoplayer.aidl.*;
import com.google.gson.Gson;

import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Environment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.Button;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/////////////////////////////////////////////////////

public class MainActivity extends Activity{
	SharedPreferences mPrefs = null;	// 액티비티 강제 종료 후 변수 복구시 필요
	
	private TextView timeText1;			// 영상의 현재 재생 시간을 나타내는 텍스트뷰
	private TextView timeText2;			// 영상의 총 재생 시간을 나타내는 텍스트 뷰
	
	private int current_video_hours = 0;		
	private int current_video_minutes = 0;
	private int current_video_seconds = 0;
	
	private int current_position = 0;
	private boolean mBound = false;
	private String filename = null;
	private Intent intent;
	
	private static boolean isBackFromHomeKey = false;
	private PlayService playservice;	// 액티비티가 백그라운드로 갈 때 소리를 재생시키는 서비스
	private VideoView videoview = null;		//  
	private OnCompletionListener onCompletionListener;
	
	private MediaController mc;			// 미디어 컨트롤러 (사용 X, 자체 UI 제작)
	
	private Button back_Button;			// BACK 버튼
	private Button home_Button;			// HOME 버튼
	private Button menu_Button;			// MENU 버튼
	private Button function_Button;		// FUNCTION 버튼
	private Button setting_Button;		// SETTING 버튼
	
	private Button play_Button;			// PLAY 버튼
	private Button ff_Button;			// FF 버튼
	private Button rw_Button;			// RW 버튼
	private Button list_Button;			// 리스트 버튼
	
	private TextView movie_Name = null;
	
	private FrameLayout FrameLayout1 = null;
	private FrameLayout FrameLayout3 = null;
	Semaphore sema = null;

	private LinearLayout LinearLayout;
	private LinearLayout List;
	
	private SeekBar seekBar = null;
	private SeekBar.OnSeekBarChangeListener soundcontrolListener;
	
	private Runnable onEverySecond = null;
	
	private int First_Flag = 0; 
	
	static final private int list_Flag = 0;
	static final private int setting_Flag = 1;
	static final private int function_Flag = 2;
	static final private int menu_Flag = 3;
	
	private int fade_Count = 0;		// fade in/out 에 사용되는 카운트
	
	private int isVideoPlaying = 0;
	
	private int isLinearLayout_Visible = 0;
	private int isListLayout_Visible = 0;
	private int isFrameLayout_Visible = 0; 
	
	
	private int Flag[] = new int[4];
	
	//Menu
	private Cursor videocursor;
	private int video_column_index;
	private ListView videolist;
	int count;
	
	String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
			MediaStore.Video.Thumbnails.VIDEO_ID };
	
	//////////////////
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			 
			//mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			LocalBinder binder = (LocalBinder) service;
			playservice = binder.getService();
			//mBound = true;
		}
	};
	
	//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
                
        
        mPrefs = getPreferences(MODE_PRIVATE);
        
        if(savedInstanceState == null){
        	Toast.makeText(MainActivity.this, "onCreate 1()", Toast.LENGTH_LONG).show();
        
        //intent = new Intent(this, PlayService.class);
        
        //stopService(intent);

        //VideoView (videoview), MediaController (mc)객체 초기화
        videoview = (VideoView)this.findViewById(R.id.VideoView1);//
        //videoview.setKeepScreenOn(true);
        
        //playservice.setVideoview((VideoView)this.findViewById(R.id.VideoView1));
        mc = new MediaController(this);
        
        //Flag 초기화
        
        //SeekBar 초기화
        seekBar = (SeekBar)findViewById(R.id.SeekBar1);
        soundcontrolListener = new SeekBar.OnSeekBarChangeListener() {
    		@Override
    		public void onStopTrackingTouch(SeekBar seekBar) {
    			// TODO Auto-generated method stub
    		}
    		
    		@Override
    		public void onStartTrackingTouch(SeekBar seekBar) {
    			// TODO Auto-generated method stub
    		}
    		
    		@Override
    		public void onProgressChanged(SeekBar seekBar, int progress,
    				boolean fromUser) {
    			// TODO Auto-generated method stub
    			seekBar.setProgress(progress);
    			
   				if(fromUser){
   					videoview.seekTo(progress);
   				}
   				
   				int seconds = (int)(progress / 1000);
   				int seconds_display = (int)(seconds % 60);
   				int minutes = (int)(seconds / 60);
   				int minutes_display = (int)(minutes % 60);
   				int hours = (int)(minutes / 60);
   				
   				String time = String.format("%02d", hours) + ":" + String.format("%02d", minutes_display) + ":" + String.format("%02d", seconds_display);
   				timeText1.setText(time);
   				
    		}
    	};
    	onCompletionListener = new OnCompletionListener() {
			
			@Override
			public void onCompletion(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.stop();
			}
		};
		
		sema = new Semaphore(1);
    	
		videoview.setOnCompletionListener(onCompletionListener);
        seekBar.setOnSeekBarChangeListener(soundcontrolListener);
        
        movie_Name = (TextView)findViewById(R.id.MovieName);
        
        //TextView 초기화
        this.timeText1 = (TextView)findViewById(R.id.TimeText1);
        this.timeText2 = (TextView)findViewById(R.id.TimeText2);
        
        //버튼 초기화
        back_Button = (Button)findViewById(R.id.Button01);
        home_Button = (Button)findViewById(R.id.Button02);
        menu_Button = (Button)findViewById(R.id.Button03);
        function_Button = (Button)findViewById(R.id.Button04);
        setting_Button = (Button)findViewById(R.id.Button05);
        
        play_Button = (Button)findViewById(R.id.Button06);
        rw_Button = (Button)findViewById(R.id.Button07);
        ff_Button = (Button)findViewById(R.id.Button08);
        list_Button = (Button)findViewById(R.id.Button09);
        
        list_Button.requestFocus();
        List = (LinearLayout)findViewById(R.id.List);
        LinearLayout = (LinearLayout)findViewById(R.id.LinearLayout16);
        FrameLayout1 = (FrameLayout)findViewById(R.id.FrameLayout1);
        FrameLayout3 = (FrameLayout)findViewById(R.id.FrameLayout3);
        
        Button[] button_Array = {back_Button, home_Button, menu_Button, function_Button, setting_Button, play_Button,
        		ff_Button, rw_Button, list_Button};        
        
        for(int i = 0 ; i < button_Array.length  ; i++){
        	button_Array[i].setFocusable(true);
        	button_Array[i].setFocusableInTouchMode(true);
        	button_Array[i].requestFocus();
        }
        
        // 버튼 초기화 끝
               
        // BACK 버튼 클릭시 이벤트
        back_Button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		
        			back_Button.setSelected(true);
        			back_Button.setPressed(true);
        			
        			int cnt;
        			
        			for(int i = 0 ; i < Flag.length ; i++){
        				if(Flag[i] != 0){
        					cnt = i;
        					switch(cnt)
        					{
        					case 0:
        						List.setVisibility(View.INVISIBLE);
        						LinearLayout.setVisibility(View.VISIBLE);
        						
        						Flag[i]=0;
        						break;
        					case 1:
        						
        					case 2:
        						
        					case 3:
        						
        					default:
        						break;
        					}
        				}
        			}
        	}
        });
        
        onEverySecond = new Runnable(){
        	@Override
        	public void run(){
        		if(seekBar != null){
        			seekBar.setProgress(videoview.getCurrentPosition());
        		}
        		if(videoview.isPlaying()){
        			seekBar.postDelayed(onEverySecond, 1000);
        		}
        	}
        };
        //onEverySecond.run();
        
        /*
        FrameLayout1.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub

				fade_Count = 0;
			
				FrameLayout3.setVisibility(View.VISIBLE);
				LinearLayout.setVisibility(View.VISIBLE);
				List.setVisibility(View.INVISIBLE);
				isFrameLayout_Visible = 1;
				isLinearLayout_Visible = 1;
				isListLayout_Visible = 0;
				
				new CounterTask().execute(0);
			}
		});*/
        
        // PLAY 버튼 클릭시 이벤트
        play_Button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(videoview.isPlaying()==false){
        			isVideoPlaying = 1;
        			videoview.start();
        			
        			/*
        			videoview.requestFocus();
        			videoview.setOnPreparedListener(new OnPreparedListener() {
						
						@Override
						public void onPrepared(MediaPlayer mp) {
							// TODO Auto-generated method stub
							seekBar.setProgress(videoview.getCurrentPosition());
							videoview.seekTo(videoview.getCurrentPosition());
							videoview.start();
						}
					});
					
					*/
        		}
        		else if(videoview.isPlaying()==true){
        			isVideoPlaying = 0;
        			videoview.pause();
        		}
        	}
        });
        
        home_Button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent showOptions = new Intent(Intent.ACTION_MAIN);
				showOptions.addCategory(Intent.CATEGORY_HOME);
				startActivity(showOptions);				
			}
		});
        
        // LIST 버튼 클릭시 이벤트
        list_Button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		//list_Button.setSelected(true);
        		//list_Button.setPressed(true);
        		Flag[list_Flag] = 1;
        		init_phone_video_grid();
        	}
        });
        
        
        ff_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int seekTime = videoview.getCurrentPosition() + 3000;
				videoview.seekTo(seekTime);
				
			}
		});
        rw_Button.requestFocus();
        
        rw_Button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				int seekTime = videoview.getCurrentPosition() - 3000;
				videoview.seekTo(seekTime);

			}
		});
		
        // seekbar
        
        //videoview 객체에 mc 객체 부착 -> 현재는 mc 를 이용하지 않으므로 매개변수에 null 대입
        videoview.setMediaController(null);
        
        //내부 저장소의 위치를 얻어온다.        
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath();

        //내부 저장소의 trailer.mp4 를 재생 파일로 지정
        //videoview.setVideoPath(folder + "/trailer.mp4");
        
        //포커스를 맞춘다.
        videoview.requestFocus();
        
        //비디오를 재생한다.
        this.getWindow().getDecorView().
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        
        

        /*
        FrameLayout3.setMeasureAllChildren(true);
        FrameLayout3.setVisibility(View.VISIBLE);
        isFrameLayout_Visible = 1;*/
        
        List.setMeasureWithLargestChildEnabled(true);
        List.setVisibility(View.INVISIBLE);


        LinearLayout.setMeasureWithLargestChildEnabled(true);
        LinearLayout.setVisibility(View.VISIBLE);
        
        //videoview.start();
        
        }
        else{
        	Toast.makeText(MainActivity.this, "onCreate 2 elseelseelse()", Toast.LENGTH_LONG).show();
        	/*
        	Toast.makeText(MainActivity.this, "onCreate else()", Toast.LENGTH_LONG).show();
    		filename = savedInstanceState.getString("videopath");
    		videoview.setVideoPath(filename);
    		
    		Gson gson = new Gson();
    		String json = mPrefs.getString("intent", "");
    		intent = gson.fromJson(json, Intent.class);
    		
    		stopService(intent);
    		
    		this.filename = playservice.getFilePath();
    		this.current_position = playservice.getSeekTime();
    		
    		videoview.setVideoPath(filename);
    		videoview.seekTo(current_position);
			videoview.requestFocus();
			videoview.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					isPlaying = 1;
					seekBar.setMax(videoview.getDuration());
					//seekBar.setProgress(0);
					//seekBar.postDelayed(onEverySecond, 1000);
					videoview.start();
					//onEverySecond.run();
					
				}
			});    		
    		//videoview.start();
    		 */
        }
    }
	
	@SuppressWarnings("deprecation")
	private void init_phone_video_grid() {

		LinearLayout.setVisibility(View.INVISIBLE);
		List.setVisibility(View.VISIBLE); 
       
		System.gc();
		String[] proj = { MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.DISPLAY_NAME,
				MediaStore.Video.Media.SIZE,
				MediaStore.Video.Media.DURATION};
		videocursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
				proj, null, null, null);
		count = videocursor.getCount();
		videolist = (ListView) findViewById(R.id.PhoneVideoList);
		videolist.setAdapter(new VideoAdapter(getApplicationContext()));
		videolist.setOnItemClickListener(videogridlistener);
	}
	
	private OnItemClickListener videogridlistener = new OnItemClickListener() {
		public void onItemClick(AdapterView parent, View v, int position,
				long id) {
			
			System.gc();
			video_column_index = videocursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			videocursor.moveToPosition(position);
			
			filename  = videocursor.getString(video_column_index);
			videoview.setVideoPath(filename);
			videocursor.moveToPosition(position);
			
			video_column_index = videocursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
			movie_Name.setText(videocursor.getString(video_column_index));
			videocursor.moveToPosition(position);
			
			video_column_index = videocursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
			String iduration = videocursor.getString(video_column_index);
			
			long timeInmillisec = Long.parseLong(iduration);
			long duration = timeInmillisec / 1000;
			int hours = (int)(duration / 3600);
			int minutes = (int) ((duration - hours * 3600)/ 60);
			int seconds = (int) (duration - (hours * 3600 + minutes * 60));
			
			current_video_hours = hours;
			current_video_minutes = minutes;
			current_video_seconds = seconds;
			
			String time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
			timeText2.setText(time);

			videoview.requestFocus();
		
			videoview.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					isVideoPlaying = 1;
					seekBar.setMax(videoview.getDuration());
					Toast.makeText(MainActivity.this, Integer.toString(videoview.getDuration()), Toast.LENGTH_LONG).show();
					videoview.start();
					onEverySecond.run();
					Log.i("droiddroid", filename);
				}
			});			
			

			List.setVisibility(View.INVISIBLE);
			LinearLayout.setVisibility(View.VISIBLE);
			
		}
	};

	public class VideoAdapter extends BaseAdapter {
		private Context vContext;

		public VideoAdapter(Context c) {
			vContext = c;
		}

		public int getCount() {
			return count;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			System.gc();
			ViewHolder holder;
			String id = null;

			
			convertView = null;
			if (convertView == null) {
				//
				convertView = LayoutInflater.from(vContext).inflate(
						R.layout.listitem, parent, false);
				holder = new ViewHolder();
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.txtTitle);
				holder.txtSize = (TextView) convertView
						.findViewById(R.id.txtSize);
				holder.runningTime = (TextView) convertView
						.findViewById(R.id.runningTime);				
				holder.thumbImage = (ImageView) convertView
						.findViewById(R.id.imgIcon);
				
				// ListView 에 Display_name 표시
				video_column_index = videocursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
				videocursor.moveToPosition(position);
				id = videocursor.getString(video_column_index);
				holder.txtTitle.setText(id);
				Log.i("asdf",movie_Name.getText().toString());
				
				
				// ListView 에 File Size 표시
				video_column_index = videocursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
				videocursor.moveToPosition(position);
				String size = videocursor.getString(video_column_index);
				
				long lsize = Long.parseLong(size);
				double dsize = lsize/1000000;
				String ssize = String.format("%4.1f", dsize);
				holder.txtSize.setText(ssize + "Mb" +  " | ");
				
				// ListView 에 재생시간 표시
				video_column_index = videocursor
						.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
				videocursor.moveToPosition(position);
				String iduration = videocursor.getString(video_column_index);

				String time;
				
				long timeInmillisec = Long.parseLong(iduration);
				long duration = timeInmillisec / 1000;
				int hours = (int)(duration / 3600);
				int minutes = (int) ((duration - hours * 3600)/ 60);
				int seconds = (int) (duration - (hours * 3600 + minutes * 60));
				
				time = String.format("%02d", hours) + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
				
				holder.runningTime.setText(time);
				videocursor.moveToPosition(position);
				
				String[] proj = { MediaStore.Video.Media._ID,
						MediaStore.Video.Media.DISPLAY_NAME,
						MediaStore.Video.Media.DATA,
						MediaStore.Video.Media.DURATION};
				@SuppressWarnings("deprecation")
				Cursor cursor = managedQuery(
						MediaStore.Video.Media.EXTERNAL_CONTENT_URI, proj,
						MediaStore.Video.Media.DISPLAY_NAME + "=?",
						new String[] { id }, null);
				cursor.moveToFirst();
				long ids = cursor.getLong(cursor
						.getColumnIndex(MediaStore.Video.Media._ID));

				ContentResolver crThumb = getContentResolver();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(
						crThumb, ids, MediaStore.Video.Thumbnails.MINI_KIND, // .MICRO_KIND,
						options);
				//Bitmap sizingBmp = Bitmap.createScaledBitmap(curThumb, 140, 90, true);
				holder.thumbImage.setImageBitmap(curThumb);
				curThumb = null;
			} /*
			 * else holder = (ViewHolder) convertView.getTag();
			 */
			return convertView;
		}
	}
	
    @Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Toast.makeText(MainActivity.this, "onResume()", Toast.LENGTH_LONG).show();

	}

	@Override
    public void onRestart()
    {
        super.onRestart();
        Toast.makeText(MainActivity.this, "onRestart()", Toast.LENGTH_LONG).show();
        Log.i("SunSun", Integer.toString(First_Flag));
        if(First_Flag == 1){
        	try {
				this.current_position = playservice.mService.getCurrentTime();
				Log.i("SunSun", Integer.toString(this.current_position));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	unbindService(serviceConnection);
        	mBound = false;
        	stopService(intent);
        	
        	/*
        	
            FrameLayout1.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				// TODO Auto-generated method stub

    				fade_Count = 0;
    			
    				FrameLayout3.setVisibility(View.VISIBLE);
    				LinearLayout.setVisibility(View.VISIBLE);
    				List.setVisibility(View.INVISIBLE);
    				
    				new CounterTask().execute(0);
    			}
    		});
    		
    		*/
            
        	videoview = (VideoView)findViewById(R.id.VideoView1);
        	seekBar = (SeekBar)findViewById(R.id.SeekBar1);

        	String temp = Integer.toString(current_position);
        	
        	Toast.makeText(MainActivity.this, temp + "::::: " + filename, Toast.LENGTH_LONG).show();
       	
        	seekBar.setMax(videoview.getDuration());
        	videoview.setVideoPath(filename);
        	seekBar.setOnSeekBarChangeListener(soundcontrolListener);

            soundcontrolListener = new SeekBar.OnSeekBarChangeListener() {
        		@Override
        		public void onStopTrackingTouch(SeekBar seekBar) {
        			// TODO Auto-generated method stub
        		}
        		
        		@Override
        		public void onStartTrackingTouch(SeekBar seekBar) {
        			// TODO Auto-generated method stub
        		}
        		
        		@Override
        		public void onProgressChanged(SeekBar seekBar, int progress,
        				boolean fromUser) {
        			// TODO Auto-generated method stub
        			seekBar.setProgress(progress);
       				if(fromUser){
       					videoview.seekTo(progress);
       				}
       				
       				
       				int seconds = (int)(progress / 1000);
       				int seconds_display = (int)(seconds % 60);
       				int minutes = (int)(seconds / 60);
       				int minutes_display = (int)(minutes % 60);
       				int hours = (int)(minutes / 60);
       				
       				String time = String.format("%02d", hours) + ":" + String.format("%02d", minutes_display) + ":" + String.format("%02d", seconds_display);
       				timeText1.setText(time);

        		}
        	};        

        	videoview.requestFocus();
        	videoview.setOnPreparedListener(new OnPreparedListener(){
        		public void onPrepared(MediaPlayer mp){
        			int isMusicIsPlaying = playservice.getMusicIsPlaying();
        			Log.i("ismusicplaying", Integer.toString(isMusicIsPlaying));
       				//seekBar.setMax(videoview.getDuration());
       				Toast.makeText(MainActivity.this, Integer.toString(videoview.getDuration()), Toast.LENGTH_LONG).show();
       				//seekBar.postDelayed(onEverySecond, 1000);
   					
       				seekBar.setMax(videoview.getDuration());
       				seekBar.setProgress(current_position);
       				videoview.seekTo(current_position);
  				
       				
       				if(isMusicIsPlaying == 1){
       					isVideoPlaying = 1;
       					videoview.start();
       					  					

       				}else{
       					isVideoPlaying = 0;
       					videoview.pause();
       				}
       				
       				onEverySecond.run();     
        		}
        		
        	});
        }
    }
     
    @Override	// 액티비티 -> onPause
    public void onPause()
    {
    	super.onPause();
    	Toast.makeText(MainActivity.this, "onPause()", Toast.LENGTH_LONG).show();
  	
   	
    	if(videoview.isPlaying()==true){
    		isVideoPlaying = 1;
    	}
    	
    	int current_position = videoview.getCurrentPosition();
    	
        videoview.pause();
    	//videoview.stopPlayback();

        First_Flag = 1;

        intent = new Intent(this, PlayService.class);
        intent.putExtra("currentTime", current_position);
        intent.putExtra("path", filename);
        intent.putExtra("isVideoPlaying", isVideoPlaying);
        startService(intent);
        bindService(intent, this.serviceConnection, Context.BIND_AUTO_CREATE);

    }
     
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(MainActivity.this, "onDestroy()", Toast.LENGTH_LONG).show();
      
    }
     
    @Override
    public void onStop()
    {
    	super.onStop();
    	Toast.makeText(MainActivity.this, "onStop()", Toast.LENGTH_LONG).show();
    }	

	static class ViewHolder {
		TextView txtTitle;
		TextView txtSize;
		ImageView thumbImage;
		TextView runningTime;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onRestoreInstanceState(savedInstanceState);
		Toast.makeText(MainActivity.this, "onRestoreInstanceState()", Toast.LENGTH_LONG).show();
	} 
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		
		super.onSaveInstanceState(outState);
		
		Toast.makeText(MainActivity.this, "onSaveInstanceState()", Toast.LENGTH_LONG).show();
		outState.putString("videopath", this.filename);
		outState.putInt("seektime", this.current_position);
		
		Editor prefsEditor = mPrefs.edit();
		Gson gson = new Gson();
		String json = gson.toJson(intent);
		prefsEditor.putString("intent", json);
		prefsEditor.commit();
	}
	
	class CounterTask extends AsyncTask<Integer, Integer, Integer>{

		protected void onPreExecute(){

			LinearLayout.setVisibility(View.VISIBLE);
			List.setVisibility(View.INVISIBLE);

		}
		
		@Override
		protected Integer doInBackground(Integer... arg0) {
			// TODO Auto-generated method stub
			while(fade_Count < 5){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				fade_Count++;
			}
			return 1;
		}
		
		protected void onPostExecute(Integer result){
			LinearLayout.setVisibility(View.INVISIBLE);
			List.setVisibility(View.INVISIBLE);
			
		}
	}
	
/*
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		this.fade_Count = 0;
		FrameLayout.setVisibility(View.VISIBLE);
		LinearLayout.setVisibility(View.VISIBLE);
		List.setVisibility(View.INVISIBLE);
		new CounterTask().execute(0);
		
	}
*/	
}