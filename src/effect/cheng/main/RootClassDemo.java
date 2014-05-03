package effect.cheng.main;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import effect.cheng.adapter.RootAdapter;
import effect.cheng.listener.ShakeListener;
import effect.cheng.listener.ShakeListener.OnShakeListener;

/**
 * ��װ
 * 
 * @author chengkai 
 * 
 */
public class RootClassDemo extends Activity implements OnShakeListener {
	private ListView list;
	public View mainView;
	private String[] srr = { "365�˵�","��������", "�Ա���Ч", "����ʵ��", "������Ч", "��ϵ����Ч", "�������",
			"�����б�" };
	private Class[] crr = { SideMenuDemo.class,
			CircleProgressDemo.class, ViewFlowDemo.class,
			CropDemo.class, NeteaseListDemo.class, PushListDemo.class,
			FlowMonitorDemo.class, ApplicationListDemo.class };
	private RootAdapter adapter;
	ShakeListener listener;

	public void onCreate(Bundle save) {
		super.onCreate(save);
		setContentView(R.layout.ck_main);
		list = (ListView) findViewById(R.id.main_listview);
		listener = new ShakeListener(this);
		listener.setOnShakeListener(this);
		setAdapter();
		list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivity(new Intent(RootClassDemo.this, crr[position]));
			}
		});
	}

	private void setAdapter() {
		List<String> data = new ArrayList<String>();
		for (int i = 0; i < srr.length; i++) {
			data.add(srr[i]);
		}
		adapter = new RootAdapter(this, data);
		list.setAdapter(adapter);
	}

	@Override
	public void onShake() {
		System.out.println("�ֻ����ڻζ���");
	}

}