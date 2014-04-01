package surfaceview_test;

import jayvee.visualization_test.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_main);
		final TextView tv = (TextView) findViewById(R.id.edit_content);
		Button btn = (Button) findViewById(R.id.btn_draw);
		
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String filename = tv.getText().toString();
				Intent intent = new Intent(MainActivity.this, SurfaceViewMain.class);
				Bundle bundle = new Bundle();
				bundle.putString("filename", filename);
				intent.putExtras(bundle);
				startActivity(intent);
			}
			
		});
	}
}
