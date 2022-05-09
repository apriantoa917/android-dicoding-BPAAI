package com.aprianto.dicostory.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import com.aprianto.dicostory.data.viewmodel.*
import com.aprianto.dicostory.databinding.ActivityMainBinding
import com.aprianto.dicostory.ui.auth.AuthActivity
import com.aprianto.dicostory.ui.dashboard.explore.ExploreFragment
import com.aprianto.dicostory.ui.dashboard.folder.FolderFragment
import com.aprianto.dicostory.ui.dashboard.home.HomeFragment
import com.aprianto.dicostory.ui.dashboard.profile.ProfileFragment
import com.aprianto.dicostory.ui.dashboard.story.CameraActivity
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.aprianto.dicostory.utils.SettingPreferences
import com.aprianto.dicostory.utils.dataStore
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    /* load image in folderFragments */
    private val folderViewModel: FolderViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private val pref = SettingPreferences.getInstance(dataStore)
    private val settingViewModel: SettingViewModel by viewModels { ViewModelSettingFactory(pref) }
    private var token = ""
    private var fragmentHome: HomeFragment? = null
    private lateinit var startNewStory: ActivityResultLauncher<Intent>

    @RequiresApi(Build.VERSION_CODES.M)
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentProfile = ProfileFragment()
        fragmentHome = HomeFragment()
        val fragmentExplore = ExploreFragment()
        val fragmentDownloaded = FolderFragment()

        /* load latest story when new story successfully uploaded */
        startNewStory =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    fragmentHome?.onRefresh()
                }
            }

        /* obtain token for reuse in child fragments */
        settingViewModel.getUserPreferences(Constanta.UserPreferences.UserToken.name)
            .observe(this) {
                token = "Bearer $it"
                /* start load data after token obtained */
                switchFragment(fragmentHome!!)
            }

        binding.bottomNavigationView.background = null // hide abnormal layer in bottom nav

        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    switchFragment(fragmentHome!!)
                    true
                }
                R.id.navigation_explore -> {
                    if (Helper.isPermissionGranted(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    ) {
                        switchFragment(fragmentExplore)
                        true
                    } else {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            Constanta.LOCATION_PERMISSION_CODE
                        )
                        false
                    }
                }
                R.id.navigation_downloaded -> {
                    if (Helper.isPermissionGranted(
                            this,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        switchFragment(fragmentDownloaded)
                        true
                    } else {
                        ActivityCompat.requestPermissions(
                            this@MainActivity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constanta.STORAGE_PERMISSION_CODE
                        )
                        false
                    }
                }
                R.id.navigation_profile -> {
                    switchFragment(fragmentProfile)
                    true
                }
                else -> false
            }

        }
        binding.fab.setOnClickListener {
            /* ask permission for camera first before launch camera */
            if (Helper.isPermissionGranted(this, Manifest.permission.CAMERA)) {
                val intent = Intent(this@MainActivity, CameraActivity::class.java)
                startNewStory.launch(intent)
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    Constanta.CAMERA_PERMISSION_CODE
                )

            }
        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            Constanta.CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Helper.notifyGivePermission(this, "Berikan aplikasi izin mengakses kamera  ")
                }
            }
            Constanta.LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Helper.notifyGivePermission(
                        this,
                        "Berikan aplikasi izin lokasi untuk membaca lokasi  "
                    )
                }
            }
            Constanta.STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Helper.notifyGivePermission(
                        this,
                        "Berikan aplikasi izin storage untuk membaca dan menyimpan story"
                    )
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onResume() {
        super.onResume()

        /* after return from other activity, reinitialize folder data (after download story i.e)*/
        loadFolderData()


    }

    /* return current token from dataPreference to child fragment */
    fun getUserToken() = token

    fun getStoryViewModel(): StoryPagerViewModel {
        val viewModel: StoryPagerViewModel by viewModels {
            ViewModelStoryFactory(
                this,
                ApiConfig.getApiService(),
                getUserToken()
            )
        }
        return viewModel
    }


    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun loadFolderData() {
        /* init load folder async */
        GlobalScope.launch {
            /* load thumbnail image for folder fragment while app opened */
            folderViewModel.loadImage(this@MainActivity)
        }
    }

    fun routeToAuth() = startActivity(Intent(this, AuthActivity::class.java))

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }


}