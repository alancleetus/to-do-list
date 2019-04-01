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

public class ActiveFragment extends Fragment {

    View view;
    private LinearLayout ParentLayout;


    public ActiveFragment()
    {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.active_fragment, container, false);

        ArrayList<Task> activeTaskArray =  ((MainActivity) getActivity()).loadActive();

        for( Task t : activeTaskArray)
            addToActiveList(t);

        return view;
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        ArrayList<Task> activeTaskArray =  ((MainActivity) getActivity()).loadActive();

        for( Task t : activeTaskArray)
            addToActiveList(t);
    }

    //add a item to be done
    public void addToActiveList(final Task t) {

        ParentLayout = view.findViewById(R.id.activeTaskList);

        final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);
        TextView taskText = (TextView) toDoItem.findViewById(R.id.taskTextView);
        Button radioButton = (Button) toDoItem.findViewById(R.id.radioButton); //button next to task

        //set the text and id of task based on incoming parameters
        taskText.setText(t.getTopic());
        toDoItem.setTag(t.getID());

        System.out.println("LOG: "+(taskText).getText());

        //make the task long clickable

        //todo:this does not work becasue id is null
        toDoItem.setLongClickable(true);

        //when user clicks on the edit button the status will be updated in the db,
        //removed from to do list, and moved to done list
        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((MainActivity) getActivity()).updateTaskInDB(toDoItem.getTag().toString(),  true);
                //we remove the item from the to be done section
                ((LinearLayout) ParentLayout).removeView(toDoItem);
            }
        });

        ParentLayout.addView(toDoItem);
    }

}
