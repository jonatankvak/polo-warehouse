package com.polo.warehouse.application

import android.app.Application
import com.polo.authentication.di.AuthenticationModule
import com.polo.authentication.di.module as authenticationModule
import com.polo.dashboard.di.DashboardModule
import com.polo.dashboard.di.module as dashboardModule
import com.polo.data.DataModule
import com.polo.data.module as dataModule
import com.polo.firebase.FirebaseModule
import com.polo.firebase.module as firebaseModule
import com.polo.pallet.di.PalletModule
import com.polo.pallet.di.module as palletModule
import com.polo.scanner.di.ScannerModule
import com.polo.scanner.di.module as scannerModule
import com.polo.warehouse.di.AppModule
import com.polo.warehouse.di.module as appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.startKoin

@KoinApplication
class WarehouseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@WarehouseApplication)
            modules(
                AppModule().appModule(),
                DataModule().dataModule(),
                FirebaseModule().firebaseModule(),
                AuthenticationModule().authenticationModule(),
                DashboardModule().dashboardModule(),
                PalletModule().palletModule(),
                ScannerModule().scannerModule()
            )
        }
    }
}
