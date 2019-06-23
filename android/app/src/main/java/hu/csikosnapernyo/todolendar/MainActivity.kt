package hu.csikosnapernyo.todolendar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.android.gms.tasks.Task
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.drive.DriveScopes
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private val TAG = "SignInActivity"
    private val RC_SIGN_IN = 9001

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Scope(DriveScopes.DRIVE_APPDATA), Scope(CalendarScopes.CALENDAR))
            .requestId()
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        updateUI()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        Log.d(TAG, "handleSignInResult:" + completedTask.isSuccessful)

        try {
            // Signed in successfully, show authenticated U
            val account = completedTask.getResult(ApiException::class.java)
            Log.d(TAG, account!!.grantedScopes.toString())
            updateUI()
        } catch (e: ApiException) {
            // Signed out, show unauthenticated UI.
            Log.w(TAG, "handleSignInResult:error", e)
        }

    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun signOut() {
        mGoogleSignInClient.signOut()
        updateUI()
    }

    private fun onSignedIn() {
        updateUI()

    }

    private fun onSignedOut() {
        updateUI()
    }

    private fun updateUI() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account == null) {
            btn_signin.text = getText(R.string.sign_in_button_caption)
            tw_result.text = ""
            btn_signin.setOnClickListener { signIn() }
            btn_create_calendar.visibility = View.GONE
        } else {
            btn_signin.text = getString(R.string.sign_out_button_caption)
            tw_result.text = """${account.displayName}
                |${account.idToken}
                |${account.id}
                |${account.grantedScopes}
                |${account.isExpired}
            """.trimMargin()
            btn_create_calendar.visibility = View.VISIBLE
            btn_create_calendar.setOnClickListener { createCalendar() }
            btn_signin.setOnClickListener { signOut() }
        }
    }


    private val lastSignInAccount: GoogleSignInAccount
        get() = GoogleSignIn.getLastSignedInAccount(this)!!

    private val credential: GoogleAccountCredential
        get() = GoogleAccountCredential.usingOAuth2(this, listOf(DriveScopes.DRIVE_APPDATA, CalendarScopes.CALENDAR))

    private fun createCalendar() {
        CreateCalendarTask(lastSignInAccount.account!!, credential).execute()
    }
}
