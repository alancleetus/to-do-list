package io.github.alancleetus.todolist;

import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    //defining variables
    private Button SubmitBtn;
    private EditText InputBox;
    private LinearLayout ParentLayoutDone;
    private LinearLayout ParentLayoutNotDone;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /****My code starts here*****/

        //initializing variables
        myDb = new DatabaseHelper(this);
        SubmitBtn = (Button) findViewById(R.id.submitButton);
        InputBox = (EditText) findViewById(R.id.newItemInputBox);
        ParentLayoutDone = (LinearLayout) findViewById(R.id.doneSection);
        ParentLayoutNotDone = (LinearLayout) findViewById(R.id.toBeDoneSection);


        /****load saved data***/
        load();

        /***this is what happens when the submit button is clicked***/
        SubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if the user has not written anything, return
                if (InputBox.getText().toString().matches("")) return;

                //save item to database
                long id = myDb.insert(InputBox.getText().toString());

                //add item to list on app
                addToDoList(id, InputBox.getText().toString(), "general");

                //clear the input box
                InputBox.setText("");
            }
        });

        /***this is what happens when the button is long pressed***/
        SubmitBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //if the user has not written anything, return
                if (InputBox.getText().toString().matches("")) return false;

                //open a new dialog
                /*
                 * the dialog has one edit text for the task string
                 * the dialog has a second edit text for the tag of the checkbox
                 * it has a submit button for submitting the task
                 */

                return true;
            }
        });


        /****what happens when enter key is presses on the edit text***/
        InputBox.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {

                    //if the user has not written anything, return
                    if (InputBox.getText().toString().matches("")) return true;

                    //save item to database
                    long id = myDb.insert(InputBox.getText().toString());

                    //add item to list on app
                    addToDoList(id, InputBox.getText().toString(), "general");

                    //clear the input box
                    InputBox.setText("");

                    return true;
                }
                return false;
            }
        });


    }

    /**
     * loads all the task when the app is first opened
     **/
    public void load() {
        Cursor data = myDb.getAllData();

        while (data.moveToNext()) {
            if (data.getString(3).matches("false"))
                addToDoList(data.getLong(0), data.getString(1), data.getString(2));
            else
                addToDoneList(data.getLong(0), data.getString(1), data.getString(2));
        }
    }

    public void addToDoList(long id, final String newItem, final String type) {
        final CheckBox toDoItem = new CheckBox(getApplicationContext());

        toDoItem.setText(newItem);
        toDoItem.setTag(id);
        toDoItem.setLongClickable(true);

        //styles for the items
        toDoItem.setTextSize(16);
        toDoItem.setTypeface(Typeface.create("casual", Typeface.NORMAL));
        toDoItem.setPadding(2, 10, 2, 10);
        toDoItem.setBackgroundResource(R.drawable.card);

        //this is what happens when the to be done item is checked
        toDoItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //update the status to true
                myDb.updateDataStatus(toDoItem.getTag().toString(), "true");

                //we remove the item from the to be done section
                ((LinearLayout) ParentLayoutNotDone).removeView(toDoItem);

                //we then add it to the done section
                addToDoneList(toDoItem);


            }
        });

        toDoItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(MainActivity.this, "long click", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        ParentLayoutNotDone.addView(toDoItem, 0);
    }

    public void addToDoneList(final CheckBox toDoItem) {
        //this is what happens when you check an item in the done section
        toDoItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //update the status to false
                myDb.updateDataStatus(toDoItem.getTag().toString(), "false");

                //first we remove it from the not done section
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);

                //then we add it to the list again
                addToDoList((long) toDoItem.getTag(), toDoItem.getText().toString(), "general");
            }
        });


        toDoItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                myDb.delete(""+ toDoItem.getTag());
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();

                return true;
            }
        });



        ((LinearLayout) ParentLayoutDone).addView(toDoItem, 0);

    }

    public void addToDoneList(long id, final String newItem, final String type) {

        final CheckBox toDoItem = new CheckBox(getApplicationContext());

        toDoItem.setText(newItem);
        toDoItem.setTag(id);
        toDoItem.setLongClickable(true);
        toDoItem.setChecked(true);

        //styles for the items
        toDoItem.setTextSize(16);
        toDoItem.setTypeface(Typeface.create("casual", Typeface.NORMAL));
        toDoItem.setPadding(2, 10, 2, 10);
        toDoItem.setBackgroundResource(R.drawable.card);

        //this is what happens when you check an item in the done section
        toDoItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //update the status to false
                myDb.updateDataStatus(toDoItem.getTag().toString(), "false");

                //first we remove it from the not done section
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);

                //then we add it to the list again
                addToDoList((long) toDoItem.getTag(), toDoItem.getText().toString(), type);
            }
        });


        toDoItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                myDb.delete(""+ toDoItem.getTag());
                ((LinearLayout) ParentLayoutDone).removeView(toDoItem);
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();

                return true;
            }
        });


        ((LinearLayout) ParentLayoutDone).addView(toDoItem, 0);

    }

    /****************************************************
     * this function adds new to do list item to the    *
     * linear layout and also the array list of         *
     * all items                                        *
     *                                                   *
     ****************************************************/
    public void addToList2(long id, final String newItem) {
        if (!newItem.matches("")) {
            final ListView toDoItem = new ListView(getApplicationContext());
           /* GridLayout wrapper = new GridLayout(getApplicationContext(), null, R.style.wrapper);
            TextView mainText = new TextView(getApplicationContext(), null, R.style.mainTextStyle);
            TextView subText = new TextView(getApplicationContext(), null, R.style.subText);
            Button doneButton = new Button(getApplicationContext(), null, R.style.doneButton);
*/
            GridLayout wrapper = (GridLayout) getLayoutInflater().inflate(R.layout.wrapper, null);
            TextView mainText = (TextView) getLayoutInflater().inflate(R.layout.maintext, null);
            TextView subText = (TextView) getLayoutInflater().inflate(R.layout.subtext, null);
            Button doneButton = (Button) getLayoutInflater().inflate(R.layout.donebutton, null);

            //styles for the items
            toDoItem.setBackgroundResource(R.drawable.card);
            mainText.setTypeface(Typeface.create("casual", Typeface.NORMAL));
            subText.setTypeface(Typeface.create("casual", Typeface.NORMAL));


            //this is what happens when the to be done item is checked
            /*toDoItem.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    //first we remove it from the toDoItem array list
                    removeFromList(toDoItem.getText().toString());

                    //then we save the arraylist
                    save();

                    //then we remove the item from the to be done section
                    ((LinearLayout) ParentLayoutDone).removeView(toDoItem);

                    //we then add it to the done section
                    ((LinearLayout) ParentLayoutNotDone).addView(toDoItem, 0);

                    //this is what happens when you check an item in the done section
                    toDoItem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                        {
                            //first we remove it from the done section
                            ((LinearLayout) ParentLayoutNotDone).removeView(toDoItem);

                            //then we add it to the list again
                            addToList(toDoItem.getText().toString());
                            save();
                        }
                    });
                    //Toast.makeText(MainActivity.this, toDoItem.getText()+"- marked done" , Toast.LENGTH_SHORT).show();
                }
            });
*/
            mainText.setText(newItem);
            subText.setText("subText");
            doneButton.setText(">");


            wrapper.addView(mainText);
            wrapper.addView(subText);
            wrapper.addView(doneButton);


            ParentLayoutDone.addView(wrapper, 0);

        }
    }
}
