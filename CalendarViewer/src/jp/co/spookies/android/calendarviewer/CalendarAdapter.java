package jp.co.spookies.android.calendarviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CalendarAdapter extends ArrayAdapter<CalendarEntry> {
	private LayoutInflater inflater;

	public CalendarAdapter(Context context, int resource) {
		super(context, resource);
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_calendar, null);
		}
		CalendarEntry calendar = getItem(position);
		((TextView) view.findViewById(R.id.calendar_title)).setText(calendar
				.getName());
		return view;
	}

}