package imo.readme_updater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.eclipse.jgit.api.Git;

public class MainActivity extends Activity {
    final static String REPO_URL_KEY = "REPO_URL_KEY";
    final static String WIDGET_STRING_KEY = "WIDGET_STRING_KEY";
    Button fetchButton;
    Button fetchAndCloseButton;
    TextView outputTextview;
    EditText repoLinkEdittext;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        repoLinkEdittext = findViewById(R.id.repo_link_edittext);
        outputTextview = findViewById(R.id.command_output_textview);
        fetchButton = findViewById(R.id.update_button);
        fetchAndCloseButton = findViewById(R.id.update_and_close_button);
        
        SharedPreferences sp = getSharedPreferences("hehe", Context.MODE_PRIVATE);
        repoLinkEdittext.setText(sp.getString(REPO_URL_KEY, ""));
        
        fetchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData(false);
                }
            });
            
        fetchAndCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData(true);
                }
            });
    }
    
    void saveData(final boolean closeAfter){
        final String repositoryURL = repoLinkEdittext.getText().toString().trim();
		if (! repositoryURL.startsWith("https://")) return;
		
        String output = fetchReadme(this, repositoryURL);
		
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
	
	public static String fetchReadme(final Context context, final String repoUrl) {
		File tempDir = new File(context.getCacheDir(), "jgit-temp-" + System.currentTimeMillis());
		String content = "README.md not found.";

		try {
			Git.cloneRepository()
				.setURI(repoUrl)
				.setDirectory(tempDir)
				.call().close();

			File readmeFile = new File(tempDir, "README.md");
			if (readmeFile.exists()) content = readFileToString(readmeFile);

		} catch (Exception e) {
			content = "Error: " + e.getMessage();
		}

		if (tempDir.exists()) deleteRecursively(tempDir);
		return content;
	}

	private static String readFileToString(File file) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line).append("\n");
		}
		reader.close();
		return sb.toString();
	}

	private static void deleteRecursively(File fileOrDir) {
		if (fileOrDir.isDirectory()) {
			for (File child : fileOrDir.listFiles()) {
				deleteRecursively(child);
			}
		}
		fileOrDir.delete();
	}
}
