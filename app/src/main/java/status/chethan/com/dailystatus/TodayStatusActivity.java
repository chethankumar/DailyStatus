package status.chethan.com.dailystatus;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Rahul on 3/2/2015.
 */
public class TodayStatusActivity extends Activity {

    @InjectView(R.id.teamName)TextView teamName;
    @InjectView(R.id.membersView)ImageButton teamMembersButton;
    @InjectView(R.id.settingsView)ImageButton settingsButton;
    @InjectView(R.id.calendarView)ImageButton calendarViewButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.today_status);
        ButterKnife.inject(this);
        teamName = (TextView)findViewById(R.id.teamName);
        teamName.setSelected(true);

        settingsButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageButton view = (ImageButton ) v;
                        view.getBackground().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        v.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                        // Your action here on button click

                    case MotionEvent.ACTION_CANCEL: {
                        ImageButton view = (ImageButton) v;
                        view.getBackground().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });
    }

    public void startCalanderActivity(View view){
        Intent calView = new Intent(TodayStatusActivity.this, CalendarActivity.class);
        startActivity(calView);
    }
}
