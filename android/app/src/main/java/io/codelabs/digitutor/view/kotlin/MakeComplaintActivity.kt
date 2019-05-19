package io.codelabs.digitutor.view.kotlin

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import io.codelabs.digitutor.R
import io.codelabs.digitutor.core.base.BaseActivity
import io.codelabs.digitutor.core.datasource.remote.FirebaseDataSource
import io.codelabs.digitutor.core.util.AsyncCallback
import io.codelabs.digitutor.core.util.InputValidator
import io.codelabs.digitutor.databinding.ActivityMakeComplaintBinding
import io.codelabs.sdk.util.debugLog
import io.codelabs.sdk.util.toast

/**
 * Make a new [Complaint]
 */
class MakeComplaintActivity : BaseActivity() {
    private lateinit var binding: ActivityMakeComplaintBinding

    companion object {
        const val EXTRA_TUTOR = "tutor"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_make_complaint)


    }


    fun send(view: View) {
        if (InputValidator.hasValidInput()) {
            FirebaseDataSource.postComplaint(
                this,
                prefs.key,
                intent.getStringExtra(EXTRA_TUTOR),
                "",
                object : AsyncCallback<Void?> {
                    override fun onSuccess(response: Void?) {

                    }

                    override fun onComplete() {

                    }

                    override fun onError(error: String?) {
                        debugLog(error)
                    }

                    override fun onStart() {
                        finishAfterTransition()
                    }
                })
        } else {
            toast("Enter a message first...")
        }
    }

}