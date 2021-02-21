package com.drax.notificationlistener;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static com.drax.notificationlistener.MainActivity.TAG;

public class LogsAdapter extends RecyclerView.Adapter<LogsAdapter.ViewHolder> {

    Context context;
    List<Logs> logsList;
    RecyclerView rvLogs;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeStamp;
        TextView packageName;
        TextView title;
        TextView text;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeStamp = itemView.findViewById(R.id.item_timeStamp);
            packageName = itemView.findViewById(R.id.item_packageName);
            title = itemView.findViewById(R.id.item_title);
            text = itemView.findViewById(R.id.item_text);
        }
    }

    public LogsAdapter(Context context, List<Logs> logsList, RecyclerView rvLogs) {
        this.context = context;
        this.logsList = logsList;
        this.rvLogs = rvLogs;
    }

    final View.OnClickListener onClickListener = new MyOnClickListener();
    @NonNull
    @Override
    public LogsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.single_item, parent, false);
        view.setOnClickListener(onClickListener);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LogsAdapter.ViewHolder holder, int position) {
        Logs log = logsList.get(position);

        Log.d(TAG, String.valueOf(position));

        holder.timeStamp.setText("" + log.getTimeStamp());
        holder.packageName.setText("" + log.getPackageName());
        holder.title.setText("" + log.getTitle());
        holder.text.setText("" + log.getText());
    }

    @Override
    public int getItemCount() {
        return logsList.size();
    }

    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int itemPosition = rvLogs.getChildLayoutPosition(v);
            String item = logsList.get(itemPosition).getPackageName();
            Toast.makeText(context, item, Toast.LENGTH_LONG).show();
        }
    }
}
