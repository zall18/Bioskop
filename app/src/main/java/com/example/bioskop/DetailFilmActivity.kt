package com.example.bioskop

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class DetailFilmActivity : AppCompatActivity() {
    lateinit var session : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_film)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var name: TextView = findViewById(R.id.name_detail)
        var category: TextView = findViewById(R.id.category_detail)
        var image: ShapeableImageView = findViewById(R.id.image_detail)
        var durasi: TextView = findViewById(R.id.durasi_detail)
        var rating: TextView = findViewById(R.id.rating_detail)
        var jadwal: TextView = findViewById(R.id.jadwal)
        var detail: TextView = findViewById(R.id.detail)
        var id = intent.getStringExtra("id")
        var connection = Connection()
        session = getSharedPreferences("session", Context.MODE_PRIVATE)

        lifecycleScope.launch {
            var result = getRequest(connection.connection + "movie/get/$id", session.getString("token", ""))

            result.fold(
                onSuccess = {
                    response -> var jsonObject = JSONObject(response)
                    if (!jsonObject.getString("movies").isNullOrEmpty())
                    {
                        var jsonObject2 = JSONObject(jsonObject.getString("movies"))

                        name.text = jsonObject2.getString("judul")
                        category.text = jsonObject2.getString("kategori")
                        durasi.text = jsonObject2.getString("durasi")
                        rating.text = jsonObject2.getString("rating")
                        MainScope().launch {
                            var bitmap = getImageFromUrl("https://mayfly-stable-remarkably.ngrok-free.app/storage/thumbnail/" + jsonObject2.getString("gambar"))
                            image.setImageBitmap(bitmap)
                        }
                    }
                },
                onFailure = {
                    error -> error.printStackTrace()
                }
            )
        }
        replaceFragment(JadwalFragment())
        jadwal.setOnClickListener {
            replaceFragment(JadwalFragment())
        }

        detail.setOnClickListener {
            replaceFragment(DetailFragment())
        }
    }
    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content_detail, fragment).commit()
    }
}