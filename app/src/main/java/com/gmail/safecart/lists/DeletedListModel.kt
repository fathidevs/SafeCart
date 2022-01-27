package com.gmail.safecart.lists


class DeletedListModel(
    val id: Int,
    val deletedListId: Int,
    val deletedListName: String,
    val deletedListPosition: Int,
    val deletedAt: String,
    val removeAt: String
)