package suarez.sergio.shareapplibraryapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import suarez.sergio.shareappslibrary.ShareSelectorFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

	private Button button;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		button = (Button) findViewById(R.id.button);

		button.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button:
				instantiateShareView();
				break;
		}
	}

	public void instantiateShareView() {
		//Calculate items
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
		intent.setType("text/plain");
		//Instantiate fragment
		ShareSelectorFragment.newInstance(this, null, "Title", intent);
	}


}
