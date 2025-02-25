package mx.edu.utng.mycrudproyect

import Movie
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load // Importación CORRECTA

class MovieAdapter(
    private var movies: List<Movie>,
    private val onItemClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>(), Filterable {

    private var moviesFull: List<Movie> = ArrayList(movies)

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvDirector: TextView = itemView.findViewById(R.id.tvDirector)
        private val tvYear: TextView = itemView.findViewById(R.id.tvYear)
        private val tvGenre: TextView = itemView.findViewById(R.id.tvGenre)
        private val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        private val ivPoster: ImageView = itemView.findViewById(R.id.ivMoviePoster)

        fun bind(movie: Movie) {
            tvTitle.text = movie.title
            tvDirector.text = itemView.context.getString(R.string.director_format, movie.director)
            tvYear.text = movie.year.toString()
            tvGenre.text = movie.genre
            ratingBar.rating = movie.rating

            // Carga de imagen con Coil (Versión estable)
            ivPoster.load(movie.imageUrl) {
                crossfade(true)
                placeholder(R.drawable.placeholder_poster)
                error(R.drawable.error_poster)
            }

            itemView.setOnClickListener { onItemClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    fun updateList(newList: List<Movie>) {
        val diffResult = DiffUtil.calculateDiff(MovieDiffCallback(movies, newList))
        movies = newList
        moviesFull = ArrayList(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return FilterResults().apply {
                values = when {
                    constraint.isNullOrEmpty() -> moviesFull
                    else -> moviesFull.filter {
                        it.title.contains(constraint, true) ||
                                it.director.contains(constraint, true) ||
                                it.genre.contains(constraint, true)
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            (results?.values as? List<Movie>)?.let { filteredList ->
                updateList(filteredList)
            }
        }
    }

    private class MovieDiffCallback(
        private val oldList: List<Movie>,
        private val newList: List<Movie>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size
        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
            oldList[oldPos].id == newList[newPos].id

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean =
            oldList[oldPos] == newList[newPos]
    }
}