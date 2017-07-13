package io.github.alancleetus.todolist;

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

public class MainActivity extends AppCompatActivity
{
    //defining variables
    private LinearLayout ParentLayoutDone;
    private LinearLayout ParentLayoutNotDone;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /****My code starts here*****/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog, null);
                final EditText task = (EditText) mView.findViewById(R.id.TaskInput);
                final EditText type = (EditText) mView.findViewById(R.id.TypeInput);
                Button addButton = (Button) mView.findViewById(R.id.NewTaskButton);

                addButton.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view)
                    {
                        //if the user has not written anything, return
                        if (task.getText().toString().matches("") ) return;

                        if(type.getText().toString().matches("")) type.setText("general");
                        //save item to database
                        long id = myDb.insert(task.getText().toString(), type.getText().toString());

                        //add item to list on app
                        addToDoList(id, task.getText().toString(), type.getText().toString());

                        //clear the input box
                        task.setText("");
                        type.setText("");
                    }

                });

                alertBuilder.setView(mView);
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
            }
        });

        //initializing variables
        myDb = new DatabaseHelper(this);
        ParentLayoutDone = (LinearLayout) findViewById(R.id.doneSection);
        ParentLayoutNotDone = (LinearLayout) findViewById(R.id.toBeDoneSection);

        /****load saved data***/
        load();
    }

    /**
     * loads all the task when the app is first opened
     **/
    public void load()
    {
        Cursor data = myDb.getAllData();

        while (data.moveToNext()) {
            if (data.getString(3).matches("false"))
                addToDoList(data.getLong(0), data.getString(1), data.getString(2));
            else
                addToDoneList(data.getLong(0), data.getString(1), data.getString(2));
        }
    }

    public void addToDoList(long id, final String newItem, final String type)
    {
        final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);
        final TextView task = (TextView) toDoItem.findViewById(R.id.taskTextView);
        final TextView typeOfTask = (TextView) toDoItem.findViewById(R.id.topicTextView);
        Button editButton = (Button) toDoItem.findViewById(R.id.editButtonForTask);

        editButton.setBackgroundTintList(ColorStateList.valueOf( Color.GREEN ));

        editButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                //update the status to true
                myDb.updateDataStatus(toDoItem.getTag().toString(), "true");

                //we remove the item from the to be done section
                ((LinearLayout) ParentLayoutNotDone).removeView(toDoItem);

                //we then add it to the done section
                addToDoneList(toDoItem);

            }
        });

        task.setText(newItem);
        typeOfTask.setText(type);

        toDoItem.setTag(id);
        toDoItem.setLongClickable(true);

        View.OnLongClickListener doItemClicked = new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view)
            {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog, null);
                final EditText taskInAlert = (EditText) mView.findViewById(R.id.TaskInput);
                final EditText typeOfTaskinAlert = (EditText) mView.findViewById(R.id.TypeInput);
                Button addButtonInAlert = (Button) mView.findViewById(R.id.NewTaskButton);

                taskInAlert.setText(newItem);
                typeOfTaskinAlert.setText(type);
                addButtonInAlert.setText("edit");

                addButtonInAlert.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View view)
                    {
                        //if the user has not written anything, return
                        if (taskInAlert.getText().toString().matches("") || typeOfTaskinAlert.getText().toString().matches("")) return;

                        //save item to database
                        myDb.updateData(toDoItem.getTag().toString(), taskInAlert.getText().toString(), typeOfTaskinAlert.getText().toString(), "false");

                        task.setText(taskInAlert.getText().toString());

                        //clear the input box
                        taskInAlert.setText("");
                        typeOfTaskinAlert.setText("");

                    }

                });

                alertBuilder.setView(mView);
                AlertDialog dialog = alertBuilder.create();
                dialog.show();
                return true;
            }
        };

        //this is what happens when the to be done item is checked
        toDoItem.setOnLongClickListener(doItemClicked);
        task.setOnLongClickListener(doItemClicked);
        typeOfTask.setOnLongClickListener(doItemClicked);

        ParentLayoutNotDone.addView(toDoItem, 0);
    }

    public void addToDoneList(final View toDoItem)
    {
        final TextView task = (TextView) toDoItem.findViewById(R.id.taskTextView);
        final TextView typeOfTask = (TextView) toDoItem.findViewById(R.id.topicTextView);

        Button editButton = (Button) toDoItem.findViewById(R.id.editButtonForTask);

        editButton.setBackgroundResource(R.drawable.cross);
        editButton.setBackgroundTintList(ColorStateList.valueOf( Color.RED ));

        editButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                myDb.delete(""+ toDoItem.getTag());
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();

            }
        });


        View.OnClickListener doneItemClicked = new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                //update the status to false
                myDb.updateDataStatus(toDoItem.getTag().toString(), "false");

                //first we remove it from the not done section
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);

                //then we add it to the list again
                addToDoList((long) toDoItem.getTag(), task.getText().toString(), typeOfTask.getText().toString());
            }
        };

        //this is what happens when you check an item in the done section
        toDoItem.setOnClickListener(doneItemClicked);
        task.setOnClickListener(doneItemClicked);
        typeOfTask.setOnClickListener(doneItemClicked);

       ((LinearLayout) ParentLayoutDone).addView(toDoItem, 0);

    }

    public void addToDoneList(long id, final String newItem, final String type) {


        final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);
        final TextView task = (TextView) toDoItem.findViewById(R.id.taskTextView);
        final TextView typeOfTask = (TextView) toDoItem.findViewById(R.id.topicTextView);
        Button editButton = (Button) toDoItem.findViewById(R.id.editButtonForTask);

        editButton.setBackgroundResource(R.drawable.cross);
        editButton.setBackgroundTintList(ColorStateList.valueOf( Color.RED ));

        editButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                myDb.delete(""+ toDoItem.getTag());
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();

            }
        });

        task.setText(newItem);
        typeOfTask.setText(type);

        toDoItem.setTag(id);
        toDoItem.setLongClickable(true);

        View.OnClickListener doneItemClicked = new View.OnClickListener(){

            @Override
            public void onClick(View view)
            {
                //update the status to false
                myDb.updateDataStatus(toDoItem.getTag().toString(), "false");

                //first we remove it from the not done section
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);

                //then we add it to the list again
                addToDoList((long) toDoItem.getTag(), task.getText().toString(), typeOfTask.getText().toString());
            }
        };

        //this is what happens when you check an item in the done section
        toDoItem.setOnClickListener(doneItemClicked);
        task.setOnClickListener(doneItemClicked);
        typeOfTask.setOnClickListener(doneItemClicked);

        ((LinearLayout) ParentLayoutDone).addView(toDoItem, 0);

    }


}
