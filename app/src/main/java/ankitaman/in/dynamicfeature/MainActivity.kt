package ankitaman.`in`.dynamicfeature

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import android.widget.Toast
import ankitaman.`in`.dynamicfeature.utils.Utils
import com.google.android.play.core.splitinstall.*
import com.google.android.play.core.splitinstall.model.SplitInstallErrorCode
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Ankit Aman
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var splitInstallManager: SplitInstallManager
    private var dynamicModuleClassName = ""
    private var packageNameModule = ""
    private val moduleName = "details_feature"
    private var isDynamicModuleInstalled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        splitInstallManager = SplitInstallManagerFactory.create(this)
        packageNameModule = packageName

        loadDynamicModules()

        btn_load_fragment.setOnClickListener {
            if (isDynamicModuleInstalled)
                loadFragmentDynamicModule()
        }
        btn_load_activity.setOnClickListener {
            if (isDynamicModuleInstalled)
                loadActivityFeatureModule()
        }
    }

    override fun onResume() {
        splitInstallManager.registerListener(listener)
        super.onResume()
    }

    override fun onPause() {
        splitInstallManager.unregisterListener(listener)
        super.onPause()
    }


    /** Listener used to handle changes in state for install requests. */
    private val listener = SplitInstallStateUpdatedListener { state ->
        state.moduleNames().forEach { name ->
            // Handle changes in state.
            when (state.status()) {
                SplitInstallSessionStatus.DOWNLOADING -> {
                    //  In order to see this, the application has to be uploaded to the Play Store.
                    Toast.makeText(this,"Downloading",Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Downloading $name")
                }
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    Toast.makeText(this,"REQUIRES_USER_CONFIRMATION",Toast.LENGTH_SHORT).show()
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Updates the app’s context with the code and resources of the
                        // installed module.
                        SplitInstallHelper.updateAppInfo(this)
                        Toast.makeText(this,"INSTALLED",Toast.LENGTH_SHORT).show()
                    }
                    isDynamicModuleInstalled = true
                }

                SplitInstallSessionStatus.INSTALLING -> {
                    Toast.makeText(this,"INSTALLING",Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Downloading $name")
                }
                SplitInstallSessionStatus.FAILED -> {
                    Toast.makeText(this,"Error: ${state.errorCode()} for module ${state.moduleNames()}",Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Error: ${state.errorCode()} for module ${state.moduleNames()}")
                }
            }

        }
    }

    /* Install all features deferred. */
    private fun loadDynamicModules() {
        if (splitInstallManager.installedModules.contains(moduleName)) {
            loadActivityFeatureModule()
            return
        }

        val request = SplitInstallRequest.newBuilder()
            .addModule(moduleName)
            .build()

        splitInstallManager.startInstall(request)
            .addOnFailureListener {
                run {
                    when ((it as (SplitInstallException)).errorCode) {
                        SplitInstallErrorCode.NETWORK_ERROR -> {
                            // Display a message that requests the user to establish a
                            // network connection.
                            // If No Network loading Local Fragment
                            Log.e(TAG,"No Network Error")
                            Toast.makeText(this,"No Network Error",Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
                            Log.e(TAG,"Error")
                        }
                    }
                }
            }
    }

    private fun loadActivityFeatureModule() {
        dynamicModuleClassName = "ankitaman.`in`.details_feature.DetailsActivity"
        if (!Utils.isClassAvailableInProject(dynamicModuleClassName)) {
            return
        }
        Intent().setClassName(packageNameModule, dynamicModuleClassName).also {
            startActivity(it)
        }
    }


    private fun loadFragmentDynamicModule() {
        dynamicModuleClassName = "ankitaman.`in`.details_feature.DetailsFragment"
        if (!Utils.isClassAvailableInProject(dynamicModuleClassName)) {
            return
        }
        container_feature_module.visibility = View.VISIBLE
        val childFragTrans = supportFragmentManager.beginTransaction()
        var frag = Fragment()
        frag = Fragment.instantiate(this, dynamicModuleClassName)
        childFragTrans.add(R.id.container_feature_module, frag)
        childFragTrans.addToBackStack("feature_module_fragment")
        childFragTrans.commit()

        //Hide progressbar
        ll_buttons_container.visibility = View.GONE

    }

}
