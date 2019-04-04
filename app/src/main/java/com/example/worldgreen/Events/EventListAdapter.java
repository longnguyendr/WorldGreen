package com.example.worldgreen.Events;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.worldgreen.DataModel.Event;
import com.example.worldgreen.R;

import java.util.ArrayList;
import java.util.List;

class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ViewHolder> {
    private ArrayList<Event> mData;
    private LayoutInflater mInflater;
    private itemClickListener onItemClickListener;

    // data is passed into the constructor
    EventListAdapter(Context context, ArrayList<Event> eventData) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = eventData;
    }

    // inflates the row layout from xml when needed
    @Override
    public EventListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.event_row, parent, false);
        return new EventListAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(EventListAdapter.ViewHolder holder, int position) {
        String animal = mData.get(position).getDescription();
        holder.myTextView.setText(animal);
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.event_row);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null ) {
                        onItemClickListener.onItemClick(view, getAdapterPosition()) ;
                    }
                }
            });
        }

    }
    public void setItemClickListener (itemClickListener clickListener) {
        onItemClickListener = clickListener;
    }

    public interface itemClickListener {
        void onItemClick(View view , int position);
    }
}
