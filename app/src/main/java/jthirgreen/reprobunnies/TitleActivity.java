package jthirgreen.reprobunnies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jthirgreen.reprobunnies.Utilities.jToast;

import static jthirgreen.reprobunnies.R.styleable.View;

public class TitleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        Button start = (Button) findViewById(R.id.startButton);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v) {
                jToast.showToast("Button has been pressed",getApplicationContext());
                Intent MainIntent = new Intent(TitleActivity.this, MainActivity.class);
                startActivity(MainIntent);
            };
        });

    }


}
