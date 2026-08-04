package com.intentflow.engine.context.provider

import android.content.Context
import android.provider.ContactsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-device Provider for Recent Contacts using Android ContactsContract.
 */
@Singleton
class ContactContextProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getRecentContacts(limit: Int = 10): List<String> {
        val contacts = mutableListOf<String>()
        try {
            val cursor = context.contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                arrayOf(ContactsContract.Contacts.DISPLAY_NAME),
                null,
                null,
                "${ContactsContract.Contacts.LAST_TIME_CONTACTED} DESC LIMIT $limit"
            )

            cursor?.use {
                val nameIndex = it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                while (it.moveToNext()) {
                    if (nameIndex >= 0) {
                        val name = it.getString(nameIndex)
                        if (!name.isNullOrBlank()) {
                            contacts.add(name)
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            // Permission not granted; return empty list safely without crashing
        } catch (e: Exception) {
            // Catch any provider error
        }
        return contacts
    }
}
