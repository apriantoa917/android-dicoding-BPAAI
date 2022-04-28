package com.aprianto.dicostory.ui.dashboard.home

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.aprianto.dicostory.R
import com.aprianto.dicostory.data.model.Story
import com.aprianto.dicostory.databinding.RvStoryBinding
import com.aprianto.dicostory.ui.detail.DetailActivity
import com.aprianto.dicostory.utils.Constanta
import com.aprianto.dicostory.utils.Helper
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<Story, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }

    inner class ViewHolder(private val binding: RvStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(story: Story) {
            with(binding) {
                storyName.text = story.name
                storyUploadTime.text =
                    "🕓 ${itemView.context.getString(R.string.const_text_uploaded)} ${
                        Helper.getTimelineUpload(
                            itemView.context,
                            story.createdAt
                        )
                    }"
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .into(storyImage)
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java)
                    intent.putExtra(Constanta.StoryDetail.UserName.name, story.name)
                    intent.putExtra(Constanta.StoryDetail.ImageURL.name, story.photoUrl)
                    try {
                        intent.putExtra(Constanta.StoryDetail.Latitude.name, story.lat.toString())
                        intent.putExtra(Constanta.StoryDetail.Longitude.name, story.lon.toString())
                    } catch (e: Exception) {
                        /* if story don't have location (lat, lon is null) -> skip put extra*/
                        Log.e(Constanta.TAG_STORY, e.toString())
                    }
                    intent.putExtra(
                        Constanta.StoryDetail.ContentDescription.name,
                        story.description
                    )
                    intent.putExtra(
                        Constanta.StoryDetail.UploadTime.name,
                        /*
                        dynamic set uploaded time locally
                            en : uploaded + on + 30 April 2022 00.00
                            id : diupload + pada + 30 April 2022 00.00
                        */
                        "${itemView.context.getString(R.string.const_text_uploaded)} ${
                            itemView.context.getString(
                                R.string.const_text_time_on
                            )
                        } ${Helper.getUploadStoryTime(story.createdAt)}"

                    )
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            /* transition between recyclerview & activity detail */
                            androidx.core.util.Pair(storyImage, "story_image"),
                            androidx.core.util.Pair(storyName, "user_name"),
                            androidx.core.util.Pair(defaultAvatar, "user_avatar"),
                        )
                    itemView.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }
}