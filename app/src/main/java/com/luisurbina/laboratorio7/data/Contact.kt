package com.luisurbina.laboratorio7.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "contact_table")
data class Contact(
    var name: String,
    var phone: String,
    var email: String,
    var priority: Int = 1
){
    //it doesn´t matter wether they´re private or not

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var photo: ByteArray? = null
}