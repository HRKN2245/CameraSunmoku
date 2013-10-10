package sample.calendar;

import java.util.Calendar;

import sample.calendar.R;
import jp.recognize.scenery.android.sampleActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class EditActivity extends Activity implements OnClickListener {
	private int entry_id;
	private int calendar_id;
	private EventEntryDao eventEntries;
	private ArrayAdapter<Integer> yearAdapter;
	private ArrayAdapter<Integer> monthAdapter;
	private ArrayAdapter<Integer> dayAdapter;
	private ArrayAdapter<Integer> hourAdapter;
	private ArrayAdapter<String> minuteAdapter;
	
	private static final int EDIT_ACTIVITY = 1000;

	// 分のformat
	private static final String minuteFormat = "%02d";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.edit);
		
		//認識ボタンを押したときの処理
		Button bt = (Button) findViewById(R.id.button1);
		bt.setOnClickListener(this);

		// Spinnerに渡すadapter
		yearAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item);
		monthAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item);
		dayAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item);
		hourAdapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_item);
		minuteAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);

		// 年一覧
		for (int i = 2010; i < 2100; i++) {
			yearAdapter.add(i);
		}
		((Spinner) findViewById(R.id.edit_dtstart_year))
				.setAdapter(yearAdapter);

		// 月一覧
		for (int i = 1; i <= 12; i++) {
			monthAdapter.add(i);
		}
		((Spinner) findViewById(R.id.edit_dtstart_month))
				.setAdapter(monthAdapter);

		// 日一覧
		for (int i = 1; i <= 31; i++) {
			dayAdapter.add(i);
		}
		((Spinner) findViewById(R.id.edit_dtstart_day)).setAdapter(dayAdapter);

		// 時間一覧
		for (int i = 0; i < 24; i++) {
			hourAdapter.add(i);
		}
		((Spinner) findViewById(R.id.edit_dtstart_hour))
				.setAdapter(hourAdapter);
		((Spinner) findViewById(R.id.edit_dtend_hour)).setAdapter(hourAdapter);

		// 分一覧
		for (int i = 0; i < 60; i += 5) {
			minuteAdapter.add(String.format(minuteFormat, i));
		}
		((Spinner) findViewById(R.id.edit_dtstart_minute))
				.setAdapter(minuteAdapter);
		((Spinner) findViewById(R.id.edit_dtend_minute))
				.setAdapter(minuteAdapter);

		// イベント取得
		entry_id = getIntent().getIntExtra(EventEntryDao.KEY_EVENT_ID, -1);
		calendar_id = getIntent()
				.getIntExtra(EventEntryDao.KEY_CALENDAR_ID, -1);
		eventEntries = new EventEntryDao(getContentResolver(), calendar_id);

		// リフレッシュ
		refresh();
	}

	/**
	 * 保存ボタン
	 * 
	 * @param v
	 */
	public void onSaveClicked(View v) {
		// FORMから値を取得
		String title = ((EditText) findViewById(R.id.edit_title)).getText()
				.toString();
		String description = ((EditText) findViewById(R.id.edit_description))
				.getText().toString();
		String eventLocation = ((EditText) findViewById(R.id.edit_location))
				.getText().toString();
		int year = (Integer) (((Spinner) findViewById(R.id.edit_dtstart_year))
				.getSelectedItem());
		int month = (Integer) (((Spinner) findViewById(R.id.edit_dtstart_month))
				.getSelectedItem()) - 1;
		int day = (Integer) (((Spinner) findViewById(R.id.edit_dtstart_day))
				.getSelectedItem());
		int startHour = (Integer) (((Spinner) findViewById(R.id.edit_dtstart_hour))
				.getSelectedItem());
		int startMinute = Integer
				.parseInt((String) (((Spinner) findViewById(R.id.edit_dtstart_minute))
						.getSelectedItem()));
		int endHour = (Integer) (((Spinner) findViewById(R.id.edit_dtend_hour))
				.getSelectedItem());
		int endMinute = Integer
				.parseInt((String) (((Spinner) findViewById(R.id.edit_dtend_minute))
						.getSelectedItem()));
		Calendar calendarStart = Calendar.getInstance();
		Calendar calendarEnd = Calendar.getInstance();
		calendarStart.set(year, month, day, startHour, startMinute);
		calendarEnd.set(year, month, day, endHour, endMinute);
		long dtstart = calendarStart.getTimeInMillis();
		long dtend = calendarEnd.getTimeInMillis();

		// EventEntry生成
		final EventEntry eventEntry = new EventEntry(entry_id, calendar_id,
				title, description, eventLocation, dtstart, dtend);

		// 保存ダイアログ表示
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle(this.getString(R.string.save_message));
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();

		// 保存処理は別スレッドに　
		new Thread(new Runnable() {
			@Override
			public void run() {
				eventEntries.save(eventEntry);
				dialog.dismiss();
				finish();
			}
		}).start();
	}

	/**
	 * 削除ボタン
	 * 
	 * @param v
	 */
	public void onDeleteClicked(View v) {
		// 削除ダイアログ表示
		final ProgressDialog dialog = new ProgressDialog(this);
		dialog.setTitle(this.getString(R.string.delete_message));
		dialog.setIndeterminate(false);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setCancelable(false);
		dialog.show();
		// 削除処理は別スレッドに
		new Thread(new Runnable() {
			@Override
			public void run() {
				eventEntries.delete(entry_id);
				dialog.dismiss();
				finish();
			}
		}).start();
	}

	/**
	 * フォームの値設定
	 */
	public void refresh() {
		Calendar dtstart = Calendar.getInstance();
		Calendar dtend = Calendar.getInstance();
		if (entry_id < 0) {
			// 新規作成時もタイトルにデフォルト値を入れる
			((EditText) findViewById(R.id.edit_title))
					.setText("new EventEntry");
		} else {
			// 更新時はイベントを取得してフォームに値を入れる
			EventEntry eventEntry = eventEntries.find(entry_id);
			if (eventEntry == null) {
				throw new IllegalArgumentException(
						"entry_id cannot find the entry.");
			}
			dtstart.setTimeInMillis(eventEntry.getDtstart());
			dtend.setTimeInMillis(eventEntry.getDtend());
			((EditText) findViewById(R.id.edit_title)).setText(eventEntry
					.getTitle());
			((EditText) findViewById(R.id.edit_description)).setText(eventEntry
					.getDescription());
			((EditText) findViewById(R.id.edit_location)).setText(eventEntry
					.getEventLocation());
		}

		// Spinnerにadapterを割り当てる
		((Spinner) findViewById(R.id.edit_dtstart_year))
				.setSelection(yearAdapter.getPosition(dtstart
						.get(Calendar.YEAR)));
		((Spinner) findViewById(R.id.edit_dtstart_month))
				.setSelection(monthAdapter.getPosition(dtstart
						.get(Calendar.MONTH) + 1));
		((Spinner) findViewById(R.id.edit_dtstart_day)).setSelection(dayAdapter
				.getPosition(dtstart.get(Calendar.DAY_OF_MONTH)));
		((Spinner) findViewById(R.id.edit_dtstart_hour))
				.setSelection(hourAdapter.getPosition(dtstart
						.get(Calendar.HOUR_OF_DAY)));
		((Spinner) findViewById(R.id.edit_dtstart_minute))
				.setSelection(minuteAdapter.getPosition(String.format(
						minuteFormat, dtstart.get(Calendar.MINUTE) / 5 * 5)));
		((Spinner) findViewById(R.id.edit_dtend_hour)).setSelection(hourAdapter
				.getPosition(dtend.get(Calendar.HOUR_OF_DAY)));
		((Spinner) findViewById(R.id.edit_dtend_minute))
				.setSelection(minuteAdapter.getPosition(String.format(
						minuteFormat, dtend.get(Calendar.MINUTE) / 5 * 5)));
	}

	@Override
	public void onClick(View v) {
		// TODO 自動生成されたメソッド・スタブ
		Intent intent = new Intent(this, sampleActivity.class);
		//intent.setAction(Intent.ACTION_MAIN);
		//intent.setClassName("jp.recognize.scenery.android", "jp.recognize.scenery.android.sampleActivity");
		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		startActivityForResult(intent, EDIT_ACTIVITY);
	}

	@Override
	//認識結果を受け取る
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);
		String exWord[];
		if(requestCode == EDIT_ACTIVITY){
			if(resultCode == RESULT_OK){
				exWord  = data.getStringArrayExtra("str");
				int[] ScheduleNum = new int[exWord.length];
				System.out.println(ScheduleNum.length);
				
				for(int i=0; i<ScheduleNum.length; i++){
					System.out.println(exWord[i]);
					ScheduleNum[i] = ParseInt(exWord[i]);
					//年月日の判断
					if(exWord[i].length() == 4){
						((Spinner) findViewById(R.id.edit_dtstart_year)).
						setSelection(yearAdapter.getPosition((ScheduleNum[i])));
					}
					else if(exWord[i].length() < 4) {
						char ch = exWord[i].charAt(exWord[i].length()-1);
						if(ch == '月' || ch == '/' || ch == '／' || ch == '.'){
							((Spinner) findViewById(R.id.edit_dtstart_month))
									.setSelection(monthAdapter.getPosition(ScheduleNum[i]));
						}	
						else{
							((Spinner) findViewById(R.id.edit_dtstart_day)).setSelection(dayAdapter
									.getPosition(ScheduleNum[i]));
						}
					}
				}
			}
		}
	}
	
	private int ParseInt(String str){
		int num;
		try{
			num = Integer.parseInt(str);
		}catch(NumberFormatException nfe){
			str = str.substring(0, str.length()-1);
			return ParseInt(str);
		}
		return num;
	}
	
	
}
