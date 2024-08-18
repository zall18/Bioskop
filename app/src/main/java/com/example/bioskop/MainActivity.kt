package com.example.bioskop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var session: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var email: EditText = findViewById(R.id.email_input)
        var password: EditText = findViewById(R.id.password_input)
        var login: AppCompatButton  =findViewById(R.id.login)
        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var progress: ProgressBar = findViewById(R.id.progress_login)
        var editor = session.edit()
        var connection = Connection()

        login.setOnClickListener {
            if (email.text.isNullOrBlank())
            {
                email.setError("Email wajib diiisi")
            }else if(password.text.isNullOrBlank()){
                password.setError("Password wajib diisi")
            }else
            {

                var jsonObject = JSONObject().apply {
                    put("email", email.text)
                    put("password", password.text)
                }

                lifecycleScope.launch {
                    var result = postRequest(connection.connection + "auth/login", jsonObject, null)

                    result.fold(
                        onSuccess = {
                                response -> var jsonObject2 = JSONObject(response)
                            if (!jsonObject2.getString("token").isNullOrEmpty())
                            {
                                var jsonObject3 = JSONObject(jsonObject2.getString("user"))

                                editor.putString("token", jsonObject2.getString("token"))
                                editor.putString("id", jsonObject3.getString("id"))
                                editor.putString("name", jsonObject3.getString("name"))
                                editor.commit()

                                progress.visibility = View.GONE
                                Toast.makeText(applicationContext, "Login Berhasil!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(applicationContext, BottomActivity::class.java))
                            }
                        },
                        onFailure = {
                                error -> error.printStackTrace()
                            Toast.makeText(applicationContext, "Login Gagal!", Toast.LENGTH_SHORT).show()
                            progress.visibility = View.GONE
                        }
                    )
                }

            }
        }
    }
}