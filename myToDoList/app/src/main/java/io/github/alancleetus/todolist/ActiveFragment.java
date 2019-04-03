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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.active_fragment, container, false);

        ParentLayout = view.findViewById(R.id.activeTaskList);

        ParentLayout.removeAllViews();
        ArrayList<Task> activeTaskArray =  ((MainActivity) getActivity()).loadActive();
        for( Task t : activeTaskArray) { addToActiveList(t); }
        return view;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        ParentLayout.removeAllViews();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        ParentLayout.removeAllViews();
        ArrayList<Task> activeTaskArray = ((MainActivity) getActivity()).loadActive();
        for (Task t : activeTaskArray) {
            addToActiveList(t);
        }
    }

    public void addToActiveList(final Task t)
    {
        final View toDoItem = getLayoutInflater().inflate(R.layout.taskholder, null);
        final TextView taskText = (TextView) toDoItem.findViewById(R.id.taskTextView);
        Button radioButton = (Button) toDoItem.findViewById(R.id.radioButton);

        taskText.setText(t.getTopic());
        toDoItem.setTag(t.getID());

        radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).updateTaskInDB(toDoItem.getTag().toString(), true);
                ((LinearLayout) ParentLayout).removeView(toDoItem);
            }
        });

        /***item long click start***/
        toDoItem.setLongClickable(true);
        toDoItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick (View view){

                System.out.println("Log: item long clicked");
                ((MainActivity) getActivity()).editTask(toDoItem);

                return true;
            }
        });
        /***item long click end***/
        ParentLayout.addView(toDoItem,0);
    }
}
