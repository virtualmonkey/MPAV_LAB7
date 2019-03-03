package com.luisurbina.laboratorio7

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.luisurbina.laboratorio7.data.Contact
import com.luisurbina.laboratorio7.viewmodels.ContactViewModel
import kotlinx.android.synthetic.main.activity_view_contact.*

class ViewContactActivity : AppCompatActivity() {

    companion object {
        const val EDIT_CONTACT_REQUEST = 3
    }
    private lateinit var contactViewModel: ContactViewModel
    private var currentPhoto: ByteArray? = null
    private var currentId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_contact)

        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)
        val priorityString = "Priority: " + intent.getIntExtra("savedContactPriority", 1).toString()

        text_view_contact_priority.text = priorityString
        text_view_contact_name.text = intent.getStringExtra("savedContactName")
        text_view_contact_phone.text = intent.getStringExtra("savedContactPhone")
        text_view_contact_email.text = intent.getStringExtra("savedContactEmail")

        currentId = intent.getIntExtra("savedContactId", 1)
        currentPhoto = intent.getByteArrayExtra("savedContactPhoto")

        if (currentPhoto != null){
            Glide.with(this).load(currentPhoto).into(view_contact_image_view)
        }



        btn_back_to_main.setOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.view_contact_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.edit_contact ->{
                redirectToEditActivity()
                Toast.makeText(this, "Redirecting to Edit Screen!", Toast.LENGTH_SHORT).show()
                true
            }else ->{
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun redirectToEditActivity(){
        var intent = Intent(baseContext, AddEditContactActivity::class.java)
        intent.putExtra("savedContactId", currentId)
        intent.putExtra("savedContactName", text_view_contact_name.text.toString())
        intent.putExtra("savedContactEmail", text_view_contact_email.text.toString())
        intent.putExtra("savedContactPriority", text_view_contact_priority.text.toString())
        intent.putExtra( "savedContactPhone",text_view_contact_phone.text.toString())
        intent.putExtra("savedContactPhoto", currentPhoto)


        startActivityForResult(intent, EDIT_CONTACT_REQUEST)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == EDIT_CONTACT_REQUEST && resultCode == Activity.RESULT_OK){

            val contactToUpdate = Contact(
                data!!.getStringExtra("savedContactName"),
                data.getStringExtra("savedContactPhone"),
                data.getStringExtra("savedContactEmail"),
                data.getIntExtra("savedContactPriority", 1)
            )

            contactToUpdate.photo = data.getByteArrayExtra("savedContactPhoto")
            contactToUpdate.id = data.getIntExtra("savedContactId", -1)

            contactViewModel.update(contactToUpdate)

            text_view_contact_priority.text = contactToUpdate.priority.toString()
            text_view_contact_name.text = contactToUpdate.name
            text_view_contact_phone.text = contactToUpdate.phone
            text_view_contact_email.text = contactToUpdate.email

            currentId = contactToUpdate.id
            currentPhoto = contactToUpdate.photo!!

            Toast.makeText(this, "Contact updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Error updating contact", Toast.LENGTH_SHORT).show()
        }
    }
}
