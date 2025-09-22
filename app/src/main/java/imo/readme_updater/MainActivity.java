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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import android.widget.EditText;

public class MainActivity extends Activity {
    final static String REPO_URL_KEY = "REPO_URL_KEY";
    final static String WIDGET_STRING_KEY = "WIDGET_STRING_KEY";
    Button updateButton;
    Button updateAndCloseButton;
    TextView outputTextview;
    EditText repoLinkEdittext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        repoLinkEdittext = findViewById(R.id.repo_link_edittext);
        outputTextview = findViewById(R.id.command_output_textview);
        updateButton = findViewById(R.id.update_button);
        updateAndCloseButton = findViewById(R.id.update_and_close_button);
        
        SharedPreferences sp = getSharedPreferences("hehe", Context.MODE_PRIVATE);
        repoLinkEdittext.setText(sp.getString(REPO_URL_KEY, ""));
        
        updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData(false);
                }
            });
            
        updateAndCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData(true);
                }
            });
    }
    
    void saveData(final boolean closeAfter){
        final String repositoryURL = repoLinkEdittext.getText().toString().trim();
        String output = /* TODO: get README.md contents using jgit */;
		
		final SharedPreferences.Editor spEditor = getSharedPreferences("hehe", Context.MODE_PRIVATE).edit();
		spEditor.putString(WIDGET_STRING_KEY, output);
		spEditor.putString(REPO_URL_KEY, repositoryURL);
		spEditor.apply();

		updateWidget();

		if(closeAfter) finish();
    }
    
    void updateWidget(){
        Intent intent = new Intent(MainActivity.this, Widget.class);
        intent.setAction(Widget.ACTION_WIDGET_UPDATE);
        sendBroadcast(intent);
        Toast.makeText(MainActivity.this, "widget updated:D", Toast.LENGTH_SHORT).show();
    }
}
