package status.chethan.com.view.pager.fragement;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.doomonafireball.betterpickers.calendardatepicker.CalendarDatePickerDialog;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.melnykov.fab.FloatingActionButton;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
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
public class DeleteTestCalendarFragment extends  BaseFragment implements ObservableScrollViewCallbacks {

    ArrayList<PersonStatus> statusList;

    TextView calendarDateHeader1;
    ObservableListView statusListView1;
    // CalendarPickerView calendarPickerView;
    ProgressWheel loading;
    FloatingActionButton calendarButton1;
    private View mToolbarView;
    private View mHeaderView;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";

    private int mBaseTranslationY;

    public static final String ARG_INITIAL_POSITION = "ARG_INITIAL_POSITION";
    private static final String TAG = DeleteTestCalendarFragment.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.delete_test_layout, container, false);
        final Activity parentActivity = getActivity();
        calendarDateHeader1 = (TextView)view.findViewById(R.id.calendar_date_text1);
        statusListView1 = (ObservableListView)view.findViewById(R.id.status_listview1);
        mHeaderView = view.findViewById(R.id.header);
        ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
        ((ActionBarActivity)getActivity()).setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));
        // calendarPickerView = (CalendarPickerView)view.findViewById(R.id.calendar_view);
        mToolbarView = (View)view.findViewById(R.id.toolbar);
        statusListView1.setScrollViewCallbacks(this);
        statusListView1.addHeaderView(inflater.inflate(R.layout.padding, statusListView1, false)); // toolbar
        statusListView1.addHeaderView(inflater.inflate(R.layout.padding, statusListView1, false));
       // setDummyData(statusListView1);
        loading = (ProgressWheel)view.findViewById(R.id.calendar_progress_wheel);
        calendarButton1 = (FloatingActionButton)view.findViewById(R.id.calendar_button1);

        calendarButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getActivity().getFragmentManager();
                org.joda.time.DateTime now = org.joda.time.DateTime.now();
                CalendarDatePickerDialog calendarDatePickerDialog = CalendarDatePickerDialog
                        .newInstance(DeleteTestCalendarFragment.this, now.getYear(), now.getMonthOfYear() - 1,
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
        statusListView1.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.v(TAG, "onScrollStateChanged: " + scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.v(TAG, "onScroll: firstVisibleItem: " + firstVisibleItem + " visibleItemCount: " + visibleItemCount + " totalItemCount: " + totalItemCount);
            }
        });


        statusList = new ArrayList<PersonStatus>();
        //statusListView = (ListView)findViewById(R.id.status_listview);

        statusListView1.setAdapter(new BaseAdapter() {
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

        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.DAY_OF_MONTH, 1);

        Calendar previousYear = Calendar.getInstance();
        previousYear.add(Calendar.YEAR, -1);

//        calendarPickerView = (CalendarPickerView)findViewById(R.id.calendar_view);
        calendarDateHeader1.setTypeface(Utils.getLightTypeface(getActivity().getApplicationContext()));

        DateTime today = DateTime.forInstant(new Date().getTime(), TimeZone.getDefault());
        calendarDateHeader1.setText(today.format(Constants.DATE_HEADER_FORMAT, Locale.ENGLISH));

        populateData(new Date());
        return view;
    }




    @DebugLog
    private void populateData(Date date){
        statusList.clear();

        ParseQuery<ParseObject> getStatus = ParseQuery.getQuery(
                Prefs.with(getActivity().getApplicationContext()).getString(Constants.TEAM_NAME, null)
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
                        ((BaseAdapter)statusListView1.getAdapter()).notifyDataSetChanged();
                    }
                    Timber.d(e.getMessage()+e.getCode());
                }else{
                    if(parseObjects.size()==0){
                        PersonStatus ps = new PersonStatus();
                        ps.setPersonName("No Updates!!");
                        ps.setPersonStatusUpdate("It wasn't much of a productive day");
                        statusList.add(ps);
                        ((BaseAdapter)statusListView1.getAdapter()).notifyDataSetChanged();
                    }else{
                        for(ParseObject parseObject:parseObjects){
                            PersonStatus ps = new PersonStatus();
                            ps.setPersonName(resolveEmailToUserName(parseObject.getString(Constants.USERNAME_COLUMN)));
                            ps.setPersonStatusUpdate(Utils.parsedStatusUpdate( parseObject.getString(Constants.STATUS_COLUMN)));
                            statusList.add(ps);
                            ((BaseAdapter)statusListView1.getAdapter()).notifyDataSetChanged();
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

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        if (dragging) {
            int toolbarHeight = mToolbarView.getHeight();
            if (firstScroll) {
                float currentHeaderTranslationY = ViewHelper.getTranslationY(mHeaderView);
                if (-toolbarHeight < currentHeaderTranslationY) {
                    mBaseTranslationY = scrollY;
                }
            }
            float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewHelper.setTranslationY(mHeaderView, headerTranslationY);

        }
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        mBaseTranslationY = 0;

        if (scrollState == ScrollState.DOWN) {
            showToolbar();
        } else if (scrollState == ScrollState.UP) {
            int toolbarHeight = mToolbarView.getHeight();
            int scrollY = statusListView1.getCurrentScrollY();
            if (toolbarHeight <= scrollY) {
                hideToolbar();
            } else {
                showToolbar();
            }
        } else {
            // Even if onScrollChanged occurs without scrollY changing, toolbar should be adjusted
            if (!toolbarIsShown() && !toolbarIsHidden()) {
                // Toolbar is moving but doesn't know which to move:
                // you can change this to hideToolbar()
                showToolbar();
            }
        }
    }

    private boolean toolbarIsShown() {
        return ViewHelper.getTranslationY(mHeaderView) == 0;
    }

    private boolean toolbarIsHidden() {
        return ViewHelper.getTranslationY(mHeaderView) == -mToolbarView.getHeight();
    }

    private void showToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        if (headerTranslationY != 0) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(0).setDuration(200).start();
        }
//        if (getActivity() instanceof ObservableScrollViewCallbacks) {
//            // Scroll to the specified position after layout
//            Bundle args = getArguments();
//            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
//                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
//                ScrollUtils.addOnGlobalLayoutListener(statusListView1, new Runnable() {
//                    @Override
//                    public void run() {
//                        // scrollTo() doesn't work, should use setSelection()
//                        statusListView1.setSelection(initialPosition);
//                    }
//                });
//            }
//            statusListView1.setScrollViewCallbacks((ObservableScrollViewCallbacks) getActivity());
//        }
    }

    private void hideToolbar() {
        float headerTranslationY = ViewHelper.getTranslationY(mHeaderView);
        int toolbarHeight = mToolbarView.getHeight();
        if (headerTranslationY != -toolbarHeight) {
            ViewPropertyAnimator.animate(mHeaderView).cancel();
            ViewPropertyAnimator.animate(mHeaderView).translationY(-toolbarHeight).setDuration(200).start();
        }
        if (getActivity() instanceof ObservableScrollViewCallbacks) {
            // Scroll to the specified position after layout
            Bundle args = getArguments();
            if (args != null && args.containsKey(ARG_INITIAL_POSITION)) {
                final int initialPosition = args.getInt(ARG_INITIAL_POSITION, 0);
                ScrollUtils.addOnGlobalLayoutListener(statusListView1, new Runnable() {
                    @Override
                    public void run() {
                        // scrollTo() doesn't work, should use setSelection()
                        statusListView1.setSelection(initialPosition);
                    }
                });
            }
            statusListView1.setScrollViewCallbacks((ObservableScrollViewCallbacks) getActivity());
        }
    }

    static class ViewHolder{
        TextView personName;
        TextView personStatus;
    }
}
