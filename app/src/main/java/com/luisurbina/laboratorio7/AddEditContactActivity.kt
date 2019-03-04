package com.luisurbina.laboratorio7

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.luisurbina.laboratorio7.viewmodels.ContactViewModel
import kotlinx.android.synthetic.main.activity_add_edit_contact.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddEditContactActivity : AppCompatActivity() {
    companion object {
        private const val EXTERNAL_WRITE_REQUEST_CODE = 112
        private const val GALLERY = 1
    }
    private lateinit var contactViewModel: ContactViewModel
    private var currentId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_contact)

        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        number_picker_priority.minValue = 1
        number_picker_priority.maxValue = 10

        //get the currentContactId (in case the user selected an item of the recyclerView) this will be different from -1
        val currentContactId = intent.getIntExtra("savedContactId", -1)

        //in case the contact already exists
        if (currentContactId != -1){
            //fill the editTexts and imageView with the currentContact Data
            edit_text_edit_contact_name.setText(intent.getStringExtra("savedContactName"))
            edit_text_edit_contact_phone.setText(intent.getStringExtra("savedContactPhone"))
            edit_text_edit_contact_email.setText(intent.getStringExtra("savedContactEmail"))
            number_picker_priority.value = intent.getIntExtra("savedContactPriority", 1)

            if (intent.getByteArrayExtra("savedContactPhoto") != null){
                Glide.with(this).load(intent.getByteArrayExtra("savedContactPhoto")).into(edit_contact_image_view)
            }
            //set permissions for store access
            checkOrSetPermissions()
            //set the current contact Id
            currentId = currentContactId

            text_view_edit_contact.text = getString(R.string.edit_contact)

        } else {
            text_view_edit_contact.text = getString(R.string.save_contact)
        }

        choosePhotoButton.setOnClickListener {
            pickPhotoFromDevice()
        }
    }

    //Starts the activity of selecting a photo from the gallery expecting the result
    private fun pickPhotoFromDevice(){

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY )
    }
    //check if the app has the storage permissions, in case the app doesn´t have them, ask the user to
    //give the permissions
    private fun checkOrSetPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_WRITE_REQUEST_CODE)
        }
    }

    //set the toolbar options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_contact_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.save_contact -> {
                saveContact()
                true
            }
            R.id.close -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                this.finish()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    //saves the contact with the specified data in the layout
    private fun saveContact(){
        //extract the photo, compress it and cast it to byteArray type
        val photoAsBitmap = (edit_contact_image_view.drawable as BitmapDrawable).bitmap
        val outputStream = ByteArrayOutputStream()
        photoAsBitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
        val contactPhoto = outputStream.toByteArray()
        //verify all the fields are filled
        if (edit_text_edit_contact_name.text.toString().isNotEmpty() && edit_text_edit_contact_phone.text.toString().isNotEmpty()
            && edit_text_edit_contact_email.text.toString().isNotEmpty()){
            //pass the data to the MainActivity to save / edit the contact
            val data = Intent().apply {
                if (currentId != -1) {
                    putExtra("savedContactId", currentId)
                }
                putExtra("savedContactName", edit_text_edit_contact_name.text.toString())
                putExtra("savedContactPhone", edit_text_edit_contact_phone.text.toString())
                putExtra("savedContactEmail", edit_text_edit_contact_email.text.toString())
                putExtra("savedContactPriority", number_picker_priority.value)
                putExtra("savedContactPhoto", contactPhoto)

            }
            //sets result that will be catched by the MainActivity onActivityResult method
            setResult(Activity.RESULT_OK, data)
            finish()
        } else {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    //cathces the result of the Gallery intent
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //always true
        if (requestCode == GALLERY){
            //verify if data is not null and if it's not, convert the image to a bitmap and set it in the edit_contact_image_view
            if (data!=null){
                val contentUri = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                    Toast.makeText(this@AddEditContactActivity, "Image has been set successfully", Toast.LENGTH_SHORT).show()
                    edit_contact_image_view!!.setImageBitmap(bitmap)
                } catch (e: IOException){
                    e.printStackTrace()
                    Toast.makeText(this@AddEditContactActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        this.finish()
    }
}
