package com.advc.pendroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class PencilPropertiesDialog extends AppCompatDialogFragment {

    private TextView txtThicknessValue;
    private SeekBar seekBarThickness;
    private RadioGroup radioGroupPencilModes;
    private CheckBox checkBoxFilled;
    private int pencilMode;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_pencil, null);

        builder.setView(view)
                .setTitle("Pencil")
                .setCancelable(true)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int radioButtonId = radioGroupPencilModes.getCheckedRadioButtonId();
                        View radioButton = radioGroupPencilModes.findViewById(radioButtonId);
                        int modeSelected = radioGroupPencilModes.indexOfChild(radioButton);

                        pencilMode = modeSelected;

                        ((MainActivity) getActivity()).getCanvasView().setThickness(seekBarThickness.getProgress());
                        ((MainActivity) getActivity()).getCanvasView().setPencilMode(pencilMode);
                        ((MainActivity) getActivity()).getCanvasView().setFilledMode(checkBoxFilled.isChecked());

                        Log.i("STATUS", "Pencil Mode: " + pencilMode);

                    }

                });

        txtThicknessValue = view.findViewById(R.id.txtThicknessValue);
        int thickness = ((MainActivity) getActivity()).getCanvasView().getThickness();
        txtThicknessValue.setText( String.valueOf(thickness) );

        seekBarThickness = view.findViewById(R.id.seekBarThickness);
        seekBarThickness.setProgress(thickness);
        seekBarThickness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtThicknessValue.setText( String.valueOf(progress) );
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        radioGroupPencilModes = view.findViewById(R.id.radioGroupPencilModes);
        int modeSelected = ((MainActivity) getActivity()).getCanvasView().getPencilMode();
        ((RadioButton) radioGroupPencilModes.getChildAt( modeSelected )).setChecked(true);
        radioGroupPencilModes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                View radioButton = radioGroupPencilModes.findViewById(checkedId);
                int index = radioGroupPencilModes.indexOfChild(radioButton);
                pencilMode = index;
                Log.i("PENCIL MODE", String.valueOf(pencilMode));

            }

        });

        checkBoxFilled = view.findViewById(R.id.checkBoxFilled);
        checkBoxFilled.setChecked( ((MainActivity) getActivity()).getCanvasView().getFilledMode() );

        return builder.create();

    }

    @Override
    public void onStart() {
        super.onStart();

        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00BCD4"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00BCD4"));
    }
}
