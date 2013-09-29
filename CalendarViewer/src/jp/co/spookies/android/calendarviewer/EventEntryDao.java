package jp.co.spookies.android.calendarviewer;

import java.util.ArrayList;
import java.util.List;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;

public class EventEntryDao {
	public static final Uri EVENT_URI = CalendarContract.Events.CONTENT_URI;
	public static final String KEY_EVENT_ID = "event_id";
	public static final String KEY_CALENDAR_ID = "calendar_id";
	private ContentResolver resolver;
	private int calendar_id;

	private static final String ROW_ID = "_id";
	private static final String ROW_TITLE = "title";
	private static final String ROW_DESCRIPTION = "description";
	private static final String ROW_LOCATION = "eventLocation";
	private static final String ROW_DTSTART = "dtstart";
	private static final String ROW_DTEND = "dtend";
	private static final String ROW_CALENDAR_ID = "calendar_id";

	private static final String[] ROWS = { ROW_ID, ROW_TITLE, ROW_DESCRIPTION,
			ROW_LOCATION, ROW_DTSTART, ROW_DTEND };

	public EventEntryDao(ContentResolver resolver, int calendar_id) {
		this.resolver = resolver;
		this.calendar_id = calendar_id;
		if (calendar_id < 0) {
			// 有効なカレンダーidではなければ例外を投げる
			throw new IllegalArgumentException("calendar_id is not negative.");
		}
	}

	/**
	 * カレンダーに所属する全てのイベントのリストを取得
	 * 
	 * @return
	 */
	public List<EventEntry> findAll() {
		// query生成
		Cursor cursor = resolver.query(EVENT_URI, ROWS, "deleted = 0 and "
				+ ROW_CALENDAR_ID + " = ?",
				new String[] { String.valueOf(calendar_id) }, ROW_DTSTART
						+ " asc");
		if (cursor == null) {
			return null;
		}

		List<EventEntry> events = new ArrayList<EventEntry>();
		// 1行ずつfetch
		while (cursor.moveToNext()) {
			int _id = cursor.getInt(cursor.getColumnIndexOrThrow(ROW_ID));
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow(ROW_TITLE));
			String description = cursor.getString(cursor
					.getColumnIndexOrThrow(ROW_DESCRIPTION));
			String eventLocation = cursor.getString(cursor
					.getColumnIndexOrThrow(ROW_LOCATION));
			long dtstart = cursor.getLong(cursor
					.getColumnIndexOrThrow(ROW_DTSTART));
			long dtend = cursor
					.getLong(cursor.getColumnIndexOrThrow(ROW_DTEND));
			events.add(new EventEntry(_id, calendar_id, title, description,
					eventLocation, dtstart, dtend));
		}
		// 忘れずにcursorをclose
		cursor.close();
		return events;
	}

	/**
	 * イベントidから取得
	 * 
	 * @param id
	 * @return
	 */
	public EventEntry find(int id) {
		// query生成
		Cursor cursor = resolver.query(EVENT_URI, ROWS, ROW_ID + " = ? and "
				+ ROW_CALENDAR_ID + " = ?", new String[] { String.valueOf(id),
				String.valueOf(calendar_id) }, null);
		if (cursor == null) {
			return null;
		}

		EventEntry eventEntry = null;
		// 1行fetch
		if (cursor.moveToFirst()) {
			int _id = cursor.getInt(cursor.getColumnIndexOrThrow(ROW_ID));
			String title = cursor.getString(cursor
					.getColumnIndexOrThrow(ROW_TITLE));
			String description = cursor.getString(cursor
					.getColumnIndexOrThrow(ROW_DESCRIPTION));
			String eventLocation = cursor.getString(cursor
					.getColumnIndexOrThrow(ROW_LOCATION));
			long dtstart = cursor.getLong(cursor
					.getColumnIndexOrThrow(ROW_DTSTART));
			long dtend = cursor
					.getLong(cursor.getColumnIndexOrThrow(ROW_DTEND));
			eventEntry = new EventEntry(_id, calendar_id, title, description,
					eventLocation, dtstart, dtend);
		}
		// cursorのclose
		cursor.close();
		return eventEntry;
	}

	/**
	 * 削除
	 * 
	 * @param id
	 */
	public void delete(int id) {
		// idをURIに付加してdelete
		resolver.delete(ContentUris.withAppendedId(EVENT_URI, id), null, null);
	}

	/**
	 * 保存
	 * 
	 * @param eventEntry
	 */
	public void save(EventEntry eventEntry) {
		ContentValues cv = new ContentValues();
		cv.put(ROW_CALENDAR_ID, calendar_id);
		cv.put(ROW_TITLE, eventEntry.getTitle());
		cv.put(ROW_DESCRIPTION, eventEntry.getDescription());
		cv.put(ROW_LOCATION, eventEntry.getEventLocation());
		cv.put(ROW_DTSTART, eventEntry.getDtstart());

		// googleカレンダーと同期するために必要
		//cv.put(Calendars.ACCOUNT_TYPE, "com.google");

		// dtendがdtstartより前なら
		if (eventEntry.getDtstart() > eventEntry.getDtend()) {
			// dtstartの1時間後にする
			cv.put(ROW_DTEND, eventEntry.getDtstart() + 60 * 60 * 1000);
		} else {
			cv.put(ROW_DTEND, eventEntry.getDtend());
		}
		if (eventEntry.getId() < 0) {
			// idが負の数なら挿入
			resolver.insert(EVENT_URI, cv);
		} else {
			// idが有効なら更新
			resolver.update(
					ContentUris.withAppendedId(EVENT_URI, eventEntry.getId()),
					cv, null, null);
		}
	}

}
