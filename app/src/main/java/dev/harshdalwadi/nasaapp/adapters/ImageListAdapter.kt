package dev.harshdalwadi.nasaapp.adapters

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dev.harshdalwadi.nasaapp.R
import dev.harshdalwadi.nasaapp.base.BaseRecyclerViewAdapter
import dev.harshdalwadi.nasaapp.databinding.RowImageBinding
import dev.harshdalwadi.nasaapp.di.modules.GlideApp
import dev.harshdalwadi.nasaapp.models.RespNasaDataItem
import dev.harshdalwadi.nasaapp.utils.extensions.safeClickListener

@Suppress("DEPRECATION")
class ImageListAdapter(
    private val clickCallback: (position: Int, nasaDataItem: RespNasaDataItem?) -> Unit,
) :
    BaseRecyclerViewAdapter<RowImageBinding>() {
    private var modelList: List<RespNasaDataItem>? = ArrayList()

    fun updateList(modelList: List<RespNasaDataItem>?) {
        this.modelList = modelList
        notifyDataSetChanged()
    }

    override fun setLayoutItem() = R.layout.row_image

    override fun setCount() = modelList?.size ?: 0

    override fun onViewReady(holder: ViewHolder<RowImageBinding>, position: Int) {
        val model = modelList?.get(position)

        holder.apply {

            GlideApp.with(mContext)
                .load(model?.url)
                .thumbnail(0.01f)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.progress_animation)
                .error(R.drawable.logo_nasa)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .into(holder.mBinding.ivNasa)

            mBinding.ivNasa.safeClickListener {
                clickCallback.invoke(position, getItem(position))
            }
        }
    }

    private fun getItem(position: Int): RespNasaDataItem? {
        return modelList?.get(position)
    }
}
