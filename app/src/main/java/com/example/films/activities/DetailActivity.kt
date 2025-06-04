package com.example.films.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.films.R
import com.example.films.data.Movie
import com.example.films.data.MovieService
import com.example.films.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MOVIE_ID = "MOVIE_ID"
    }

    private lateinit var binding: ActivityDetailBinding
    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Muestra el botón atrás
            setHomeAsUpIndicator(R.drawable.ic_back) // Icono personalizado
            title =
                "" // O título que quieras, por ejemplo el nombre de la peli luego de cargar datos
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val id = intent.getStringExtra(EXTRA_MOVIE_ID)
        if (id != null) {
            findMovieById(id)
        } else {
            Toast.makeText(this, "No se recibió el ID de la película", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Vuelve a la pantalla anterior
        return true
    }

    private fun getRetrofit(): MovieService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(MovieService::class.java)
    }

    private fun findMovieById(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                val result = service.findMoviesById(id)
                movie = result
                Log.i("DetailActivity", "Movie: $movie")

                withContext(Dispatchers.Main) {
                    loadData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("DetailActivity", "Error: ${e.message}")
                runOnUiThread {
                    Toast.makeText(this@DetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun loadData() {
        Picasso.get().load(movie.imageURL).into(binding.movieImageView)
        binding.moviesTitleTextView.text = movie.title
        binding.moviesgenreTextView.text = movie.genre
        binding.movieYearTextView.text = getString(R.string.year_format, movie.year)
        binding.moviesruntimetextView.text = getString(R.string.duration_format, movie.runtime)
        binding.moviesdirectortextView.text = getString(R.string.director_format, movie.director)
        binding.moviescountrytextView.text = getString(R.string.country_format, movie.country)
        binding.moviesplottextView.text = movie.plot

        // Opcional: Cambiar título Toolbar al nombre de la película
        supportActionBar?.title = movie.title
    }
}
