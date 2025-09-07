package imo.readme_updater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    final static String WIDGET_STRING_KEY = "WIDGET_STRING_KEY";
    Button updateButton;
    TextView outputTextview;
    int i = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        CommandTermux.checkAndRequestPermissions(this);
        
        outputTextview = findViewById(R.id.output_textview);
        updateButton = findViewById(R.id.update_button);
        
        updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData();
                }
            });
    }
    
    void saveData(){
        String command = "pwd";
        final SharedPreferences.Editor spEditor = getSharedPreferences("hehe", Context.MODE_PRIVATE).edit();
        final String previousString = outputTextview.getText().toString();
        updateButton.setEnabled(false);

        new CommandTermux(command, MainActivity.this)
            .quickSetOutput(outputTextview, new Runnable(){
                @Override
                public void run(){
                    String output = outputTextview.getText().toString();
                    spEditor.putString(WIDGET_STRING_KEY, output);
                    spEditor.apply();
                    updateWidget();
                }
            })
            .setOnError(new Runnable(){// this runs if sending command to termux encounter an error
                @Override
                public void run(){
                    CommandTermux.stopDetector(); // still waits for output and should be stopped
                    outputTextview.setText(previousString);
                    updateButton.setEnabled(true);
                }
            })
            .run();
    }
    
    void updateWidget(){
        Intent intent = new Intent(MainActivity.this, Widget.class);
        intent.setAction(Widget.ACTION_WIDGET_UPDATE);
        sendBroadcast(intent);
        Toast.makeText(MainActivity.this, "widget updated:D", Toast.LENGTH_SHORT).show();
        finish();
    }
}
