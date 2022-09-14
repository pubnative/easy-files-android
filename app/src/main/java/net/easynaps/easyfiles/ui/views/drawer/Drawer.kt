package net.easynaps.easyfiles.ui.views.drawer

import net.easynaps.easyfiles.activities.MainActivity
import com.google.android.material.navigation.NavigationView
import net.easynaps.easyfiles.ui.views.drawer.ActionViewStateManager
import kotlin.jvm.Volatile
import com.android.volley.toolbox.ImageLoader
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import net.easynaps.easyfiles.ui.views.drawer.CustomNavigationView
import android.widget.RelativeLayout
import android.view.View
import android.widget.ImageView
import net.easynaps.easyfiles.utils.application.AppConfig
import android.widget.LinearLayout
import android.view.Gravity
import android.view.Menu
import java.util.ArrayList
import java.io.File
import androidx.annotation.DrawableRes
import net.easynaps.easyfiles.R
import net.easynaps.easyfiles.ui.views.drawer.Drawer
import net.easynaps.easyfiles.ui.views.drawer.MenuMetadata
import java.util.Collections
import net.easynaps.easyfiles.fragments.CloudSheetFragment
import com.cloudrail.si.interfaces.CloudStorage
import com.cloudrail.si.services.Dropbox
import net.easynaps.easyfiles.database.CloudHandler
import com.cloudrail.si.services.Box
import com.cloudrail.si.services.OneDrive
import com.cloudrail.si.services.GoogleDrive
import net.easynaps.easyfiles.fragments.preference_fragments.PreferencesConstants
import net.easynaps.easyfiles.fragments.preference_fragments.QuickAccessPref
import net.easynaps.easyfiles.fragments.FTPServerFragment
import net.easynaps.easyfiles.fragments.AppsListFragment
import android.content.Intent
import net.easynaps.easyfiles.activities.PreferencesActivity
import android.view.MenuItem
import androidx.annotation.StringRes
import java.lang.IllegalStateException
import net.easynaps.easyfiles.utils.theme.AppTheme
import android.graphics.Color
import android.os.Build.VERSION
import android.os.Build
import net.easynaps.easyfiles.filesystem.HybridFile
import net.easynaps.easyfiles.fragments.MainFragment
import net.easynaps.easyfiles.utils.cloud.CloudUtil
import android.widget.Toast
import net.easynaps.easyfiles.ui.dialogs.GeneralDialogCreation
import net.easynaps.easyfiles.filesystem.RootHelper
import com.android.volley.toolbox.ImageLoader.ImageListener
import com.android.volley.toolbox.ImageLoader.ImageContainer
import com.android.volley.VolleyError
import java.lang.Exception
import androidx.annotation.ColorInt
import net.easynaps.easyfiles.utils.color.ColorUsage
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.FragmentTransaction
import net.easynaps.easyfiles.BuildConfig
import net.easynaps.easyfiles.utils.*
import net.easynaps.easyfiles.utils.files.FileUtils
import java.lang.Runnable

class Drawer(private val mainActivity: MainActivity) :
    NavigationView.OnNavigationItemSelectedListener {

    private val resources: Resources
    private val dataUtils = DataUtils.getInstance()
    private val actionViewStateManager: ActionViewStateManager
    var isSomethingSelected = false

    @Volatile
    var storageCount = 0 // number of storage available (internal/external/otg etc)
        private set
    var isLocked = false
        private set
    private var pending_fragmentTransaction: FragmentTransaction? = null
    private var pendingPath: String? = null
    private val mImageLoader: ImageLoader
    var firstPath: String? = null
        private set
    var secondPath: String? = null
        private set
    private val mDrawerLayout: DrawerLayout
    private var mDrawerToggle: ActionBarDrawerToggle? = null
    private val navView: CustomNavigationView
    private val drawerHeaderParent: RelativeLayout
    private val drawerHeaderLayout: View
    private val drawerHeaderView: ImageView
    private fun setNavViewDimension(navView: CustomNavigationView) {
        val screenWidth = AppConfig.getInstance().screenUtils.screenWidthInDp
        val desiredWidthInDp = screenWidth - ScreenUtils.TOOLBAR_HEIGHT_IN_DP
        val desiredWidthInPx =
            AppConfig.getInstance().screenUtils.convertDbToPx(desiredWidthInDp.toFloat())
        navView.layoutParams = DrawerLayout.LayoutParams(
            desiredWidthInPx,
            LinearLayout.LayoutParams.MATCH_PARENT,
            Gravity.START
        )
    }

    fun refreshDrawer() {
        val menu = navView.menu
        menu.clear()
        actionViewStateManager.deselectCurrentActionView()
        var order = 0
        val storageDirectories = mainActivity.storageDirectories
        storageCount = 0
        for (file in storageDirectories) {
            val f = File(file)
            var name: String
            @DrawableRes var icon1 = R.drawable.ic_sd_storage_white_24dp
            if ("/storage/emulated/legacy" == file || "/storage/emulated/0" == file || "/mnt/sdcard" == file) {
                name = resources.getString(R.string.storage)
            } else if ("/storage/sdcard1" == file) {
                name = resources.getString(R.string.extstorage)
            } else if ("/" == file) {
                name = resources.getString(R.string.rootdirectory)
                icon1 = R.drawable.ic_drawer_root_white
            } else if (file.contains(OTGUtil.PREFIX_OTG)) {
                name = "OTG"
                icon1 = R.drawable.ic_usb_white_24dp
            } else name = f.name
            if (f.isDirectory || f.canExecute()) {
                addNewItem(
                    menu, STORAGES_GROUP, order++, name, MenuMetadata(file), icon1,
                    R.drawable.ic_show_chart_black_24dp
                )
                if (storageCount == 0) firstPath = file else if (storageCount == 1) secondPath =
                    file
                storageCount++
            }
        }
        dataUtils.setStorages(storageDirectories)
        if (dataUtils.servers.size > 0) {
            Collections.sort(dataUtils.servers, BookSorter())
            synchronized(dataUtils.servers) {
                for (file in dataUtils.servers) {
                    addNewItem(
                        menu, SERVERS_GROUP, order++, file[0],
                        MenuMetadata(file[1]), R.drawable.ic_settings_remote_white_24dp,
                        R.drawable.ic_edit_24dp
                    )
                }
            }
        }
        val accountAuthenticationList = ArrayList<Array<String>>()
        if (CloudSheetFragment.isCloudProviderAvailable(mainActivity)) {
            for (cloudStorage in dataUtils.accounts) {
                if (cloudStorage is Dropbox) {
                    addNewItem(
                        menu, CLOUDS_GROUP, order++, CloudHandler.CLOUD_NAME_DROPBOX,
                        MenuMetadata(CloudHandler.CLOUD_PREFIX_DROPBOX + "/"),
                        R.drawable.ic_dropbox_white_24dp, R.drawable.ic_edit_24dp
                    )
                    accountAuthenticationList.add(
                        arrayOf(
                            CloudHandler.CLOUD_NAME_DROPBOX,
                            CloudHandler.CLOUD_PREFIX_DROPBOX + "/"
                        )
                    )
                } else if (cloudStorage is Box) {
                    addNewItem(
                        menu, CLOUDS_GROUP, order++, CloudHandler.CLOUD_NAME_BOX,
                        MenuMetadata(CloudHandler.CLOUD_PREFIX_BOX + "/"),
                        R.drawable.ic_box_white_24dp, R.drawable.ic_edit_24dp
                    )
                    accountAuthenticationList.add(
                        arrayOf(
                            CloudHandler.CLOUD_NAME_BOX,
                            CloudHandler.CLOUD_PREFIX_BOX + "/"
                        )
                    )
                } else if (cloudStorage is OneDrive) {
                    addNewItem(
                        menu, CLOUDS_GROUP, order++, CloudHandler.CLOUD_NAME_ONE_DRIVE,
                        MenuMetadata(CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/"),
                        R.drawable.ic_onedrive_white_24dp, R.drawable.ic_edit_24dp
                    )
                    accountAuthenticationList.add(
                        arrayOf(
                            CloudHandler.CLOUD_NAME_ONE_DRIVE,
                            CloudHandler.CLOUD_PREFIX_ONE_DRIVE + "/"
                        )
                    )
                } else if (cloudStorage is GoogleDrive) {
                    addNewItem(
                        menu, CLOUDS_GROUP, order++, CloudHandler.CLOUD_NAME_GOOGLE_DRIVE,
                        MenuMetadata(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/"),
                        R.drawable.ic_google_drive_white_24dp, R.drawable.ic_edit_24dp
                    )
                    accountAuthenticationList.add(
                        arrayOf(
                            CloudHandler.CLOUD_NAME_GOOGLE_DRIVE,
                            CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE + "/"
                        )
                    )
                }
            }
            Collections.sort(accountAuthenticationList, BookSorter())
        }
        if (mainActivity.getBoolean(PreferencesConstants.PREFERENCE_SHOW_SIDEBAR_FOLDERS)) {
            if (dataUtils.books.size > 0) {
                Collections.sort(dataUtils.books, BookSorter())
                synchronized(dataUtils.books) {
                    for (file in dataUtils.books) {
                        addNewItem(
                            menu, FOLDERS_GROUP, order++, file[0],
                            MenuMetadata(file[1]), R.drawable.ic_folder_white_24dp,
                            R.drawable.ic_edit_24dp
                        )
                    }
                }
            }
        }
        val quickAccessPref = TinyDB.getBooleanArray(
            mainActivity.prefs, QuickAccessPref.KEY,
            QuickAccessPref.DEFAULT
        )
        if (mainActivity.getBoolean(PreferencesConstants.PREFERENCE_SHOW_SIDEBAR_QUICKACCESSES)) {
            if (quickAccessPref[0]) {
                addNewItem(
                    menu, QUICKACCESSES_GROUP, order++, R.string.quick,
                    MenuMetadata("5"), R.drawable.ic_star_white_24dp, null
                )
            }
            if (quickAccessPref[1]) {
                addNewItem(
                    menu, QUICKACCESSES_GROUP, order++, R.string.recent,
                    MenuMetadata("6"), R.drawable.ic_history_white_24dp, null
                )
            }
            if (quickAccessPref[2]) {
                addNewItem(
                    menu, QUICKACCESSES_GROUP, order++, R.string.images,
                    MenuMetadata("0"), R.drawable.ic_photo_library_white_24dp, null
                )
            }
            if (quickAccessPref[3]) {
                addNewItem(
                    menu, QUICKACCESSES_GROUP, order++, R.string.videos,
                    MenuMetadata("1"), R.drawable.ic_video_library_white_24dp, null
                )
            }
            if (quickAccessPref[4]) {
                addNewItem(
                    menu, QUICKACCESSES_GROUP, order++, R.string.audio,
                    MenuMetadata("2"), R.drawable.ic_library_music_white_24dp, null
                )
            }
            if (quickAccessPref[5]) {
                addNewItem(
                    menu, QUICKACCESSES_GROUP, order++, R.string.documents,
                    MenuMetadata("3"), R.drawable.ic_library_books_white_24dp, null
                )
            }
            if (quickAccessPref[6]) {
                addNewItem(
                    menu, QUICKACCESSES_GROUP, order++, R.string.apks,
                    MenuMetadata("4"), R.drawable.ic_apk_library_white_24dp, null
                )
            }
        }
        addNewItem(
            menu, LASTGROUP, order++, R.string.ftp,
            MenuMetadata {
                val transaction2 = mainActivity.supportFragmentManager.beginTransaction()
                transaction2.replace(R.id.content_frame, FTPServerFragment())
                mainActivity.appbar
                    .appbarLayout
                    .animate()
                    .translationY(0f)
                    .setInterpolator(DecelerateInterpolator(2F))
                    .start()
                pending_fragmentTransaction = transaction2
                if (!isLocked) close() else onDrawerClosed()
            },
            R.drawable.ic_ftp_white_24dp, null
        )
        addNewItem(
            menu, LASTGROUP, order++, R.string.apps,
            MenuMetadata {
                val transaction2 = mainActivity.supportFragmentManager.beginTransaction()
                transaction2.replace(R.id.content_frame, AppsListFragment())
                mainActivity.appbar
                    .appbarLayout
                    .animate()
                    .translationY(0f)
                    .setInterpolator(DecelerateInterpolator(2F))
                    .start()
                pending_fragmentTransaction = transaction2
                if (!isLocked) close() else onDrawerClosed()
            },
            R.drawable.ic_android_white_24dp, null
        )
        addNewItem(
            menu, LASTGROUP, order++, R.string.setting,
            MenuMetadata {
                val `in` = Intent(mainActivity, PreferencesActivity::class.java)
                mainActivity.startActivity(`in`)
                mainActivity.finish()
            },
            R.drawable.ic_settings_white_24dp, null
        )
        for (i in 0 until navView.menu.size()) {
            navView.menu.getItem(i).isEnabled = true
        }
        for (group in GROUPS) {
            menu.setGroupCheckable(group, true, true)
        }
        val item = navView.selected
        if (item != null) {
            item.isChecked = true
            actionViewStateManager.selectActionView(item)
            isSomethingSelected = true
        }
    }

    private fun addNewItem(
        menu: Menu, group: Int, order: Int, @StringRes text: Int, meta: MenuMetadata,
        @DrawableRes icon: Int, @DrawableRes actionViewIcon: Int?
    ) {
        check(!(BuildConfig.DEBUG && menu.findItem(order) != null)) { "Item already id exists: $order" }
        val item = menu.add(group, order, order, text).setIcon(icon)
        dataUtils.putDrawerMetadata(item, meta)
        if (actionViewIcon != null) {
            item.setActionView(R.layout.layout_draweractionview)
            val imageView = item.actionView!!.findViewById<ImageView>(R.id.imageButton)
            imageView.setImageResource(actionViewIcon)
            if (mainActivity.appTheme != AppTheme.LIGHT) {
                imageView.setColorFilter(Color.WHITE)
            }
            item.actionView!!.setOnClickListener { view: View? -> onNavigationItemActionClick(item) }
        }
    }

    private fun addNewItem(
        menu: Menu, group: Int, order: Int, text: String, meta: MenuMetadata,
        @DrawableRes icon: Int, @DrawableRes actionViewIcon: Int?
    ) {
        check(!(BuildConfig.DEBUG && menu.findItem(order) != null)) { "Item already id exists: $order" }
        val item = menu.add(group, order, order, text).setIcon(icon)
        dataUtils.putDrawerMetadata(item, meta)
        if (actionViewIcon != null) {
            item.setActionView(R.layout.layout_draweractionview)
            val imageView = item.actionView!!.findViewById<ImageView>(R.id.imageButton)
            imageView.setImageResource(actionViewIcon)
            if (mainActivity.appTheme != AppTheme.LIGHT) {
                imageView.setColorFilter(Color.WHITE)
            }
            item.actionView!!.setOnClickListener { view: View? -> onNavigationItemActionClick(item) }
        }
    }

    fun onActivityResult(requestCode: Int, responseCode: Int, intent: Intent?) {
        if (mainActivity.prefs != null && intent != null && intent.data != null) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mainActivity.contentResolver.takePersistableUriPermission(
                    intent.data!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            mainActivity.prefs.edit().putString(
                PreferencesConstants.PREFERENCE_DRAWER_HEADER_PATH,
                intent.data.toString()
            ).commit()
            setDrawerHeaderBackground()
        }
    }

    fun closeIfNotLocked() {
        if (!isLocked) {
            close()
        }
    }

    val isOpen: Boolean
        get() = mDrawerLayout.isDrawerOpen(navView)

    fun open() {
        mDrawerLayout.openDrawer(navView)
    }

    fun close() {
        mDrawerLayout.closeDrawer(navView)
    }

    fun onDrawerClosed() {
        if (pending_fragmentTransaction != null) {
            pending_fragmentTransaction!!.commit()
            pending_fragmentTransaction = null
        }
        if (pendingPath != null) {
            val hFile = HybridFile(OpenMode.UNKNOWN, pendingPath)
            hFile.generateMode(mainActivity)
            if (hFile.isSimpleFile) {
                FileUtils.openFile(File(pendingPath), mainActivity, mainActivity.prefs)
                pendingPath = null
                return
            }
            val mainFrag = mainActivity.currentMainFragment
            if (mainFrag != null) {
                mainFrag.loadlist(pendingPath, false, OpenMode.UNKNOWN)
            } else {
                mainActivity.goToMain(pendingPath)
                return
            }
            pendingPath = null
        }
        mainActivity.supportInvalidateOptionsMenu()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        actionViewStateManager.deselectCurrentActionView()
        actionViewStateManager.selectActionView(item)
        isSomethingSelected = true
        val title = item.title.toString()
        val meta = dataUtils.getDrawerMetadata(item)
        when (meta.type) {
            MenuMetadata.ITEM_ENTRY -> {
                if (dataUtils.containsBooks(arrayOf(title, meta.path)) != -1) {
                    FileUtils.checkForPath(mainActivity, meta.path, mainActivity.isRootExplorer)
                }
                if (dataUtils.accounts.size > 0 && (meta.path.startsWith(CloudHandler.CLOUD_PREFIX_BOX) ||
                            meta.path.startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX) ||
                            meta.path.startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE) ||
                            meta.path.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE))
                ) {
                    // we have cloud accounts, try see if token is expired or not
                    CloudUtil.checkToken(meta.path, mainActivity)
                }
                pendingPath = meta.path
                if (meta.path.contains(OTGUtil.PREFIX_OTG) && (mainActivity.prefs
                        .getString(MainActivity.KEY_PREF_OTG, null)
                            == MainActivity.VALUE_PREF_OTG_NULL)
                ) {
                    // we've not gotten otg path yet
                    // start system request for storage access framework
                    Toast.makeText(
                        mainActivity,
                        mainActivity.getString(R.string.otg_access),
                        Toast.LENGTH_LONG
                    ).show()
                    val safIntent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    mainActivity.startActivityForResult(safIntent, MainActivity.REQUEST_CODE_SAF)
                } else {
                    closeIfNotLocked()
                    if (isLocked) {
                        onDrawerClosed()
                    }
                }
            }
            MenuMetadata.ITEM_INTENT -> meta.onClickListener.onClick()
        }
        return true
    }

    fun onNavigationItemActionClick(item: MenuItem) {
        val title = item.title.toString()
        val meta = dataUtils.getDrawerMetadata(item)
        val path = meta.path
        when (item.groupId) {
            STORAGES_GROUP -> if (path != "/") {
                GeneralDialogCreation.showPropertiesDialogForStorage(
                    RootHelper.generateBaseFile(File(path), true),
                    mainActivity, mainActivity.appTheme
                )
            }
            SERVERS_GROUP, CLOUDS_GROUP, FOLDERS_GROUP -> if (dataUtils.containsBooks(
                    arrayOf(
                        title,
                        path
                    )
                ) != -1
            ) {
                mainActivity.renameBookmark(title, path)
            } else if (path.startsWith("smb:/")) {
                mainActivity.showSMBDialog(title, path, true)
            } else if (path.startsWith("ssh:/")) {
                mainActivity.showSftpDialog(title, path, true)
            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_DROPBOX)) {
                GeneralDialogCreation.showCloudDialog(
                    mainActivity,
                    mainActivity.appTheme,
                    OpenMode.DROPBOX
                )
            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_GOOGLE_DRIVE)) {
                GeneralDialogCreation.showCloudDialog(
                    mainActivity,
                    mainActivity.appTheme,
                    OpenMode.GDRIVE
                )
            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_BOX)) {
                GeneralDialogCreation.showCloudDialog(
                    mainActivity,
                    mainActivity.appTheme,
                    OpenMode.BOX
                )
            } else if (path.startsWith(CloudHandler.CLOUD_PREFIX_ONE_DRIVE)) {
                GeneralDialogCreation.showCloudDialog(
                    mainActivity,
                    mainActivity.appTheme,
                    OpenMode.ONEDRIVE
                )
            }
        }
    }

    fun setDrawerHeaderBackground() {
        val path1 =
            mainActivity.prefs.getString(PreferencesConstants.PREFERENCE_DRAWER_HEADER_PATH, null)
                ?: return
        try {
            val headerImageView = ImageView(mainActivity)
            headerImageView.setImageDrawable(drawerHeaderParent.background)
            mImageLoader[path1, object : ImageListener {
                override fun onResponse(response: ImageContainer, isImmediate: Boolean) {
                    headerImageView.setImageBitmap(response.bitmap)
                    drawerHeaderView.setImageResource(R.drawable.easyfiles_header)
                }

                override fun onErrorResponse(error: VolleyError) {}
            }]
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun selectCorrectDrawerItemForPath(path: String?) {
        val id = dataUtils.findLongestContainingDrawerItem(path)
        if (id == null) deselectEverything() else {
            val item = navView.menu.findItem(id)
            navView.setCheckedItem(item)
            actionViewStateManager.selectActionView(item)
        }
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        mDrawerLayout.setStatusBarBackgroundColor(color)
        drawerHeaderParent.setBackgroundColor(color)
    }

    fun resetPendingPath() {
        pendingPath = null
    }

    fun syncState() {
        if (mDrawerToggle != null) {
            mDrawerToggle?.syncState()
        }
    }

    fun onConfigurationChanged(newConfig: Configuration?) {
        if (mDrawerToggle != null) mDrawerToggle?.onConfigurationChanged(newConfig)
    }

    fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return mDrawerToggle != null && mDrawerToggle?.onOptionsItemSelected(item) == true
    }

    fun setDrawerIndicatorEnabled() {
        if (mDrawerToggle != null) {
            mDrawerToggle?.setDrawerIndicatorEnabled(true)
            mDrawerToggle?.setHomeAsUpIndicator(R.drawable.ic_drawer_l)
        }
    }

    fun deselectEverything() {
        actionViewStateManager.deselectCurrentActionView() //If you set the item as checked the listener doesn't trigger
        if (!isSomethingSelected) {
            return
        }
        navView.deselectItems()
        for (i in 0 until navView.menu.size()) {
            navView.menu.getItem(i).isChecked = false
        }
        isSomethingSelected = false
    }

    /**
     * @param mode [DrawerLayout.LOCK_MODE_LOCKED_CLOSED],
     * [DrawerLayout.LOCK_MODE_LOCKED_OPEN]
     * or [DrawerLayout.LOCK_MODE_UNDEFINED]
     */
    fun lock(mode: Int) {
        mDrawerLayout.setDrawerLockMode(mode, navView)
        isLocked = true
    }

    fun unlock() {
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, navView)
        isLocked = false
    }

    companion object {
        const val image_selector_request_code = 31
        const val STORAGES_GROUP = 0
        const val SERVERS_GROUP = 1
        const val CLOUDS_GROUP = 2
        const val FOLDERS_GROUP = 3
        const val QUICKACCESSES_GROUP = 4
        const val LASTGROUP = 5
        val GROUPS = intArrayOf(
            STORAGES_GROUP, SERVERS_GROUP, CLOUDS_GROUP, FOLDERS_GROUP,
            QUICKACCESSES_GROUP, LASTGROUP
        )
    }

    init {
        resources = mainActivity.resources
        drawerHeaderLayout = mainActivity.layoutInflater.inflate(R.layout.drawerheader, null)
        drawerHeaderParent = drawerHeaderLayout.findViewById(R.id.drawer_header_parent)
        drawerHeaderView = drawerHeaderLayout.findViewById(R.id.drawer_header)
        drawerHeaderView.setOnLongClickListener { v: View? ->
            val intent1: Intent
            if (VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                intent1 = Intent()
                intent1.action = Intent.ACTION_GET_CONTENT
            } else {
                intent1 = Intent(Intent.ACTION_OPEN_DOCUMENT)
            }
            intent1.addCategory(Intent.CATEGORY_OPENABLE)
            intent1.type = "image/*"
            mainActivity.startActivityForResult(intent1, image_selector_request_code)
            false
        }
        mImageLoader = AppConfig.getInstance().imageLoader
        navView = mainActivity.findViewById(R.id.navigation)

        //set width of drawer in portrait to follow material guidelines
        /*if(!Utils.isDeviceInLandScape(mainActivity)){
            setNavViewDimension(navView);
        }*/navView.setNavigationItemSelectedListener(this)
        val accentColor = mainActivity.colorPreference.getColor(ColorUsage.ACCENT)
        val idleColor: Int
        idleColor = if (mainActivity.appTheme == AppTheme.LIGHT) {
            mainActivity.resources.getColor(R.color.item_light_theme)
        } else {
            Color.WHITE
        }
        actionViewStateManager = ActionViewStateManager(navView, idleColor, accentColor)
        val drawerColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_enabled),
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_pressed)
            ), intArrayOf(accentColor, idleColor, idleColor, idleColor, idleColor)
        )
        navView.itemTextColor = drawerColors
        navView.itemIconTintList = drawerColors
        if (mainActivity.appTheme == AppTheme.DARK) {
            navView.setBackgroundColor(Utils.getColor(mainActivity, R.color.holo_dark_background))
        } else if (mainActivity.appTheme == AppTheme.BLACK) {
            navView.setBackgroundColor(Utils.getColor(mainActivity, android.R.color.black))
        } else {
            navView.setBackgroundColor(Color.WHITE)
        }
        mDrawerLayout = mainActivity.findViewById(R.id.drawer_layout)
        //mDrawerLayout.setStatusBarBackgroundColor(Color.parseColor((currentTab==1 ? skinTwo : skin)));
        drawerHeaderView.setImageResource(R.drawable.easyfiles_header)
        //drawerHeaderParent.setBackgroundColor(Color.parseColor((currentTab==1 ? skinTwo : skin)));
        if (mainActivity.findViewById<View?>(R.id.tab_frame) != null) {
            lock(DrawerLayout.LOCK_MODE_LOCKED_OPEN)
            open()
            mDrawerLayout.setScrimColor(Color.TRANSPARENT)
            mDrawerLayout.post { open() }
        } else if (mainActivity.findViewById<View?>(R.id.tab_frame) == null) {
            unlock()
            close()
            mDrawerLayout.post { close() }
        }
        navView.addHeaderView(drawerHeaderLayout)
        if (!isLocked) {
            mDrawerToggle = object : ActionBarDrawerToggle(
                mainActivity,  /* host Activity */
                mDrawerLayout,  /* DrawerLayout object */ //                    R.drawable.ic_drawer_l,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close /* "close drawer" description for accessibility */
            ) {
                override fun onDrawerClosed(view: View) {
                    this@Drawer.onDrawerClosed()
                }

                override fun onDrawerOpened(drawerView: View) {}
            }
            mDrawerLayout.setDrawerListener(mDrawerToggle)
            mainActivity.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_drawer_l)
            mainActivity.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar!!.setHomeButtonEnabled(true)
            mDrawerToggle?.syncState()
        }
    }
}