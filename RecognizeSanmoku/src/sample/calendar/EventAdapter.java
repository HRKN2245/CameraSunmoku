package sample.calendar;

import java.text.SimpleDateFormat;
import java.util.Date;

import sample.calendar.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<EventEntry> {
	private LayoutInflater inflater;
	public static final SimpleDateFormat format = new SimpleDateFormat(
			"yyyy年MM月dd日");
	public static final SimpleDateFormat formatTime = new SimpleDateFormat(
			"HH:mm");

	public EventAdapter(Context context, int resource) {
		super(context, resource);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_entry, null);
		}
		EventEntry entry = getItem(position);
		((TextView) view.findViewById(R.id.entry_title)).setText(entry
				.getTitle());
		((TextView) view.findViewById(R.id.entry_date)).setText(format
				.format(new Date(entry.getDtstart())));
		((TextView) view.findViewById(R.id.entry_time)).setText(formatTime
				.format(new Date(entry.getDtstart()))
				+ "～"
				+ formatTime.format(new Date(entry.getDtend())));
		((TextView) view.findViewById(R.id.entry_location)).setText(entry
				.getEventLocation());
		return view;
	}

}