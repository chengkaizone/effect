package effect.cheng.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import effect.cheng.adapter.ImageAdapter;
import effect.cheng.widget.ViewFlow;
import effect.cheng.widget.ViewFlow.OnViewSwitchListener;

/**
 * ·ÂÌÔ±¦¿Í»§¶ËÊ×Ò³ÍÆ¼öÀ¸
 * 
 * @author chengkai
 * 
 */
public class ViewFlowDemo extends Activity implements OnViewSwitchListener {
	private ViewFlow viewFlow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewflow_main);
		viewFlow = (ViewFlow) findViewById(R.id.viewflow);
		viewFlow.setAdapter(new ImageAdapter(this));
		viewFlow.setmSideBuffer(3);
		viewFlow.setTimeSpan(3000);
		viewFlow.startAutoFlowTimer();
		viewFlow.setOnSwitchListener(this);
	}

	@Override
	public void onSwitched(View view, int position) {
	}
}