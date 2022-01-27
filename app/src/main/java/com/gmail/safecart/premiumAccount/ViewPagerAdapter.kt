package com.gmail.safecart.premiumAccount

import android.content.Context
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager.widget.PagerAdapter
import com.gmail.safecart.R

class ViewPagerAdapter(
    private val context: Context,
    private val titles: Array<String>,
    private val descriptions: Array<String>,
    private val icons: Array<Int>
): PagerAdapter() {
    override fun getCount(): Int {
        return icons.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val view = LayoutInflater.from(context).inflate(R.layout.slide_item, container, false)

        val img: ImageView = view.findViewById(R.id.sliderIconIv)
        val description: TextView = view.findViewById(R.id.sliderDescriptionTv)
        val title: TextView = view.findViewById(R.id.sliderTitleTv)

        title.text = titles[position]
        description.text = descriptions[position]
        img.setImageDrawable(ResourcesCompat.getDrawable(context.resources, icons[position], null))

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }
}