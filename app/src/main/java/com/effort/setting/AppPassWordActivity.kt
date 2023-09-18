package com.effort.setting

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AppPassWordActivity : AppCompatActivity(){
    private var oldPwd =""
    private var changePwdUnlock = false
    private lateinit var etPasscode1: EditText
    private lateinit var etPasscode2: EditText
    private lateinit var etPasscode3: EditText
    private lateinit var etPasscode4: EditText
    private lateinit var etInputInfo: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_lock_password)

        val btn0: Button = findViewById(R.id.btn0)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)
        val btn5: Button = findViewById(R.id.btn5)
        val btn6: Button = findViewById(R.id.btn6)
        val btn7: Button = findViewById(R.id.btn7)
        val btn8: Button = findViewById(R.id.btn8)
        val btn9: Button = findViewById(R.id.btn9)
        val btnClear: Button = findViewById(R.id.btnClear)
        val btnErase: Button = findViewById(R.id.btnErase)
        etPasscode1 = findViewById(R.id.etPasscode1)
        etPasscode2 = findViewById(R.id.etPasscode2)
        etPasscode3 = findViewById(R.id.etPasscode3)
        etPasscode4 = findViewById(R.id.etPasscode4)
        etInputInfo = findViewById(R.id.etInputInfo)

        val buttonArray = arrayListOf<Button>(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7 ,btn8, btn9, btnClear, btnErase)
        for (button in buttonArray){
            button.setOnClickListener(btnListener)
        }
    }

    // 버튼 클릭 했을때
    private val btnListener = View.OnClickListener { view ->
        var currentValue = -1
        when(view.id){
            R.id.btn0 -> currentValue = 0
            R.id.btn1 -> currentValue = 1
            R.id.btn2 -> currentValue = 2
            R.id.btn3 -> currentValue = 3
            R.id.btn4 -> currentValue = 4
            R.id.btn5 -> currentValue = 5
            R.id.btn6 -> currentValue = 6
            R.id.btn7 -> currentValue = 7
            R.id.btn8 -> currentValue = 8
            R.id.btn9 -> currentValue = 9
            R.id.btnClear -> onClear()
            R.id.btnErase -> onDeleteKey()
        }

        val strCurrentValue = currentValue.toString() // 현재 입력된 번호 String으로 변경
        if (currentValue != -1){
            when {
                etPasscode1.isFocused -> {
                    setEditText(etPasscode1, etPasscode2, strCurrentValue)
                }
                etPasscode2.isFocused -> {
                    setEditText(etPasscode2, etPasscode3, strCurrentValue)
                }
                etPasscode3.isFocused -> {
                    setEditText(etPasscode3, etPasscode4, strCurrentValue)
                }
                etPasscode4.isFocused -> {
                    etPasscode4.setText(strCurrentValue)
                }
            }
        }

        // 비밀번호를 4자리 모두 입력시
        if (etPasscode4.text.isNotEmpty() && etPasscode3.text.isNotEmpty() && etPasscode2.text.isNotEmpty() && etPasscode1.text.isNotEmpty()) {
            inputType(intent.getIntExtra("type", 0))
        }
    }

    // 한 칸 지우기를 눌렀을때
    private fun onDeleteKey() {
        when {
            etPasscode1.isFocused -> {
                etPasscode1.setText("")
            }
            etPasscode2.isFocused -> {
                etPasscode1.setText("")
                etPasscode1.requestFocus()
            }
            etPasscode3.isFocused -> {
                etPasscode2.setText("")
                etPasscode2.requestFocus()
            }
            etPasscode4.isFocused -> {
                etPasscode3.setText("")
                etPasscode3.requestFocus()
            }
        }
    }

    // 모두 지우기
    private fun onClear(){
        etPasscode1.setText("")
        etPasscode2.setText("")
        etPasscode3.setText("")
        etPasscode4.setText("")
        etPasscode1.requestFocus()
    }

    // 입력된 비밀번호를 합치기
    private fun inputedPassword():String {
        return "${etPasscode1.text}${etPasscode2.text}${etPasscode3.text}${etPasscode4.text}"
    }

    // EditText 설정
    private fun setEditText(currentEditText : EditText, nextEditText: EditText, strCurrentValue : String){
        currentEditText.setText(strCurrentValue)
        nextEditText.requestFocus()
        nextEditText.setText("")
    }

    // Intent Type 분류
    private fun inputType(type : Int){
        when(type){
            AppLockConst.ENABLE_PASSLOCK ->{ // 잠금설정
                if(oldPwd.isEmpty()){
                    oldPwd = inputedPassword()
                    onClear()
                    etInputInfo.text = "다시 한번 입력"
                }
                else{
                    if(oldPwd == inputedPassword()){
                        AppLock(this).setPassLock(inputedPassword())
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    else{
                        onClear()
                        oldPwd = ""
                        etInputInfo.text = "비밀번호 입력"
                    }
                }
            }

            AppLockConst.DISABLE_PASSLOCK ->{ // 잠금삭제
                if(AppLock(this).isPassLockSet()){
                    if(AppLock(this).checkPassLock(inputedPassword())) {
                        AppLock(this).removePassLock()
                        setResult(Activity.RESULT_OK)
                        finish()
                    }
                    else {
                        etInputInfo.text = "비밀번호가 틀립니다."
                        onClear()
                    }
                }
                else{
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }

            AppLockConst.UNLOCK_PASSWORD ->
                if(AppLock(this).checkPassLock(inputedPassword())) {
                    setResult(Activity.RESULT_OK)
                    finish()
                }else{
                    etInputInfo.text = "비밀번호가 틀립니다."
                    onClear()
                }

            AppLockConst.CHANGE_PASSWORD -> { // 비밀번호 변경
                if (AppLock(this).checkPassLock(inputedPassword()) && !changePwdUnlock) {
                    onClear()
                    changePwdUnlock = true
                    etInputInfo.text = "새로운 비밀번호 입력"
                }
                else if (changePwdUnlock) {
                    if (oldPwd.isEmpty()) {
                        oldPwd = inputedPassword()
                        onClear()
                        etInputInfo.text = "새로운 비밀번호 다시 입력"
                    } else {
                        if (oldPwd == inputedPassword()) {
                            AppLock(this).setPassLock(inputedPassword())
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            onClear()
                            oldPwd = ""
                            etInputInfo.text = "현재 비밀번호 다시 입력"
                            changePwdUnlock = false
                        }
                    }
                } else {
                    etInputInfo.text = "비밀번호가 틀립니다."
                    changePwdUnlock = false
                    onClear()
                }
            }
        }
    }
}