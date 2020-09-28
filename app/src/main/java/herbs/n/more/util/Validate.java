package herbs.n.more.util;

import android.text.TextUtils;

public class Validate {

    public final static boolean isValidEmail(String str) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(str).matches();
    }

    public final static boolean isNull(String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        return false;
    }

    public final static boolean isShorterThan(String str) {
        if (str.length() < 6) {
            return true;
        }
        return false;
    }
}
