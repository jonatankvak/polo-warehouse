package com.polo.data.datasource

import android.content.res.Resources.NotFoundException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.firestore.ktx.toObjects
import com.polo.data.functional.Either
import com.polo.data.functional.runCatchingEither
import com.polo.data.model.CreatePallet
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.ProductDocument
import com.polo.data.model.PalletDocument
import com.polo.data.model.WarehouseDocument
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
): IFireStoreDataSource {

    override suspend fun updatePalletStatus(palletUid: String, status: PalletStatus): Either<Exception, Unit> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("pallet")
                    .document(palletUid)
                    .update(mapOf("status" to status.name))
                    .await()
                    .let {}
            }
        }
    }

    override suspend fun updatePalletStatus(
        palletUid: String,
        status: PalletStatus,
        warehouseUid: String
    ): Either<Exception, Unit> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("pallet")
                    .document(palletUid)
                    .update(mapOf("status" to status.name, "warehouseUid" to warehouseUid))
                    .await()
                    .let {}
            }
        }
    }

    override suspend fun getAllProductsAndWarehouses(): Either<Exception, Pair<List<ProductDocument>, List<WarehouseDocument>>>
        = withContext(Dispatchers.IO) {
             runCatchingEither {
                val (productSnapshot, warehouseSnapshot) = listOf(
                    firestore.collection("product").get().asDeferred(),
                    firestore.collection("warehouse").get().asDeferred()
                ).awaitAll()

                Pair(
                    productSnapshot.toObjects(ProductDocument::class.java),
                    warehouseSnapshot.toObjects(WarehouseDocument::class.java)
                )
            }
    }

    override suspend fun getAllPallets(status: PalletStatus): Flow<Either<Exception, List<PalletDocument>>> {
        return callbackFlow {
            val listener = firestore.collection("pallet")
                .whereEqualTo("status", status.name)
                .orderBy("date", DESCENDING)
                .addSnapshotListener { value, error ->
                    value?.let {
                        trySend(
                            Either.Result(value.toObjects())
                        )
                    }

                    error?.let {
                        trySend(
                            Either.Error(error)
                        )
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    override suspend fun getPallet(palletUid: String): Either<Exception, PalletDocument> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("pallet")
                    .document(palletUid)
                    .get()
                    .await()
                    .toObject(PalletDocument::class.java) ?: throw NotFoundException("Pallet was not found")
            }
        }
    }

    override suspend fun getProducts(productUid: String): Either<Exception, ProductDocument> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("product")
                    .document(productUid)
                    .get()
                    .await()
                    .toObject(ProductDocument::class.java) ?: throw NotFoundException("Pallet was not found")
            }
        }
    }

    override suspend fun getWarehouse(warehouseUid: String): Either<Exception, WarehouseDocument> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("warehouse")
                    .document(warehouseUid)
                    .get()
                    .await()
                    .toObject(WarehouseDocument::class.java) ?: throw NotFoundException("Pallet was not found")
            }
        }
    }

    override suspend fun createPallets(pallet: CreatePallet): Either<Exception, Unit> {
        return withContext(Dispatchers.IO) {
             runCatchingEither {
                    firestore.collection("pallet")
                        .document()
                        .set(pallet)
                        .await()
                        .let{}
            }
        }
    }

    override suspend fun deletePallet(palletUid: String): Either<Exception, Unit> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore
                    .collection("pallet")
                    .document(palletUid)
                    .delete()
                    .await()
                    .let{}
            }
        }
    }

    override suspend fun getAllProducts(): Either<Exception, List<ProductDocument>> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("product")
                    .orderBy("name")
                    .get()
                    .await()
                    .toObjects(ProductDocument::class.java)
            }
        }
    }

    override suspend fun queryForProduct(query: String): Either<Exception, List<ProductDocument>> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("product")
                    .orderBy("name")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThan("name", "$query~")
                    .get()
                    .await()
                    .toObjects()
            }
        }
    }

    override suspend fun getAllWarehouses(): Either<Exception, List<WarehouseDocument>> {
        return withContext(Dispatchers.IO) {
            runCatchingEither {
                firestore.collection("warehouse")
                    .orderBy("name")
                    .get()
                    .await()
                    .toObjects(WarehouseDocument::class.java)
            }
        }
    }
}