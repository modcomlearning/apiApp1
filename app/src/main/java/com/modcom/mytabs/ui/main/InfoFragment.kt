package com.modcom.mytabs.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.modcom.mytabs.R
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragment : Fragment() {
    // TODO: Rename and change types of parameter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_info, container, false)
        val firstname = root.findViewById(R.id.firstname) as EditText
        val lastname = root.findViewById(R.id.lastname) as EditText
        val residence = root.findViewById(R.id.residence) as EditText
        val phone = root.findViewById(R.id.phone) as EditText
        val request = root.findViewById(R.id.request) as EditText
        val submit = root.findViewById(R.id.submit) as Button
        val progress = root.findViewById(R.id.progress) as ProgressBar
        progress.visibility = View.GONE   //make progress go until button is pressed
        submit.setOnClickListener {
            //show progress
            progress.visibility = View.VISIBLE

            val client = AsyncHttpClient(true, 80,443)
            val jsonParams = JSONObject()
            jsonParams.put("firstname", firstname.text.toString())
            jsonParams.put("lastname", lastname.text.toString())
            jsonParams.put("residence", residence.text.toString())
            jsonParams.put("phone", phone.text.toString())
            jsonParams.put("request", request.text.toString())
            //post the to your API
            //convert above json to back to string
            val data = StringEntity(jsonParams.toString())
            client.post(activity,
                "https://modcom.pythonanywhere.com/post",
                data, "application/json",
                object : JsonHttpResponseHandler()
                {
                    override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?
                    ) {
                        if(statusCode == 200){
                            Toast.makeText(activity, ""+response, Toast.LENGTH_LONG).show()
                            progress.visibility = View.GONE
                        }
                        else {
                            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show()
                            progress.visibility = View.GONE
                        }

                    }//end success
                    override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?,
                                           throwable: Throwable?
                    ) {
                        Toast.makeText(activity, "Code $statusCode Try Again",
                            Toast.LENGTH_LONG).show()
                        progress.visibility = View.GONE
                    }//end on failure

                })//end post
        }//end listener



        // Inflate the layout for this fragment
        return root
    }

}