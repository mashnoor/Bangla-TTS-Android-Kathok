/*
 * 
 * This application is written by Mashnoor Lab, Bangladesh
 * Phone - +8801826636115
 * Facebook - facebook.com/Mashnoor
 * Email - nmmashnoor@gmail.com
 * 
 */
package com.mashnoorlab.banglareader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.mashnoorlab.kathok.AboutActivity;
import com.mashnoorlab.kathok.R;

public class MainActivity extends Activity {

	EditText eText;
	RadioGroup radioGroup;
	String sex = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		eText = (EditText) findViewById(R.id.etField);

		eText.setOnKeyListener(new View.OnKeyListener() {

			// Preventing User to press the enter key. Other keys are ok
			@Override
			public boolean onKey(View arg0, int keycode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_DOWN
						&& event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					return true;
				}

				return false;
			}
		});

		radioGroup = (RadioGroup) findViewById(R.id.rg);

	}

	public void read(View v) {

		String text = eText.getText().toString();
		if (text.trim().equals("")) {
			show_alert("Don't leave the field blank!");
			return;
		}
		int clickedID = radioGroup.getCheckedRadioButtonId();
		RadioButton radioButton = (RadioButton) findViewById(clickedID);

		if (radioButton.getText().toString().equals("পুরুষ")) {
			sex = "male";

		} else {
			sex = "female";
		}
		new playFile().execute(text);

	}

	public void about(View v) {
		Intent intent = new Intent(MainActivity.this, AboutActivity.class);
		startActivity(intent);

	}

	// Defining Function to show alert message
	private void show_alert(String message) {
		AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
				.setTitle("Error!").setMessage(message)
				.setPositiveButton("OK", null).show();
	}

	// Async Task to send data to server for processing
	class playFile extends AsyncTask<String, String, String> {

		ProgressDialog progressDialog = ProgressDialog.show(MainActivity.this,
				"", "Working...", true);

		protected void onPostExecute(String result) {
			try {

				if (progressDialog.isShowing() && progressDialog != null) {
					progressDialog.dismiss();
				}
			} catch (Exception e) {
				return;
			}

			if (result.equals("failed")) {
				show_alert("Please check your internet connection or input stuffs");
			}

			try {

				// Media Player for playing the Wav file from server
				MediaPlayer player = new MediaPlayer();
				player.setAudioStreamType(AudioManager.STREAM_MUSIC);
								
				player.setDataSource("yoursever/espeak/" + result 
						+ ".wav"); //Replace yourserver with your IP or domain
				player.prepare();

				progressDialog.setProgress(player.getCurrentPosition());
				player.start();
				player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						mp.stop();
						mp.release();
					}
				});

			} catch (IllegalArgumentException | SecurityException
					| IllegalStateException | IOException e) {
				show_alert("Failed to process your request!\nMake Sure your internet and input are ok!");
				return;
			}

		};

		@Override
		protected String doInBackground(String... pasStrings) {

			try {

				String url = "yourserver/espeak/process.py?mt="
						+ pasStrings[0].trim().replace(" ", "+") + "&sex="
						+ sex; //Replace 'yourserver' with your IP or domain
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(url);
				HttpResponse response = client.execute(request);

				String html = "";
				InputStream in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder str = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					str.append(line);
				}
				in.close();
				html = str.toString();

				return html;

			} catch (Exception e) {

				return "failed";
			}

		}
	}
}
