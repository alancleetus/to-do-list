package io.github.alancleetus.todolist;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import io.realm.Realm;
import io.realm.RealmResults;

/*
 * TODO:
 * ---1. create color coded tasks
 * 2. add statistics
 * ---3. use realm db instead of sqlite
 * ---4. remove header and show date and menu button instead
 * ---5. remove extra space where general used to be
 * 6. security
 * 7. replace task button fab
 * 8. reminder on day
 * 9. comment color section
 * 10. night mode
 * 11. view tasks by color code
 * 12.password lock
 * */

public class MainActivity extends AppCompatActivity {
	//defining variables
	private Realm realm;
    private Switch nightMode;
	private TabLayout tabLayout;
	private ViewPager viewPager;
    private final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

    @Override
	protected void onCreate(Bundle savedInstanceState) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.DarkAppTheme);
        }
        else
        {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		final Switch nightMode = findViewById(R.id.nightModeSwitch);

		if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            nightMode.setChecked(true);
        }

        nightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b)
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                else
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                restart();
            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        adapter.AddFragment(new ActiveFragment(), "Active");
        adapter.AddFragment(new CompletedFragment(), "Completed");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //initializing variables
		//open realm db
		Realm.init(this);
		realm = Realm.getDefaultInstance();


		setDate();

		/****************************
		 * fab button found on the top right had corner
		 * on click it will create a dialog box which can be filled out to
		 * create a new task
		 */
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
				View mView = getLayoutInflater().inflate(R.layout.dialog, null);

				//task info
				final EditText taskText = (EditText) mView.findViewById(R.id.TaskInput);

				//buttons to save task or to cancel the dialog box
				Button addButtonInAlert = (Button) mView.findViewById(R.id.NewTaskButton);
				Button cancelAlert = (Button) mView.findViewById(R.id.cancelDialogButton);

				//when add button is clicked, following will happen
				addButtonInAlert.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {

						//if the user has not written anything for the task,
						// return without doing anything
						if (taskText.getText().toString().matches("")) return;

						//else insert task to database
						String id = addToDB(taskText.getText().toString(), false, -1, -1, -1);

						//query to find task of given id
						Task t= realm.where(Task.class).equalTo("ID", id).findFirst();

						//add item to list on app interface
						ActiveFragment active = (ActiveFragment) adapter.getItem(0);

                        active.addToActiveList(t);

						//clear the dialog box's text field
						taskText.setText("");
					}

				});

				alertBuilder.setView(mView);
				final AlertDialog dialog = alertBuilder.create();
				dialog.show();

				//when cancel button is clicked, following will happen
				cancelAlert.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						dialog.cancel();
					}
				});
			}
		});
	}

    private void setDate(){
        TextView dayTV = (TextView) findViewById(R.id.DayOfWeekTextView);
        TextView dateTV = (TextView) findViewById(R.id.DateTextView);

        Calendar calendar = Calendar.getInstance();

        String dayOfWeek = ""+ WeekDay.forValue(calendar.get(Calendar.DAY_OF_WEEK));
        String month = ""+ WeekDay.forValue(calendar.get(Calendar.MONTH));
        String date = ""+calendar.get(calendar.DATE);
        String year = ""+calendar.get(calendar.YEAR);


        dateTV.setText(month+" "+date+" "+year);
        dayTV.setText(dayOfWeek);
    }

	/*add tasks to realm db*/
	public String addToDB(String topic,  boolean done,  int day, int month, int year)
	{
		realm.beginTransaction();

		String id = new Date().toString();

		Task t = realm.createObject(Task.class, id);
		t.setTopic(topic);
		t.setDone(done);
		t.setDueDay(day);
		t.setDueMonth(month);
		t.setDueYear(year);

		realm.commitTransaction();

		return id;
	}

    public ArrayList<Task> loadActive() {

        RealmResults<Task> tasksArr = realm.where(Task.class).findAll();

        ArrayList<Task> arr= new ArrayList<>();
        for (Task t : tasksArr) {
            if (!t.getDone())
                arr.add(t);
        }
        return arr;
    }

    public ArrayList<Task> loadCompleted() {

        RealmResults<Task> tasksArr = realm.where(Task.class).findAll();

        ArrayList<Task> arr= new ArrayList<>();
        for (Task t : tasksArr) {
            if (t.getDone())
                arr.add(t);
        }

        return arr;
    }


    public void updateTaskInDB(String id, boolean bool) {
        //update the status to true
        realm.beginTransaction();
        Task tempTask = realm.where(Task.class).equalTo("ID", id).findFirst();
        tempTask.setDone(bool);
        realm.commitTransaction();


        if (bool) {
            CompletedFragment completed = (CompletedFragment) adapter.getItem(1);
            completed.addToCompletedList(tempTask);
        }
        else
        {
            ActiveFragment active = (ActiveFragment) adapter.getItem(0);
            active.addToActiveList(tempTask);
        }

    }
    public void deleteTaskFromDB(String id)
    {
        realm.beginTransaction();
        Task tempTask = realm.where(Task.class).equalTo("ID", id).findFirst();
        tempTask.deleteFromRealm();
        realm.commitTransaction();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void restart()
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

}
