package mx.edu.utng.mycrudproyect
import Movie
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast

class DetailActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var movie: Movie? = null

    // Declarar vistas
    private lateinit var tvTitle: TextView
    private lateinit var tvDirector: TextView
    private lateinit var tvYear: TextView
    private lateinit var tvGenre: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ratingBar: RatingBar
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Inicializar vistas
        tvTitle = findViewById(R.id.tvTitle)
        tvDirector = findViewById(R.id.tvDirector)
        tvYear = findViewById(R.id.tvYear)
        tvGenre = findViewById(R.id.tvGenre)
        tvDescription = findViewById(R.id.tvDescription)
        ratingBar = findViewById(R.id.ratingBar)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)

        dbHelper = DatabaseHelper(this)
        val movieId = intent.getIntExtra("MOVIE_ID", -1)

        if(movieId != -1) {
            loadMovie(movieId)
            setupButtons()
        } else {
            finish()
        }
    }

    private fun loadMovie(id: Int) {
        dbHelper.getAllMovies().firstOrNull { it.id == id }?.let {
            movie = it
            tvTitle.text = it.title
            tvDirector.text = "Director: ${it.director}"
            tvYear.text = it.year.toString()
            tvGenre.text = it.genre
            tvDescription.text = it.description
            ratingBar.rating = it.rating
        } ?: run {
            Toast.makeText(this, "Película no encontrada", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupButtons() {
        btnEdit.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java).apply {
                putExtra("MOVIE_ID", movie?.id)
            }
            startActivity(intent)
        }

        btnDelete.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar película")
                .setMessage("¿Estás seguro de eliminar esta película?")
                .setPositiveButton("Eliminar") { _, _ ->
                    movie?.id?.let { id ->
                        dbHelper.deleteMovie(id)
                        Toast.makeText(this, "Película eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}