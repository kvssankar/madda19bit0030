package com.example.madda19bit0030;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

class Data{
    private String title;
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

public class ThirdActivity extends AppCompatActivity {

    // creating a variable for recycler view,
    // array list and adapter class.
    private RecyclerView courseRV;
    private ArrayList<RecyclerData> recyclerDataArrayList;
    private RecyclerViewAdapter recyclerViewAdapter;
    private ArrayList<String>archivedCourses;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // initializing our variables.
        courseRV = findViewById(R.id.idRVCourse);
        // creating new array list.
        recyclerDataArrayList = new ArrayList<>();
        archivedCourses=new ArrayList<>();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Data obj=dataSnapshot.getValue(Data.class);
                    //Toast.makeText(getApplicationContext(),obj.getDescription(), Toast.LENGTH_LONG).show();
                    recyclerDataArrayList.add(new RecyclerData(obj.getTitle(), obj.getDescription()));
                    recyclerViewAdapter.notifyItemInserted(recyclerDataArrayList.size());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        recyclerDataArrayList.add(new RecyclerData("Venkata Sai", "Backed developer"));
//        recyclerDataArrayList.add(new RecyclerData("Kavali Sankar", "Cloud developer"));
//        recyclerDataArrayList.add(new RecyclerData("Venkata Kumar", "Competitive coder"));
//        recyclerDataArrayList.add(new RecyclerData("Vishnu", "Cloud developer"));
//        recyclerDataArrayList.add(new RecyclerData("Naresh", "Devops engineer"));

        recyclerViewAdapter = new RecyclerViewAdapter(recyclerDataArrayList, this);

        LinearLayoutManager manager = new LinearLayoutManager(this);

        courseRV.setLayoutManager(manager);

        courseRV.setAdapter(recyclerViewAdapter);

        Button btn = (Button)findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ThirdActivity.this, FourthActivity.class);
                i.putStringArrayListExtra("data",archivedCourses);
                startActivity(i);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.LEFT;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                RecyclerData deletedCourse = recyclerDataArrayList.get(viewHolder.getAdapterPosition());
                final int position = viewHolder.getAdapterPosition();
                switch (direction){
                    case ItemTouchHelper.LEFT:
                        archivedCourses.add(deletedCourse.getTitle());
                        recyclerDataArrayList.remove(viewHolder.getAdapterPosition());
                        recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        Snackbar.make(courseRV, deletedCourse.getTitle(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recyclerDataArrayList.add(position, deletedCourse);
                                recyclerViewAdapter.notifyItemInserted(position);
                            }
                        }).show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        recyclerDataArrayList.remove(viewHolder.getAdapterPosition());
                        recyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                        Snackbar.make(courseRV, deletedCourse.getTitle(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                recyclerDataArrayList.add(position, deletedCourse);
                                recyclerViewAdapter.notifyItemInserted(position);
                            }
                        }).show();
                        break;
                }
            }
        }).attachToRecyclerView(courseRV);
    }
}
