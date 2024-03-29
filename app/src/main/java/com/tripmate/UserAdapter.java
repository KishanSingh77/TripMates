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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    public  static ArrayList<UserProfile> userList;
    //Boolean addflag = true;
    public static ArrayList<UserProfile> addedUsers =new ArrayList<>();
    public UserAdapter(ArrayList<UserProfile> userList) {
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        addedUsers = new ArrayList<>();
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_item,parent,false);
        UserViewHolder userViewHolder = new UserViewHolder(view);
        return userViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final UserViewHolder holder, int position) {
        final UserProfile u1 = userList.get(position);
        holder.userListFullName.setText(u1.getFirstName()+" " + u1.getLastName());
        Picasso.get().load(u1.getImageUrl()).into(holder.userphoto);

        holder.addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  DocumentReference tripRef = TripProfileActivity.db.collection("Trips").document(AddUsers.tripID);
                if(holder.canBeAdded){
                    addedUsers.add(u1);
//                tripRef.update("authorizedUsers", FieldValue.arrayUnion(u1.getUserUID()));
                    holder.addUser.setText("Remove");
                    holder.canBeAdded = false;// addflag =false;
                }
                else{
                    addedUsers.remove(addedUsers.indexOf(u1));
                    holder.canBeAdded = true;
                    //addflag = true;
//                 tripRef.update("authorizedUsers", FieldValue.arrayRemove(u1.getUserUID()));
                    holder.addUser.setText("Add");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder{
        public TextView userListFullName;
        public Button addUser;
        public ImageView userphoto;
        public Boolean canBeAdded;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            canBeAdded = true;
            userListFullName = itemView.findViewById(R.id.list_user_userName);
            addUser = itemView.findViewById(R.id.userAddBtn);
            userphoto = itemView.findViewById(R.id.userImage);
        }
    }
}

