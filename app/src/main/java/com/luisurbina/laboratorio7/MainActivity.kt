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
            startActivityForResult(
                Intent(this, AddEditContactActivity::class.java),
                ADD_CONTACT_REQUEST
            )
        }

        recycler_view.layoutManager = LinearLayoutManager(this)
        recycler_view.setHasFixedSize(true)

        val adapter = ContactAdapter()

        recycler_view.adapter = adapter

        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        contactViewModel.getAllContacts().observe(this, Observer<List<Contact>> {
            adapter.submitList(it)
        })

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT.or(ItemTouchHelper.RIGHT)) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                contactViewModel.delete(adapter.getContactAt(viewHolder.adapterPosition))
                Toast.makeText(baseContext, "Contact Deleted!", Toast.LENGTH_SHORT).show()
            }
        }
        ).attachToRecyclerView(recycler_view)

        adapter.setOnItemClickListener(object : ContactAdapter.OnItemClickListener {
            override fun onItemClick(contact: Contact) {
                val intent = Intent(baseContext, ViewContactActivity::class.java)
                intent.putExtra(SAVED_CONTACT_ID, contact.id)
                intent.putExtra(SAVED_CONTACT_NAME, contact.name)
                intent.putExtra(SAVED_CONTACT_EMAIL, contact.email)
                intent.putExtra(SAVED_CONTACT_PRIORITY, contact.priority)
                intent.putExtra(SAVED_CONTACT_PHONE, contact.phone)
                intent.putExtra(SAVED_CONTACT_PHOTO, contact.photo)

                startActivityForResult(intent, SHOW_CONTACT_REQUEST)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.delete_all_contacts -> {
                contactViewModel.deleteAllContacts()
                Toast.makeText(this, "All Notes Deleted!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            val newContact = Contact(
                data!!.getStringExtra(SAVED_CONTACT_NAME),
                data.getStringExtra(SAVED_CONTACT_PHONE),
                data.getStringExtra(SAVED_CONTACT_EMAIL),
                data.getIntExtra(SAVED_CONTACT_PRIORITY, 1)
            )
            newContact.photo = data.getByteArrayExtra(SAVED_CONTACT_PHOTO)

            contactViewModel.insert(newContact)

            Toast.makeText(this, "Contact Saved", Toast.LENGTH_SHORT).show()
        } else if (requestCode == SHOW_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            Toast.makeText(this, "All up to date", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Nothing pending", Toast.LENGTH_SHORT).show()
        }

    }
}

