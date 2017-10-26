package sdk.sinocare.com.sinocaresdkdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DevicesListAdapter extends BaseAdapter{
	private ArrayList<MainActivity.SiriListItem> list;
    private LayoutInflater mInflater;
  
    public DevicesListAdapter(Context context, ArrayList<MainActivity.SiriListItem> list2) {
    	list = list2;
		mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder viewHolder = null;
    	MainActivity.SiriListItem item=list.get(position);
        if(convertView == null){
        	convertView = mInflater.inflate(R.layout.list_item, null);          
        	viewHolder=new ViewHolder(
        			(View) convertView.findViewById(R.id.list_child),
        			(TextView) convertView.findViewById(R.id.chat_msg)
        	       );
        	convertView.setTag(viewHolder);
        }
        else{
        	viewHolder = (ViewHolder)convertView.getTag();
        }       
        
        if(item.isSiri)
        {
        	viewHolder.child.setBackgroundResource(R.mipmap.msgbox_rec);
        }
        else 
        {
        	viewHolder.child.setBackgroundResource(R.mipmap.msgbox_send);
        }
        viewHolder.msg.setText(item.message);    
        
        return convertView;
    }
    
    class ViewHolder {
    	  protected View child;
          protected TextView msg;
  
          public ViewHolder(View child, TextView msg){
              this.child = child;
              this.msg = msg;
              
          }
    }
}
