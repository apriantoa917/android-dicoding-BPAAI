package com.aprianto.dicostory.data.viewmodel

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aprianto.dicostory.data.model.Login
import com.aprianto.dicostory.data.model.Register
import com.aprianto.dicostory.data.repository.remote.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthViewModel : ViewModel() {

    var loading = MutableLiveData(View.GONE)
    val error = MutableLiveData("")
    val tempEmail = MutableLiveData("") // hold email to saved with user preferences

    /* for handle API response */
    val loginResult = MutableLiveData<Login>()
    val registerResult = MutableLiveData<Register>()

    private val TAG = AuthViewModel::class.simpleName

    fun login(email: String, password: String) {
        tempEmail.postValue(email) // temporary hold email for save user preferences
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().doLogin(email, password)
        client.enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful) {
                    loginResult.postValue(response.body())
                } else {
                    val errorBody: Login? = response.body()
                    error.postValue(errorBody?.message)
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }

    fun register(name: String, email: String, password: String) {
        loading.postValue(View.VISIBLE)
        val client = ApiConfig.getApiService().doRegister(name, email, password)
        client.enqueue(object : Callback<Register> {
            override fun onResponse(call: Call<Register>, response: Response<Register>) {
                if (response.isSuccessful) {
                    registerResult.postValue(response.body())
                } else {
                    val errorBody: Register? = response.body()
                    error.postValue(errorBody?.message)
                }
                loading.postValue(View.GONE)
            }

            override fun onFailure(call: Call<Register>, t: Throwable) {
                loading.postValue(View.GONE)
                Log.e(TAG, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }
        })
    }
}