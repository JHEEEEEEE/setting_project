package com.effort.setting

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private val NOTIFICATION_PERMISSION_CODE = 101
    private lateinit var btnSetLock: Button
    private lateinit var btnSetDelLock: Button
    private lateinit var btnChangePwd: Button
    var lock = true // 잠금 상태 여부 확인

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        val requestPermissionButton =
            findViewById<Button>(R.id.request_notification_permission_button)
        requestPermissionButton.setOnClickListener {
            navigateToNotificationSettings()
        }
    }

    private fun navigateToNotificationSettings() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", packageName)
            intent.putExtra("app_uid", applicationInfo.uid)
        } else {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = android.net.Uri.parse("package:$packageName")
        }
        startActivity(intent)


        // 버튼 위젯 초기화
        btnSetLock = findViewById(R.id.btnSetLock)
        btnSetDelLock = findViewById(R.id.btnSetDelLock)
        btnChangePwd = findViewById(R.id.btnChangePwd)

        init()

        // 잠금 설정 버튼을 눌렀을때
        btnSetLock.setOnClickListener {
            val intent = Intent(this, AppPassWordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.ENABLE_PASSLOCK)
            }
            startActivityForResult(intent, AppLockConst.ENABLE_PASSLOCK)
        }

        // 잠금 비활성화 버튼을 눌렀을때
        btnSetDelLock.setOnClickListener {
            val intent = Intent(this, AppPassWordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.DISABLE_PASSLOCK)
            }
            startActivityForResult(intent, AppLockConst.DISABLE_PASSLOCK)
        }

        // 암호 변경버튼을 눌렀을때
        btnChangePwd.setOnClickListener {
            val intent = Intent(this, AppPassWordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.CHANGE_PASSWORD)
            }
            startActivityForResult(intent, AppLockConst.CHANGE_PASSWORD)
        }
    }

    // startActivityForResult 결과값을 받는다.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            AppLockConst.ENABLE_PASSLOCK ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "암호 설정 됨", Toast.LENGTH_SHORT).show()
                    init()
                    lock = false
                }

            AppLockConst.DISABLE_PASSLOCK ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "암호 삭제 됨", Toast.LENGTH_SHORT).show()
                    init()
                }

            AppLockConst.CHANGE_PASSWORD ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "암호 변경 됨", Toast.LENGTH_SHORT).show()
                    lock = false
                }

            AppLockConst.UNLOCK_PASSWORD ->
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "잠금 해제 됨", Toast.LENGTH_SHORT).show()
                    lock = false
                }
        }

    }

    // 액티비티가 onStart인 경우
    override fun onStart() {
        super.onStart()
        if (lock && AppLock(this).isPassLockSet()) {
            val intent = Intent(this, AppPassWordActivity::class.java).apply {
                putExtra(AppLockConst.type, AppLockConst.UNLOCK_PASSWORD)
            }
            startActivityForResult(intent, AppLockConst.UNLOCK_PASSWORD)
        }

    }

    // 액티비티가 onPause인경우
    override fun onPause() {
        super.onPause()
        if (AppLock(this).isPassLockSet()) {
            lock = true // 잠금로 변경
        }
    }

    // 버튼 비활성화
    private fun init() {


        if (AppLock(this).isPassLockSet()) {
            btnSetLock.isEnabled = false
            btnSetDelLock.isEnabled = true
            btnChangePwd.isEnabled = true
            lock = true
        } else {
            btnSetLock.isEnabled = true
            btnSetDelLock.isEnabled = false
            btnChangePwd.isEnabled = false
            lock = false
        }
    }
}
