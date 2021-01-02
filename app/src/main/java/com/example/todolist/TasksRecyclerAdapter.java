package com.example.todolist;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.example.todolist.TODOList;
import com.example.todolist.TODOTask;
import com.example.todolist.R;
import com.example.todolist.ViewtaskActivity;

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.TODOTaskViewHolder> {

    Activity activity;
    TODOList data;
    DatabaseReference listRef;

    public TasksRecyclerAdapter(Activity activity, TODOList data, DatabaseReference listRef) {
        this.activity = activity;
        this.data = data;
        this.listRef = listRef;
    }

    @NonNull
    @Override
    public TODOTaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(activity).inflate(R.layout.item_todotask, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        root.setLayoutParams(lp);
        return new TODOTaskViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull TODOTaskViewHolder holder, int position) {
        TODOTask task = data.getTasks().get(position);
        holder.tv_task_title.setText(task.getTitle());
        holder.cb_task_check.setChecked(task.isChecked());

        if(task.isChecked()){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                holder.tv_task_title.setTextColor(activity.getColor(R.color.check_text));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.tv_task_title.setBackground(activity.getDrawable(R.drawable.checket_text_line));
            }
        }

        holder.cb_task_check.setOnCheckedChangeListener((buttonView, isChecked) -> {
            listRef.child("tasks").child(task.getId()).child("checked").setValue(isChecked);
            if(isChecked){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.tv_task_title.setTextColor(activity.getColor(R.color.check_text));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    holder.tv_task_title.setBackground(activity.getDrawable(R.drawable.checket_text_line));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    holder.tv_task_title.setTextColor(activity.getColor(R.color.uncheck_text));
                }
                holder.tv_task_title.setBackground(null);
            }
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(activity, ViewtaskActivity.class);
            intent.putExtra("listId", data.getId());
            intent.putExtra("taskId", task.getId());
            activity.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return data.getTasks().size();
    }

    public static class TODOTaskViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_task_title;
        private  final CheckBox cb_task_check;

        public TODOTaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_task_title = itemView.findViewById(R.id.tv_task_title);
            cb_task_check = itemView.findViewById(R.id.cb_task_check);
        }
    }
}