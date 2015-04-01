package status.chethan.com.dailystatus;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.pnikosis.materialishprogress.ProgressWheel;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hirondelle.date4j.DateTime;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import me.alexrs.prefs.lib.Prefs;
import status.chethan.Utils;
import status.chethan.objects.Constants;
import status.chethan.objects.PersonStatus;
import timber.log.Timber;


public class CalenderActivityBackup extends Activity {

    ArrayList<PersonStatus> statusList;

    @InjectView(R.id.calendar_date_text) TextView calendarDateHeader;
    @InjectView(R.id.status_listview) ListView statusListView;
    @InjectView(R.id.calendar_view)CalendarPickerView calendarPickerView;
    @InjectView(R.id.calendar_progress_wheel) ProgressWheel loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        ButterKnife.inject(this);
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

                if (convertView == null){
                    viewHolder = new ViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.status_card,parent,false);
                    viewHolder.personName = (TextView)convertView.findViewById(R.id.status_card_name);
                    viewHolder.personStatus = (TextView)convertView.findViewById(R.id.status_card_text);

                    viewHolder.personName.setTypeface(Utils.getRegularTypeface(getApplicationContext()));
                    viewHolder.personStatus.setTypeface(Utils.getRegularTypeface(getApplicationContext()));
                    convertView.setTag(viewHolder);
                }else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.personName.setText(statusList.get(position).getPersonName());
                viewHolder.personStatus.setText(statusList.get(position).getPersonStatusUpdate());

                return convertView;
            }
        });

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_MONTH, 1);

        Calendar previousYear = Calendar.getInstance();
        previousYear.add(Calendar.YEAR, -1);

//        calendarPickerView = (CalendarPickerView)findViewById(R.id.calendar_view);
        calendarDateHeader.setTypeface(Utils.getLightTypeface(getApplicationContext()));
        calendarPickerView.init( previousYear.getTime(),nextYear.getTime()).withSelectedDate(new Date());

        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                DateTime dateTime = DateTime.forInstant(date.getTime(), TimeZone.getDefault());
//                Toast.makeText(CalendarActivity.this,"Date selected : "+dateTime.getDay()+" "+dateTime.getMonth()+" "+dateTime.getYear(),Toast.LENGTH_SHORT).show();
                populateData(date);

                calendarPickerView.setVisibility(View.GONE);

                DateTime dateSelected = DateTime.forInstant(date.getTime(), TimeZone.getDefault());
                calendarDateHeader.setText(dateSelected.format(Constants.DATE_HEADER_FORMAT,Locale.ENGLISH));
                loading.setVisibility(View.VISIBLE);
                loading.spin();
            }

            @Override
            public void onDateUnselected(Date date) {

            }
        });

        DateTime today = DateTime.forInstant(new Date().getTime(), TimeZone.getDefault());
        calendarDateHeader.setText( today.format(Constants.DATE_HEADER_FORMAT, Locale.ENGLISH));

        populateData(new Date());

        calendarDateHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(calendarPickerView.getVisibility() == View.VISIBLE)
                    calendarPickerView.setVisibility(View.GONE);
                else {
                    calendarPickerView.setVisibility(View.VISIBLE);

                }
            }
        });
    }

    @DebugLog
    private void populateData(Date date){
        statusList.clear();

        ParseQuery<ParseObject> getStatus = ParseQuery.getQuery(
                Prefs.with(getApplicationContext()).getString(Constants.TEAM_NAME,null)
        );

        getStatus.whereEqualTo(Constants.DATE_COLUMN,DateTime.forInstant(date.getTime(),TimeZone.getDefault()).format(Constants.DATE_FORMAT));
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

    @DebugLog
    private String resolveEmailToUserName(final String email){
        //First query the local cache of username to name, if not found, then get it from Parse and update the cache
        String name = Prefs.with(getApplicationContext()).getString(email,null);
        if(name != null){
            return name;
        }else {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.USER_TABLE);
            query.whereEqualTo(Constants.EMAIL_COLUMN, email);
            try {
                List<ParseObject> poList = query.find();
                for (ParseObject parseObject : poList) {
                    if (parseObject.getString(Constants.EMAIL_COLUMN).equalsIgnoreCase(email)) {
                        Prefs.with(getApplicationContext()).save(email,parseObject.getString(Constants.NAME_COLUMN));
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
