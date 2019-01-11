package com.example.witne.popularmoviesstage1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.witne.data.Movie;
import com.example.witne.data.Trailer;
import com.example.witne.utilities.JsonUtils;
import com.example.witne.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class DetailMovieActivity extends AppCompatActivity {

    private TextView tv_movie_title;
    private ImageView iv_movie_poster;
    private TextView tv_movie_release_date;
    private TextView tv_movie_rating;
    private TextView tv_movie_overview;
    private Movie movie;

    private RecyclerView rv_movie_trailer;
    private MovieTrailerAdapter movieTrailerAdapter;
    private URL movieSearch;
    private ArrayList<Trailer> trailerList;
    //private Button bt_movie_trailer;

    //private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w185";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_movie);

        tv_movie_title = findViewById(R.id.tv_movie_title);
        iv_movie_poster = findViewById(R.id.iv_movie_poster);
        tv_movie_release_date = findViewById(R.id.tv_movie_release_date);
        tv_movie_rating = findViewById(R.id.tv_movie_vote_average);
        tv_movie_overview = findViewById(R.id.tv_movie_overview);

        //set the movie trailer recycler view
        rv_movie_trailer = findViewById(R.id.rv_movie_trailer);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_movie_trailer.setLayoutManager(linearLayoutManager);
        rv_movie_trailer.setHasFixedSize(true);
        movieTrailerAdapter = new MovieTrailerAdapter(new ArrayList<Trailer>());
        rv_movie_trailer.setAdapter(movieTrailerAdapter);

        //get movie details from the intent that started the activity
        //Intent intentStartedThisActivity = getIntent();
        Bundle movieData = getIntent().getExtras();
        movie = movieData.getParcelable("Movie_Details");

        if(movie != null){
            tv_movie_title.setText(movie.getTitle());
            //String posterPath = IMAGE_BASE_URL + movie.getPoster_path();
            Picasso.get()
                    .load(movie.getPoster_path())
                    .into(iv_movie_poster);

            tv_movie_release_date.setText((movie.getRelease_date()));
            //tv_movie_rating.setText(movie.getVote_average());
            //tv_movie_rating.setText(String.valueOf(movie.getVote_average())+ " /10");
            String movieRating = getString(R.string.movie_rating_total,String.valueOf(movie.getVote_average()));
            tv_movie_rating.setText(movieRating);
            tv_movie_overview.setText(movie.getMovie_overview());

            startMovieTrailerSearch(String.valueOf(movie.getMovieId()));
        }

    }

    private void startMovieTrailerSearch(String movieId ){
        movieSearch = NetworkUtils.buildUrl(movieId);
        //fetch data on separate thread
        // and initialize the recycler viewer with data from movie adapter
        new  FecthMovieTrailerTask().execute(movieSearch);
    }

    private void showJSONData(String jsonData){
        trailerList = new ArrayList<>();
        trailerList = JsonUtils.parseMovieTrailerJson(jsonData);
        movieTrailerAdapter.setMovieTrailerAdapter(trailerList);
    }

    //Async inner class to fetch network data
    public class FecthMovieTrailerTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params){

            URL searchUrl = params[0];
            String jsonData = null;
            try {
                jsonData = NetworkUtils.fetchData(searchUrl);
            }catch (IOException e){
                e.printStackTrace();
            }
            return jsonData;
        }

        @Override
        protected void onPostExecute(String jsonData ){
            super.onPostExecute(jsonData);
            showJSONData(jsonData);
        }
    }
}