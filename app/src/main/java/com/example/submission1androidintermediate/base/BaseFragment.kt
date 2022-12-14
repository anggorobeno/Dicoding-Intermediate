package com.example.submission1androidintermediate.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.core.data.local.IDataStore
import com.example.core.data.local.PreferencesDataStoreHelper
import javax.inject.Inject

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    @Inject
    lateinit var preferencesDataStore: IDataStore
    protected val dataStore get() = preferencesDataStore as PreferencesDataStoreHelper
    private lateinit var safeContext: Context

    protected var _binding: VB? = null
    protected val binding: VB get() = _binding!!
    protected abstract val setLayout: (LayoutInflater) -> VB

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = setLayout(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeViewModel()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // Destroy binding to prevent memory leak
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Destroy binding to prevent memory leak
    }


    abstract fun observeViewModel()

    abstract fun initView()

}