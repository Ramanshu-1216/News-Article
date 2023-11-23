package com.assignment.newsarticle

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.assignment.newsarticle.adapters.ArticlesListAdapter
import com.assignment.newsarticle.databinding.FragmentHomeBinding
import com.assignment.newsarticle.dialog.FilterDialog
import com.assignment.newsarticle.model.ArticleModel
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

class HomeFragment : Fragment(), ArticlesListAdapter.OnItemClickListener {
    private val TAG: String? = "HomeFragment"
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeBinding
    private lateinit var response: JSONObject
    private lateinit var adapter: ArticlesListAdapter
    private var articlesList = listOf<ArticleModel>()
    private lateinit var filterDialog: FilterDialog
    private var sourcesList = mutableListOf<String>("All")
    private lateinit var filterList: List<ArticleModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loading.visibility = View.VISIBLE
        binding.darkMode.setOnClickListener{
            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        viewModel.apiResponse.observe(requireActivity(), Observer { res ->
            try{
                response = JSONObject(res)
                if(response.getString("status").equals("ok")){
                    binding.loading.visibility = View.GONE
                    var articlesString : String = response.getString("articles")
                    val gson = Gson()
                    val listType = object  : TypeToken<List<ArticleModel>>() {}.type
                    articlesList = gson.fromJson(articlesString, listType)
                    adapter?.updateArticlesList(articlesList)
                    filterList = articlesList

                    val uniqueSources = articlesList.distinctBy { it.source.id }.map { it.source.name }
                    sourcesList.addAll(uniqueSources)
                    sourcesList.forEach { it ->
                        binding.chipGroup.addView(createChip(it))
                    }
                    val all = binding.chipGroup.getChildAt(0) as Chip
                    all.isChecked = true
                }
                else{
                    binding.loading.text = "Something went wrong"
                }
            }
            catch (e: Exception){
                e.printStackTrace()
            }
        })
        if(articlesList.isEmpty()){
            viewModel.fetchData()
        }
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager

        adapter = ArticlesListAdapter(articlesList, this)
        binding.recyclerView.adapter = adapter

        filterDialog = FilterDialog(requireContext()){selectedOption ->
            if(articlesList.isNotEmpty()){
                when(selectedOption){
                    0 -> {
                        val allChip = binding.chipGroup.getChildAt(0) as Chip
                        allChip.isChecked = true
                    }
                    1 -> {
                        val sortedList = filterList.sortedBy { it.publishedAt?.let { it1 -> parseDateString(it1) } }
                        adapter.updateArticlesList(sortedList)
                    }
                    2 -> {
                        val sortedList = filterList.sortedByDescending { it.publishedAt?.let { it1 -> parseDateString(it1) } }
                        adapter.updateArticlesList(sortedList)
                    }
                }
            }
            else{
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.chipGroup.setOnCheckedStateChangeListener{_, checkedId ->
            if(checkedId[0] == 1){
                filterList = articlesList
                adapter.updateArticlesList(filterList)
            }
            else{
                if(checkedId[0] < sourcesList.size){
                    filterList = articlesList.filter { it.source.name == sourcesList[checkedId[0] - 1] }
                    adapter.updateArticlesList(filterList)
                }
                else{
                    Toast.makeText(requireContext(), "Something went wrong, restart the app", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.filterButton.setOnClickListener{
            filterDialog?.show()
        }
    }

    override fun onItemClick(position: Int) {
        val webViewFragment = WebViewFragment().apply {
            arguments = Bundle().apply {
                putString("url", filterList[position].url)
                putBoolean("techcrunch", filterList[position].source.id == "techcrunch")
            }
        }
        val transaction: FragmentTransaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.fragment_container, webViewFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun parseDateString(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        try{
            return dateFormat.parse(dateString)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return null
    }

    private fun createChip(source: String): Chip{
        val chip = Chip(requireContext())
        chip.text = source
        chip.id = View.generateViewId()
        chip.isCheckable = true
        chip.setOnClickListener {
            chip.isChecked = true
        }
        return chip
    }
}