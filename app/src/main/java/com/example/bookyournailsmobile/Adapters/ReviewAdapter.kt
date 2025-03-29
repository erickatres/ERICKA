package com.example.bookyournailsmobile.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookyournailsmobile.NetUtils.ApiService
import com.example.bookyournailsmobile.R

class ReviewAdapter(private val reviews: List<ApiService.Review>) :
    RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val reviewerName: TextView = view.findViewById(R.id.tvReviewerName)
        val reviewText: TextView = view.findViewById(R.id.tvReviewText)
        val ratingBar: RatingBar = view.findViewById(R.id.reviewRatingBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.reviewerName.text = "${review.first_name} ${review.last_name}"
        holder.reviewText.text = review.description
        holder.ratingBar.rating = review.rating

        holder.ratingBar.scaleX = 0.4f
        holder.ratingBar.scaleY = 0.4f
    }

    override fun getItemCount() = reviews.size
}
