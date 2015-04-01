/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package status.chethan.com.view.pager.fragement;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.TextView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import status.chethan.Utils;
import status.chethan.com.dailystatus.R;
import status.chethan.objects.PersonStatus;
import status.chethan.objects.Status;

public class ViewPagerTabListViewFragment extends BaseFragment {

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    private FloatingActionButton sendButton;
    private EditText messageText;
    private ArrayList mMessages;
    private customListViewAdapter cAdapter;
    public static final String ARG_SCROLL_Y = "ARG_SCROLL_Y";
    ArrayList<Status> status;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.test_delete, container, false);

        Activity parentActivity = getActivity();
        sendButton = (FloatingActionButton)view.findViewById(R.id.calendar_button1);
        messageText = (EditText)view.findViewById(R.id.test_statusText);
        mMessages = new ArrayList<Message>();
        cAdapter = new customListViewAdapter(parentActivity, mMessages);
        final ObservableScrollView scrollView = (ObservableScrollView) view.findViewById(R.id.scroll);
        final ObservableListView statusList = (ObservableListView) view.findViewById(R.id.statusList);
       // setDummyDataWithHeader(statusList, inflater.inflate(R.layout.padding, null));
        //final ListView statusList = (ListView)view.findViewById(R.id.statusList);
        //statusList.setAdapter(cAdapter);
       // statusList.addHeaderView(inflater.inflate(R.layout.padding, null));
        status = new ArrayList<Status>();
        statusList.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return status.size();
            }

            @Override
            public Object getItem(int position) {
                return status.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;

                if (convertView == null) {
                    viewHolder = new ViewHolder();
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.today_card, parent, false);
                    viewHolder.statusText = (TextView) convertView.findViewById(R.id.today_card_text);

                    viewHolder.statusText.setTypeface(Utils.getRegularTypeface(getActivity().getApplicationContext()));
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.statusText.setText(status.get(position).getStatusText());

                return convertView;
            }
        });

//        if (parentActivity instanceof ObservableScrollViewCallbacks) {
//            // Scroll to the specified position after layout
//            Bundle args = getArguments();
//            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
//                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
//                ScrollUtils.addOnGlobalLayoutListener(statusList, new Runnable() {
//                    @Override
//                    public void run() {
//                        // scrollTo() doesn't work, should use setSelection()
//                        statusList.setSelection(initialPosition);
//                    }
//                });
//            }
//           statusList.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        if (parentActivity instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified offset after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_SCROLL_Y)) {
                final int scrollY = args.getInt(ARG_SCROLL_Y, 0);
                ScrollUtils.addOnGlobalLayoutListener(scrollView, new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, scrollY);
                    }
                });
            }
            scrollView.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentActivity);
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Message message = new Message("Rahul Raghuvanshi", messageText.getText().toString());
                String textMessage = messageText.getText().toString();
                Status st = new Status();
                st.setStatusText(textMessage);
                messageText.setText("");
                status.add(st);
//                cAdapter.notifyDataSetChanged();
              // ((BaseAdapter)((HeaderViewListAdapter)statusList.getAdapter()).getWrappedAdapter()).notifyDataSetChanged();
                ((BaseAdapter)statusList.getAdapter()).notifyDataSetChanged();
               // statusList.invalidate();
            }
        });
        return view;
    }

    static class ViewHolder{
        TextView statusText;
    }
}
