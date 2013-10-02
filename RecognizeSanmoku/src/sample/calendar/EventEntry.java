package sample.calendar;

public class EventEntry {

	private int id = -1; // id
	private int calendarId; // 所属するカレンダー
	private String title; // イベント名
	private String description; // 詳細
	private String eventLocation; // 場所
	private long dtstart; // 開始時間
	private long dtend; // 終了時間

	public EventEntry(int calendarId, String title, String description,
			String eventLocation, long dtstart, long dtend) {
		set(-1, calendarId, title, description, eventLocation, dtstart, dtend);
	}

	public EventEntry(int id, int calendarId, String title, String description,
			String eventLocation, long dtstart, long dtend) {
		set(id, calendarId, title, description, eventLocation, dtstart, dtend);
	}

	public void set(int id, int calendarId, String title, String description,
			String eventLocation, long dtstart, long dtend) {
		this.id = id;
		this.calendarId = calendarId;
		this.title = title;
		this.description = description;
		this.eventLocation = eventLocation;
		this.dtstart = dtstart;
		this.dtend = dtend;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCalendarId() {
		return calendarId;
	}

	public void setCalendarId(int calendarId) {
		this.calendarId = calendarId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventLocation() {
		return eventLocation;
	}

	public void setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation;
	}

	public long getDtstart() {
		return dtstart;
	}

	public void setDtstart(long dtstart) {
		this.dtstart = dtstart;
	}

	public long getDtend() {
		return dtend;
	}

	public void setDtend(long dtend) {
		this.dtend = dtend;
	}

}
