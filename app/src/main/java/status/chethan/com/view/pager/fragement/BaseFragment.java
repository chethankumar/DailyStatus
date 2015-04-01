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
import android.app.FragmentManager;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;
import status.chethan.com.dailystatus.R;

public abstract class BaseFragment extends Fragment implements CalendarDatePickerDialog.OnDateSetListener {
    public static ArrayList<String> getDummyData() {
        return BaseActivity.getDummyData();
    }

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    protected int getActionBarSize() {
        Activity activity = getActivity();
        if (activity == null) {
            return 0;
        }
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = activity.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        // Toast.makeText(getActivity(), "Year: " + year + "\nMonth: " + monthOfYear + "\nDay: " + dayOfMonth, Toast.LENGTH_LONG).show();
        Date date = new Date();
        DateTime dateTime = DateTime.forInstant(date.getTime(), TimeZone.getDefault());

        // text.setText("Year: " + year + "\nMonth: " + monthOfYear + "\nDay: " + dayOfMonth);
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialog calendarDatePickerDialog = (CalendarDatePickerDialog) getActivity().getFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialog != null) {
            calendarDatePickerDialog.setOnDateSetListener(this);
        }
    }
    protected int getScreenHeight() {
        Activity activity = getActivity();
        if (activity == null) {
            return 0;
        }
        return activity.findViewById(android.R.id.content).getHeight();
    }

    protected void setDummyData(ListView listView) {
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getDummyData()));
    }

    protected void setDummyDataWithHeader(ListView listView, View headerView) {
        listView.addHeaderView(headerView);
        setDummyData(listView);
    }

    protected void setDummyData(GridView gridView) {
        gridView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, getDummyData()));
    }

    protected void setDummyData(RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleRecyclerAdapter(getActivity(), getDummyData()));
    }

    protected void setDummyDataWithHeader(RecyclerView recyclerView, View headerView) {
        recyclerView.setAdapter(new SimpleHeaderRecyclerAdapter(getActivity(), getDummyData(), headerView));
    }
}
