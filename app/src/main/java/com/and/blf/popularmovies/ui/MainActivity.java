package com.and.blf.popularmovies.ui;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.and.blf.popularmovies.R;
import com.and.blf.popularmovies.model.Movie;
import com.and.blf.popularmovies.utils.MovieLoader;
import com.and.blf.popularmovies.utils.MovieRecyclerViewAdapter;
import com.and.blf.popularmovies.utils.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks {
    private ProgressBar mLoadingIndicator;

    public static final int MOVIE_LOADER_ID = 45;

    private MovieRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        //Loader
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_LOADER_ID);
        if (movieLoader == null){
            loaderManager.initLoader(MOVIE_LOADER_ID,new Bundle(),this).forceLoad();
        }else{
            loaderManager.restartLoader(MOVIE_LOADER_ID,new Bundle(),this).forceLoad();
        }

        //RV
        List<Movie> movieList = new ArrayList<>();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        RecyclerView movieRecyclerView = findViewById(R.id.rvMovies);
        movieRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MovieRecyclerViewAdapter(movieList,loaderManager);
        movieRecyclerView.setAdapter(mAdapter);
    }

    //Loaders
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        return new MovieLoader(this);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if(data !=  null && data instanceof ArrayList) {
            mAdapter.setMovieList((ArrayList<Movie>) data);
        }else {
            Toast.makeText(this, "Couldn't fetch the data from the MovieService", Toast.LENGTH_LONG).show();
        }
        mLoadingIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader loader) {}

    //Sorting menu handling
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_sort_menu, menu);
        String previousSortSetting = SharedPreferencesUtils.readFromSharedPreferences(this,getString(R.string.sharedPrefFileName),getString(R.string.sort_mode));
        switch (previousSortSetting){
            case "sortByPopularity":
                menu.findItem(R.id.sortByPopularityMenuItem).setChecked(true);
                setAppTitle("popular");
                break;
            case "sortByRating":
                menu.findItem(R.id.sortByRatingMenuItem).setChecked(true);
                setAppTitle("rated");
                break;
            default:
                menu.findItem(R.id.sortByPopularityMenuItem).setChecked(true);
                setAppTitle("popular");
        }
        return true;
    }

    public void onSortByPopularityMenuItemClick(MenuItem menuItem){
        MovieRecyclerViewAdapter.loadedPageCount = 1;
        mAdapter.setShouldClearList(true);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        menuItem.setChecked(true);
        setAppTitle("popular");
        SharedPreferencesUtils.writeToSharedPreferences(this,getString(R.string.sharedPrefFileName),getString(R.string.sort_mode),getString(R.string.sortByPopularity));
        getSupportLoaderManager().getLoader(MOVIE_LOADER_ID).forceLoad();
    }

    public void onSortByRatingMenuItemClick(MenuItem menuItem){
        MovieRecyclerViewAdapter.loadedPageCount = 1;
        mAdapter.setShouldClearList(true);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        menuItem.setChecked(true);
        setAppTitle("rated");
        SharedPreferencesUtils.writeToSharedPreferences(this,getString(R.string.sharedPrefFileName),getString(R.string.sort_mode),getString(R.string.sortByRating));
        getSupportLoaderManager().getLoader(MOVIE_LOADER_ID).forceLoad();
    }

    private void setAppTitle(String adding){
        setTitle(getString(R.string.app_name) + " (" + adding + ")");
    }

}