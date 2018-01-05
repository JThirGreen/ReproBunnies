package jthirgreen.reprobunnies.Utilities;

import android.content.Context;
import android.widget.Toast;

import jthirgreen.reprobunnies.MainActivity;

/**
 * Created by JThirGreen on 9/8/2017.
 */

public class jToast {
    static Toast toast;

    public static void showToast(String message, Context context) {
        if (toast != null)  // cancel previous toast if it exists
            toast.cancel();

        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.show();
    };

}
