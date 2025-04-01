package com.abapp.soundplay.ViewHalper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.abapp.soundplay.R;
import com.abapp.soundplay.Helper.MediaMetaData;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.util.Objects;

public class SongInfoView {

    Context context;
    MediaMetaData mediaMetaData;

    public SongInfoView(Context context , File uri , MediaMetaData mediaMetaData){
        this.context = context;
        this.mediaMetaData = mediaMetaData;

        if (uri == null){
            Toast.makeText(context, "Something went wrong!!!", Toast.LENGTH_SHORT).show();
        }else {
            songInfoDialog(uri);
        }
    }


    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    public void songInfoDialog(File uri) {
        if (uri == null || !uri.exists() || !uri.isFile() || !uri.canRead()) {
            Toast.makeText(context, "Invalid file! Please select a valid song.", Toast.LENGTH_SHORT).show();
            return;
        }

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context , R.style.BottomSheetDialogStyle );
        bottomSheetDialog.setContentView(R.layout.dialog_song_info);

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri.toString());

        ImageView albumArt = bottomSheetDialog.findViewById(R.id.songInfoAlbumArt);

        TextView title = bottomSheetDialog.findViewById(R.id.titleSongInfo);
        TextView artist = bottomSheetDialog.findViewById(R.id.artistSongInfo);
        TextView genre = bottomSheetDialog.findViewById(R.id.genreSongInfo);
        TextView album = bottomSheetDialog.findViewById(R.id.albumSongInfo);
        TextView size = bottomSheetDialog.findViewById(R.id.sizeSongInfo);
        TextView songDuration = bottomSheetDialog.findViewById(R.id.songDurationSongInfo);
        TextView path = bottomSheetDialog.findViewById(R.id.pathSongInfo);
        TextView bitRate = bottomSheetDialog.findViewById(R.id.bitrateSongInfo);


        //set bitmap on songInfo


        assert albumArt != null;
        if (mediaMetaData.getSongBitmap(uri) != null) {
            albumArt.setImageBitmap(mediaMetaData.getSongBitmap(uri));
        } else albumArt.setImageResource(R.drawable.baseline_music_note_24);


//        title.setText(uri.getName());
        assert title != null;
        title.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
        assert artist != null;
        artist.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        assert genre != null;
        genre.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
        assert album != null;
        album.setText(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
        assert path != null;
        path.setText(uri.toString());

        //set song size
        float l = Float.parseFloat(String.valueOf(uri.length()));
        float a = (l / 1024) / 1024;
        assert size != null;
        size.setText(String.format("%.2f", a) + " MB");

        long bit = Long.parseLong(Objects.requireNonNull(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)));
        assert bitRate != null;
        bitRate.setText(bit / 1000 + " kbps");

        //set Duration song
        long dur = Long.parseLong(Objects.requireNonNull(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
        String Sec = String.valueOf((dur % 60000) / 1000);
        String min = String.valueOf(dur / 60000);

        assert songDuration != null;
        songDuration.setText((Sec.length() == 1)? min + ":0" + Sec : min + ":" + Sec);

        bottomSheetDialog.show();

    }

}
