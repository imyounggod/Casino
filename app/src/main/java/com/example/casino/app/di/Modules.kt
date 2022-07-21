package com.example.casino.app.di

import com.example.casino.data.contract.DataManager
import com.example.casino.data.contract.FirebaseRepo
import com.example.casino.data.contract.ServerRepo
import com.example.casino.data.impl.data_manager.DataManagerImpl
import com.example.casino.data.impl.firebase.FirebaseRepoImpl
import com.example.casino.data.impl.server.impl.ServerRepoImpl
import com.example.casino.data.impl.server.odds_api.ApiConstants
import com.example.casino.data.impl.server.odds_api.OddsApi
import com.example.casino.data.impl.server.odds_api.interceptors.ApiKeyInterceptor
import com.example.casino.ui.casino.CasinoViewModel
import com.example.casino.ui.roulettes.RoulettesViewModel
import com.example.casino.ui.slots.SlotsViewModel
import com.example.casino.ui.sport.SportViewModel
import com.example.casino.ui.table_games.TableGamesViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {
    viewModel { CasinoViewModel() }
    viewModel { SlotsViewModel(get()) }
    viewModel { TableGamesViewModel(get()) }
    viewModel { RoulettesViewModel(get()) }
    viewModel { SportViewModel(get()) }
}

val networkModule = module {

    fun provideOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .build()
    }

    fun provideGsonConverterFactory(): Converter.Factory {
        return GsonConverterFactory.create(
            GsonBuilder()
                .setLenient()
                .create()
        )
    }

    fun provideRetrofit(okHttpClient: OkHttpClient, converterFactory: Converter.Factory): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(converterFactory)
            .build()
    }

    fun provideApiKeyInterceptor(): ApiKeyInterceptor {
        return ApiKeyInterceptor()
    }

    single { provideOkHttpClient(get()) }
    single { provideGsonConverterFactory() }
    single { provideApiKeyInterceptor() }
    single { provideRetrofit(get(), get()) }
}

val oddsApiModule = module {

    fun provideOddsApi(retrofit: Retrofit): OddsApi {
        return retrofit.create(OddsApi::class.java)
    }

    single { provideOddsApi(get()) }
}

val repositoryModule = module {

    fun provideServerRepo(oddsApi: OddsApi): ServerRepo {
        return ServerRepoImpl(oddsApi)
    }

    fun provideFirebaseRepo(db: FirebaseFirestore): FirebaseRepo {
        return FirebaseRepoImpl(db)
    }

    fun provideDataManager(
        serverRepo: ServerRepo,
        firebaseRepo: FirebaseRepo
    ): DataManager {
        return DataManagerImpl(serverRepo, firebaseRepo)
    }

    single { Firebase.firestore }
    single { provideServerRepo(get()) }
    single { provideFirebaseRepo(get()) }
    single { provideDataManager(get(), get()) }
}
