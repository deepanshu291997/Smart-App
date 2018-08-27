package mudit.com.smartapp;

import android.content.Intent;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.Locale;

public class ObjectDetection extends AppCompatActivity {
    TextView tvView;
    Button btStart1;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);
        tvView=(TextView) findViewById(R.id.tvView);
        Button btStart1=(Button)findViewById(R.id.btStart1);
        t1=new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        btStart1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ObjectDetection.this,DetectorActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0)
        {   String result;
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null)
                {
                    result=data.getStringExtra("RESULT");
                    tvView.setText(result);
                    t1.speak(result, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)){
            //Do something
            t1.speak(tvView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);

        }
        if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)){
            //Do something
            Intent intent=new Intent(ObjectDetection.this,DetectorActivity.class);
            startActivityForResult(intent,0);
        }
        if((keyCode == KeyEvent.KEYCODE_BACK))
        {
            onBackPressed();
        }
        return true;
    }

}
