package com.example.dailytasksamplepoc.kotlinomnicure

import android.app.SearchManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.databinding.ActivityHospitalListBinding
import com.example.dailytasksamplepoc.kotlinomnicure.viewmodel.HospitalListViewModel
import com.example.kotlinomnicure.utils.*
import com.google.gson.Gson
import com.mvp.omnicure.kotlinactivity.utils.ValidationUtil

class ActivityHospitalList : AppCompatActivity() {
    private val TAG = ActivityHospitalList::class.java.simpleName
    protected var binding: ActivityHospitalListBinding? = null
    var selectedHosp = ""
    private var viewModel: HospitalListViewModel? = null
    private var hospitalListAdapter: HospitalListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hospital_list)
        viewModel = ViewModelProviders.of(this).get(HospitalListViewModel::class.java)
        val extras = intent.extras
        selectedHosp = extras!!.getString(Constants.IntentKeyConstants.SELECTED_HOSPITAL, "")
        initViews()
    }


    private fun isValid(): Boolean {
        val errMsg: String? = binding?.let { ValidationUtil().isValidate(it) }
        if (errMsg != null) {
            if (!errMsg.isEmpty()) {
                CustomSnackBar.make(
                    binding.containerLayout,
                    this,
                    CustomSnackBar.WARNING,
                    errMsg,
                    CustomSnackBar.TOP,
                    3000,
                    0
                ).show()
                return false
            }
        }
        return true
    }

    private fun initViews() {
        binding.idBackButton.setOnClickListener { view -> finish() }
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvHospitalList.setLayoutManager(linearLayoutManager)
        getHospitalList()
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        binding.searchHospital.setActivated(true)
        binding.searchHospital.setQueryHint(resources.getString(R.string.search_hospital))
        binding.searchHospital.onActionViewExpanded()
        binding.searchHospital.setIconified(false)
        binding.searchHospital.clearFocus()
        binding.searchHospital.setSearchableInfo(
            searchManager
                .getSearchableInfo(componentName)
        )
        binding.searchHospital.setMaxWidth(Int.MAX_VALUE)
        binding.searchHospital.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                hospitalListAdapter.getFilter().filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                hospitalListAdapter.getFilter().filter(query)
                return false
            }
        })
    }

    private fun getHospitalList() {
        val providerId: Long = PrefUtility.getProviderId(this)

//        viewModel.getHospitalList().observe(this, response -> {
        viewModel.getHospitalList().observe(this) { response ->
            dismissProgressBar()
            if (response.getHospitalList() != null && !response.getHospitalList().isEmpty()) {
                Log.d(TAG, "getHospitalList response : " + Gson().toJson(response))
                hospitalListAdapter = HospitalListAdapter(object : HospitalRecyclerListener() {
                    fun onItemSelected(hospital: Hospital) {
                        Log.d(
                            TAG,
                            "selected name : " + hospital.getId()
                                .toString() + "----" + hospital.getName()
                        )
                        val intent = Intent()
                        intent.putExtra("hospitalID", hospital.getId())
                        intent.putExtra("hospitalName", hospital.getName())
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                }, response.getHospitalList(), selectedHosp)
                binding.rvHospitalList.setAdapter(hospitalListAdapter)
                hospitalListAdapter.notifyDataSetChanged()
            } else if (response.getErrorMessage() != null) {
                val errMsg: String? = ErrorMessages().getErrorMessage(
                    this,
                    response.getErrorMessage(),
                    Constants.API.getHospital
                )
                if (errMsg != null) {
                    CustomSnackBar.make(
                        binding.containerLayout, this, CustomSnackBar.WARNING,
                        errMsg, CustomSnackBar.TOP, 3000, 0
                    )?.show()
                }
                Log.d(
                    TAG,
                    "getHospitalList getErrorMessage : " + response.getErrorMessage()
                )
            } else {
                CustomSnackBar.make(
                    binding.containerLayout, this, CustomSnackBar.WARNING,
                    getString(R.string.no_hospital_list), CustomSnackBar.TOP, 3000, 0
                ).show()
            }
        }
    }
}