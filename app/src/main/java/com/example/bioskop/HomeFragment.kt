package com.example.bioskop

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import kotlinx.coroutines.launch
import org.json.JSONObject


class HomeFragment : Fragment() {

    lateinit var session: SharedPreferences
    lateinit var filmAdapter: filmAdapter
    lateinit var data: MutableList<filmModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var pager = view.findViewById<ViewPager>(R.id.viewPager)
        data = mutableListOf<filmModel>()
        var connection = Connection()
        var name = view.findViewById<TextView>(R.id.name_home)
        name.text = "Halo, " + session.getString("name", "")

        lifecycleScope.launch {
            var result = getRequest(connection.connection + "movie/get", session.getString("token", ""))

            result.fold(
                onSuccess = {
                        response -> var jsonObject = JSONObject(response)
                    if (!jsonObject.getString("movies").isNullOrEmpty()){
                        var jsonArray = jsonObject.getJSONArray("movies")
                        for (i in 0 until jsonArray.length()){
                            var jsonObject2 = jsonArray.getJSONObject(i)
                            data.add(filmModel(jsonObject2.getString("id"), jsonObject2.getString("gambar"), jsonObject2.getString("rating"), jsonObject2.getString("durasi")))
                        }

                        filmAdapter = filmAdapter(requireContext(), data)
                        pager.adapter = filmAdapter
                    }
                },
                onFailure = {
                        error -> error.printStackTrace()
                }
            )
        }

    }
}