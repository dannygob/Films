package com.example.films.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.films.R
import com.example.films.adapter.MoviesAdapter
import com.example.films.data.Movie
import com.example.films.data.MovieService
import com.example.films.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: MoviesAdapter
    private var allMovies: List<Movie> = emptyList()

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
            // Lanzar DetailActivity pasando el id de la película
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_MOVIE_ID, movie.id)
            startActivity(intent)
        }

        binding.mainRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.adapter = adapter

        // Carga inicial (opcional)
        findMoviesByName("matrix")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as? SearchView

        searchView?.queryHint = "Buscar película..."

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    findMoviesByName(query)
                    searchView.clearFocus()
                    searchItem.collapseActionView()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // No hacemos nada mientras se escribe
                return false
            }
        })

        return true
    }

    private fun findMoviesByName(name: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val service = getRetrofit()
                val result = service.searchMoviesByTitle(name)
                allMovies = result.movies

                withContext(Dispatchers.Main) {
                    adapter.updateMovies(allMovies)
                }

            } catch (e: Exception) {
                Log.e("MainActivity", "Error buscando películas", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun getRetrofit(): MovieService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.omdbapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(MovieService::class.java)
    }
}
