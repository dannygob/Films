package com.example.films.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.films.databinding.ItemMovieBinding
import com.example.films.data.Movie
import com.squareup.picasso.Picasso

class MoviesAdapter(
    var items: List<Movie>,
    val onClick: (Int) -> Unit
) : Adapter<MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding =ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size // retorna el tamaño del listado

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = items[position]
        holder.render(movie)
        holder.itemView.setOnClickListener { onClick(position) }
    }

}

class MovieViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {



    fun render(movie: Movie) {

        binding.nameTextView.text = movie.title
        binding.yearTextView.text = movie.year

        Picasso.get()
            .load(movie.imageURL)
            .into(binding.movieImageView)


    }


}

