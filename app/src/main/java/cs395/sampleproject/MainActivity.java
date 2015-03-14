package cs395.sampleproject;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import bolts.Task;

public class MainActivity extends ActionBarActivity {

    protected void startParse() {
        String applicationId = "";
        String clientKey = "";

        if (applicationId.equals("") || clientKey.equals("")){
            Log.e("startParseError", "Properly set your ApplicationId and ClientKey!");
        }

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, applicationId, clientKey);
    }

    protected ParseObject makeTask(String name) {
        // Initialize the object and the table if necessary.
        ParseObject task = new ParseObject("Task");

        // Add key-value pairs to this task.
        task.put("name", name);
        task.put("completed", false);

        // Save the object to Parse’s Cloud.
        task.saveInBackground();

        // Return the `task` so it can be displayed in our App.
        return task;
    }

    protected void displayTasks() {
        // Prepare a query to find all of the non-completed Tasks.
        ParseQuery query = new ParseQuery("Task");
        query.whereEqualTo("completed", false);

        // Prepare a FindCallback to display the Tasks that are found.
        FindCallback displayTasksCallback = getDisplayTasksCallback();

        // Perform the query and the FindCallback..
        query.findInBackground(displayTasksCallback);

    }

    protected void completeTask(ParseObject task) {
        task.put("completed", true);
        task.saveInBackground();
    }

    protected void displayTask(final ParseObject task) {
        final TableLayout table = (TableLayout) findViewById(R.id.taskList);
        final Context context = this;
        String name = task.getString("name");

        // Create a TextView to add to the TableLayout.
        // Note: There are more efficient ways to do this (eg: use a ListView).
        final TextView textView = new TextView(context);
        textView.setText(name);

        // When this task is clicked, mark it as "complete."
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTask(task);
                textView.setVisibility(View.GONE);
            }
        });

        // Display the TextView for this Task.
        table.addView(textView);
    }

    protected FindCallback getDisplayTasksCallback() {
        // Return a callback that will continually call `displayTask` on every task that
        //  the query finds.
        return new FindCallback() {
            @Override
            public void done(List results, ParseException e) {
                // If nothing failed, `results` should contain multiple Objects.
                for (Object result : results) {
                    // Convert each Object into a ParseObject and get that object's name.
                    final ParseObject task = (ParseObject) result;
                    displayTask(task);
                }
            }

        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Parse so that we can securely make queries.
        startParse();

        // Find any tasks that currently exist and display them in our TableLayout.
        displayTasks();

        // When the `addTaskButton` is clicked, create and display a new Task.
        Button button = (Button) findViewById(R.id.addTaskButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Use the `addTaskText` as the "name" for the new task.
                TextView text = (TextView) findViewById(R.id.addTaskText);
                ParseObject task = makeTask(text.getText().toString());
                displayTask(task);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
