package com.example.roomwordsample2.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.roomwordsample2.R;
import com.example.roomwordsample2.Route;
import com.example.roomwordsample2.RouteDao;
import com.example.roomwordsample2.RouteViewModel;
import com.example.roomwordsample2.WanderRouteDatabase;
import com.example.roomwordsample2.RouteListAdapter;

import java.util.List;

public class ViewRouteActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "com.example.android.wordlistsql.REPLY";

    private RouteViewModel mRouteViewModel;

    // private EditText mEditWordView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_route);
        // mEditWordView = findViewById(R.id.edit_word);

        // Get the database instance using Room
        WanderRouteDatabase database = WanderRouteDatabase.getDatabase(getApplicationContext());
        // Get the RouteDao
        RouteDao routeDao = database.routeDao();

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        mRouteViewModel = new ViewModelProvider(this).get(RouteViewModel.class);

        RouteListAdapter.RouteDiff diffCallback = new RouteListAdapter.RouteDiff();
        RouteListAdapter adapter = new RouteListAdapter(diffCallback);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mRouteViewModel.getAllRoutes().observe(this, new Observer<List<Route>>() {
            @Override
            public void onChanged(List<Route> words) {
                // Update the cached copy of the words in the adapter.
                adapter.submitList(words);
            }
        });

        adapter.setOnRouteClickListener(new RouteListAdapter.OnRouteClickListener() {
            @Override
            public void onRouteClick(int routeId) {
                // Hier wird die Aktivität beendet und die Route-ID an die MainActivity zurückgegeben
                Intent resultIntent = new Intent();
                resultIntent.putExtra("ROUTE_ID", routeId);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

    }

    public void goBack(View view) {
        onBackPressed(); // This will perform the default "Back" action
    }
}

