package com.thinhnk.fastapp.data

const val DEFAULT_LIST_ID = "saved_default"

suspend fun SavedDao.ensureDefaultList(): SavedListEntity {
    firstList()?.let { return it }
    val list = SavedListEntity(
        id = DEFAULT_LIST_ID,
        name = "Trạm đã lưu",
        createdAt = System.currentTimeMillis(),
    )
    insertList(list)
    return findList(DEFAULT_LIST_ID) ?: list
}
