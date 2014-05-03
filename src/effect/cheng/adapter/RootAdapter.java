package effect.cheng.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import effect.cheng.main.R;

public class RootAdapter extends BaseAdapter {
	private Context context;
	private List<String> data = new ArrayList<String>();
	private LayoutInflater inflater;

	public RootAdapter(Context context, List<String> data) {
		this.context = context;
		this.data = data;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder h = null;
		if (convertView == null) {
			h = new Holder();
			convertView = inflater.inflate(R.layout.root_adapter, null);
			h.img = (ImageView) convertView.findViewById(R.id.root_adapter_img);
			h.text = (TextView) convertView
					.findViewById(R.id.root_adapter_text);
			convertView.setTag(h);
		} else {
			h = (Holder) convertView.getTag();
		}
		h.text.setText(data.get(position));
		return convertView;
	}

	private class Holder {
		ImageView img;
		TextView text;
	}

}
