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
        private const val CAMERA = 2
    }
    private lateinit var contactViewModel: ContactViewModel
    private var currentId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_contact)

        contactViewModel = ViewModelProviders.of(this).get(ContactViewModel::class.java)

        number_picker_priority.minValue = 1
        number_picker_priority.maxValue = 10

        val currentContactId = intent.getIntExtra("savedContactId", -1)

        if (currentContactId != -1){
            edit_text_edit_contact_name.setText(intent.getStringExtra("savedContactName"))
            edit_text_edit_contact_phone.setText(intent.getStringExtra("savedContactPhone"))
            edit_text_edit_contact_email.setText(intent.getStringExtra("savedContactEmail"))
            number_picker_priority.value = intent.getIntExtra("savedContactPriority", 1)

            if (intent.getByteArrayExtra("savedContactPhoto") != null){
                Glide.with(this).load(intent.getByteArrayExtra("savedContactPhoto")).into(edit_contact_image_view)
            }
            checkOrSetPermissions()
            currentId = currentContactId

            text_view_edit_contact.text = getString(R.string.edit_contact)

        } else {
            text_view_edit_contact.text = getString(R.string.save_contact)
        }

        choosePhotoButton.setOnClickListener {
            pickPhotoFromDevice()
        }
    }

    private fun pickPhotoFromDevice(){

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(galleryIntent, GALLERY )
    }

    private fun checkOrSetPermissions(){
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_WRITE_REQUEST_CODE)
        }
    }


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

    private fun saveContact(){
        val photoAsBitmap = (edit_contact_image_view.drawable as BitmapDrawable).bitmap
        val outputStream = ByteArrayOutputStream()
        photoAsBitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream)
        val contactPhoto = outputStream.toByteArray()
        if (edit_text_edit_contact_name.text.toString().isNotEmpty() && edit_text_edit_contact_phone.text.toString().isNotEmpty()
            && edit_text_edit_contact_email.text.toString().isNotEmpty()){
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
            setResult(Activity.RESULT_OK, data)
            finish()
        } else {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY){
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
