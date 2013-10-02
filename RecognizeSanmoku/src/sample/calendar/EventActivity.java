package sample.calendar;


import sample.calendar.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class EventActivity extends Activity {
	private int calendar_id;
	private EventEntryDao eventEntryDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.entry);
		calendar_id = getIntent().getIntExtra(CalendarEntry.KEY_ID, -1);
		eventEntryDao = new EventEntryDao(getContentResolver(), calendar_id);
	}

	@Override
	public void onResume() {
		super.onResume();
		final ArrayAdapter<EventEntry> adapter = new EventAdapter(this,
				R.layout.list_entry);

		// 全イベントをアダプターに追加していく
		for (EventEntry entry : eventEntryDao.findAll()) {
			adapter.add(entry);
		}

		// イベントのリストを生成
		ListView view = (ListView) findViewById(R.id.entry_list_view);
		view.setAdapter(adapter);
		view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// イベントをクリックしたら編集画面へ
				EventEntry entry = adapter.getItem(position);
				Intent intent = new Intent(EventActivity.this,
						EditActivity.class);
				intent.putExtra(EventEntryDao.KEY_CALENDAR_ID, calendar_id);
				intent.putExtra(EventEntryDao.KEY_EVENT_ID, entry.getId());
				startActivity(intent);
			}
		});
	}

	public static final int MENU_NEW = 0;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_NEW, 0, "New event");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW:
			createEvent();
			return true;
		}
		return false;
	}

	private void createEvent() {
		// 新規作成はidを渡さずに編集画面へ
		Intent intent = new Intent(EventActivity.this, EditActivity.class);
		intent.putExtra(EventEntryDao.KEY_CALENDAR_ID, calendar_id);
		startActivity(intent);
	}
}