package com.raymondHariyono.playcut

import android.app.Application
import com.raymondHariyono.playcut.data.seeder.AdminAccountSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PlayCutApp : Application() {
    @Inject // Hilt akan menyuntikkan seeder yang sudah kita sediakan di AppModule
    lateinit var adminSeeder: AdminAccountSeeder

    override fun onCreate() {
        super.onCreate()

        // Menjalankan seeder di background thread agar tidak memblokir UI
        CoroutineScope(Dispatchers.IO).launch {
            adminSeeder.seedAdminAccounts()
        }
    }
}