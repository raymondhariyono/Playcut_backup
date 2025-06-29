package com.raymondHariyono.playcut

import android.app.Application
import com.raymondHariyono.playcut.data.seeder.AdminAccountSeeder
import com.raymondHariyono.playcut.data.seeder.FirestoreSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class PlayCutApp : Application() {
    @Inject
    lateinit var adminSeeder: AdminAccountSeeder

    @Inject
    lateinit var dataSeeder: FirestoreSeeder

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {
            dataSeeder.seedDataIfNeeded()
            adminSeeder.seedAdminAccounts()
        }
    }
}