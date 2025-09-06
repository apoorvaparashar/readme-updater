package imo.readme_updater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.PrintWriter;
import java.io.StringWriter;
import android.widget.TextView;

public class MainActivity extends Activity {
    int i = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button clickerButton = findViewById(R.id.clicker_button);
        final Button updateButton = findViewById(R.id.update_button);
        
        SharedPreferences sp = getSharedPreferences("hehe", Context.MODE_PRIVATE);
        
        i = sp.getInt("clicker", -1);
        clickerButton.setText(i+"");
        clickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    i++;
                    clickerButton.setText(i+"");
                }
            });

        updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor spEditor = getSharedPreferences("hehe", Context.MODE_PRIVATE).edit();
                    spEditor.putInt("clicker", i);
                    spEditor.apply();

                    Intent intent = new Intent(MainActivity.this, Widget.class);
                    intent.setAction(Widget.ACTION_WIDGET_UPDATE);
                    sendBroadcast(intent);
                    Toast.makeText(MainActivity.this, "widget updated:D", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
    }
}
