package status.chethan.com.dailystatus;

import android.app.Activity;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.achep.header2actionbar.FadingActionBarHelper;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hugo.weaving.DebugLog;
import icepick.Icepick;
import icepick.Icicle;
import status.chethan.CircularImageView;
import status.chethan.Utils;
import status.chethan.objects.Constants;
import status.chethan.objects.PersonStatus;
import timber.log.Timber;


public class ProfileActivity extends ActionBarActivity {

    @InjectView(R.id.profile_view_name) TextView profileName;
    @InjectView(R.id.profile_view_profilepic) CircularImageView profilePic;
    @InjectView(R.id.profile_view_progress_wheel) ProgressWheel loading;
    @InjectView(R.id.profile_view_status_list_view) ListView statusListView;

    @Icicle String userEmail = null;
    @Icicle String userName = null;
    @Icicle String teamName = null;
    @Icicle ArrayList<PersonStatus> statusArrayList;

    private FadingActionBarHelper fadingActionBarHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.inject(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        Timber.plant(new Timber.DebugTree());

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString(Constants.USER_EMAIL_ID);
            userName = extras.getString(Constants.USERNAME_COLUMN);
            teamName = extras.getString(Constants.TEAM_NAME);
        }

        new FadingActionBarHelper(getActionBar(),
                getResources().getDrawable(R.drawable.white_grain));

        statusArrayList=new ArrayList<PersonStatus>();

        profileName.setTypeface(Utils.getLightTypeface(getApplicationContext()));
//        profileName.setText(userName);

        statusListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return statusArrayList.size();
            }

            @Override
            public Object getItem(int position) {
                return statusArrayList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;

                if(convertView == null){
                    viewHolder = new ViewHolder();
                    convertView = getLayoutInflater().inflate(R.layout.profile_status_tile,parent,false);
                    viewHolder.date = (TextView)convertView.findViewById(R.id.profile_status_tile_date);
                    viewHolder.statusUpdate = (TextView)convertView.findViewById(R.id.profile_status_tile_statusupdate);

                    viewHolder.date.setTypeface(Utils.getRegularTypeface(getApplicationContext()));
                    viewHolder.statusUpdate.setTypeface(Utils.getRegularTypeface(getApplicationContext()));
                    convertView.setTag(viewHolder);
                }else{
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.date.setText(statusArrayList.get(position).getDateOfUpdate());
                viewHolder.statusUpdate.setText(Utils.parsedStatusUpdate(statusArrayList.get(position).getPersonStatusUpdate()));
                return convertView;
            }
        });

        if (userEmail!=null){
            fetchAllStatusForUser(userEmail);
        }
    }

    public FadingActionBarHelper getFadingActionBarHelper() {
        return fadingActionBarHelper;
    }

    @DebugLog
    private void fetchAllStatusForUser(String email){
        statusArrayList=new ArrayList<PersonStatus>();
        statusArrayList.clear();
        Timber.d("TeamName : "+teamName);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(teamName);
//        query.whereEqualTo(Constants.EMAIL_COLUMN,userEmail);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e!=null){//some error
                    if(e.getCode()==100){//no network


                    }
                    Timber.d(e.getMessage()+e.getCode());
                }else{
                    Timber.d("Parse query count : "+parseObjects.size());
                    for(ParseObject parseObject : parseObjects){
                        PersonStatus personStatus = new PersonStatus();
                        personStatus.setPersonStatusUpdate(
                                Utils.parsedStatusUpdate(parseObject.getString(Constants.STATUS_COLUMN)));
                        personStatus.setDateOfUpdate(parseObject.getString(Constants.DATE_COLUMN));
                        statusArrayList.add(personStatus);
                        personStatus.prettyPrint();
                        ((BaseAdapter)statusListView.getAdapter()).notifyDataSetChanged();
                    }
                }
            }

        });
    }


    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    class ViewHolder {
        TextView date;
        TextView statusUpdate;
    }
}

