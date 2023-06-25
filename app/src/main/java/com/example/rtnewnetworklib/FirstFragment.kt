package com.example.rtnewnetworklib

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.rtnewnetworklib.databinding.FragmentFirstBinding
import com.example.rtnewnetworklib.login.model.RTLoginParameter
import com.example.rtnewnetworklib.login.service.RTLoginService
import com.google.gson.Gson
import com.orhanobut.logger.Logger
import com.rtmart.rtretrofitlib.RTRetrofitManager
import kotlinx.coroutines.launch

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
        //{
        //	"data": {
        //		"employeeNo": "15721096991",
        //		"passWord": "123456",
        //		"storeId": "1001",
        //		"warehouseNo": "10"
        //	},
        //	"appVersionNo": "9.9.9",
        //	"deviceId": "-1129544574",
        //	"ipAddr": "10.0.2.15",
        //	"osType": "1",
        //	"osVersionNo": "10",
        //	"storeId": "1001",
        //	"token": "41763987-cdd4-43b9-af45-1cfbdbca3340",
        //	"viewSize": "1440*2560",
        //	"warehouseNo": "10"
        //}
        val loginService = RTRetrofitManager.getRequestService(RTLoginService::class.java)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginService.getLogin(RTLoginParameter()).collect {
                    Logger.d("response = ${Gson().toJson(it)}")
                }
            }
        }
        // ------
//        val path = "http://rtmart-mars-pdaapi-x.beta1.fn/app/login/employeeLogin?signature=78291deaf8b5adc3f52fd91942b56beb&idempotent=f9e073523b340ad2359f544989c2bd58"
//        val call = loginService.getLogin(RTLoginParameter())
//        val request = call.request()
//        val requestBody = request.body()
//        val buffer = Buffer()
//        requestBody?.writeTo(buffer)
//        Logger.d(buffer.readUtf8())
//
//        Logger.d("call url = ${call.request().url()}")
//        call.enqueue(object : Callback<ResponseBody> {
//            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                Logger.d(response.body()?.string())
//            }
//
//            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                Logger.d(t.localizedMessage)
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}