package com.bandaids.meatme;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

public class SelectDatesFragment extends DialogFragment {
    DatePicker datePicker;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_select_dates, null))
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SelectDatesFragment.this.getDialog().cancel();
                    }
                });

        View rootView = inflater.inflate(R.layout.fragment_select_dates, null, false);

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        AlertDialog d = (AlertDialog) getDialog();

        Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);

        datePicker = (DatePicker) d.findViewById(R.id.datePicker);


        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] date = new int[3];
                date[0] = datePicker.getDayOfMonth();
                date[1] = datePicker.getMonth();
                date[2] = datePicker.getYear();

                if (getTag().equals("fromDateTag")) {
                    CreateMeetingActivity.setFromDate(date);
                } else {
                    CreateMeetingActivity.setToDate(date);
                }

                dismiss();
            }
        });

    }
}
