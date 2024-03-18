package com.example.clockcustomview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import com.example.clockcustomview.ClockView

class SecondExampleFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_second_example, container, false)
        val button = view.findViewById<Button>(R.id.run_st_button)
        button.setOnClickListener {
            val clock = view.findViewById<ClockView>(R.id.clockView_st)
            if (clock.secondHandColor == ContextCompat.getColor(requireActivity(), R.color.black))
                clock.secondHandColor =
                    ContextCompat.getColor(requireActivity(), R.color.black)
            else
                clock.secondHandColor = ContextCompat.getColor(requireActivity(), R.color.black)
        }
        return view
    }
}