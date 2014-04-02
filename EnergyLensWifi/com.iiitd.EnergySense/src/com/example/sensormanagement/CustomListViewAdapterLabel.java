package com.example.sensormanagement;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.iiitd.EnergySenseWifi.R;

public class CustomListViewAdapterLabel extends ArrayAdapter<RowItemLabel> {

	Context context;

	public CustomListViewAdapterLabel(Context context, int resourceId,
			List<RowItemLabel> items) {
		super(context, resourceId, items);
		this.context = context;
	}

	/*private view holder class*/
	private class ViewHolder {

		TextView txtTitle;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		RowItemLabel rowItem = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.list_label, null);
			holder = new ViewHolder();

			holder.txtTitle = (TextView) convertView.findViewById(R.id.title);

			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();


		holder.txtTitle.setText(rowItem.getTitle());


		return convertView;
	}
}