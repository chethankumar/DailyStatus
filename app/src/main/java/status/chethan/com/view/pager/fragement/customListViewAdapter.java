package status.chethan.com.view.pager.fragement;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import status.chethan.com.dailystatus.R;

/**
 * Created by Rahul on 2/27/2015.
 */
public class customListViewAdapter extends ArrayAdapter<Message> {

    public customListViewAdapter(Context context, List<Message> messages) {
        super(context, 0, messages);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView = LayoutInflater.from(getContext()).
                    inflate(R.layout.message_layout, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.userName = (TextView)convertView.findViewById(R.id.userName);
            holder.message = (TextView)convertView.findViewById(R.id.message);
            convertView.setTag(holder);

        }
       final Message msg = getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag() ;
        final boolean isMe = msg.getUserName().equals("Rahul");
        holder.userName.setText(msg.getUserName());
        if (isMe)
            holder.userName.setTextColor(Color.BLUE);
        else
            holder.userName.setTextColor(Color.GREEN);
        holder.message.setText(msg.getTextMessage());
        return convertView;
    }

    final class ViewHolder {
        public TextView userName;
        public TextView message;
    }
}
