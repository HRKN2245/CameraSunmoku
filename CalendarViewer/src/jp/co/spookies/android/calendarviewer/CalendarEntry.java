package jp.co.spookies.android.calendarviewer;

public class CalendarEntry {
	private int _id; // id
	private String name; // カレンダー名
	public static final String KEY_ID = "key_id";

	public CalendarEntry(int id, String name) {
		this._id = id;
		this.name = name;
	}

	public int getId() {
		return _id;
	}

	public String getName() {
		return name;
	}
}