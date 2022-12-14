package com.example.core.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.core.BuildConfig
import com.example.core.data.local.IDataStore
import com.example.core.data.local.PreferencesDataStoreHelper
import com.example.core.data.remote.services.DicodingStoryApiService
import com.example.core.data.utils.NetworkInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoreModule {
    @Provides
    @Singleton
    fun providesBaseUrl(): String {
        return "https://story-api.dicoding.dev/v1/"
    }

    @Provides
    @Singleton
    fun providesOkHttpClient(
        @ApplicationContext context: Context,
        authInterceptor: NetworkInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
        try {
            builder.addNetworkInterceptor(authInterceptor)
            if (BuildConfig.DEBUG) {
                builder.addInterceptor(HttpLoggingInterceptor().setLevel(level = HttpLoggingInterceptor.Level.BODY))
                builder.addNetworkInterceptor(
                    ChuckerInterceptor.Builder(context)
                        .collector(ChuckerCollector(context))
                        .maxContentLength(250000L)
                        .redactHeaders(emptySet())
                        .alwaysReadResponseBody(false)
                        .build()
                )
            }
            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(baseUrl)
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): DicodingStoryApiService =
        retrofit.create(DicodingStoryApiService::class.java)
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): IDataStore {
        return PreferencesDataStoreHelper(context)
    }


}
