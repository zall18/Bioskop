package com.example.bioskop

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject


class JadwalFragment : Fragment() {

    lateinit var session: SharedPreferences
    lateinit var jadwalAdapter: JadwalAdapter
    lateinit var data: MutableList<JadwalModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_jadwal, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var harga = view.findViewById<TextView>(R.id.harga)
        var gridView = view.findViewById<GridView>(R.id.gridview)
        session = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        var connection = Connection()
        data = mutableListOf<JadwalModel>()

        lifecycleScope.launch {
            var result = getRequest(connection.connection + "jadwal/get", session.getString("token", ""))

            result.fold(
                onSuccess = {
                        response -> var jsonObject = JSONObject(response)
                    if (!jsonObject.getString("jadwal").isNullOrEmpty()){
                        var jsonArray = jsonObject.getJSONArray("jadwal")
                        for (i in 0 until jsonArray.length()){
                            var jsonObject2 = jsonArray.getJSONObject(i)
                            data.add(JadwalModel(jsonObject2.getString("jam_tayang"), jsonObject2.getString("id"), jsonObject2.getString("id_movie"), jsonObject2.getString("id_studio"), jsonObject2.getString("harga_tiket")))
                            harga.text = jsonObject2.getString("harga_tiket")
                        }

                        jadwalAdapter = JadwalAdapter(requireContext(), data)
                        gridView.adapter = jadwalAdapter
                    }
                },
                onFailure = {
                        error -> error.printStackTrace()
                }
            )
        }

    }
}