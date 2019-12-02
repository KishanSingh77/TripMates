package com.tripmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;

public class AddUserAdapter extends RecyclerView.Adapter<AddUserAdapter.UserViewHolder> {
    public  static ArrayList<UserProfile> userList;
    Boolean addflag = true;
    public AddUserAdapter(ArrayList<UserProfile> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public AddUserAdapter.UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userItem,parent,false);
        AddUserAdapter.UserViewHolder userViewHolder = new AddUserAdapter.UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        final UserProfile u1 = userList.get(position);
        holder.userListFullName.setText(u1.getFirstName()+" " + u1.getLastName());
        Picasso.get().load(u1.getImageUrl()).into(holder.userphoto);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        public TextView userListFullName;
        public ImageView userphoto;
        public Button add;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userListFullName = itemView.findViewById(R.id.list_user_userName);
            userphoto = itemView.findViewById(R.id.userImage);
            add = itemView.findViewById(R.id.userAddBtn);
            add.setVisibility(View.INVISIBLE);
        }
    }
}
