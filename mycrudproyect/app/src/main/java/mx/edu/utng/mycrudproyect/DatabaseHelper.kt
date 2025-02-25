package mx.edu.utng.mycrudproyect

import Movie
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "movies.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_NAME = "movies"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_DIRECTOR = "director"
        const val COL_YEAR = "year"
        const val COL_GENRE = "genre"
        const val COL_RATING = "rating"
        const val COL_DESCRIPTION = "description"
        const val COL_IMAGE_URL = "image_url"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TITLE TEXT NOT NULL,
                $COL_DIRECTOR TEXT,
                $COL_YEAR INTEGER,
                $COL_GENRE TEXT,
                $COL_RATING REAL,
                $COL_DESCRIPTION TEXT,
                $COL_IMAGE_URL TEXT
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_NAME ADD COLUMN $COL_IMAGE_URL TEXT")
        }
    }

    fun insertMovie(movie: Movie): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, movie.title)
            put(COL_DIRECTOR, movie.director)
            put(COL_YEAR, movie.year)
            put(COL_GENRE, movie.genre)
            put(COL_RATING, movie.rating)
            put(COL_DESCRIPTION, movie.description)
            put(COL_IMAGE_URL, movie.imageUrl)
        }
        return db.insert(TABLE_NAME, null, values)
    }

    fun getAllMovies(): List<Movie> {
        val movies = mutableListOf<Movie>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "$COL_TITLE ASC"
        )

        with(cursor) {
            while (moveToNext()) {
                val movie = Movie(
                    id = getInt(getColumnIndexOrThrow(COL_ID)), // Usa getInt
                    title = getString(getColumnIndexOrThrow(COL_TITLE)),
                    director = getString(getColumnIndexOrThrow(COL_DIRECTOR)),
                    year = getInt(getColumnIndexOrThrow(COL_YEAR)),
                    genre = getString(getColumnIndexOrThrow(COL_GENRE)),
                    rating = getFloat(getColumnIndexOrThrow(COL_RATING)),
                    description = getString(getColumnIndexOrThrow(COL_DESCRIPTION)),
                    imageUrl = getString(getColumnIndexOrThrow(COL_IMAGE_URL))
                )
                movies.add(movie)
            }
            close()
        }
        return movies
    }

    fun updateMovie(movie: Movie): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TITLE, movie.title)
            put(COL_DIRECTOR, movie.director)
            put(COL_YEAR, movie.year)
            put(COL_GENRE, movie.genre)
            put(COL_RATING, movie.rating)
            put(COL_DESCRIPTION, movie.description)
            put(COL_IMAGE_URL, movie.imageUrl)
        }
        return db.update(
            TABLE_NAME,
            values,
            "$COL_ID = ?",
            arrayOf(movie.id.toString())
        )
    }

    fun deleteMovie(id: Int): Int {
        val db = writableDatabase
        return db.delete(
            TABLE_NAME,
            "$COL_ID = ?",
            arrayOf(id.toString())
        )
    }
}