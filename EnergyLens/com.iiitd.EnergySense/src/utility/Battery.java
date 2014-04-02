package utility;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class Battery extends BroadcastReceiver{

	Context context;
	Battery(Context c){
		context=c;
		c.registerReceiver(this, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}
	
	
	class UnregisterTask {
		public void run() {

			context.unregisterReceiver(null);


		}
	}

	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		
		
	}

}
