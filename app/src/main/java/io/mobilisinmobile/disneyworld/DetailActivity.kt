package io.mobilisinmobile.disneyworld

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.mobilisinmobile.disneyworld.databinding.ActivityDetailBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private var characterId : Int = 0

    private val retrofitClient : DisneyService by lazy {
        Retrofit.Builder()
            .baseUrl((applicationContext as DisneyApplication).baseUrl)
            .addConverterFactory(
                Json {
                    ignoreUnknownKeys = true
                }.asConverterFactory("application/json".toMediaType())
            )
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .build()
            )
            .build().create(DisneyService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        characterId = intent.extras?.getInt("characterId", 0) ?: 0
    }

    override fun onStart() {
        super.onStart()
        GlobalScope.launch {
            runOnUiThread {
                binding.content.visibility = View.GONE
                binding.errorView.visibility = View.GONE
            }
            try {
                val characterResult = retrofitClient.getCharacter(characterId)
                runOnUiThread {
                    binding.content.visibility = View.VISIBLE
                    binding.errorView.visibility = View.GONE
                    binding.name.text = characterResult.character.name
                    binding.avatar.load(characterResult.character.imageUrl) {
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                    binding.tvShows.text = "Séries Télés : "+characterResult.character.tvShows.joinToString(", ")

                    binding.disneyAttractions.text = "Attractions Disney : "+characterResult.character.parkAttractions.joinToString(", ")
                }
            } catch (exception : Exception) {
                exception.printStackTrace()
                runOnUiThread {
                    binding.content.visibility = View.GONE
                    binding.errorView.visibility = View.VISIBLE
                    binding.errorLabel.text = "Erreur lors de la récupération des données"
                }
            }
        }
    }
}