package ankitaman.`in`.dynamicfeature

import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.google.android.play.core.splitinstall.*
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
                loadFragmentDeatureModule()
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
                    Log.d(TAG, "Downloading $name")
                }
                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                    Log.d(TAG, "Downloading $name")
                }
                SplitInstallSessionStatus.INSTALLED -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        // Updates the app’s context with the code and resources of the
                        // installed module.
                        SplitInstallHelper.updateAppInfo(this)
                    }
                    isDynamicModuleInstalled = true
                }

                SplitInstallSessionStatus.INSTALLING -> {
                    Log.d(TAG, "Downloading $name")
                }
                SplitInstallSessionStatus.FAILED -> {
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
    }

    private fun loadActivityFeatureModule() {
        dynamicModuleClassName = "ankitaman.`in`.details_feature.DetailsActivity"
        Intent().setClassName(packageNameModule, dynamicModuleClassName).also {
            startActivity(it)
        }
    }


    private fun loadFragmentDeatureModule() {
        container_feature_module.visibility = View.VISIBLE

        dynamicModuleClassName = "ankitaman.`in`.details_feature.DetailsFragment"
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
