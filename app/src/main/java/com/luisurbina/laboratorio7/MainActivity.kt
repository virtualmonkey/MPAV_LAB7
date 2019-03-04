package com.luisurbina.laboratorio7

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.luisurbina.laboratorio7.adapters.ContactAdapter
import com.luisurbina.laboratorio7.data.Contact
import com.luisurbina.laboratorio7.viewmodels.ContactViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var contactViewModel: ContactViewModel

    //Constants for the intents's extras
    companion object {
        const val SAVED_CONTACT_ID = "savedContactId"
        const val SAVED_CONTACT_NAME = "savedContactName"
        const val SAVED_CONTACT_PHONE = "savedContactPhone"
        const val SAVED_CONTACT_EMAIL = "savedContactEmail"
        const val SAVED_CONTACT_PRIORITY = "savedContactPriority"
        const val SAVED_CONTACT_PHOTO = "savedContactPhoto"
        const val ADD_CONTACT_REQUEST = 1
        const val SHOW_CONTACT_REQUEST = 2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonAddContact.setOnClickListener {
            //starts AddEditContactActivity in Add contact Mode
            startActivityForResult(
                Intent(this, AddEditContactActivity::class.java),
                ADD_CONTACT_REQUEST
            )
        }

        //sets a linearLayoutManager to te recycler_view
        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        //instance of the recyclerView Adapter
        val adapter = ContactAdapter()

        //set adapter for the recyclerView
        recycler_view.adapter = adapter


        //decalration of the viewModel that will be used along the lifecycle of the app
        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        //get all the contacts in the database and set in the recyclerView via the submitList method in the adapter
        contactViewModel.getAllContacts().observe(this, Observer<List<Contact>> {
            adapter.submitList(it)
        })


        //Set an ItemTouch helper to the recyclerView
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            //When the element is swiped left or right, the delete method of contactViewModel will be called with
            //the contact at the current ViewHolder position
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                contactViewModel.delete(adapter.getContactAt(viewHolder.adapterPosition))
                Toast.makeText(baseContext, "Contact Deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        ).attachToRecyclerView(recycler_view)


        adapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {

            //when the user selects an itme by clicking It
            override fun onItemClick(contact: Contact) {
                //a new intent is instantiated and the extras are passed to fill the editTexts in ViewContact Activity
                val intent = Intent(baseContext, ViewContactActivity::class.java)
                intent.putExtra(SAVED_CONTACT_ID, contact.id)
                intent.putExtra(SAVED_CONTACT_NAME, contact.name)
                intent.putExtra(SAVED_CONTACT_EMAIL, contact.email)
                intent.putExtra(SAVED_CONTACT_PRIORITY, contact.priority)
                intent.putExtra(SAVED_CONTACT_PHONE, contact.phone)
                intent.putExtra(SAVED_CONTACT_PHOTO, contact.photo)

                //start Activity and wait for the result
                startActivityForResult(intent, SHOW_CONTACT_REQUEST)
            }
        })
    }

    //bind the options in the toolbar menu with the main_menu layout file
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.delete_all_contacts -> {
                //when user selects delete_all_contacts option, all contacts will be deleted from the database
                //by invoking the deleteAllContacts() method from the contactViewModel
                contactViewModel.deleteAllContacts()
                Toast.makeText(this, "All Notes Deleted!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }


    //Catch the result of the StartActivityForResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //If the user previously edited or created a new contact succesfullt
        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            //instance of the new or updated contact
            val newContact = Contact(
                data!!.getStringExtra(SAVED_CONTACT_NAME),
                data.getStringExtra(SAVED_CONTACT_PHONE),
                data.getStringExtra(SAVED_CONTACT_EMAIL),
                data.getIntExtra(SAVED_CONTACT_PRIORITY, 1)
            )
            newContact.photo = data.getByteArrayExtra(SAVED_CONTACT_PHOTO)
            //insert the new contact in the database
            contactViewModel.insert(newContact)

            Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show()
        }
        //if user only saw the contact but did´t make changes, just show a toast
        else if (requestCode == SHOW_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "All up to date", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nothing pending", Toast.LENGTH_SHORT).show()
        }

    }
}

