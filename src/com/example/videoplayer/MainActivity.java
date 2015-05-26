package com.example.videoplayer;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.app.Activity;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.os.Environment;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.VideoView;
import android.widget.MediaController;
import android.widget.Button;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private VideoView videoview;
	private MediaController mc;
	
	private Button back_Button;
	private Button home_Button;
	private Button menu_Button;
	private Button function_Button;
	private Button setting_Button;
	
	private Button play_Button;
	private Button ff_Button;
	private Button rw_Button;
	private Button list_Button;
	
	private FrameLayout FrameLayout;
	private LinearLayout LinearLayout;
	private LinearLayout List;
	
	static final private int list_Flag = 0;
	static final private int setting_Flag = 1;
	static final private int function_Flag = 2;
	static final private int menu_Flag = 3;
	
	private int Flag[] = new int[4];
	
	//Menu
	private Cursor videocursor;
	private int video_column_index;
	private ListView videolist;
	int count;
	
	String[] thumbColumns = {MediaStore.Video.Thumbnails.DATA,
			MediaStore.Video.Thumbnails.VIDEO_ID };
	
	//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setContentView(R.layout.listitem);

        //VideoView (videoview), MediaController (mc)객체 초기화
        videoview = (VideoView)this.findViewById(R.id.VideoView1);
        mc = new MediaController(this);
        
        //Flag 초기화
        
        //버튼 초기화
        back_Button = (Button)findViewById(R.id.Button01);
        home_Button = (Button)findViewById(R.id.Button02);
        menu_Button = (Button)findViewById(R.id.Button03);
        function_Button = (Button)findViewById(R.id.Button04);
        setting_Button = (Button)findViewById(R.id.Button05);
        
        play_Button = (Button)findViewById(R.id.Button06);
        ff_Button = (Button)findViewById(R.id.Button07);
        rw_Button = (Button)findViewById(R.id.Button08);
        list_Button = (Button)findViewById(R.id.Button09);
        
        List = (LinearLayout)findViewById(R.id.List);
        LinearLayout = (LinearLayout)findViewById(R.id.LinearLayout16);
        
        Button[] button_Array = {back_Button, home_Button, menu_Button, function_Button, setting_Button, play_Button,
        		ff_Button, rw_Button, list_Button};        
        
        for(int i = 0 ; i < button_Array.length  ; i++){
        	button_Array[i].setFocusable(true);
        	button_Array[i].setFocusableInTouchMode(true);
        	button_Array[i].requestFocus();
        }
        
        videoview.setMediaController(null);

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

        play_Button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		
        			play_Button.setSelected(true);
        			play_Button.setPressed(true);        			
        			videoview.start();
        	}
        });
        
        list_Button.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        			list_Button.setSelected(true);
        			list_Button.setPressed(true);
        			Flag[list_Flag] = 1;
        			init_phone_video_grid();
        	}
        });
        
        //videoview 객체에 mc 객체 부착
        
        //내부 저장소의 위치를 얻어온다.        
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath();

        //내부 저장소의 trailer.mp4 를 재생 파일로 지정
        videoview.setVideoPath(folder + "/trailer.mp4");

        //포커스를 맞춘다.
        videoview.requestFocus();
        
        //비디오를 재생한다.
            
        this.getWindow().getDecorView().
        setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        
        /*
        FrameLayout3 = (FrameLayout)findViewById(R.id.FrameLayout3);
        FrameLayout3.setMeasureAllChildren(true);
        FrameLayout3.setVisibility(View.VISIBLE);*/
        
        List.setMeasureWithLargestChildEnabled(true);
        List.setVisibility(View.INVISIBLE);

        LinearLayout.setMeasureWithLargestChildEnabled(true);
        LinearLayout.setVisibility(View.VISIBLE);      
    }
    
   
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		videoview.stopPlayback();
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
			String filename = videocursor.getString(video_column_index);
			
			/*
			Intent intent = new Intent(VideoStoredInSDCard.this,
					ViewVideo.class);
			intent.putExtra("videofilename", filename);
			startActivity(intent);*/
			
			videoview.setVideoPath(filename);
			videoview.start();
			
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
				holder.thumbImage.setImageBitmap(curThumb);
				curThumb = null;
			} /*
			 * else holder = (ViewHolder) convertView.getTag();
			 */
			return convertView;
		}
	}

	static class ViewHolder {
		TextView txtTitle;
		TextView txtSize;
		ImageView thumbImage;
		TextView runningTime;
	}	
}
