package com.example.homework331;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BackgroundSelectionActivity extends AppCompatActivity {
	private static final String LOG_TAG = BackgroundSelectionActivity.class.getSimpleName();
	private static Drawable background;

	private EditText picturePathInput;
	private Button applyButton;
	private Button browseFileButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background_selection);
		initViews();

		browseFileButton.setOnClickListener(v -> {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");

			startActivityForResult(intent, 1);
		});
		applyButton.setOnClickListener(v -> loadFromPath(picturePathInput.getText().toString()));

		setBackground(this);
	}

	private void initViews() {
		picturePathInput = findViewById(R.id.picturePathInput);
		applyButton = findViewById(R.id.applyButton);
		browseFileButton = findViewById(R.id.browseFileButton);
	}

	private void loadFromPath(String pathString) {
		if (pathString.isEmpty()) {
			setDefaultBackground(this);
			setBackground(this);
			Toast.makeText(this, "Set default background", Toast.LENGTH_SHORT).show();
			return;
		}

		boolean access = ContextCompat.checkSelfPermission(this,
				Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
		if (!access) {
			ActivityCompat.requestPermissions(this, new String[]{
					Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
			Toast.makeText(this, "Please first allow the" +
					" app to read files", Toast.LENGTH_SHORT).show();
		} else {
			File f = new File(Environment.getExternalStoragePublicDirectory(
					Environment.DIRECTORY_DOWNLOADS), pathString);
			try (FileInputStream in = new FileInputStream(f)) {
				background = new BitmapDrawable(getResources(), in);
				setBackground(this);
			} catch (FileNotFoundException e) {
				Toast.makeText(this, "The file can't be accessed or doesn't exist",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(this, "Failed to read the file",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (data != null && data.getData() != null) {
			try (InputStream in = getContentResolver().openInputStream(data.getData())) {
				background = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(in));
				setBackground(this);
			} catch (IOException e) {
				Toast.makeText(this, "Error while loading", Toast.LENGTH_SHORT).show();
				Log.v(LOG_TAG, "Failed to load: " + e.getMessage() +
						", path: " + data.getDataString());
			}
		}
	}

	static void setBackground(Activity c) {
		ImageView v = c.findViewById(R.id.backgroundImage);
		if (background == null)
			setDefaultBackground(c);
		v.setImageDrawable(background);
	}

	private static void setDefaultBackground(Activity c) {
		background = c.getDrawable(R.drawable.effective_top_manager);
	}
}
