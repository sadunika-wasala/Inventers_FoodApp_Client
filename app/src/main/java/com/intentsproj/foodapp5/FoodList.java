package com.intentsproj.foodapp5;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.intentsproj.foodapp5.Interface.ItemClickListener;
import com.intentsproj.foodapp5.ViewHolder.foodViewHolder;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference FoodList;

    String categoryId="";
    FirebaseRecyclerAdapter<food, foodViewHolder> adapter;

    //search Functionality
    FirebaseRecyclerAdapter<food, foodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //firebase
        database = FirebaseDatabase.getInstance();
        FoodList = database.getReference("Food");

        recyclerView=(RecyclerView)findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get Intent here
        if(getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null){

            loadListFood(categoryId);
        }
        //search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your Food");
        //materialSearchBar.setSpeechMode(false); : no need this function because we alredy difind it at xml
        loadSuggest(); //write function to load suggest from firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //when user type their text, we will change suggest list

                List<String> suggest = new ArrayList<String>();
                   for(String search:suggestList)//loop in sudgest list
                   {
                       if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                           suggest.add(search);
                   }
                   materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener(){
            @Override
            public void onSearchStateChanged(boolean enabled){
                //when Search Bar is Closed
                //restart Original Adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text){
                //when search finished
                //show result of search adapter
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode){

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<food, foodViewHolder>(
                food.class,
                R.layout.food_item,
                foodViewHolder.class,
                FoodList.orderByChild("name").equalTo(text.toString())//compare serching name and list name
        ) {
            @Override
            protected void populateViewHolder(foodViewHolder viewHolder, food model, int position) {
                viewHolder.food_name.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.food_image);

                final food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //start Ndew Activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());//send Food Id to new Activity
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);//set adapter for recycler view id serch result
    }

    private void loadSuggest() {
        FoodList.orderByChild("menyId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        for(DataSnapshot postSnapshot:snapshot.getChildren())
                        {
                            food item = postSnapshot.getValue(food.class);
                            suggestList.add(item.getName());//Add namer of food to suggest list

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }

    private void loadListFood(String categoryId) {
        System.out.println("inside loadlist food");
        adapter = new FirebaseRecyclerAdapter<food, foodViewHolder>(food.class,
                R.layout.food_item,
                foodViewHolder.class,
                FoodList.orderByChild("menyId").equalTo(categoryId)//like:select * from foods where MenyId=categoryId
                 ) {

             @Override
            protected void populateViewHolder(foodViewHolder viewHolder, food model, int position) {
                 System.out.println("inside populateViewHolder food");
                 System.out.println("Model: "+ model);
                 System.out.println("Model name: "+ model.getName());
                 System.out.println("Model image: "+ model.getImage());

                 viewHolder.food_name.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage())
                    .into(viewHolder.food_image);

                    final food local = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            //start Ndew Activity
                           Intent foodDetail = new Intent(FoodList.this,FoodDetail.class);
                           foodDetail.putExtra("FoodId",adapter.getRef(position).getKey());//send Food Id to new Activity
                           startActivity(foodDetail);
                        }
                    });
                 }
            };
        System.out.println("TEST DEBUG");
        //set Adapter
        recyclerView.setAdapter(adapter);
    }
};