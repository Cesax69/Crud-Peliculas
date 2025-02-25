package mx.edu.utng.mycrudproyect

import Movie
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class FormActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var movieId: Int = -1

    private lateinit var etTitle: TextInputEditText
    private lateinit var etDirector: TextInputEditText
    private lateinit var etYear: TextInputEditText
    private lateinit var etGenre: TextInputEditText
    private lateinit var etRating: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etImageUrl: TextInputEditText
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        etTitle = findViewById(R.id.etTitle)
        etDirector = findViewById(R.id.etDirector)
        etYear = findViewById(R.id.etYear)
        etGenre = findViewById(R.id.etGenre)
        etRating = findViewById(R.id.etRating)
        etDescription = findViewById(R.id.etDescription)
        etImageUrl = findViewById(R.id.etImageUrl)
        btnSave = findViewById(R.id.btnSave)

        dbHelper = DatabaseHelper(this)
        movieId = intent.getIntExtra("MOVIE_ID", -1)

        if (movieId != -1) {
            loadMovieData()
        }

        btnSave.setOnClickListener {
            if (validateForm()) {
                saveMovie()
            }
        }
    }

    private fun loadMovieData() {
        dbHelper.getAllMovies().firstOrNull { it.id == movieId }?.let { movie ->
            etTitle.setText(movie.title)
            etDirector.setText(movie.director)
            etYear.setText(movie.year.toString())
            etGenre.setText(movie.genre)
            etRating.setText(movie.rating.toString())
            etDescription.setText(movie.description)
            etImageUrl.setText(movie.imageUrl)
        }
    }

    private fun validateForm(): Boolean {
        return when {
            etTitle.text.toString().trim().isEmpty() -> {
                etTitle.error = "Título requerido"
                false
            }
            etDirector.text.toString().trim().isEmpty() -> {
                etDirector.error = "Director requerido"
                false
            }
            etYear.text.toString().trim().isEmpty() -> {
                etYear.error = "Año requerido"
                false
            }
            else -> true
        }
    }

    private fun saveMovie() {
        val movie = Movie(
            id = movieId,
            title = etTitle.text.toString().trim(),
            director = etDirector.text.toString().trim(),
            year = etYear.text.toString().trim().toInt(),
            genre = etGenre.text.toString().trim(),
            rating = etRating.text.toString().trim().toFloatOrNull() ?: 0f,
            description = etDescription.text.toString().trim(),
            imageUrl = etImageUrl.text.toString().trim()
        )

        val result = if (movieId == -1) {
            dbHelper.insertMovie(movie)
        } else {
            dbHelper.updateMovie(movie)
        }

        if (result != -1L) {
            Toast.makeText(this, "Película guardada", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        }
    }
}