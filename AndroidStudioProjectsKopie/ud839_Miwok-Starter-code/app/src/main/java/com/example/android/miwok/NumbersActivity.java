package com.example.android.miwok;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class NumbersActivity extends AppCompatActivity {
    // Handles playback of all the sound files
    private MediaPlayer mMediaPlayer;
    //Handles audio focus when playing a sound file
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_list);

        // Create and setup the (@link AudioManager) to reqest audio focus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Create an array of words, (shows in Android Monitor)
        //ArrayList<String> words = new ArrayList<String>();
        //words.add("one");
        //words.add("two");
        //words.add("three");
        //words.add("four");
        //words.add("five");
        //words.add("six");
        //words.add("seven");
        //words.add("eight");
        //words.add("nine");
        //words.add("ten");
        final ArrayList<Word> words = new ArrayList<Word>();
        words.add(new Word("one", "lutti", R.drawable.number_one, R.raw.number_one));
        words.add(new Word("two", "otiiko", R.drawable.number_two, R.raw.number_two));
        words.add(new Word("three", "tolookosu", R.drawable.number_three, R.raw.number_three));
        words.add(new Word("four", "oyyisa", R.drawable.number_four, R.raw.number_four));
        words.add(new Word("five", "masokka", R.drawable.number_five, R.raw.number_five));
        words.add(new Word("six", "temmokka", R.drawable.number_six, R.raw.number_six));
        words.add(new Word("seven", "kenekaku", R.drawable.number_seven, R.raw.number_seven));
        words.add(new Word("eight", "kawinta", R.drawable.number_eight, R.raw.number_eight));
        words.add(new Word("nine", "wo'e", R.drawable.number_nine, R.raw.number_nine));
        words.add(new Word("ten", "na'aacha", R.drawable.number_ten, R.raw.number_ten));

        //for (int i = 0; i < 10; ++i) {
        //    Log.v("NumberActivity", "Word at index " + i + ": " + words.get(i));
        //}

        // Create a TextView programaticly
        //LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        //TextView wordView;
        //for (int i = 0; i < words.size(); i++) {
        //    wordView = new TextView(this);
        //    wordView.setText(words.get(i));
        //    rootView.addView(wordView);
        //}

        // Switch over to ListView
        //ArrayAdapter<Word> itemsAdapter = new ArrayAdapter<Word>(this, R.layout.list_item, words);
        WordAdapter adapter = new WordAdapter(this, words, R.color.category_numbers);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(adapter);

        // Test a GridView
        //ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, words);
        //GridView gridView = (GridView) findViewById(R.id.gridview);
        //gridView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Release the media player if it currently esists because we are about to
                // play a different sound file.
                releaseMediaPlayer();

                // Get the (@link Word) object at the given position the user clicked on.
                Word word = words.get(position);

                //mMediaPlayer = MediaPlayer.create(NumbersActivity.this, word.getAudioResourceId());
                //Log.v("NumbersActivity", "mMediaPlayer = newly created");
                // Request audio focus so in order to play the audio file. The app needs to play a
                // short audio file, so we will request audio focus with a short amount of time
                // with AUDIOFOCUS_GAIN_TRANSIENT.
                int result = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                        AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

                // Start the adio file.
                //mMediaPlayer.start();
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                    // Setup a listener on the media player, so that we can stio and release the
                    // media player once the sound has finished playing.
                    //mMediaPlayer.setOnCompletionListener(mCompletionListener);
                    // Create and setup the (@link MediaPlayer) for the audio resourdce associated
                    // with the current word
                    mMediaPlayer = MediaPlayer.create(NumbersActivity.this, word.getAudioResourceId());

                    // Start the audio file.
                    mMediaPlayer.start();

                    // Setup a listener on the media player, so that we can stop and release the
                    // media player once the sound has finished playing.
                    mMediaPlayer.setOnCompletionListener(mCompletionListener);
                }
            }
        });
    }

    /**
     * This listener gets triggered when the (@link MediaPlayer) has completed
     * playing the adio file.
     */
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            // Now that the sound file has finished playing, release the media player resources.
            releaseMediaPlayer();
        }

    };

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release ots resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null, for our code, we've decided that
            // setting the media player to null is an easy way to tell the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
            //Log.v("NumbersActivity", "mMediaPlayer = null");

            // Regardless of whether or not we were granted audio focus , abandon it. This also
            // nregisters the AudioFocusChangeListener so we don't get anymore callbacks.
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
        Log.v("NmbersActivity", "onStop() releaseMediaPlayer()");
    }

    /**
     * This listener gets triggered whenever the audio focus changes
     * (i.e., we gain or lose audio focus because of another app or device)
     */
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                    focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // The AUDIOFOCUS_LOSS_TRANSIENT case means taht we' ve lost audio focus for a
                // short amount of time. The AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK case means that
                // our app is allowed to continue playing sound but on a lower volume. We'll treat
                // both case the same way because our app is playing short sound files.

                // Pause playback and reset player to the start of the file. That way, we can
                // play the word from the beginning when we resume playback.
                mMediaPlayer.pause();
                mMediaPlayer.seekTo(0);

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // The AUDIOFOCUS_GAIN case means we have regained focus and can resume playback.
                mMediaPlayer.start();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // The AUDIOFOCUS_LOSS case means we' ve lost audio focus and
                // stop playback and clean up resources
                releaseMediaPlayer();
            }
        }
    };
}
