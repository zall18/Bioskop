package com.example.bioskop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date

class KursiActivity : AppCompatActivity() {
    lateinit var session: SharedPreferences
    lateinit var kursiAdapter: KursiAdapter
    lateinit var data: MutableList<KursiModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kursi)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var gridView: GridView = findViewById(R.id.kursi_gridview)
        var connection =Connection()
        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        data = mutableListOf<KursiModel>()
        var id_studio = session.getString("id_studio", "")
        var dataKursi = mutableListOf<String>()

        lifecycleScope.launch {
            var result = getRequest(connection.connection + "studio/get/$id_studio", session.getString("token", ""))

            result.fold(
                onSuccess = {
                        response -> var jsonObject = JSONObject(response)
                    if (!jsonObject.getString("studio").isNullOrEmpty()){
                        var jsonObject2 = JSONObject(jsonObject.getString("studio"))
                        var jml = Integer.parseInt(jsonObject2.getString("jumlah_kursi"))
                        jml = jml / 6

                        var abjad = 'A'
                        for (i in 0 until jml)
                        {
                            for (a in 1 until 7)
                            {
                                data.add(KursiModel("$abjad" + a.toString()))
                                Log.d("data", "onCreate: $data")
                            }
                            abjad++
                        }
                        kursiAdapter = KursiAdapter(applicationContext, data, dataKursi)
                        gridView.adapter = kursiAdapter
                    }
                },
                onFailure = {
                        error -> error.printStackTrace()
                }
            )
        }

        var bayar: AppCompatButton = findViewById(R.id.bayar)
        bayar.setOnClickListener {
            val formatter = SimpleDateFormat("yyyy-MM-dd")
            val date = Date()
            val current = formatter.format(date)
            var jsonObject = JSONObject().apply {
                put("id_user", session.getString("id", ""))
                put("id_jadwal_tayang", session.getString("id_jadwal", "") )
                put("waktu_pemesanan", current.toString() )
            }

            lifecycleScope.launch {
                var result = postRequest(connection.connection + "pemesanan/add", jsonObject, session.getString("token", ""))

                result.fold(
                    onSuccess = {
                            response -> var jsonObject2 = JSONObject(response)
                        startActivity(Intent(applicationContext, Invoice::class.java))

                    },
                    onFailure = {
                            error -> error.printStackTrace()
                        Toast.makeText(applicationContext, "Gagal!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }


    }
}