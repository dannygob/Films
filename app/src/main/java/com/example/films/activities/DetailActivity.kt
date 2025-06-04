package com.example.films.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.films.databinding.ActivityDetailBinding
import com.example.films.data.Movie
import com.example.films.data.MovieService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_MOVIE_ID = "MOVIE_ID"// esto sirve para pasar datos a la actividad
    }

    lateinit var binding: ActivityDetailBinding
    lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getStringExtra(EXTRA_MOVIE_ID)!!

        findMoviesById(id)
    }
    fun loadData() {
        Picasso.get().load(movie.imageURL).into(binding.movieImageView)
        binding.moviesTitleTextView.text = movie.title
        binding.moviesgenreTextView.text = movie.genre
        binding.movieYearTextView.text = movie.year
        binding.moviesruntimetextView.text = movie.runtime
        binding.moviesdirectortextView.text = movie.director
        binding.moviescountrytextView.text = movie.country
        binding.moviesplottextView.text = movie.plot


    }
    fun getRetrofit(): MovieService {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MovieService::class.java)
    }

    private fun findMoviesById(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                val result = service.findMoviesById(id)
                Log.i("MainActivity", "Response: $result")
                movie = result
                Log.i("MainActivity", "Response: $movie")


                withContext(Dispatchers.Main) {
                    loadData()

                }

            } catch (e: Exception) {
                e.printStackTrace()
                Log.i("DetailError", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
