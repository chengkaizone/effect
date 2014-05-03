package effect.cheng.main;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import effect.cheng.widget.Panel;

public class SideMenuDemo extends Activity{

	private Panel menuPanel;
	
	public void onCreate(Bundle save){
		super.onCreate(save);
		setContentView(R.layout.rightside_main);
		
		menuPanel=(Panel)findViewById(R.id.rightPanel);
		
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_MENU){
			if(menuPanel.isOpen()){
				menuPanel.setOpen(false, true);
			}else{
				menuPanel.setOpen(true, true);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	
	
}
