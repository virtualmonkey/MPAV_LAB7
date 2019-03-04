package com.luisurbina.laboratorio7.data

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class ContactRepository(application: Application) {

    //instance of the ContactDao object
    private var contactDao: ContactDao

    //list that will be updated automatically by listening to the data in the database
    private var allContacts: LiveData<List<Contact>>

    init {
        //database instance in the actual application context
        val database: ContactDatabase = ContactDatabase.getInstance(
            application.applicationContext
        )!!
        contactDao = database.contactDao()
        allContacts = contactDao.getAllContacts()
    }

    fun insert(contact: Contact){
        val insertContactAsyncTask = InsertContactAsyncTask(contactDao).execute(contact)
    }
    fun update(contact: Contact){
        val updateContactAsyncTask = UpdateContactAsyncTask(contactDao).execute(contact)
    }
    fun delete(contact: Contact){
        val deleteContactAsyncTask = DeleteContactAsyncTask(contactDao).execute(contact)
    }
    fun deleteAllContacts(){
        val deleteAllContactsAsyncTaks = DeleteAllContactsAsyncTask(
            contactDao
        ).execute()
    }
    fun getAllContacts(): LiveData<List<Contact>> {
        return allContacts
    }

    companion object {
        //Here are all the declarations of the async tasks methods to operate de database
        //using the contactDao object that will modify data in the contact_table entity.
        private class InsertContactAsyncTask(contactDao: ContactDao) : AsyncTask<Contact, Unit, Unit>(){
            val contactDao = contactDao

            override fun doInBackground(vararg p0: Contact?) {
                contactDao.insert(p0[0]!!)
            }
        }

        private class UpdateContactAsyncTask(contactDao: ContactDao) : AsyncTask<Contact, Unit, Unit>(){
            val contactDao = contactDao
            override fun doInBackground(vararg p0: Contact?) {
                contactDao.update(p0[0]!!)
            }
        }

        private class DeleteContactAsyncTask(contactDao: ContactDao) : AsyncTask<Contact, Unit, Unit>(){
            val contactDao = contactDao
            override fun doInBackground(vararg p0: Contact?) {
                contactDao.delete(p0[0]!!)
            }
        }

        private class  DeleteAllContactsAsyncTask(contactDao: ContactDao) : AsyncTask<Unit, Unit, Unit>() {
            val contactDao = contactDao
            override fun doInBackground(vararg p0: Unit?) {
                contactDao.deleteAllContacts()
            }
        }

    }
}