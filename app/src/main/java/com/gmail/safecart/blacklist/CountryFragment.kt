package com.gmail.safecart.blacklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gmail.safecart.R
import com.gmail.safecart.scanResultLib.CountryList


class CountryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_country, container, false)
        val rv: RecyclerView = view.findViewById(R.id.blCountryRv)
        val countryList = CountryList
        val countries = countryList.getCountries
        val codes = countryList.getCountryCodes
        val adapter = CountryListAdapter(this.requireContext(), countries,codes)

        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this.context)
        rv.adapter = adapter
        return view
    }
}