/*
package com.example.roomwordsample2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.roomwordsample2.Poi;
import com.example.roomwordsample2.PoiDao;
import com.example.roomwordsample2.PoiListAdapter;
import com.example.roomwordsample2.PoiViewModel;
import com.example.roomwordsample2.R;
import com.example.roomwordsample2.WanderRouteDatabase;

import java.util.List;

public class ViewPoiActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    private PoiViewModel mPoiViewModel;

    // private EditText mEditWordView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_poi);

        // Get the database instance using Room
        WanderRouteDatabase database = WanderRouteDatabase.getDatabase(getApplicationContext());
        // Get the RouteDao
        PoiDao poiDao = database.poiDao();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        mPoiViewModel = new ViewModelProvider(this).get(PoiViewModel.class);

        PoiListAdapter.PoiDiff diffCallback = new PoiListAdapter.PoiDiff();
        PoiListAdapter adapter = new PoiListAdapter(diffCallback);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPoiViewModel.getAllPois().observe(this, new Observer<List<Poi>>() {
            @Override
            public void onChanged(List<Poi> words) {
                // Update the cached copy of the words in the adapter.
                adapter.submitList(words);
            }
        });

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            /*
            if (TextUtils.isEmpty(mEditWordView.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String word = mEditWordView.getText().toString();
                replyIntent.putExtra(EXTRA_REPLY, word);
                setResult(RESULT_OK, replyIntent);
            }


            finish();
        });
    }

    public void goBack(View view) {
        onBackPressed(); // This will perform the default "Back" action
    }
}

 */