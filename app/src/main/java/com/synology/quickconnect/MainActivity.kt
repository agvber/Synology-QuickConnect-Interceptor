package com.synology.quickconnect

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.synology.quickconnect.ui.theme.SynologyQuickconnectAndroidTheme
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

private const val QUICK_CONNECT_ID = "example_id"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(QuickConnectInterceptor())
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .baseUrl("https://$QUICK_CONNECT_ID")
            .client(okHttpClient)
            .build()

        val service = retrofit.create(NetworkDataSource::class.java)

        setContent {
            SynologyQuickconnectAndroidTheme {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = {
                            lifecycleScope.launch {
                                Log.d("MainActivity", service.getApiInfo())
                            }
                        }
                    ) {
                        Text("Click me!")
                    }
                }
            }
        }
    }
}

private interface NetworkDataSource {

    @GET("/webapi/query.cgi")
    suspend fun getApiInfo(
        @Header(QuickConnectInterceptor.QUICK_CONNECT_HEADER) header: String = "true",
        @Query("api") api: String = "SYNO.API.Info",
        @Query("version") version: Int = 1,
        @Query("method") method: String = "query",
        @Query("query") query: String = "all",
    ): String
}