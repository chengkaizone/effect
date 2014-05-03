package effect.cheng.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import effect.cheng.adapter.PushAdapter;
import effect.cheng.widget.PushableListView;
import effect.cheng.widget.PushableListView.OnPushListener;

public class PushListDemo extends Activity {
	private PushAdapter mAdapter;
	private PushableListView mListView;
	private List<String> mList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.push_main);
		init();
	}

	private void init() {
		mList = new ArrayList<String>();
		String a = "";
		// ±£´æ26¸ö×Ö·û
		for (int i = 0; i < 26; i++) {
			a = ((char) ('A' + i)) + "";
			mList.add(a);
			mList.add(a);
			mList.add(a);
		}
		mListView = (PushableListView) findViewById(R.id.lv);
		mAdapter = new PushAdapter(this, mList);
		mListView.setTitleView(LayoutInflater.from(PushListDemo.this).inflate(
				R.layout.title, mListView, false));
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mListView.setOnPushListener(mAdapter);
	}

}
