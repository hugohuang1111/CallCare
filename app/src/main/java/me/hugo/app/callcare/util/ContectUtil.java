package me.hugo.app.callcare.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.logging.Logger;

/**
 * Created by htl on 2017/9/11.
 */

public class ContectUtil {

    static public boolean inContectList(Context ctx, String number) {
        boolean exist = false;
        Uri uri = Uri.parse("content://com.android.contacts/data/phones/filter/" + number);
        ContentResolver resolver = ctx.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"display_name"}, null, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                exist = true;
            }
            cursor.close();
        }

        return exist;
    }

}
