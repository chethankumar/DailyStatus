package status.chethan.com.dailystatus;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import hirondelle.date4j.DateTime;
import icepick.Icepick;
import me.alexrs.prefs.lib.Prefs;
import status.chethan.Utils;
import status.chethan.objects.Constants;
import status.chethan.objects.PersonStatus;
import timber.log.Timber;


public class TeamActivity extends Activity {

    @InjectView(R.id.team_view_listview) ListView teamViewListView;
    @InjectView(R.id.team_view_header_text)TextView teamViewHeader;
    @InjectView(R.id.team_view_progress_wheel)ProgressWheel loading;

    ArrayList<String> personEmailList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        ButterKnife.inject(this);
        Icepick.restoreInstanceState(this, savedInstanceState);
        Timber.plant(new Timber.DebugTree());

        teamViewHeader.setTypeface(Utils.getLightTypeface(getApplicationContext()));
        teamViewHeader.setText(Prefs.with(getApplicationContext()).getString(Constants.TEAM_NAME,null));

        teamViewListView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return personEmailList.size();
            }

            @Override
            public Object getItem(int position) {
                return personEmailList.get(position);
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
                    convertView = getLayoutInflater().inflate(R.layout.team_person,parent,false);
                    viewHolder.personName = (TextView)convertView.findViewById(R.id.team_person_item);

                    viewHolder.personName.setTypeface(Utils.getLightTypeface(getApplicationContext()));
                    convertView.setTag(viewHolder);
                }else {
                    viewHolder = (ViewHolder) convertView.getTag();
                }

                viewHolder.personName.setText(personEmailList.get(position));
                return convertView;
            }
        });

        fetchTeamMembers();
    }

    private void fetchTeamMembers(){
        personEmailList.clear();
        ParseQuery<ParseObject> getStatus = ParseQuery.getQuery(Constants.USER_TABLE);
        getStatus.whereEqualTo(Constants.TEAM_NAME,Prefs.with(getApplicationContext()).getString(Constants.TEAM_NAME,null));
        getStatus.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e!=null){//some exception
                    if(e.getCode() == 100){ // no network connection
                         personEmailList.add("No network connection! Please try again when connected to internet");
                        ((BaseAdapter)teamViewListView.getAdapter()).notifyDataSetChanged();
                    }
                    Timber.d(e.getMessage()+e.getCode());
                }else{
                    if(parseObjects.size()==0){
                        personEmailList.add("No Team members yet!!");
                        ((BaseAdapter)teamViewListView.getAdapter()).notifyDataSetChanged();
                    }else{
                        for(ParseObject parseObject:parseObjects){
                            personEmailList.add(parseObject.getString(Constants.NAME_COLUMN));
                            ((BaseAdapter)teamViewListView.getAdapter()).notifyDataSetChanged();
                        }
                    }

                }
                loading.stopSpinning();
                loading.setVisibility(View.GONE);
            }
        });

    }

    static class ViewHolder{
        TextView personName;
    }
}
