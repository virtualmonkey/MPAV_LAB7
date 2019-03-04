package com.luisurbina.laboratorio7.data

import androidx.lifecycle.LiveData
import androidx.room.*


//Interface with all the operations to use over the database
//it will be implemented by the ContactDatabase to operate over de data.
@Dao
interface ContactDao {
    @Insert
    fun insert(contact: Contact)

    @Update
    fun update(contact: Contact)

    @Delete
    fun delete(contact: Contact)


    @Query("DELETE FROM contact_table")
    fun deleteAllContacts()

    @Query("SELECT * FROM contact_table ORDER BY priority DESC")
    fun getAllContacts(): LiveData<List<Contact>>
}