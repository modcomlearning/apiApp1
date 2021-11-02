package com.modcom.mytabs.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.modcom.mytabs.R
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONArray
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProductFragment : Fragment() {
    // TODO: Rename and change types of parameters
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_product, container, false)
        val phone = root.findViewById(R.id.phone) as EditText
        val submit = root.findViewById(R.id.submit) as Button
        val progreess = root.findViewById(R.id.progress) as ProgressBar
        progreess.visibility = View.GONE
        submit.setOnClickListener {
            progreess.visibility = View.VISIBLE
            val client = AsyncHttpClient(true, 80,443)
            val jsonParams = JSONObject()
            jsonParams.put("phone", phone.text.toString())
            //post the to your API
            //convert above json to back to string
            val data = StringEntity(jsonParams.toString())
            client.post(activity,
            "https://modcom.pythonanywhere.com/api/search_post",
            data,"application/json",  object : JsonHttpResponseHandler()
                {
                    override fun onSuccess(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        response: JSONArray
                    ) {
                        val textview = root.findViewById(R.id.data) as TextView
                        textview.text = ""
                        if(statusCode == 200) {
                            for (i in 0 until response.length()) {
                                val json = response.getJSONObject(i)
                                val firstname = json.optString("firstname").toString()
                                val lastname = json.optString("lastname").toString()
                                val phone = json.optString("phone").toString()
                                val request = json.optString("request").toString()
                                val residence = json.optString("residence").toString()
                                val textview = root.findViewById(R.id.data) as TextView
                                textview.append("$firstname\n $lastname\n $phone\n $request\n $residence\n")
                                textview.append("\n")
                                progreess.visibility = View.GONE
                            }//end for
                        }
                        else {
                            for (i in 0 until response.length()) {
                                val json = response.getJSONObject(i)
                                val textview = root.findViewById(R.id.data) as TextView
                                val msg = json.optString("msg").toString()
                                textview.append("$msg \n")
                                textview.append("\n")
                                progreess.visibility = View.GONE
                            }//end for
                        }//end else
                        //super.onSuccess(statusCode, headers, response)
                    }
                    override fun onFailure(
                        statusCode: Int,
                        headers: Array<out Header>?,
                        responseString: String?,
                        throwable: Throwable?
                    ) {
                        //super.onFailure(statusCode, headers, responseString, throwable)
                    }

                }
            )//end client post
        }//end listener
        return root
    }

}