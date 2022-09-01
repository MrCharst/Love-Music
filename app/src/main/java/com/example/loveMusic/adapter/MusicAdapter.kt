package com.example.loveMusic.adapter

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import com.example.loveMusic.adapter.MusicAdapter.MyHolder
import com.example.loveMusic.databinding.DetailsViewBinding
import com.example.loveMusic.databinding.ItemMusicBinding
import com.example.loveMusic.databinding.MoreFeaturesBinding
import com.example.loveMusic.model.Music
import com.example.loveMusic.model.formatDuration
import com.example.loveMusic.model.setDialogBtnBackground
import com.example.loveMusic.ui.main.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MusicAdapter(
    private val context: Context,
    private var musicList: ArrayList<Music>,
    private val playlistDetails: Boolean = false,
    private val selectionActivity: Boolean = false
) : RecyclerView.Adapter<MyHolder>() {

    class MyHolder(binding: ItemMusicBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.songNameMV
        val album = binding.songAlbumMV
        val image = binding.imageMV
        val duration = binding.songDuration
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemMusicBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.album.text = musicList[position].album
        holder.duration.text = formatDuration(musicList[position].duration)
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(
                RequestOptions().placeholder(R.drawable.love_music_icon_slash_screen).centerCrop()
            )
            .into(holder.image)

        //for play next feature
        if (!selectionActivity)
            holder.root.setOnLongClickListener {
                val customDialog =
                    LayoutInflater.from(context).inflate(R.layout.more_features, holder.root, false)
                val bindingMF = MoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                bindingMF.AddToPNBtn.setOnClickListener {
                    try {
                        if(PlayNext.playNextList.isEmpty()){
                            PlayNext.playNextList.add(PlayerActivity.musicListPA[PlayerActivity.songPosition])
                            PlayerActivity.songPosition = 0
                            Toast.makeText(context, context.getString(R.string.dathembaihatvaodanhsachphat), Toast.LENGTH_SHORT).show()
                        }
                        Toast.makeText(context, context.getString(R.string.dathembaihatvaodanhsachphat), Toast.LENGTH_SHORT).show()
                        PlayNext.playNextList.add(musicList[position])
                        PlayerActivity.musicListPA = ArrayList()
                        PlayerActivity.musicListPA.addAll(PlayNext.playNextList)

                    } catch (e: Exception) {
                        Snackbar.make(context, holder.root, context.getString(R.string.phatbaihatdautien), 3000).show()
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

        when {
            playlistDetails -> {
                holder.root.setOnClickListener {
                    sendIntent(ref = "PlaylistDetailsAdapter", pos = position)
                }
            }
            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position]))
                        holder.root.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.cool_pink
                            )
                        )
                    else
                        holder.root.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )

                }
            }
            else -> {
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> sendIntent(
                            ref = "MusicAdapterSearch",
                            pos = position
                        )
                        musicList[position].id == PlayerActivity.nowPlayingId ->
                            sendIntent(ref = "NowPlaying", pos = PlayerActivity.songPosition)
                        else -> sendIntent(ref = "MusicAdapter", pos = position)
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    fun updateMusicList(searchList: ArrayList<Music>) {
        musicList = ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }

    private fun sendIntent(ref: String, pos: Int) {
        val intent = Intent(context, PlayerActivity::class.java)
        intent.putExtra("index", pos)
        intent.putExtra("class", ref)
        ContextCompat.startActivity(context, intent, null)
    }

    private fun addSong(song: Music): Boolean {
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if (song.id == music.id) {
                PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.removeAt(
                    index
                )
                return false
            }
        }
        PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist.add(song)
        return true
    }

    fun refreshPlaylist() {
        musicList = ArrayList()
        musicList = PlaylistActivity.musicPlaylist.ref[PlaylistDetails.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }
}