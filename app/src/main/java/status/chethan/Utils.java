package status.chethan;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.view.Display;


/**
 * Created by chethan on 13/01/15.
 */
public class Utils {

    private static Point size = null;

    public static int getPx(Activity activity, int dps) {
        final float scale = activity.getBaseContext().getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static int getPx(Context context, int dps) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dps * scale + 0.5f);
    }

    public static int getSizeX(Activity activity) {
        if (size == null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Utils.size = size;
        }
        return size.x;
    }

    public static int getSizeY(Activity activity) {
        if (size == null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Utils.size = size;
        }
        return size.y;
    }

    public static Typeface getRegularTypeface(Context context) {
        return Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/segoeui.ttf");
    }

    public static Typeface getLightTypeface(Context context) {
        return Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/segoeuil.ttf");
    }

    public static String parsedStatusUpdate(String update){
        String modifiedUpdate ="\u2022 "+update;
        return modifiedUpdate.replace("|","\n\u2022");
    }
}