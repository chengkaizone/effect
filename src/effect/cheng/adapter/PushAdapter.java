package effect.cheng.adapter;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;
import effect.cheng.main.R;
import effect.cheng.widget.PushableListView;
import effect.cheng.widget.PushableListView.OnPushListener;

public class PushAdapter extends BaseAdapter implements SectionIndexer,
		OnScrollListener, OnPushListener {

	private Context context;
	private List<String> mList;
	private String sections[];

	public PushAdapter(Context context, List<String> list) {
		this.context = context;
		sections = new String[26];
		Collections.sort(list);
		mList = list;
		int pos = 0;
		for (int i = 0; i < mList.size(); i++) {
			String cur = mList.get(i);
			String pre = (i - 1) >= 0 ? mList.get(i - 1) : "";
			if (!(pre.equals(cur))) {
				sections[pos] = cur;
				pos++;
			}
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		if (convertView != null) {
			holder = (ViewHolder) convertView.getTag();
		} else {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.list_item, null);
			holder.title = (TextView) convertView.findViewById(R.id.item_title);
			holder.content = (TextView) convertView
					.findViewById(R.id.item_content);
			convertView.setTag(holder);
		}
		holder.content.setText(mList.get(position));
		String cur = mList.get(position);
		String pre = position - 1 >= 0 ? mList.get(position - 1) : "";
		// 如果当前项和前一项数据相同隐藏标题头；否则显示
		if (!(pre.equals(cur))) {
			holder.title.setVisibility(View.VISIBLE);
			holder.title.setText(cur);
		} else {
			holder.title.setVisibility(View.GONE);
		}
		return convertView;
	}

	class ViewHolder {
		TextView title;
		TextView content;
	}

	public int getTitleState(int position) {
		if (position < 0 || getCount() == 0) {

			return 0;
		}
		int index = getSectionForPosition(position);
		if (index == -1 || index > sections.length) {

			return 0;
		}
		int section = getSectionForPosition(position);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1 && position == nextSectionPosition - 1) {

			return 2;
		}

		return 1;
	}

	@Override
	public int getPositionForSection(int section) {

		String sec = sections[section];
		int pos = mList.indexOf(sec);
		return pos;
	}

	@Override
	public int getSectionForPosition(int position) {
		String a = mList.get(position);
		for (int i = 0; i < sections.length; i++) {
			if (sections[i] == a) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return sections;
	}

	public void setTitleText(View mHeader, int firstVisiblePosition) {
		String title = mList.get(firstVisiblePosition);
		TextView sectionHeader = (TextView) mHeader;
		sectionHeader.setText(title);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof PushableListView) {
			((PushableListView) view).titleLayout(firstVisibleItem);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void handleTitle(View title, int position) {
		setTitleText(title, position);
	}

	@Override
	public int getStateFromAdapter(int position) {
		return getTitleState(position);
	}
}