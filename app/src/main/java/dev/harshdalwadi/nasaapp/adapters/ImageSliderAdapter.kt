package dev.harshdalwadi.nasaapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.databinding.RowImageDetailBinding
import dev.harshdalwadi.nasaapp.di.modules.GlideApp
import dev.harshdalwadi.nasaapp.models.RespNasaDataItem


@Suppress("DEPRECATION")
class ImageSliderAdapter(private val context: Context, private val list: List<RespNasaDataItem>) : PagerAdapter() {
    private var inflater: LayoutInflater? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {

        return view === `object`
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding: RowImageDetailBinding = DataBindingUtil.inflate(inflater!!, R.layout.row_image_detail, container, false)
        val model = list[position]

        binding.apply {
            GlideApp.with(context)
                .load(model.url)
                .thumbnail(0.01f)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.logo_nasa)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .into(ivImage)

            tvImageTitle.text = model.title
            tvImageDesc.text = model.explanation

        }

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        val vp = container as ViewPager
        val view = `object` as View
        vp.removeView(view)
    }

}