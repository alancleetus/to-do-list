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

    private View view;
    private LinearLayout ParentLayout;
    public ActiveFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.active_fragment, container, false);
        ArrayList<Task> activeTaskArray =  ((MainActivity) getActivity()).loadActive();
        for( Task t : activeTaskArray) { addToActiveList(t); }
        return view;
    }

    public void addToActiveList(final Task t)
    {
        ParentLayout = view.findViewById(R.id.activeTaskList);

        final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);
        TextView taskText = (TextView) toDoItem.findViewById(R.id.taskTextView);
        Button radioButton = (Button) toDoItem.findViewById(R.id.radioButton);

        taskText.setText(t.getTopic());
        toDoItem.setTag(t.getID());

        //toDoItem.setLongClickable(true);

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).updateTaskInDB(toDoItem.getTag().toString(),  true);
                ((LinearLayout) ParentLayout).removeView(toDoItem);
            }
        });

        ParentLayout.addView(toDoItem,0);
    }
}
