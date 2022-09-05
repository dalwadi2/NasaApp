package dev.harshdalwadi.nasaapp.di.modules

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import dev.harshdalwadi.nasaapp.BuildConfig

@GlideModule
class MyAppGlideModule : AppGlideModule() {

    @SuppressLint("CheckResult")
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        if (BuildConfig.DEBUG) {
            builder.setLogLevel(Log.ERROR)
        }
        builder.apply {
            RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)
                .signature(ObjectKey(System.currentTimeMillis().toShort()))
        }
        super.applyOptions(context, builder)
    }

}
