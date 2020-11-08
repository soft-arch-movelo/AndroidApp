package com.example.movelo.layout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.movelo.R
import kotlinx.android.synthetic.main.b_user_menu.*
import kotlinx.android.synthetic.main.registration_fragment.*

class BUserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.b_user_menu, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startRoute.setOnClickListener(View.OnClickListener {
            findNavController().navigate(R.id.go_to_map)
        })
    }
    companion object {
        fun newInstance(): BUserFragment {
            val fragment = BUserFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}