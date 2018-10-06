package io.github.alancleetus.todolist;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
 * */

public class MainActivity extends AppCompatActivity {
	//defining variables
	private LinearLayout ParentLayout;
	private Realm realm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/****My code starts here*****/

		//initializing variables
		//open realm db
		Realm.init(this);
		realm = Realm.getDefaultInstance();

		ParentLayout = (LinearLayout) findViewById(R.id.taskSection);

		EditText dateEditText = (EditText) findViewById(R.id.currDate);


		String currDate = getCurrDate();
		dateEditText.setText(currDate);

		/****load saved data***/
		load();

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

				//text for task user inputted
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
						String id = addToDB(taskText.getText().toString(), false, "ffffff", -1, -1, -1);

						//query to find task of given id
						Task t= realm.where(Task.class).equalTo("ID", id).findFirst();

						//add item to list on app interface
						addToDoList(t, 0);

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

	/*make current date into a string*/
	public String getCurrDate()
	{
		Calendar calendar = Calendar.getInstance();

		String currDate = "";

		switch(calendar.get(Calendar.DAY_OF_WEEK))
		{
			case 1:
				currDate+="Sunday,";
				break;

			case 2:
				currDate+="Monday,";
				break;

			case 3:
				currDate+="Tuesday,";
				break;

			case 4:
				currDate+="Wednesday,";
				break;

			case 5:
				currDate+="Thursday,";
				break;

			case 6:
				currDate+="Friday,";
				break;

			case 7:
				currDate+="Saturday,";
				break;

			default:
				currDate+="Error";
				break;
		}

		switch(calendar.get(Calendar.MONTH)+1)
		{
			case 1:
				currDate+=" January";
				break;

			case 2:
				currDate+=" February";
				break;

			case 3:
				currDate+=" March";
				break;

			case 4:
				currDate+=" April";
				break;

			case 5:
				currDate+=" May";
				break;

			case 6:
				currDate+=" June";
				break;

			case 7:
				currDate+=" July";
				break;

			case 8:
				currDate+=" August";
				break;

			case 9:
				currDate+=" September";
				break;

			case 10:
				currDate+=" October";
				break;

			case 11:
				currDate+=" November";
				break;

			case 12:
				currDate+=" December";
				break;

			default:
				currDate+="Error";
				break;
		}

		currDate+= " ";
		currDate+= (calendar.get(Calendar.DAY_OF_MONTH) <10)? "0"+(calendar.get(Calendar.DAY_OF_MONTH)):(calendar.get(Calendar.DAY_OF_MONTH));


		return currDate;
	}

	/*add tasks to realm db*/
	public String addToDB(String topic,  boolean done, String color, int day, int month, int year)
	{
		realm.beginTransaction();

		String id = new Date().toString();

		Task t = realm.createObject(Task.class, id);
		t.setTopic(topic);
		t.setDone(done);
		t.setHexColor(color);
		t.setDueDay(day);
		t.setDueMonth(month);
		t.setDueYear(year);

		realm.commitTransaction();

		return id;
	}

	//loads all the task when the app is first opened
	public void load() {

		//fetch all tasks from realm db and append them to main app screen
		RealmResults<Task> tasksArr = realm.where(Task.class).findAll();

		for (Task t : tasksArr) {
			if (t.getDone())
				addToDoneList(t, 0);
			else
				addToDoList(t, 0);
		}
	}

	//add a item to be done
	public void addToDoList(final Task t, int index) {

		final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);

		final TextView taskText = (TextView) toDoItem.findViewById(R.id.taskTextView);
		Button radioButton = (Button) toDoItem.findViewById(R.id.radioButton); //button next to task

		//set the text and id of task based on incoming parameters
		taskText.setText(t.getTopic());
		toDoItem.setTag(t.getID());

		//make the task long clickable
		toDoItem.setLongClickable(true);

		//when user clicks on the edit button the status will be updated in the db,
		//removed from to do list, and moved to done list
		radioButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				//update the status to true
				realm.beginTransaction();
				Task tempTask = realm.where(Task.class).equalTo("ID", toDoItem.getTag().toString()).findFirst();
				tempTask.setDone(true);
				realm.commitTransaction();

				//getting current position of item in parent layout
				int index = ParentLayout.indexOfChild(toDoItem);

				//we remove the item from the to be done section
				((LinearLayout) ParentLayout).removeView(toDoItem);

				//we then add it to the done section
				addToDoneList(tempTask, index);
			}
		});

		//when the task is long clicked, user will be able to edit the text
		View.OnLongClickListener doItemClicked = new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
				View mView = getLayoutInflater().inflate(R.layout.dialog, null);

				final EditText taskTextInAlert = (EditText) mView.findViewById(R.id.TaskInput);
				Button addButtonInAlert = (Button) mView.findViewById(R.id.NewTaskButton);
				Button cancelAlert = (Button) mView.findViewById(R.id.cancelDialogButton);

				final Task tempTask = realm.where(Task.class).equalTo("ID", toDoItem.getTag().toString()).findFirst();
				taskTextInAlert.setText(tempTask.getTopic());

				addButtonInAlert.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View view) {
						//if the user has not written anything new, return
						if (taskTextInAlert.getText().toString().matches("") || taskTextInAlert.getText().toString().matches(tempTask.getTopic()))
							return;

						//update topic in database
						realm.beginTransaction();
						Task tempTask = realm.where(Task.class).equalTo("ID", toDoItem.getTag().toString()).findFirst();
						tempTask.setTopic(taskTextInAlert.getText().toString());
						realm.commitTransaction();

						//clear the input box
						taskTextInAlert.setText("");
					}
				});

				alertBuilder.setView(mView);
				final AlertDialog dialog = alertBuilder.create();
				dialog.show();

				cancelAlert.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						dialog.cancel();
					}
				});

				return true;
			}
		};

		//this is what happens when the to be done item is checked
		toDoItem.setOnLongClickListener(doItemClicked);
		toDoItem.setOnLongClickListener(doItemClicked);

		//add the task index position
		ParentLayout.addView(toDoItem, index);
	}

	//add an item that is marked done but not yet deleted
	public void addToDoneList(final Task t, int index) {

		final View toDoItem = getLayoutInflater().inflate(R.layout.donetaskholder, null);
		final TextView taskText = (TextView) toDoItem.findViewById(R.id.taskTextView);
		Button deleteButton = (Button) toDoItem.findViewById(R.id.deleteButtonForTask);
		Button radioButton = (Button) toDoItem.findViewById(R.id.radioButton);

		deleteButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				realm.beginTransaction();
				Task tempTask = realm.where(Task.class).equalTo("ID", toDoItem.getTag().toString()).findFirst();
				tempTask.deleteFromRealm();
				realm.commitTransaction();
				ParentLayout.removeView(toDoItem);
				Toast.makeText(MainActivity.this, "Deleted: "+taskText.getText(), Toast.LENGTH_SHORT).show();
			}
		});

		taskText.setText(t.getTopic());
		toDoItem.setTag(t.getID());
		toDoItem.setLongClickable(true);

		radioButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//update the status to false
				realm.beginTransaction();

				Task tempTask = realm.where(Task.class).equalTo("ID", toDoItem.getTag().toString()).findFirst();
				tempTask.setDone(false);

				realm.commitTransaction();
				//getting current position of item in parent layout
				int index = ParentLayout.indexOfChild(toDoItem);

				//first we remove it from the not done section
				((LinearLayout) ParentLayout).removeView(toDoItem);

				//then we add it to the list again
				addToDoList(tempTask, index);
			}
		});

		ParentLayout.addView(toDoItem, index);
	}
}
