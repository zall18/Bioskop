package com.example.bioskop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class filmAdapter(var context: Context, var data: MutableList<filmModel>): PagerAdapter() {
    var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var imageLoader = MainScope()
    override fun getCount(): Int {
        return data.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view = inflater.inflate(R.layout.filmitem, null)

        var image = view.findViewById<ShapeableImageView>(R.id.image_film)
        var durasi = view.findViewById<TextView>(R.id.durasi_film)
        var rating = view.findViewById<TextView>(R.id.rating_film)
        var item = view.findViewById<RelativeLayout>(R.id.item)

        var film = data[position] as filmModel

        imageLoader.launch {
            var bitmap = getImageFromUrl("https://mayfly-stable-remarkably.ngrok-free.app/storage/thumbnail/" + film.image)
            image.setImageBitmap(bitmap)
        }
        durasi.text = film.duration
        rating.text = film.rating

        item.setOnClickListener {
            var intent = Intent(context, DetailFilmActivity::class.java)
            intent.putExtra("id", film.id)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

        container.addView(view)
        return view

    }
}