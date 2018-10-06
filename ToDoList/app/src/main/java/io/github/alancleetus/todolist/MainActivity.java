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

public class MainActivity extends AppCompatActivity {
    //defining variables
    private LinearLayout ParentLayoutDone;
    private LinearLayout ParentLayoutNotDone;
    private DatabaseHelper myDb;


    /*
     * TODO:
     * 1. create color coded tasks
     * 2. add statistics
     * 3. use realm db instead of sqlite
     * 4. remove header and show date and menu button instead
     * 5. remove extra space where general used to be
     * 6. security
     * 7. replace task button fab
     * 8. reminder on day
     * */
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /****My code starts here*****/

        //initializing variables
        myDb = new DatabaseHelper(this);
        ParentLayoutDone = (LinearLayout) findViewById(R.id.doneSection);
        ParentLayoutNotDone = (LinearLayout) findViewById(R.id.toBeDoneSection);

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
                final EditText task = (EditText) mView.findViewById(R.id.TaskInput);

                //buttons to save task or to cancel the dialog box
                Button addButtonInAlert = (Button) mView.findViewById(R.id.NewTaskButton);
                Button cancelAlert = (Button) mView.findViewById(R.id.cancelDialogButton);

                //when add button is clicked, following will happen
                addButtonInAlert.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        //if the user has not written anything for the task,
                        // return without doing anything
                        if (task.getText().toString().matches("")) return;

                        //else insert task to database
                        long id = myDb.TB_1_insert(task.getText().toString());

                        //add item to list on app interface
                        addToDoList(id, task.getText().toString());

                        //clear the dialog box's text field
                        task.setText("");
                    }

                });

                alertBuilder.setView(mView);
                final AlertDialog dialog = alertBuilder.create();
                dialog.show();

                //when add button is clicked, following will happen
                cancelAlert.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.cancel();

                    }

                });
            }
        });
    }

    /**
     * loads all the task when the app is first opened
     **/
    public void load() {

        Cursor data = myDb.TB_1_getAllData();

        while (data.moveToNext()) {

            System.out.println(data.getString(0));

            System.out.println(data.getString(1));

            System.out.println(data.getString(2));
            if (data.getString(2).matches("false"))
                addToDoList(data.getLong(0), data.getString(1));
            else
                addToDoneList(data.getLong(0), data.getString(1));
        }

    }

    public void addToDoList(long id, final String newItem) {

        final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);
        final TextView task = (TextView) toDoItem.findViewById(R.id.taskTextView);
        Button editButton = (Button) toDoItem.findViewById(R.id.editButtonForTask); //button next to task

        //while in the to be done section the button will be a green check mark
        editButton.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

        //when user clicks on the edit button the status will be updated in the db,
        //removed from to do list, and moved to done list
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //update the status to true
                myDb.TB_1_updateDataStatus(toDoItem.getTag().toString(), "true");

                //we remove the item from the to be done section
                ((LinearLayout) ParentLayoutNotDone).removeView(toDoItem);

                //we then add it to the done section
                addToDoneList(toDoItem);

            }
        });

        //set the text and id of task based on incoming parameters
        task.setText(newItem);
        toDoItem.setTag(id);

        //make the task long clickable
        toDoItem.setLongClickable(true);

        //when the task is long clicked, user will be able to edit the text
        View.OnLongClickListener doItemClicked = new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog, null);
                final EditText taskInAlert = (EditText) mView.findViewById(R.id.TaskInput);
                Button addButtonInAlert = (Button) mView.findViewById(R.id.NewTaskButton);
                Button cancelAlert = (Button) mView.findViewById(R.id.cancelDialogButton);

                taskInAlert.setText(task.getText());

                addButtonInAlert.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //if the user has not written anything, return
                        if (taskInAlert.getText().toString().matches(""))
                            return;

                        //save item to database
                        myDb.TB_1_updateData(toDoItem.getTag().toString(), taskInAlert.getText().toString(), "false");

                        task.setText(taskInAlert.getText().toString());

                        //clear the input box
                        taskInAlert.setText("");

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
        task.setOnLongClickListener(doItemClicked);

        //add the task to the top of the to be done list on app interface
        ParentLayoutNotDone.addView(toDoItem, 0);
    }

    public void addToDoneList(final View toDoItem) {

        final TextView task = (TextView) toDoItem.findViewById(R.id.taskTextView);

        Button editButton = (Button) toDoItem.findViewById(R.id.editButtonForTask);

        editButton.setBackgroundResource(R.drawable.cross);
        editButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

        //when the red cross next to tasks are clicked the task is deleted
        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                myDb.TB_1_delete("" + toDoItem.getTag());
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);
                Toast.makeText(MainActivity.this, "Deleted: "+task.getText(), Toast.LENGTH_SHORT).show();

            }
        });

        //if the task is in the done list and the task is clicked, then the task will be moved back
        //to the to be done list
        View.OnClickListener doneItemClicked = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //update the status to false
                myDb.TB_1_updateDataStatus(toDoItem.getTag().toString(), "false");

                //first we remove it from the not done section
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);

                //then we add it to the list again
                addToDoList((long) toDoItem.getTag(), task.getText().toString());
            }
        };

        //this is what happens when you click an item in the done section
        toDoItem.setOnClickListener(doneItemClicked);
        task.setOnClickListener(doneItemClicked);

        ((LinearLayout) ParentLayoutDone).addView(toDoItem, 0);

    }

    public void addToDoneList(long id, final String newItem) {


        final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);
        final TextView task = (TextView) toDoItem.findViewById(R.id.taskTextView);

        Button editButton = (Button) toDoItem.findViewById(R.id.editButtonForTask);

        editButton.setBackgroundResource(R.drawable.cross);
        editButton.setBackgroundTintList(ColorStateList.valueOf(Color.RED));

        editButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                myDb.TB_1_delete("" + toDoItem.getTag());
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);
                Toast.makeText(MainActivity.this, "Deleted: "+task.getText(), Toast.LENGTH_SHORT).show();

            }
        });

        task.setText(newItem);

        toDoItem.setTag(id);
        toDoItem.setLongClickable(true);

        View.OnClickListener doneItemClicked = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //update the status to false
                myDb.TB_1_updateDataStatus(toDoItem.getTag().toString(), "false");

                //first we remove it from the not done section
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);

                //then we add it to the list again
                addToDoList((long) toDoItem.getTag(), task.getText().toString());
            }
        };

        //this is what happens when you check an item in the done section
        toDoItem.setOnClickListener(doneItemClicked);
        task.setOnClickListener(doneItemClicked);

        ((LinearLayout) ParentLayoutDone).addView(toDoItem, 0);

    }


}
