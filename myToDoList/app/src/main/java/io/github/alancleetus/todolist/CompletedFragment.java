package io.github.alancleetus.todolist;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CompletedFragment extends Fragment {

    View view;
    private LinearLayout ParentLayout;
    public CompletedFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.completed_fragment, container, false);
        ArrayList<Task> completedTaskArray =  ((MainActivity) getActivity()).loadCompleted();
        for( Task t : completedTaskArray){ addToCompletedList(t); }
        return view;
    }

    public void addToCompletedList(final Task t)
    {
        ParentLayout = view.findViewById(R.id.completedTaskList);

        final View toDoItem = getLayoutInflater().inflate(R.layout.donetaskholder, null);
        TextView taskText = (TextView) toDoItem.findViewById(R.id.taskTextView);
        Button radioButton = (Button) toDoItem.findViewById(R.id.radioButton);
        Button deleteButton = (Button) toDoItem.findViewById(R.id.deleteButtonForTask);

        //set the text and id
        taskText.setText(t.getTopic());
        toDoItem.setTag(t.getID());

        //make the task long clickable
        //toDoItem.setLongClickable(true);

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).updateTaskInDB(toDoItem.getTag().toString(), false);
                //remove the item from the completed list
                ((LinearLayout) ParentLayout).removeView(toDoItem);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).deleteTaskFromDB(toDoItem.getTag().toString());
                ParentLayout.removeView(toDoItem);
            }
        });

        ParentLayout.addView(toDoItem,0);
    }//end add to completed list
}
