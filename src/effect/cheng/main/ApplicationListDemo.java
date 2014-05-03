package effect.cheng.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 程序列表
 * 
 * @author Administrator
 * 
 */
public class ApplicationListDemo extends Activity {
	private ListView listview;
	private List<ResolveInfo> list;
	private AppAdapter adapter;
	private PackageManager pm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_list);
		list = new ArrayList<ResolveInfo>();
		pm = this.getPackageManager();
		listview = (ListView) findViewById(R.id.listview);
		// 查询所有在launcher中启动的活动
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		list = pm.queryIntentActivities(intent, 0);
		adapter = new AppAdapter(this, list);
		listview.setAdapter(adapter);
	}

	private class AppAdapter extends BaseAdapter {
		LayoutInflater inflater;
		List<ResolveInfo> infos;

		AppAdapter(Context context, List<ResolveInfo> infos) {
			inflater = LayoutInflater.from(context);
			this.infos = infos;
		}

		public int getCount() {
			return list.size();
		}

		public Object getItem(int position) {
			return list.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder = null;
			if (convertView == null) {
				holder = new Holder();
				convertView = inflater.inflate(R.layout.simple_item_2, null);
				holder.img = (ImageView) convertView.findViewById(R.id.icon);
				holder.title = (TextView) convertView
						.findViewById(R.id.p_title);
				holder.send = (TextView) convertView.findViewById(R.id.p_send);
				holder.receive = (TextView) convertView
						.findViewById(R.id.p_recieve);
				holder.total = (TextView) convertView
						.findViewById(R.id.p_total);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			ResolveInfo info = infos.get(position);
			holder.img.setBackgroundDrawable(info.activityInfo.loadIcon(pm));
			holder.title.setText(info.activityInfo.loadLabel(pm));
			holder.send.setText("5M");
			holder.receive.setText("3M");
			holder.total.setText("8M");
			return convertView;
		}

		private class Holder {
			ImageView img;
			TextView title;
			TextView send;
			TextView receive;
			TextView total;
		}

	}
}