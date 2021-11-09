package us.frogracing.rallytimecardcheckin;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void showStartTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(this, v, findViewById(R.id.actualStartH), findViewById(R.id.actualStartM));
        newFragment.show(this.getSupportFragmentManager(), "timePicker");
        calculateTimes(v);
    }

    public void showFinishTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment(this, v, findViewById(R.id.finishH), findViewById(R.id.finishM));
        newFragment.show(this.getSupportFragmentManager(), "timePicker");
    }

    public void pickBogey(View v) {
        final Dialog d = new Dialog(MainActivity.this);
        d.setContentView(R.layout.picker_dialog);
        TextView title = d.findViewById(R.id.dialogTitle);
        title.setText(" Bogey time in minutes (aka lateness/slow time) ");
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
        np.setMaxValue(120);
        np.setMinValue(0);
        TextView bogeyM = findViewById(R.id.bogeyM);
        if (bogeyM == null || bogeyM.getText() == null || bogeyM.getText().toString().isEmpty())
            np.setValue(5);
        else
            try {
                np.setValue(Integer.parseInt(bogeyM.getText().toString()));
            } catch (NumberFormatException e) {
                np.setValue(5);
            }

        Button set = (Button) d.findViewById(R.id.set);
        Button cancel = (Button) d.findViewById(R.id.cancel);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bogeyM.setText("" + np.getValue());
                calculateTimes(v);
                d.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    public void pickTransit(View v) {
        final Dialog d = new Dialog(MainActivity.this);
        d.setContentView(R.layout.picker_dialog);
        TextView title = d.findViewById(R.id.dialogTitle);
        title.setText(" Transit time in minutes ");
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
        np.setMaxValue(120);
        np.setMinValue(0);
        TextView transitM = findViewById(R.id.transitM);
        if (transitM == null || transitM.getText() == null)
            np.setValue(5);
        else
            try {
                np.setValue(Integer.parseInt(transitM.getText().toString()));
            } catch (NumberFormatException e) {
                np.setValue(5);
            }

        Button set = (Button) d.findViewById(R.id.set);
        Button cancel = (Button) d.findViewById(R.id.cancel);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitM.setText("" + np.getValue());
                calculateTimes(v);
                d.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
            }
        });

        d.show();
    }

    public void calculateTimes(View v) {
        TextView result = findViewById(R.id.resultDisplay);
        TextView vStartH = findViewById(R.id.actualStartH);
        TextView vFinishH = findViewById(R.id.finishH);
        TextView vStartM = findViewById(R.id.actualStartM);
        TextView vFinishM = findViewById(R.id.finishM);
        TextView vFinishS = findViewById(R.id.finishS);
        TextView bogeyM = findViewById(R.id.bogeyM);
        TextView transitM = findViewById(R.id.transitM);
        TextView stageM = findViewById(R.id.stageTimeM);
        TextView stageS = findViewById(R.id.stageTimeS);
        TextView startTransitH = findViewById(R.id.startTransitH);
        TextView startTransitM = findViewById(R.id.startTransitM);
        TextView atcInH = findViewById(R.id.atcInH);
        TextView atcInM = findViewById(R.id.atcInM);

        TextClock currentTime = findViewById(R.id.clock);
        TextView atcCheckin = findViewById(R.id.checkinClock);

        SimpleDateFormat df = new SimpleDateFormat("HH'H'mm'M'");
        if (vStartH == null || vStartH.getText() == null || vStartM == null || vStartM.getText() == null) {
            result.setText(Html.fromHtml("No valid start time"));
            return;
        }
        if (vFinishH == null || vFinishH.getText() == null || vFinishM == null || vFinishM.getText() == null) {
            result.setText(Html.fromHtml("No finish start time"));
            return;
        }

        Date start;
        try {
            start = df.parse(vStartH.getText().toString() + "H" + vStartM.getText().toString() + "M");
        } catch (ParseException e) {
            result.setText(Html.fromHtml("No valid start time"));
            return;
        }
        Date finish;
        try {
            finish = df.parse(vFinishH.getText().toString() + "H" + vFinishM.getText().toString() + "M");
        } catch (ParseException e) {
            result.setText(Html.fromHtml("No valid finish time"));
            return;
        }
        long diffInMillies = finish.getTime() - start.getTime();
        long stageMins = diffInMillies / 60 / 1000;
        if (stageM != null)
            stageM.setText("" + stageMins);
        if (stageS != null && vFinishS != null && vFinishS.getText() != null)
            stageS.setText(vFinishS.getText());

        String text = "<b>Stage time: " + stageMins + " minutes</b> ";
        if (stageMins < 0)
            text += "<font color='red'> !!! WARNING!!! Stage time is negative, check your finish time</font><br/>";

        if (bogeyM == null || bogeyM.getText() == null) {
            text += "<br/><font color='red'>No bogey/lateness time defined</font>";
            result.setText(Html.fromHtml(text));
            return;
        }

        int bogey;
        try {
            bogey = Integer.parseInt(bogeyM.getText().toString());
        } catch (NumberFormatException e) {
            text += "<br/><font color='red'>No bogey/lateness time defined</font><br/>";
            result.setText(Html.fromHtml(text));
            return;
        }

        Date startTransit;
        if (stageMins < bogey) {
            text += "(" + (bogey - stageMins) + " minutes faster than bogey)<br/>";
            startTransit = new Date(start.getTime() + (bogey) * 60 * 1000);
        } else {
            text += "(" + (stageMins - bogey) + " minutes slower than bogey)<br/>";
            startTransit = new Date(start.getTime() + (stageMins) * 60 * 1000);
        }
        text += "Start transit: " + df.format(startTransit) + "<br/>";

        if (transitM == null || transitM.getText() == null) {
            text += "<font color='red'>No transit time set</font><br/>";
            result.setText(Html.fromHtml(text));
            return;
        }
        int transit;
        try {
            transit = Integer.parseInt(transitM.getText().toString());
        } catch (NumberFormatException e) {
            text += "<font color='red'>No transit time set</font><br/>";
            result.setText(Html.fromHtml(text));
            return;
        }
        Date atcIn;
        if (stageMins < bogey) {
            text += "ATC Checkin time: " + df.format(start.getTime()) + " + " + bogey + " (bogey) + " + transit + " (transit)<br/>";
            atcIn = new Date(start.getTime() + (bogey + transit) * 60 * 1000);
        } else {
            text += "ATC Checkin time: " + df.format(start.getTime()) + " + " + stageMins + " (stage time) + " + transit + " (transit)<br/>";
            atcIn = new Date(start.getTime() + (stageMins + transit) * 60 * 1000);
        }

        text += "<b>ATC IN: " + df.format(atcIn) + "</b><br/>";
        if (startTransitH != null && startTransitM != null) {
            startTransitH.setText("" + startTransit.getHours());
            startTransitM.setText("" + startTransit.getMinutes());
        }
        if (atcInH != null && atcInM != null) {
            atcInH.setText("" + atcIn.getHours());
            atcInM.setText("" + atcIn.getMinutes());
        }

        SimpleDateFormat ms = new SimpleDateFormat("mm:ss");
        SimpleDateFormat cf = new SimpleDateFormat("hh:mm:ss");
        currentTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    long timeLeft = atcIn.getTime() - cf.parse(s.toString()).getTime();
                    if (timeLeft > 0) {
                        atcCheckin.setText(Html.fromHtml("<font color='blue'>Wait " + ms.format(new Date(timeLeft)) + "</font>"));
                    } else if (timeLeft >= -60000) {
                        atcCheckin.setText(Html.fromHtml("<font color='green'>NOW! " + (60 + timeLeft / 1000) + "s left</font>"));
                    } else {
                        atcCheckin.setText(Html.fromHtml("<font color='red'>Late " + ms.format(new Date(-timeLeft)) + "</font>"));
                    }
                } catch (ParseException e) {
                    atcCheckin.setText("Error " + e);
                }
            }
        });

        result.setText(Html.fromHtml(text));
    }

}

