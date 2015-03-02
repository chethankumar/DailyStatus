package status.chethan.com.dailystatus;

import android.app.Application;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import me.alexrs.prefs.lib.Prefs;
import status.chethan.objects.Constants;

/**
 * Created by chethan on 16/01/15.
 */
public class DailyStatusApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(this, "KbUVqSXsbldMcEkCjmCndQFJBG3sNKmKZEiomwHI", "Z3KIrJVVvjAUXrUeoUOmhwNo2OduWUmyF2eNR93Y");

        cacheNamesOfTeamMembers();

    }

    //should call this once in a while, possibly once a day
    private void cacheNamesOfTeamMembers(){
        String teamName = Prefs.with(getApplicationContext()).getString(Constants.TEAM_NAME,null);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.USER_TABLE);
        query.whereEqualTo(Constants.TEAM_NAME,teamName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e!=null){
                 //some error
                    e.printStackTrace();
                }else {
                    for (ParseObject po : parseObjects) {
                        Prefs.with(getApplicationContext()).save(
                                po.getString(Constants.EMAIL_COLUMN), po.getString(Constants.NAME_COLUMN));
                    }
                }
            }
        });
    }
}
