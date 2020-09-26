package com.intentsproj.foodapp5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {

    TextView food_name,food_price,food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String foodId="";

    FirebaseDatabase database;
    DatabaseReference foods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Food");

        //Init View
        numberButton = (ElegantNumberButton)findViewById(R.id.number_button);
        btnCart = (FloatingActionButton)findViewById(R.id.btnCart);

        food_description = (TextView)findViewById(R.id.food_description);
        food_name= (TextView)findViewById(R.id.food_name);
        food_price= (TextView)findViewById(R.id.food_price);
        food_image = (ImageView)findViewById(R.id.img_food);

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandendAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //get food Id from Intent
        if(getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
            if(!foodId.isEmpty())
            {
                getDetailFood(foodId);
            }

    }

    private void getDetailFood(String foodId) {

        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot datasnapshot) {
                food  food = datasnapshot.getValue(food.class);

                //set Image
                Picasso.with(getBaseContext()).load(food.getImage())
                        .into(food_image);

                collapsingToolbarLayout.setTitle(food.getName());
                food_price.setText(food.getPrice());

                food_name.setText(food.getName());
                food_description.setText(food.getDescription());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}