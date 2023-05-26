package com.feiniu.rtnetworklib

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.feiniu.rtnetworklib.databinding.FragmentFirstBinding
import com.feiniu.rtnetworklib.network.ApiService
import com.feiniu.rtnetworklib.network.FNBaseResponse
import com.feiniu.rtnetworklib.network.FNHandleInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
        getRequest()
    }

    /**
     * 简单的get请求
     * baseURl 不可省略
     */
    private fun getRequest() {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.200.48.214:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(ApiService::class.java)
        val request = api.getJsonData()
        request.enqueue(object : Callback<FNBaseResponse<FNHandleInfo>> {
            override fun onResponse(
                call: Call<FNBaseResponse<FNHandleInfo>>,
                response: Response<FNBaseResponse<FNHandleInfo>>
            ) {
                Log.d("success", "onResponse: ${response.body()}")
            }

            override fun onFailure(call: Call<FNBaseResponse<FNHandleInfo>>, t: Throwable) {

                Log.d("api", "onFailure: ")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}