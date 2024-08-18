package com.example.bioskop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class JadwalAdapter(var context: Context, var data: MutableList<JadwalModel>): BaseAdapter() {

    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView ?: inflater.inflate(R.layout.jadwalitem, null, false)

        var jam = view.findViewById<TextView>(R.id.jam)
        var session = context.getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()
        var jadwal = getItem(position) as JadwalModel
        var item = view.findViewById<LinearLayout>(R.id.jadwal)
        jam.text = jadwal.jadwal

        item.setOnClickListener {
            editor.putString("id_jadwal", jadwal.id)
            editor.putString("id_movie", jadwal.id_movie)
            editor.putString("id_studio", jadwal.id_studio)
            editor.putString("harga", jadwal.harga)
            editor.commit()

            var intent = Intent(context, KursiActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        return view
    }
}