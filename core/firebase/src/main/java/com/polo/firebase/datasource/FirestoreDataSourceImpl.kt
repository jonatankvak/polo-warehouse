package com.polo.firebase.datasource

import android.content.res.Resources.NotFoundException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction.DESCENDING
import com.google.firebase.firestore.toObjects
import com.polo.data.model.CreatePallet
import com.polo.data.model.CreatePallet.PalletStatus
import com.polo.data.model.PalletDocument
import com.polo.data.model.ProductDocument
import com.polo.data.model.WarehouseDocument
import com.polo.data.datasource.FirestoreDataSource
import com.polo.domain.functional.runSuspendCatching
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreDataSource {

    override suspend fun updatePalletStatus(palletUid: String, status: PalletStatus): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("pallet")
                    .document(palletUid)
                    .update(mapOf("status" to status.name))
                    .await()
                Unit
            }
        }
    }

    override suspend fun updatePalletStatus(
        palletUid: String,
        status: PalletStatus,
        warehouseUid: String
    ): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("pallet")
                    .document(palletUid)
                    .update(mapOf("status" to status.name, "warehouseUid" to warehouseUid))
                    .await()
                Unit
            }
        }
    }

    override suspend fun getAllProductsAndWarehouses(): Result<Pair<List<ProductDocument>, List<WarehouseDocument>>> =
        withContext(Dispatchers.IO) {
            runSuspendCatching {
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

    override suspend fun getAllPallets(status: PalletStatus): Flow<Result<List<PalletDocument>>> {
        return callbackFlow {
            val listener = firestore.collection("pallet")
                .whereEqualTo("status", status.name)
                .orderBy("date", DESCENDING)
                .addSnapshotListener { value, error ->
                    value?.let {
                        trySend(
                            Result.success(value.toObjects())
                        )
                    }

                    error?.let {
                        trySend(
                            Result.failure(error)
                        )
                    }
                }

            awaitClose {
                listener.remove()
            }
        }
    }

    override suspend fun getPallet(palletUid: String): Result<PalletDocument> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("pallet")
                    .document(palletUid)
                    .get()
                    .await()
                    .toObject(PalletDocument::class.java) ?: throw NotFoundException("Pallet was not found")
            }
        }
    }

    override suspend fun getProducts(productUid: String): Result<ProductDocument> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("product")
                    .document(productUid)
                    .get()
                    .await()
                    .toObject(ProductDocument::class.java) ?: throw NotFoundException("Pallet was not found")
            }
        }
    }

    override suspend fun getWarehouse(warehouseUid: String): Result<WarehouseDocument> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("warehouse")
                    .document(warehouseUid)
                    .get()
                    .await()
                    .toObject(WarehouseDocument::class.java) ?: throw NotFoundException("Pallet was not found")
            }
        }
    }

    override suspend fun createPallets(pallet: CreatePallet): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("pallet")
                    .document()
                    .set(pallet)
                    .await()
                Unit
            }
        }
    }

    override suspend fun deletePallet(palletUid: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore
                    .collection("pallet")
                    .document(palletUid)
                    .delete()
                    .await()
                Unit
            }
        }
    }

    override suspend fun getAllProducts(): Result<List<ProductDocument>> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("product")
                    .orderBy("name")
                    .get()
                    .await()
                    .toObjects(ProductDocument::class.java)
            }
        }
    }

    override suspend fun queryForProduct(query: String): Result<List<ProductDocument>> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
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

    override suspend fun getAllWarehouses(): Result<List<WarehouseDocument>> {
        return withContext(Dispatchers.IO) {
            runSuspendCatching {
                firestore.collection("warehouse")
                    .orderBy("name")
                    .get()
                    .await()
                    .toObjects(WarehouseDocument::class.java)
            }
        }
    }
}
