package com.luisurbina.laboratorio7.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.luisurbina.laboratorio7.R
import com.luisurbina.laboratorio7.data.Contact
import kotlinx.android.synthetic.main.contact_item.view.*

class ContactAdapter : ListAdapter<Contact, ContactAdapter.ContactHolder>(DIFF_CALLBACK) {
    
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Contact>() {
            override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
                return oldItem.name == newItem.name && oldItem.phone == newItem.phone
                        && oldItem.priority == newItem.priority && oldItem.email == newItem.email
            }
        }
    }

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
        return ContactHolder(itemView)
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        val currentContact: Contact = getItem(position)

        holder.textViewName.text = currentContact.name
        holder.textViewPriority.text = currentContact.priority.toString()
        holder.textViewPhone.text = currentContact.phone
    }

    fun getContactAt(position: Int): Contact{
        return getItem(position)
    }

    inner class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    listener?.onItemClick(getItem(position))
                }
            }
        }
        var textViewName: TextView = itemView.text_view_name
        var textViewPriority: TextView = itemView.text_view_priority
        var textViewPhone: TextView = itemView.text_view_priority
    }

    interface OnItemClickListener{
        fun onItemClick(contact: Contact)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

}