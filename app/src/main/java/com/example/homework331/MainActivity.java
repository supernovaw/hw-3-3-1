package com.example.homework331;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
	private static final char POINT = '.';

	private TextView textView;
	private Button[] buttons, scientificButtons;

	private static final String POINT_STRING = Character.toString(POINT);
	private String inputDigits = "0";
	private boolean sign = true; // true-positive; false-negative
	private boolean inverseTrig;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initViews();

		textView.setText(inputDigits);
		View.OnClickListener padListener = this::handlePress;
		for (Button b : buttons)
			b.setOnClickListener(padListener);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			for (Button b : scientificButtons)
				b.setOnClickListener(padListener);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		BackgroundSelectionActivity.setBackground(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		startActivity(new Intent(this, BackgroundSelectionActivity.class));
		return super.onOptionsItemSelected(item);
	}

	private void initViews() {
		textView = findViewById(R.id.textView);

		buttons = new Button[13];
		scientificButtons = new Button[4];

		buttons[0] = findViewById(R.id.button0);
		buttons[1] = findViewById(R.id.button1);
		buttons[2] = findViewById(R.id.button2);
		buttons[3] = findViewById(R.id.button3);
		buttons[4] = findViewById(R.id.button4);
		buttons[5] = findViewById(R.id.button5);
		buttons[6] = findViewById(R.id.button6);
		buttons[7] = findViewById(R.id.button7);
		buttons[8] = findViewById(R.id.button8);
		buttons[9] = findViewById(R.id.button9);

		buttons[10] = findViewById(R.id.buttonDot);
		buttons[11] = findViewById(R.id.buttonClear);
		buttons[12] = findViewById(R.id.buttonSwitchSign);

		scientificButtons[0] = findViewById(R.id.buttonSin);
		scientificButtons[1] = findViewById(R.id.buttonCos);
		scientificButtons[2] = findViewById(R.id.buttonTan);
		scientificButtons[3] = findViewById(R.id.buttonTrigInv);
	}

	private void handlePress(View v) {
		switch (v.getId()) {
			case R.id.buttonDot:
				dotPress();
				break;
			case R.id.buttonClear:
				clear();
				break;
			case R.id.buttonSwitchSign:
				switchSign();
				break;
			case R.id.buttonTrigInv:
				invertTrigButtons();
				break;
			default:
				numPress(v.getId());
				break;
		}

		textView.setText(getInputString());
	}

	private void numPress(int id) { // proceeds number press
		for (int i = 0; i < 10; i++)
			if (id == buttons[i].getId()) {
				inputDigits += i;

				if (inputDigits.charAt(0) == '0') { // cut leading zero
					boolean isPointAfterZero = false;
					if (inputDigits.length() >= 2) // to prevent IndexOutOfBoundsException
						isPointAfterZero = inputDigits.charAt(1) == POINT;

					if (!isPointAfterZero) // in case it's i.e. "0.1", don't cut
						inputDigits = inputDigits.substring(1);
				}

				return;
			}
	}

	private void dotPress() { // proceeds dot press
		if (inputDigits.contains(POINT_STRING))
			return;

		inputDigits += POINT;
	}

	private void clear() {
		inputDigits = "0";
		sign = true;
	}

	private void switchSign() {
		sign = !sign;
	}

	private String getInputString() {
		String sign = this.sign ? "" : "-";
		return sign + inputDigits;
	}

	private void invertTrigButtons() {
		inverseTrig = !inverseTrig;
		String tmp = inverseTrig ? getString(R.string.inv_trig) : "%s";
		scientificButtons[0].setText(String.format(tmp, getString(R.string.sin_func)));
		scientificButtons[1].setText(String.format(tmp, getString(R.string.cos_func)));
		scientificButtons[2].setText(String.format(tmp, getString(R.string.tan_func)));
	}
}
