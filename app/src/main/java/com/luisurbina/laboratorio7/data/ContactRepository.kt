package com.luisurbina.laboratorio7.data

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData

class ContactRepository(application: Application) {
    private var contactDao: ContactDao

    private var allContacts: LiveData<List<Contact>>

    init {
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