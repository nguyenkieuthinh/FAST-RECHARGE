package com.thinhnk.fastapp.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Hồ sơ người dùng trên Firestore. Khóa là uid nên cùng tài khoản
 * đăng nhập ở máy nào cũng đọc ghi cùng một tài liệu.
 *
 * Giai đoạn 1 chỉ lưu hồ sơ và cài đặt; danh sách đã lưu còn ở Room.
 */
class UserProfileRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    suspend fun ensureProfile(user: FirebaseUser) {
        val doc = firestore.collection("users").document(user.uid)
        val snapshot = doc.get().await()

        if (!snapshot.exists()) {
            doc.set(
                mapOf(
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: ""),
                    "providers" to user.providerData.map { it.providerId },
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp(),
                    "settings" to mapOf(
                        "lang" to "vi",
                        "units" to "km",
                        "theme" to "system",
                        "navApp" to "google",
                        "favAlerts" to false,
                    ),
                ),
            ).await()
        } else {
            doc.update(
                mapOf(
                    "email" to (user.email ?: ""),
                    "displayName" to (user.displayName ?: ""),
                    "providers" to user.providerData.map { it.providerId },
                    "updatedAt" to FieldValue.serverTimestamp(),
                ),
            ).await()
        }
    }
}
