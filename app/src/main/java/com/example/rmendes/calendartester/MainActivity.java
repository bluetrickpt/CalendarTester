package com.example.rmendes.calendartester;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    private static final String CALENDAR_PERMISSION = Manifest.permission.READ_CALENDAR;
    final private int REQUEST_PERMISSION_CODE = 123;

    public static final String[] FIELDS = { //This list is not a complete set of event information
            CalendarContract.Events._ID,
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.EVENT_TIMEZONE,
            CalendarContract.Events.EVENT_END_TIMEZONE,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.RRULE,
            CalendarContract.Events.RDATE,
            CalendarContract.Events.AVAILABILITY,
            CalendarContract.Events.GUESTS_CAN_MODIFY,
            CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS,
            CalendarContract.Events.GUESTS_CAN_SEE_GUESTS
    };

    private TextView[] lastEventTextViews = new TextView[FIELDS.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeLastEventTextViews();

        Button getLogBtn = (Button) findViewById(R.id.get_log_btn);
        getLogBtn.setOnClickListener(new View.OnClickListener() { //On button click, runs our permission logger (runPermissionLogger())
            @Override
            public void onClick(View view) {
                runCalendarLogger();
            }
        });
    }

    private void runCalendarLogger() {
        Context context = getApplicationContext();
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        if(hasCalendarPermission(context)) {
            Cursor eventCursor = contentResolver.query(uri, FIELDS, null, null, null);
            if(eventCursor!=null) {
                int eventCount = eventCursor.getCount();
                if(eventCount > 0) { //there's at least one event
                    while(eventCursor.moveToNext()) {
                        Log.d(TAG, "Event " + (eventCursor.getPosition()+1) + " of " + eventCount);
                        //for (String field : FIELDS) {
                        for(int fIndex=0; fIndex<FIELDS.length; ++fIndex) {
                            String field = FIELDS[fIndex];
                            String value = eventCursor.getString(eventCursor.getColumnIndex(field));
                            Log.d(TAG, field + ": " + value);
                            updateLastEventTextView(fIndex, value);
                        }
                    }
                } else {
                    Log.d(TAG, "No calendar events found.");
                }
            } else {
                Log.w(TAG, "Failed to retrieve calendars");
            }
            eventCursor.close();
        } else {
            Log.w(TAG, "No calendar access permission");
        }
    }

    private boolean hasCalendarPermission(Context context) {

        if(!hasRuntimePermissions())//If it's before Marshmallow, it is granted at install time
            return true;

        //Android M and over have runtime permissions
        int result = context.checkSelfPermission(CALENDAR_PERMISSION);
        if (result == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[] {CALENDAR_PERMISSION}, REQUEST_PERMISSION_CODE);
        }
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    this.runCalendarLogger();
                } else {
                    // Permission Denied
                    if(hasRuntimePermissions() && shouldShowRequestPermissionRationale(CALENDAR_PERMISSION)) {
                        presentPermissionRequestDialog();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean hasRuntimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private void presentPermissionRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_dialog_title)
                .setMessage(R.string.dialog_message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // yes
                        if(hasRuntimePermissions())
                            requestPermissions(new String[] {CALENDAR_PERMISSION}, REQUEST_PERMISSION_CODE);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, R.string.no_permission_alert, Toast.LENGTH_SHORT).show();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void initializeLastEventTextViews() {

        TableLayout tableLayout = (TableLayout) findViewById(R.id.last_event_table);

        for (int i = 0; i < tableLayout.getChildCount(); i++) {
            View child = tableLayout.getChildAt(i);

            if (child instanceof TableRow) {
                TableRow row = (TableRow) child;

                lastEventTextViews[i] = (TextView) row.getChildAt(1);
            } else {
                Log.e(TAG, "Error: table child is not a table row");
            }
        }
    }

    private void updateLastEventTextView(int textViewIndex, String text) {
        if(lastEventTextViews[textViewIndex] != null)
            lastEventTextViews[textViewIndex].setText(text);
        else
            Log.e(TAG, "Undefined error. Check number of fields and number of table rows.");
    }
}
