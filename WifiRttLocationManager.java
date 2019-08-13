import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.util.Log;

import java.util.List;


public class WifiRttLocationManager {
    private static final String TAG = WifiRttLocationManager.class.getSimpleName();

    private WifiRttManager wifiRttManager;
    private WifiManager wifiManager;

    public WifiRttLocationManager(final Context mContext){

        //기기가 WIFI RTT를 지원하는지 확인
        if( mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)){

            //유저가 WIFI를 껏는지 켰는지 변화 감지
            IntentFilter filter = new IntentFilter(WifiRttManager.ACTION_WIFI_RTT_STATE_CHANGED);
            BroadcastReceiver myReceiver = new BroadcastReceiver() {

                @Override
                @SuppressLint("MissingPermission")
                public void onReceive(Context context, Intent intent) {
                    Log.i(TAG,"onReceive() in");
                    //wifiRttManager.isAvailable()을 이용하기 위해선 minimum sdk가 28이어야 한다.
                    if (wifiRttManager.isAvailable()) {
                        RangingRequest.Builder builder = new RangingRequest.Builder();

                        List<ScanResult> scanResults = wifiManager.getScanResults();

                        if(scanResults.size() != 0){
                            Log.i(TAG,"Scan reulst is not zero!");
                            builder.addAccessPoints(scanResults);
                            RangingRequest req = builder.build();

                            WifiRttManager mgr =
                                    (WifiRttManager) mContext.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

                            mgr.startRanging(req, mContext.getMainExecutor(), new RangingResultCallback() {

                                @Override
                                public void onRangingFailure(int code) {
                                    Log.d(TAG, "onRangingFailure() code: " + code);
                                }

                                @Override
                                public void onRangingResults(List<RangingResult> results) {
                                    Log.d(TAG, "onRangingResults(): " + results);
                                }
                            });
                        }

                    } else {

                    }
                }
            };
            mContext.registerReceiver(myReceiver, filter);

        } else{ //지원 안함
            Log.i(TAG,"WIFI rtt 지원안함");

        }




    }
}
