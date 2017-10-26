package sdk.sinocare.com.sinocaresdkdemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sinocare.Impl.SC_BlueToothSearchCallBack;
import com.sinocare.domain.BlueToothInfo;
import com.sinocare.handler.SN_MainHandler;

import java.util.ArrayList;

/**
 * 主界面
 */
public class MainActivity extends Activity {

    private ListView mListView;
    private ArrayList<SiriListItem> list;
    private DevicesListAdapter mAdapter;
    private Context mContext = null;
    private SN_MainHandler Sn_MainHandler = null;
    private Button searchButton, commandButton, disconnectButton, clearButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hud_main);
        init();
    }

    /**
     * 容器
     */
    private void initData() {
        list = new ArrayList<>();
        mAdapter = new DevicesListAdapter(this, list);
    }

    /**
     * 添加事件
     */
    private void initEvent() {
        searchButton.setOnClickListener(searchButtonClickListener);
        mListView.setOnItemClickListener(mDeviceClickListener);
    }

    /**
     * 初始化界面UI
     */
    private void initView() {
        searchButton = (Button) findViewById(R.id.bt_search);
        commandButton = (Button) findViewById(R.id.bt_command);
        disconnectButton = (Button) findViewById(R.id.bt_disconnect);
        clearButton = (Button) findViewById(R.id.bt_clear);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        setActivityInIdleState();
    }

    /**
     * 初始化
     */
    private void init() {
        mContext = this;
        Sn_MainHandler = SN_MainHandler.getBlueToothInstance();
        initData();
        initView();
        initEvent();
    }

    /**
     * 未连接状态界面刷新
     */
    private void setActivityInIdleState() {
        commandButton.setVisibility(View.GONE);
        searchButton.setVisibility(View.VISIBLE);
        disconnectButton.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        if (Sn_MainHandler.isSearching()) {
            searchButton.setText("停止搜索");
        } else {
            searchButton.setText("搜索/连接");
        }
    }

    private OnClickListener searchButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View arg0) {
            if (Sn_MainHandler.isConnecting()) {
                Toast.makeText(mContext, "正在断开，请稍等", Toast.LENGTH_SHORT).show();
                Sn_MainHandler.disconnectDevice();
                return;
            }
            list.clear();
            mAdapter.notifyDataSetChanged();
            if (!Sn_MainHandler.isBlueToothEnable()) {
                Intent enableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, 3);
            }
            if (Sn_MainHandler.isConnected()) {
                Sn_MainHandler.disconnectDevice();
                searchButton.setText("搜索/连接");
            } else if (Sn_MainHandler.isSearching()) {
                Sn_MainHandler.cancelSearch();
                searchButton.setText("停止搜索");
            } else {
                searchButton.setText("搜索/连接");
                Sn_MainHandler.searchBlueToothDevice(new SC_BlueToothSearchCallBack<BlueToothInfo>() {
                    @Override
                    public void onBlueToothSeaching(BlueToothInfo newDevice) {

                        SiriListItem sir = new SiriListItem(newDevice.getName() + "\n"
                                + newDevice.getDevice().getAddress(), false, newDevice);

                        //过滤掉已添加在设备列表中设备
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).info.getDevice().getAddress().equals(newDevice.getDevice().getAddress())) {
                                if (newDevice.getName().equals(list.get(i).info.getName()))
                                    return;
                            }
                        }
                        //添加list
                        list.add(sir);
                        mAdapter.notifyDataSetChanged();
                        mListView.setSelection(list.size() - 1);
                    }
                });
            }
        }
    };

    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            final SiriListItem item = list.get(arg2);
            AlertDialog.Builder StopDialog = new AlertDialog.Builder(mContext);//定义一个弹出框对象
            StopDialog.setTitle("是否连接设备？");
            StopDialog.setMessage(item.message);
            StopDialog.setPositiveButton("连接", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Sn_MainHandler.cancelSearch();
                    Intent intent = new Intent(MainActivity.this, CommunicationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("device", item.info.getDevice());
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            StopDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            StopDialog.show();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!Sn_MainHandler.isBlueToothEnable()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 3);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //作为SDK时销毁Activity不关闭Handler，保持蓝牙通信不断开
        //导航软件请在主进程退出时调用close()方法关闭蓝牙通信模块
        Sn_MainHandler.close();
    }

    public class SiriListItem {
        String message;
        boolean isSiri;
        BlueToothInfo info;

        public SiriListItem(String msg, boolean siri, BlueToothInfo infos) {
            message = msg;
            isSiri = siri;
            info = infos;
        }
    }
}

