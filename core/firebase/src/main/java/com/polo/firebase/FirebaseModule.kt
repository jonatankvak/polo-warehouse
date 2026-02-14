package com.polo.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@Configuration
@ComponentScan("com.polo.firebase")
class FirebaseModule {

    @Single
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Single
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }
}
