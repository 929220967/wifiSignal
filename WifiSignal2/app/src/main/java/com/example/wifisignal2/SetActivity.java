package com.example.wifisignal2;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
//设置采集存储的位置以及采集参数（例如采样间隔和时长）
public class SetActivity extends Activity {
	EditText num_sample;
	EditText interval;
	EditText delay;
	EditText offline_dir;
	EditText online_dir;
	Button confirm;
	TextView showStatus;
	SharedPreferences sharedPreferences;
	int val_num_sample;
	int val_delay;
	int val_interval;
	String val_offline;
	String val_online;
	private Boolean isVibrate;
	private Boolean isRing;
	private CheckBox vibrate;
	private CheckBox ring;
	private static final String TAG="reminder";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		num_sample=(EditText)findViewById(R.id.num_sample);
		interval=(EditText)findViewById(R.id.interval);
		delay=(EditText)findViewById(R.id.delay);
		offline_dir=(EditText)findViewById(R.id.offline_dir);
		//online_dir=(EditText)findViewById(R.id.online_dir);
		confirm=(Button)findViewById(R.id.confirm);
		showStatus=(TextView)findViewById(R.id.showStatus);
		vibrate=(CheckBox)findViewById(R.id.checkBox_vibrate);
		ring=(CheckBox)findViewById(R.id.checkBox_ring);
		sharedPreferences=getSharedPreferences("datas", MODE_PRIVATE);
		val_num_sample=sharedPreferences.getInt("num_sample",300);
		val_delay=sharedPreferences.getInt("delay", 0);
		val_interval=sharedPreferences.getInt("interval", 200);
		val_offline=sharedPreferences.getString("offline_dir", "sampling");


		//val_online=sharedPreferences.getString("online_dir", "locating");
		showStatus.setTextSize(20);
		showStatus.setText("目前参数为：\n\r");
		showStatus.append("\t\t\t采集时间：\t"+val_num_sample+"s\n\r");
		showStatus.append("\t\t\t间隔：\t"+val_interval+"ms\n\r");
		showStatus.append("\t\t\t延迟：\t"+val_delay+"ms\n\r");
		showStatus.append("\t\t\t离线文件夹：\t"+val_offline+"\n\r");
		//showStatus.append("\t\t\t在线文件夹：\t"+val_online+"\n\r");

	/*	num_sample.setText();
		interval.setText(val_interval);
		delay.setText(val_delay);*/
		num_sample.setText(""+val_num_sample);
		interval.setText(""+val_interval);
		delay.setText(""+val_delay);
		offline_dir.setText(val_offline);
		//online_dir.setText(val_online);



	}
	public void onCheckboxClicked(View view){
		Boolean checked=((CheckBox)view).isChecked();
		sharedPreferences=getSharedPreferences("datas",MODE_PRIVATE);
		Editor editor=sharedPreferences.edit();
		int checkBoxId = view.getId();
		if (checkBoxId == R.id.checkBox_vibrate) {
			editor.putBoolean("vibrate", checked);
		} else if (checkBoxId == R.id.checkBox_ring) {
			editor.putBoolean("ring", checked);
		}
//		switch(view.getId()){
//			case R.id.checkBox_vibrate:
//				editor.putBoolean("vibrate", checked);
//				break;
//			case R.id.checkBox_ring:
//				editor.putBoolean("ring", checked);
//				break;
//		}
		editor.commit();
	}
	public void saveData(View v){
		int value_num=Integer.valueOf(num_sample.getText().toString());
		int value_interval=Integer.valueOf(interval.getText().toString());
		int value_delay=Integer.valueOf(delay.getText().toString());
		String value_offline=offline_dir.getText().toString();
		//String value_online=online_dir.getText().toString();
		sharedPreferences=getSharedPreferences("datas",MODE_PRIVATE);
		Editor editor=sharedPreferences.edit();
		editor.putInt("num_sample", value_num);
		editor.putInt("interval", value_interval);
		editor.putInt("delay", value_delay);
		editor.putString("offline_dir", value_offline);
		// editor.putString("online_dir", value_online);
		editor.commit();
		showStatus.setText("目前参数为：\n\r");
		showStatus.append("\t\t\t采集时间：\t"+value_num+"s\n\r");
		showStatus.append("\t\t\t间隔：\t"+value_interval+"ms\n\r");
		showStatus.append("\t\t\t延迟：\t"+value_delay+"ms\n\r");
		showStatus.append("\t\t\t存储文件夹：\t"+value_offline+"\n\r");
		//showStatus.append("\t\t\t在线文件夹：\t"+value_online+"\n\r");
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.set, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		return super.onOptionsItemSelected(item);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		Log.v(TAG, "看一看");
		isVibrate=sharedPreferences.getBoolean("vibrate", false);
		isRing=sharedPreferences.getBoolean("ring", false);
		vibrate.setChecked(isVibrate);
		ring.setChecked(isRing);
		super.onResume();
	}

}
