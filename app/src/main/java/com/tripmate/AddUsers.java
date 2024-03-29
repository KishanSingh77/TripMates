package com.tripmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;

public class AddUsers extends AppCompatActivity {
    RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    public static RecyclerView.Adapter mAdapter;
    ProgressDialog pb;
    ArrayList<UserProfile> userList = new ArrayList<>();

    int flag = 0;
    public static String tripID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users);
        Intent i = getIntent();
        tripID = i.getStringExtra("TRIPID");
        initialize();


        findViewById(R.id.btn_Save_trip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //      Log.d("demo",UserAdapter.addedUsers.toString());
                // DocumentReference tripRef = TripProfileActivity.db.collection("Trips").document(tripID);
//                tripRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//                            DocumentSnapshot document = task.getResult();
//                          ArrayList<String> usersID = (ArrayList<String>) document.getData().get("authorizedUsers");
//                            for(int i=0;i<userList.size();i++){
//                                for(int j=0;j<usersID.size();j++){
//                                    if(userList.get(i).getUserUID().equals(usersID.get(j))){
//                                        addedUsers.add(userList.get(i));
//
//                                    }
//                                }
//                            }
                Intent returnIntent = new Intent();
                returnIntent.putExtra("ADDEDUSERS",UserAdapter.addedUsers);
                setResult(RESULT_OK,returnIntent);
                finish();

            }
        });
    }

    public void initialize(){
        recyclerView = findViewById(R.id.user_recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(AddUsers.this);

        pb = ProgressDialog.show(AddUsers.this,"","Getting Users...",true);
        TripActivity.db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserProfile userProfile = document.toObject(UserProfile.class);
                                for(int i =0;i<TripActivity.addedUsers.size();i++){
//                                    if(!TripProfileActivity.addedUsers.get(i).getUserUID().equals(userProfile.userUID))
//                                    {
                                    userList.add(userProfile);
//                                        break;
//                                    }
//                                    else{
//                                        userList.add(userProfile);}
                                }

                            }
                            if(flag == 0) {
                                recyclerView.setLayoutManager(layoutManager);
                                mAdapter = new UserAdapter(userList);
                                recyclerView.setAdapter(mAdapter);
                            }
                            mAdapter.notifyDataSetChanged();
                            flag = 1;
                            pb.dismiss();
                        } else {
                            Log.d("demo", "Error getting User list: ", task.getException());
                        }
                    }
                });
    }
}
