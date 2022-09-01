package com.example.loveMusic.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.loveMusic.R
import com.example.loveMusic.databinding.DetailsViewBinding
import com.example.loveMusic.databinding.FavouriteViewBinding
import com.example.loveMusic.databinding.MoreFeaturesBinding
import com.example.loveMusic.model.Music
import com.example.loveMusic.model.setDialogBtnBackground
import com.example.loveMusic.ui.main.PlayNext
import com.example.loveMusic.ui.main.PlayerActivity
import com.example.loveMusic.ui.main.PlaylistActivity
import com.example.loveMusic.ui.main.PlaylistDetails
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class FavouriteAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    val playNext: Boolean = false
) : RecyclerView.Adapter<FavouriteAdapter.MyHolder>() {

    class MyHolder(binding: FavouriteViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImgFV
        val name = binding.songNameFV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.love_music_icon_slash_screen).centerCrop()
            )
            .into(holder.image)

        //when play next music is clicked
        if (playNext) {
            holder.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "PlayNext")
                ContextCompat.startActivity(context, intent, null)
            }
            holder.root.setOnLongClickListener {
                val customDialog =
                    LayoutInflater.from(context).inflate(R.layout.more_features, holder.root, false)
                val bindingMF = MoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                bindingMF.AddToPNBtn.text = context.getString(R.string.Xoa)
                bindingMF.AddToPNBtn.setOnClickListener {
                    if(position == PlayerActivity.songPosition)
                        Snackbar.make((context as Activity).findViewById(R.id.linearLayoutPN),
                            context.getString(R.string.khongthexoabaihatdangphat), Snackbar.LENGTH_SHORT).show()
                    else{
                        Log.d("position","hihi $position")
                        Toast.makeText(context, context.getString(R.string.daxoabaihatkhoidanhsachtiep), Toast.LENGTH_SHORT).show()
                        if(PlayerActivity.songPosition < position && PlayerActivity.songPosition != 0) --PlayerActivity.songPosition
                        PlayNext.playNextList.removeAt(position)
                        PlayerActivity.musicListPA.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    dialog.dismiss()
                }

                bindingMF.infoBtn.setOnClickListener {
                    dialog.dismiss()
                    val detailsDialog = LayoutInflater.from(context)
                        .inflate(R.layout.details_view, bindingMF.root, false)
                    val binder = DetailsViewBinding.bind(detailsDialog)
                    binder.detailsTV.setTextColor(Color.WHITE)
                    binder.root.setBackgroundColor(Color.TRANSPARENT)
                    val dDialog = MaterialAlertDialogBuilder(context)
//                        .setBackground(ColorDrawable(0x99000000.toInt()))
                        .setView(detailsDialog)
                        .setPositiveButton(R.string.ok) { self, _ -> self.dismiss() }
                        .setCancelable(false)
                        .create()
                    dDialog.show()
                    dDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    setDialogBtnBackground(context, dDialog)
                    dDialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                    val str = SpannableStringBuilder().bold { append("${context.getString(R.string.DETAILS)}\n\n${context.getString(R.string.Name)} ") }
                        .append(musicList[position].title)
                        .bold { append("\n\n${context.getString(R.string.Duration)} ") }
                        .append(DateUtils.formatElapsedTime(musicList[position].duration / 1000))
                        .bold { append("\n\n${context.getString(R.string.Location )} ") }.append(musicList[position].path)
                    binder.detailsTV.text = str
                }
                return@setOnLongClickListener true
            }
        } else {
            holder.root.setOnClickListener {
                val intent = Intent(context, PlayerActivity::class.java)
                intent.putExtra("index", position)
                intent.putExtra("class", "FavouriteAdapter")
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFavourites(newList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }
    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

}