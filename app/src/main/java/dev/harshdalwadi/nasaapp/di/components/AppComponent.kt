package dev.harshdalwadi.nasaapp.di.components

import android.app.Application
import android.content.pm.PackageInfo
import android.os.Build
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dev.harshdalwadi.nasaapp.MyApp
import dev.harshdalwadi.nasaapp.api.ApiServices
import dev.harshdalwadi.nasaapp.api.URLFactory
import dev.harshdalwadi.nasaapp.utils.JsonLogger
import dev.harshdalwadi.nasaapp.utils.PreferenceHelper
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal class AppComponent {

    @Singleton
    @Provides
    fun application(): MyApp {
        return MyApp.getInstance()!!
    }


    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }


    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(URLFactory.provideHttpUrl())
            .build()
    }

    @Provides
    @Singleton
    fun okHttpClient(
        cache: Cache,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
        preferenceHelper: PreferenceHelper,
    ): OkHttpClient {
        val pInfo: PackageInfo = application().packageManager.getPackageInfo(application().packageName, 0)
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(object : Interceptor {
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original = chain.request()
                    val builder = original.newBuilder().method(original.method, original.body)
                    if (preferenceHelper.loadAuthKey().toString().isNotEmpty())
                        builder.header("Authorization", "Bearer " + preferenceHelper.loadAuthKey().toString())
                    builder.header("device-name", Build.MODEL)
                    builder.header("device-os", "Android")
                    builder.header("device-os-version", Build.VERSION.RELEASE)
                    builder.header("app-version", pInfo.versionName)
                    return chain.proceed(builder.build())
                }
            })
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(chuckerInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun providesChkkerInterceptor(application: Application): ChuckerInterceptor {
        val chuckerCollector = ChuckerCollector(
            context = application,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        val chuckerInterceptor = ChuckerInterceptor.Builder(application)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .alwaysReadResponseBody(true)
            .build()
        return chuckerInterceptor
    }

    @Provides
    @Singleton
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor(JsonLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun gsonConverterFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().setPrettyPrinting().setVersion(1.0).create()
    }

    @Provides
    @Singleton
    fun cache(file: File): Cache {
        return Cache(file, (10 * 1000 * 1000).toLong()) // 10 mb cache file
    }

    @Provides
    @Singleton
    fun file(application: Application): File {
        val file = File(application.cacheDir, "OkHttpCache")
        file.mkdirs()
        return file
    }


}