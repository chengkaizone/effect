package effect.cheng.main;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import effect.cheng.service.TrafficService;

/**
 * 流量监控页面
 * 
 * @author Administrator
 * 
 */
public class FlowMonitorDemo extends Activity {
	private TextView upRate, downRate;

	long old_totalRx = 0;
	long old_totalTx = 0;
	long totalRx = 0;
	long totalTx = 0;

	private Handler handler = new Handler();
	private Runnable runnable = new Runnable() {
		public void run() {
			this.update();
			handler.postDelayed(this, 1000);
			// 记录接收总数(byte)
			old_totalRx = TrafficStats.getTotalRxBytes();
			// 记录fa song总数(byte)
			old_totalTx = TrafficStats.getTotalTxBytes();
		}

		// 刷新记录
		private void update() {
			totalRx = TrafficStats.getTotalRxBytes();
			totalTx = TrafficStats.getTotalTxBytes();
			long mrx = totalRx - old_totalRx;
			old_totalRx = totalRx;
			long mtx = totalTx - old_totalTx;
			old_totalTx = totalTx;
			upRate.setText(TrafficService.convert(mtx) + "/s");
			downRate.setText(TrafficService.convert(mrx) + "/s");
		}

	};

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.flow_main);
		upRate = (TextView) findViewById(R.id.upRate);
		downRate = (TextView) findViewById(R.id.downRate);
		handler.postDelayed(runnable, 1000);
		/*
		 * toolbarGrid = (GridView) findViewById(R.id.GridView_toolbar);
		 * toolbarGrid.setSelector(R.drawable.toolbar_menu_item);
		 * toolbarGrid.setBackgroundResource(R.drawable.menu_bg2);// 设置背景
		 * toolbarGrid.setNumColumns(5);// 设置每行列数
		 * toolbarGrid.setGravity(Gravity.CENTER);// 位置居中
		 * toolbarGrid.setVerticalSpacing(20);// 垂直间隔
		 * toolbarGrid.setHorizontalSpacing(7);// 水平间隔
		 * toolbarGrid.setAdapter(getMenuAdapter(menu_toolbar_name_array,
		 * menu_toolbar_image_array));// 设置菜单Adapter
		 * toolbarGrid.setOnItemClickListener(new OnItemClickListener() { public
		 * void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
		 * { switch (arg2) { // case TOOLBAR_ITEM_KEEPINTIME: // Intent intent1
		 * = new Intent(); // intent1.setClass(this, keepInTime.class); //
		 * startActivity(intent1); // // break; // case TOOLBAR_ITEM_NETLIST: //
		 * Intent intent5 = new Intent(); // intent5.setClass(this,
		 * netList.class); // startActivity(intent5); // // break; // case
		 * TOOLBAR_ITEM_STATIST: // Intent intent2 = new Intent(); //
		 * intent2.setClass(this, statist.class); // startActivity(intent2); //
		 * // break; // case TOOLBAR_ITEM_SETTING: // Intent intent3 = new
		 * Intent(); // intent3.setClass(this, setting.class); //
		 * startActivity(intent3); // // break; // case TOOLBAR_ITEM_ABOUT: //
		 * Intent intent4 = new Intent(); // intent4.setClass(this,
		 * aboutus.class); // startActivity(intent4); // // break;
		 * 
		 * } } });
		 */

	}

}
