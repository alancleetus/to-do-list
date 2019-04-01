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

    public CompletedFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.completed_fragment, container, false);

        ArrayList<Task> completedTaskArray =  ((MainActivity) getActivity()).loadCompleted();

        for( Task t : completedTaskArray)
            addToCompletedList(t);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        ArrayList<Task> activeTaskArray =  ((MainActivity) getActivity()).loadCompleted();

        for( Task t : activeTaskArray)
            addToCompletedList(t);
    }

    //add a item to be done
    public void addToCompletedList(final Task t) {

        ParentLayout = view.findViewById(R.id.completedTaskList);

        final View toDoItem = getLayoutInflater().inflate(R.layout.donetaskholder, null);
        TextView taskText = (TextView) toDoItem.findViewById(R.id.taskTextView);
        Button radioButton = (Button) toDoItem.findViewById(R.id.radioButton); //button next to task
Button deleteButton = (Button) toDoItem.findViewById(R.id.deleteButtonForTask);

        //set the text and id of task based on incoming parameters
        taskText.setText(t.getTopic());
        toDoItem.setTag(t.getID());

        System.out.println("LOG: "+(taskText).getText());

        //make the task long clickable

        //todo:this does not work becasue id is null
        toDoItem.setLongClickable(true);

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) getActivity()).updateTaskInDB(toDoItem.getTag().toString(), false);
                //we remove the item from the to be done section
                ((LinearLayout) ParentLayout).removeView(toDoItem);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                ((MainActivity) getActivity()).deleteTaskFromDB(toDoItem.getTag().toString());

                ParentLayout.removeView(toDoItem);
                //Toast.makeText(((MainActivity) getActivity()), "Deleted: "+ t.getTopic(), Toast.LENGTH_SHORT).show();

            }
        });



        ParentLayout.addView(toDoItem);
    }
}
