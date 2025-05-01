package com.example.films.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.films.activities.DetailActivity.Companion.EXTRA_MOVIE_ID
import com.example.films.adapter.MoviesAdapter
import com.example.films.databinding.ActivityMainBinding
import com.example.films.R
import com.example.films.data.MovieService
import com.example.films.data.Movie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MoviesAdapter
    var allMovies : List<Movie> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        adapter = MoviesAdapter(allMovies) { position ->

            val movie = allMovies[position]

            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(EXTRA_MOVIE_ID, movie.id)
            Toast.makeText(this, "IdMovie: ${movie.id}", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        binding.mainRecyclerView.adapter = adapter
        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)

        //findMoviesById("tt2380307")
        findMoviesByName("matrix")

    }

    fun getRetrofit(): MovieService {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MovieService::class.java)
    }



    private fun findMoviesByName(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                val result = service.searchMoviesByTitle(name)
                Log.i("MainActivity", "Response: $result")
                allMovies = result.movies

                withContext(Dispatchers.Main) {
                    adapter.items = allMovies
                    adapter.notifyDataSetChanged()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("MainActivity", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


}