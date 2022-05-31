package com.example.cse110_lab5.activity.navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cse110_lab5.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder>{
    private List<String> detailedPath = new ArrayList<>();

    public void update(List<String> detailedPath) {
        this.detailedPath = detailedPath;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.direction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.exhibitName.setText(detailedPath.get(position));
    }

    @Override
    public int getItemCount() {
        return detailedPath.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView exhibitName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            exhibitName = (TextView) itemView.findViewById(R.id.direction);
        }

    }

}
