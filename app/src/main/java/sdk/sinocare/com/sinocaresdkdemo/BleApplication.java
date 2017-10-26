package sdk.sinocare.com.sinocaresdkdemo;

import android.app.Application;

import com.sinocare.handler.SN_MainHandler;
import com.sinocare.protocols.ProtocolVersion;

public class BleApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		//初始化
		SN_MainHandler.getBlueToothInstance().initSDK(this, ProtocolVersion.WL_1);
	}
	
}
