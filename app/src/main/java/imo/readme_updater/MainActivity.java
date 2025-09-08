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
    TextView commandOutputTextview;
    EditText repoLinkEdittext;
    int i = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        CommandTermux.checkAndRequestPermissions(this);
        
        repoLinkEdittext = findViewById(R.id.repo_link_edittext);
        commandOutputTextview = findViewById(R.id.command_output_textview);
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
        final String folderName = "downloaded_repository";
        final String outputSeparator = "OUTPUT_SEPARATOR";
        
        if(repositoryURL.isEmpty() || ! repositoryURL.contains("https://")) return;
        
        String command = readFileFromAssets("command.sh");
        command = command.replace("<repo_url>", repositoryURL);
        command = command.replace("<folder_name>", folderName);
        command = command.replace("<output_separator>", outputSeparator);
        
        final SharedPreferences.Editor spEditor = getSharedPreferences("hehe", Context.MODE_PRIVATE).edit();
        final String previousString = commandOutputTextview.getText().toString();
        updateButton.setEnabled(false);

        new CommandTermux(command, MainActivity.this)
            .quickSetOutputWithLoading(commandOutputTextview, new Runnable(){
                @Override
                public void run(){
                    String output = commandOutputTextview.getText().toString();
                    output = output.split(outputSeparator)[1].trim();
                    
                    spEditor.putString(WIDGET_STRING_KEY, output);
                    spEditor.putString(REPO_URL_KEY, repositoryURL);
                    spEditor.apply();
                    
                    updateWidget();
                    
                    if(closeAfter) finish();
                }
            })
            .setOnError(new Runnable(){// this runs if sending command to termux encounter an error
                @Override
                public void run(){
                    CommandTermux.stopDetector(); // still waits for output and should be stopped
                    commandOutputTextview.setText(previousString);
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
    }
    
    boolean checkIfGitRepository(String s){
        return true;
    }
    
    private String readFileFromAssets(String filePath) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = this.getAssets().open(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            reader.close();

        } catch (IOException e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return sw.toString();
        }
        return stringBuilder.toString();
    }
}
