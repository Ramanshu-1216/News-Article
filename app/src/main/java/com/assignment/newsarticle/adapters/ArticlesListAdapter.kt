package com.assignment.newsarticle.adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.assignment.newsarticle.R
import com.assignment.newsarticle.model.ArticleModel
import com.assignment.newsarticle.utils.ImageCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.coroutines.suspendCoroutine
import kotlin.math.log

class ArticlesListAdapter(private var articlesList: List<ArticleModel>, private var listener: OnItemClickListener):RecyclerView.Adapter<ArticlesListAdapter.ViewHolder>() {
    private val imageCache = ImageCache()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticlesListAdapter.ViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.article_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticlesListAdapter.ViewHolder, position: Int) {
        val article = articlesList[position]
        holder.titleTV.text = article.title
        val cachedImage = imageCache.getCachedImage(article.urlToImage)
        Log.d("CachedImage", "onBindViewHolder: $cachedImage")
        if(cachedImage != null){
            holder.imageView.setImageBitmap(cachedImage)
        }
        else{
            loadImage(article.urlToImage, holder.imageView)
        }
        holder.descriptionTV.setText(article.description)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val time = dateFormat.parse(article.publishedAt)
            val format = SimpleDateFormat("MMM dd yyyy HH:mm")
            holder.timeTV.text = format.format(time).toString()
        }
        catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return articlesList.size
    }

    fun updateArticlesList(newList: List<ArticleModel>){
        articlesList = newList
        notifyDataSetChanged()
    }
    fun getArticlesList() : List<ArticleModel>{
        return articlesList
    }
    fun setOnClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    inner class ViewHolder(articleView : View): RecyclerView.ViewHolder(articleView){
        val titleTV: TextView = articleView.findViewById(R.id.article_title)
        val imageView: ImageView = articleView.findViewById(R.id.rounded_image_view)
        val descriptionTV : TextView = articleView.findViewById(R.id.description)
        private val articleContainer : ConstraintLayout = articleView.findViewById(R.id.article_container)
        val timeTV :TextView = articleView.findViewById(R.id.time)
        private val clickToViewMoreTV: TextView = articleView.findViewById(R.id.click)
        init {
            imageView.setImageDrawable(ContextCompat.getDrawable(articleView.context, R.drawable.ic_launcher_foreground))
            articleView.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    if(descriptionTV.maxLines == 3){
                        titleTV.textSize = 20f
                        titleTV.maxLines = 10
                        descriptionTV.maxLines = 10
                        descriptionTV.textSize = 15f
                        descriptionTV.setTextColor(ContextCompat.getColor(articleView.context, R.color.light))
                        articleContainer.setBackgroundColor(ContextCompat.getColor(articleView.context, R.color.dark))
                        articlesList[position].expanded = true
                        clickToViewMoreTV.visibility = View.VISIBLE
                    }
                    else{
                        descriptionTV.maxLines = 3
                        titleTV.textSize = 14f
                        titleTV.maxLines = 3
                        descriptionTV.textSize = 14f
                        descriptionTV.setTextColor(ContextCompat.getColor(articleView.context, R.color.faint))
                        articleContainer.setBackgroundColor(ContextCompat.getColor(articleView.context, R.color.transparent))
                        listener.onItemClick(position)
                        articlesList[position].expanded = false
                        clickToViewMoreTV.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun loadImage(imageURL: String, imageView: ImageView){
        GlobalScope.launch(Dispatchers.Main){
            var bitmap = loadImageFromURL(imageURL)
            if(bitmap != null){
                imageView.setImageBitmap(bitmap)
            }
        }
    }

    private suspend fun loadImageFromURL(imageURL: String):Bitmap? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        var bitmap: Bitmap? = null

        try {
            val url = URL(imageURL)
            connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()

            val inputStream = connection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
            imageCache.setCachedImage(imageURL, bitmap)
        }
        catch (e:IOException){
            e.printStackTrace()
        }
        finally {
            connection?.disconnect()
        }
        bitmap
    }

    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
}
