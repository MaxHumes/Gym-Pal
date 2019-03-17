package com.example.easyin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
{
    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mCourseNames = new ArrayList<>();
    private ArrayList<String> mCourseDescriptions = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context context, ArrayList<String> courseNames, ArrayList<String> courseDescriptions)
    {
        mCourseNames = courseNames;
        mCourseDescriptions = courseDescriptions;
        mContext = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gyms_listitem, parent, false);
       ViewHolder holder = new ViewHolder(view);
       return holder;
    }

    public void onBindViewHolder(ViewHolder holder, int position)
    {
        holder.arrow.setImageResource(R.drawable.ic_keyboard_arrow_right_black_24dp);
        holder.course.setText(mCourseNames.get(position));
        holder.courseDescription.setText(mCourseDescriptions.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener()
        {
              public void onClick(View view){
                  //GOTO course
              }
        });
    }

    public int getItemCount()
    {
        return mCourseNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView course, courseDescription;
        ImageView arrow;
        RelativeLayout parentLayout;

        public ViewHolder (View itemView)
        {
            super(itemView);
            course = itemView.findViewById(R.id.course);
            courseDescription =  itemView.findViewById(R.id.course_details);
            arrow = itemView.findViewById(R.id.arrow);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
