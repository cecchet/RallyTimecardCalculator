package us.frogracing.rallytimecardcheckin;


import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private final MainActivity mainActivity;
    private final View mainView;
    private TextView h, m;

    public TimePickerFragment(MainActivity mainActivity, View mainView, TextView h, TextView m) {
        this.h = h;
        this.m = m;
        this.mainActivity = mainActivity;
        this.mainView = mainView;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (h != null)
            h.setText("" + hourOfDay);
        if (m != null)
            m.setText("" + minute);
        mainActivity.calculateTimes(mainView);
    }
}