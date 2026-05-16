package com.example.checkit;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> taskList;

    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.tvTitle.setText(task.getTitle());
        holder.tvSubtitle.setText(task.getSubtitle());

        if (task.isCompleted()) {
            holder.cbTask.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.task_checked_tint));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
            holder.tvSubtitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
            holder.editContainer.setVisibility(View.GONE);
        } else {
            holder.cbTask.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.task_unchecked_tint));
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvTitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
            holder.tvSubtitle.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
            holder.editContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        View cbTask;
        TextView tvTitle, tvSubtitle;
        View editContainer, deleteContainer;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTask = itemView.findViewById(R.id.cb_task);
            tvTitle = itemView.findViewById(R.id.tv_task_title);
            tvSubtitle = itemView.findViewById(R.id.tv_task_subtitle);
            editContainer = itemView.findViewById(R.id.edit_container);
            deleteContainer = itemView.findViewById(R.id.delete_container);
        }
    }
}
