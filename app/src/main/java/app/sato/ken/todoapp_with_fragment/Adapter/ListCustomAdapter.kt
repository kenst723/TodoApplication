package app.sato.ken.todoapplication.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import app.sato.ken.todoapp_with_fragment.R
import app.sato.ken.todoapp_with_fragment.data.Images

class ListCustomAdapter(context: Context, var mAnimalList: ArrayList<Images>) : ArrayAdapter<Images>(context, 0, mAnimalList){

    private val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Animalの取得
        val animal = mAnimalList[position]

        // レイアウトの設定
        var view = convertView
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.listview_item, parent, false)
        }

        // 各Viewの設定
        val imageView = view?.findViewById<ImageView>(R.id.imageView)
        imageView?.setImageResource(animal.imageId)

        val name = view?.findViewById<TextView>(R.id.name)
        name?.text = animal.name

        return view!!
    }

}