package com.example.popcorn.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.popcorn.Models.Person;
import com.example.popcorn.R;

import java.util.List;

public class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.ViewHolder> {

    private Context context;
    private List<Person> peopleList;

    public PeopleAdapter(Context context, List<Person> peopleList) {
        this.context = context;
        this.peopleList = peopleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.person_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Person person = peopleList.get(position);
        holder.nameTextView.setText(person.getName());
        holder.roleTextView.setText(person.getRole());

        Glide.with(context)
                .load(person.getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return peopleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        TextView roleTextView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            roleTextView = itemView.findViewById(R.id.roleTextView);
            imageView = itemView.findViewById(R.id.personImageView);
        }
    }


}
