package victorhsiao.view.captcha;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import victorhsiao.view.captchalibrary.Captcha;


public class MainActivity extends Activity {

    private Captcha captcha = null;
    private EditText editText = null;
    private Button button = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        captcha = (Captcha) findViewById(R.id.captcha_imageView);
        captcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captcha.refreshCaptcha();
                Toast.makeText(MainActivity.this, "invalidTextWidth: " + captcha.invalidTextWidth(), Toast.LENGTH_SHORT).show();
            }
        });
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (!captcha.verify(text)) {
                    Toast.makeText(MainActivity.this, "Invalid Captcha", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Valid Captcha", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (captcha != null) captcha.release();
        super.onDestroy();
    }
}
