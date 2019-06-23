package hu.csikosnapernyo.todolendar

import android.accounts.Account
import android.os.AsyncTask
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.calendar.Calendar

class CreateCalendarTask(account: Account, private val credential: GoogleAccountCredential) :
    AsyncTask<Unit, Unit, Unit>() {
    init {
        credential.selectedAccount = account
    }

    private companion object {
        private val httpTransport = AndroidHttp.newCompatibleTransport()
        private val jsonFactory = JacksonFactory.getDefaultInstance()
    }

    override fun doInBackground(vararg params: Unit?) {

        val calendarService = Calendar.Builder(httpTransport, jsonFactory, credential)
            .setApplicationName("Todolendar Android")
            .build()
        val newCalendar = com.google.api.services.calendar.model.Calendar()
        newCalendar.description = "Programatically created calendar. Truly the best calendar ever."
        newCalendar.summary = "HELLOOOO"
        calendarService.calendars().insert(newCalendar).execute()
    }

}

