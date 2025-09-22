package imo.readme_updater;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.Executors;
import org.eclipse.jgit.api.Git;
import android.widget.CompoundButton;

public class MainActivity extends Activity {
    final static String REPO_URL_KEY = "REPO_URL_KEY";
    final static String WIDGET_STRING_KEY = "WIDGET_STRING_KEY";
    Button fetchButton;
    Button fetchAndCloseButton;
    TextView outputEdittext;
    EditText repoLinkEdittext;
	CheckBox isLineWrapCheckbox;
	boolean buttonOnClickCloseApp = false;
	String repositoryURL;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        repoLinkEdittext = findViewById(R.id.repo_link_edittext);
        outputEdittext = findViewById(R.id.command_output_edittext);
        fetchButton = findViewById(R.id.update_button);
        fetchAndCloseButton = findViewById(R.id.update_and_close_button);
		isLineWrapCheckbox = findViewById(R.id.is_linewrap_checkbox);
		
		outputEdittext.setFocusable(false);
		outputEdittext.setFocusableInTouchMode(false);
        
        SharedPreferences sp = getSharedPreferences("hehe", Context.MODE_PRIVATE);
		repositoryURL = sp.getString(REPO_URL_KEY, "");
        repoLinkEdittext.setText(repositoryURL);
        
        fetchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveData();
                }
            });
            
        fetchAndCloseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
					buttonOnClickCloseApp = true;
					saveData();
                }
            });
		
		isLineWrapCheckbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton v, boolean isChecked){
				outputEdittext.setHorizontallyScrolling(isChecked);
				outputEdittext.setMovementMethod(new ScrollingMovementMethod());
			}
		});
    }
    
    void saveData(){
        repositoryURL = repoLinkEdittext.getText().toString().trim();
		if (! repositoryURL.startsWith("https://")) return;
		
		outputEdittext.setText("Please Wait...");
		fetchButton.setEnabled(false);
		fetchAndCloseButton.setEnabled(false);
        fetchReadmeAsync(this, repositoryURL);
    }
    
	public void fetchReadmeAsync(final Context context, final String repoUrl) {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
				@Override
				public void run() {
					fetchReadme(context, repoUrl);
				}
			});
	}
	
	private void fetchReadme(final Context context, final String repoUrl){
		File tempDir = new File(context.getCacheDir(), "jgit-temp-" + System.currentTimeMillis());
		String output = "";
		try {
			Git.cloneRepository()
				.setURI(repoUrl)
				.setDirectory(tempDir)
				.call().close();

			File readmeFile = new File(tempDir, "README.md");
			if (readmeFile.exists()) 
				output = readFileToString(readmeFile);
			else
				output = "README.md not found.";

		} catch (final Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			output = sw.toString();

		} finally {
			if (tempDir.exists()) deleteRecursively(tempDir);
			
			final String finalOutput = output;
			new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						saveOutput(finalOutput);
					}
				});
		}
	}
	
	void saveOutput(String output){
		outputEdittext.setText(output);
		fetchButton.setEnabled(true);
		fetchAndCloseButton.setEnabled(true);

		final SharedPreferences.Editor spEditor = getSharedPreferences("hehe", Context.MODE_PRIVATE).edit();
		spEditor.putString(WIDGET_STRING_KEY, output);
		spEditor.putString(REPO_URL_KEY, repositoryURL);
		spEditor.apply();

		updateWidget();

		if(buttonOnClickCloseApp) finish();
	}
	
	void updateWidget(){
        Intent intent = new Intent(MainActivity.this, Widget.class);
        intent.setAction(Widget.ACTION_WIDGET_UPDATE);
        sendBroadcast(intent);
        Toast.makeText(MainActivity.this, "widget updated:D", Toast.LENGTH_SHORT).show();
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
