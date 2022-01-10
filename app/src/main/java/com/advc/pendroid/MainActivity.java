package com.advc.pendroid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    private CanvasView canvasView;
    private ImageButton btnColorPicker;
    private ImageButton btnPencil;
    private ImageButton btnEraser;
    private ImageButton btnMove;
    private ImageButton btnSave;
    private ImageButton btnClear;
    private ImageButton btnUndo;
    private ImageButton btnRedo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasView = new CanvasView(this);
        canvasView.requestFocus();
        LinearLayout background = findViewById(R.id.Background);
        background.addView(canvasView);

        btnColorPicker = findViewById(R.id.btnColorPicker);
        btnColorPicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showColorPickerDialog(canvasView.getColor());
            }

        });

        btnPencil = findViewById(R.id.btnPencil);
        btnPencil.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showPencilDialog();
            }

        });

        btnEraser = findViewById(R.id.btnEraser);
        btnEraser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean newEraserMode = !canvasView.getEraserMode();
                canvasView.setEraserMode( newEraserMode );

                int colorBackground = 0;

                if(newEraserMode) {
                    colorBackground = Color.rgb(1, 135, 134);
                }
                else {
                    colorBackground = Color.rgb(0, 188, 212);
                }
                btnEraser.getBackground().setColorFilter(colorBackground, PorterDuff.Mode.SRC_ATOP);

            }

        });

        btnMove = findViewById(R.id.btnMove);
        btnMove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean newMoveMode = !canvasView.getMoveMode();
                canvasView.setMoveMode( newMoveMode );

                int colorBackground = 0;

                if(newMoveMode) {
                    colorBackground = Color.rgb(1, 135, 134);
                    canvasView.setMode(CanvasView.DRAG);
                } else {
                    colorBackground = Color.rgb(0, 188, 212);
                    canvasView.setMode(CanvasView.DRAW);
                }
                btnMove.getBackground().setColorFilter(colorBackground, PorterDuff.Mode.SRC_ATOP);

            }

        });

        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                canvasView.setSaveImage(true);
                canvasView.invalidate();

                Bitmap image = viewToBitmap(canvasView);
                saveImage(image);

                canvasView.invalidate();

                Toast.makeText(MainActivity.this, "Saved image.", Toast.LENGTH_SHORT).show();

            }

        });

        btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                confirmClear();

            }

        });

        btnUndo = findViewById(R.id.btnUndo);
        btnUndo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                canvasView.undo();
            }

        });

        btnRedo = findViewById(R.id.btnRedo);
        btnRedo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                canvasView.redo();
            }

        });

    }

    private void confirmClear() {

        new AlertDialog.Builder(this)
                .setTitle("Clear Canvas")
                .setMessage("Clear the Canvas and start a new drawing?")
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        canvasView.clearCanvas();
                    }

                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    private void saveImage(Bitmap bitmap) {

        OutputStream imageOutputStream = null;

        ContentValues cv = new ContentValues();

        File imageDir = new File(Environment.DIRECTORY_PICTURES + "/pendroid");
        imageDir.mkdirs();

        cv.put(MediaStore.Images.Media.DISPLAY_NAME, "image.jpg");
        cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/pendroid");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        try {

            imageOutputStream = getContentResolver().openOutputStream(uri);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream);

            imageOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Bitmap viewToBitmap(View view) {

        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;

    }

    private void showColorPickerDialog(int defaultColor) {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                canvasView.setColor(color);
                DrawableCompat.setTint(btnColorPicker.getDrawable(), color);

            }

        });

        ambilWarnaDialog.show();

        ambilWarnaDialog.getDialog().getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#00BCD4"));
        ambilWarnaDialog.getDialog().getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#00BCD4"));

    }

    private void showPencilDialog() {

        PencilPropertiesDialog pencilDialog = new PencilPropertiesDialog();

        pencilDialog.show(getSupportFragmentManager(), "Pencil TAG");

    }

    public CanvasView getCanvasView() {
        return canvasView;
    }

}