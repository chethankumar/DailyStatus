package status.chethan.com.view.pager.fragement;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.melnykov.fab.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import me.alexrs.prefs.lib.Prefs;
import status.chethan.Utils;
import status.chethan.com.dailystatus.R;
import status.chethan.objects.Constants;
import status.chethan.objects.PersonStatus;
import timber.log.Timber;

/**
 * Created by Rahul on 3/17/2015.
 */
public class TestCalenderFragment extends  BaseFragment  {

    ArrayList<PersonStatus> statusList;

    TextView calendarDateHeader;
    ListView statusListView;
   // CalendarPickerView calendarPickerView;
    ProgressWheel loading;
    FloatingActionButton calendarButton;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    private static final String TAG = TestCalenderFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar_fragment, container, false);
        final Activity parentActivity = getActivity();
        calendarDateHeader = (TextView)view.findViewById(R.id.calendar_date_text);
        statusListView = (ListView)view.findViewById(R.id.status_listview);
       // calendarPickerView = (CalendarPickerView)view.findViewById(R.id.calendar_view);
        loading = (ProgressWheel)view.findViewById(R.id.calendar_progress_wheel);
        calendarButton = (FloatingActionButton)view.findViewById(R.id.calendar_button1);

        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getFragmentManager();
                org.joda.time.DateTime now = org.joda.time.DateTime.now();
                Log.v(TAG, "............................: " + now.getDayOfMonth() + " " + (now.getMonthOfYear()-1) + " " + now.getYear());
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(TestCalenderFragment.this, now.getYear(), now.getMonthOfYear() - 1,
                                now.getDayOfMonth());
                calendarDatePickerDialog.setStyle(DialogFragment.STYLE_NO_TITLE
                        , android.R.style.Theme_Holo_Light_Dialog
                ); //Theme_Holo_Dialog , Theme_DeviceDefault_Panel , Theme_DeviceDefault_Light_Panel

                calendarDatePickerDialog.show(fm, FRAG_TAG_DATE_PICKER);
            }
        });
       // ButterKnife.inject(parentActivity);
        Icepick.restoreInstanceState(this, savedInstanceState);
        Timber.plant(new Timber.DebugTree());

        statusList = new ArrayList<PersonStatus>();
        //statusListView = (ListView)findViewById(R.id.status_listview);

        statusListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return statusList.size();
            }

            @Override
            public Object getItem(int position) {
                return statusList.get(position);
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
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.status_card, parent, false);
                    viewHolder.personName = (TextView) convertView.findViewById(R.id.status_card_name);
                    viewHolder.personStatus = (TextView) convertView.findViewById(R.id.status_card_text);

                    viewHolder.personName.setTypeface(Utils.getRegularTypeface(getActivity().getApplicationContext()));
                    viewHolder.personStatus.setTypeface(Utils.getRegularTypeface(getActivity().getApplicationContext()));
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.personName.setText(statusList.get(position).getPersonName());
                viewHolder.personStatus.setText(statusList.get(position).getPersonStatusUpdate());

                return convertView;
            }
        });

//        Calendar nextYear = Calendar.getInstance();
//        nextYear.add(Calendar.DAY_OF_MONTH, 1);
//
//        Calendar previousYear = Calendar.getInstance();
//        previousYear.add(Calendar.YEAR, -1);

//        calendarPickerView = (CalendarPickerView)findViewById(R.id.calendar_view);
        calendarDateHeader.setTypeface(Utils.getLightTypeface(getActivity().getApplicationContext()));

        DateTime today = DateTime.forInstant(new Date().getTime(), TimeZone.getDefault());
        calendarDateHeader.setText(today.format(Constants.DATE_HEADER_FORMAT, Locale.ENGLISH));
        org.joda.time.DateTime todayDate= org.joda.time.DateTime.now();
        String defaultDate =  String.format("%02d",todayDate.getDayOfMonth()) + " " + String.format("%02d",todayDate.getMonthOfYear()) + " " + todayDate.getYear();
        populateData(defaultDate);
        return view;
    }




    @DebugLog
    private void populateData(String date){
        statusList.clear();

        ParseQuery<ParseObject> getStatus = ParseQuery.getQuery(
                Prefs.with(getActivity().getApplicationContext()).getString(Constants.TEAM_NAME, null)
        );
        getStatus.whereEqualTo(Constants.DATE_COLUMN,date);
        getStatus.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e!=null){//some exception
                    if(e.getCode() == 100){ // no network connection
                        PersonStatus ps = new PersonStatus();
                        ps.setPersonName("");
                        ps.setPersonStatusUpdate("No network connection! Please try again when connected to internet");
                        statusList.add(ps);
                        ((BaseAdapter)statusListView.getAdapter()).notifyDataSetChanged();
                    }
                    Timber.d(e.getMessage()+e.getCode());
                }else{
                    if(parseObjects.size()==0){
                        PersonStatus ps = new PersonStatus();
                        ps.setPersonName("No Updates!!");
                        ps.setPersonStatusUpdate("It wasn't much of a productive day");
                        statusList.add(ps);
                        ((BaseAdapter)statusListView.getAdapter()).notifyDataSetChanged();
                    }else{
                        for(ParseObject parseObject:parseObjects){
                            PersonStatus ps = new PersonStatus();
                            ps.setPersonName(resolveEmailToUserName(parseObject.getString(Constants.USERNAME_COLUMN)));
                            ps.setPersonStatusUpdate(Utils.parsedStatusUpdate( parseObject.getString(Constants.STATUS_COLUMN)));
                            statusList.add(ps);
                            ((BaseAdapter)statusListView.getAdapter()).notifyDataSetChanged();
                        }
                    }

                }
                loading.stopSpinning();
                loading.setVisibility(View.GONE);
            }
        });

        //currently dummy data for testing
//        ParseObject po = new ParseObject(ParseUser.getCurrentUser().getString(Constants.TEAM_NAME));
//        po.put(Constants.DATE_COLUMN,DateTime.forInstant(date.getTime(),TimeZone.getDefault()).format(Constants.DATE_FORMAT));
//        po.put(Constants.STATUS_COLUMN,"Cordova upgrade | xamarin");
//        po.put(Constants.USERNAME_COLUMN,ParseUser.getCurrentUser().getEmail());
//        po.saveInBackground();

    }

    @Override
    public void onDateSet(CalendarDatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {

        String selectedDate =  String.format("%02d",dayOfMonth) + " " + String.format("%02d",monthOfYear+1) + " " + year;
        Log.v(TAG, "############:" +selectedDate);
        populateData(selectedDate);
    }

    @Override
    public void onResume() {
        // Example of reattaching to the fragment
        super.onResume();
        CalendarDatePickerDialog calendarDatePickerDialog = (CalendarDatePickerDialog)getActivity().getFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialog != null) {
            calendarDatePickerDialog.setOnDateSetListener(this);
        }
    }

    @DebugLog
    private String resolveEmailToUserName(final String email){
        //First query the local cache of username to name, if not found, then get it from Parse and update the cache
        String name = Prefs.with(getActivity().getApplicationContext()).getString(email,null);
        if(name != null){
            return name;
        }else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.USER_TABLE);
            query.whereEqualTo(Constants.EMAIL_COLUMN, email);
            try {
                List<ParseObject> poList = query.find();
                for (ParseObject parseObject : poList) {
                    if (parseObject.getString(Constants.EMAIL_COLUMN).equalsIgnoreCase(email)) {
                        Prefs.with(getActivity().getApplicationContext()).save(email,parseObject.getString(Constants.NAME_COLUMN));
                        return parseObject.getString(Constants.NAME_COLUMN);
                    }
                }

            } catch (ParseException e) {
                Timber.d(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }

    static class ViewHolder{
        TextView personName;
        TextView personStatus;
    }
}
