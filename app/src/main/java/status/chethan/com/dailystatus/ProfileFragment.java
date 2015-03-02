package status.chethan.com.dailystatus;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.achep.header2actionbar.HeaderFragment;

/**
 * Created by chethan on 21/01/15.
 */
public class ProfileFragment extends HeaderFragment {

    private ListView listView;
    private FrameLayout mContentOverlay;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        setHeaderBackgroundScrollMode(HEADER_BACKGROUND_SCROLL_PARALLAX);
        setOnHeaderScrollChangedListener(new OnHeaderScrollChangedListener() {
            @Override
            public void onHeaderScrollChanged(float progress, int height, int scroll) {
                height -= getActivity().getActionBar().getHeight();
                progress = (float) scroll / height;
                if (progress > 1f) progress = 1f;
                progress = (1 - (float) Math.cos(progress * Math.PI)) * 0.5f;

                ((ProfileActivity) getActivity())
                        .getFadingActionBarHelper()
                        .setActionBarAlpha((int) (255 * progress));
            }
        });
    }

    @Override
    public View onCreateHeaderView(LayoutInflater layoutInflater, ViewGroup container) {
        return layoutInflater.inflate(R.layout.profile_fragment_header, container, false);
    }

    @Override
    public View onCreateContentView(LayoutInflater layoutInflater, ViewGroup container) {
        listView = (ListView) layoutInflater.inflate(R.layout.profile_fragment_listview, container, false);
        fetchAllStatus();
        return listView;
    }

    @Override
    public View onCreateContentOverlayView(LayoutInflater layoutInflater, ViewGroup container) {
        ProgressBar progressBar = new ProgressBar(getActivity());
        mContentOverlay = new FrameLayout(getActivity());
        mContentOverlay.addView(progressBar, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        mContentOverlay.setVisibility(View.GONE);
        return mContentOverlay;
    }

    private void fetchAllStatus(){

    }
}
