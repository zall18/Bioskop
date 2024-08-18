package com.example.bioskop

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView

class KursiAdapter(var context: Context, var data: MutableList<KursiModel>, var dataKursi: MutableList<String>): BaseAdapter() {
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
        var view = convertView ?: inflater.inflate(R.layout.kursiitem, null, false)

        Log.d("data2", "getView: $data")
        var no = view.findViewById<TextView>(R.id.no)
        var kursi = getItem(position) as KursiModel
        var item = view.findViewById<LinearLayout>(R.id.kursi)
        no.text = kursi.posisi
        item.setOnClickListener {
                dataKursi.add(no.text.toString())
                item.setBackgroundResource(R.color.darkBlue2)
        }

        return view
    }
}