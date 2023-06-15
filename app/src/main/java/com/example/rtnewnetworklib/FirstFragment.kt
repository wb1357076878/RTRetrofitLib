package com.example.rtnewnetworklib

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.rtnewnetworklib.databinding.FragmentFirstBinding
import com.example.rtnewnetworklib.network.RTLoginData
import com.example.rtnewnetworklib.network.RTLoginService
import com.orhanobut.logger.Logger
import com.rtmart.rtretrofitlib.RTRetrofitManager
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        // test
        val loginService = RTRetrofitManager.getRequestService(RTLoginService::class.java)
        val path = "http://rtmart-mars-pdaapi-x.beta1.fn/app/login/employeeLogin?signature=78291deaf8b5adc3f52fd91942b56beb&idempotent=f9e073523b340ad2359f544989c2bd58"
        val call = loginService.getLogin(path, RTLoginData())
        val request = call.request()
        val requestBody = request.body()
        val buffer = Buffer()
        requestBody?.writeTo(buffer)
        Logger.d(buffer.readUtf8())

        Logger.d("call url = ${call.request().url()}")
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Logger.d(response.body()?.string())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Logger.d(t.localizedMessage)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}