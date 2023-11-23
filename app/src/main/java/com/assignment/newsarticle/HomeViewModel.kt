package com.assignment.newsarticle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class HomeViewModel : ViewModel() {
    private val _apiResponse = MutableLiveData<String>()
    val apiResponse: LiveData<String>
        get() = _apiResponse

    fun fetchData(){
        GlobalScope.launch(Dispatchers.IO){
            try{
                val response = makeAPICall();
                launch(Dispatchers.Main){
                    _apiResponse.value = response;
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    private fun makeAPICall() : String{
        val apiURL = "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json";
        try{
            val url = URL(apiURL)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            val responseCode = connection.responseCode
            if(responseCode == HttpURLConnection.HTTP_OK){
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line:String?
                while (reader.readLine().also { line = it } != null){
                    response.append(line)
                }
                connection.disconnect()
                return response.toString()
            }
            else{
                return "Request error with response code $responseCode"
            }
        }
        catch (e: Exception){
            return "$e.message"
        }
    }
}