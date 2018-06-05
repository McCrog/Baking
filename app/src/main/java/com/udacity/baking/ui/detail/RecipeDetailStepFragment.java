/*
 * Copyright (C) 2018. The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.udacity.baking.ui.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.Guideline;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.baking.R;
import com.udacity.baking.model.Step;
import com.udacity.baking.utilities.InjectorUtils;
import com.udacity.baking.viewmodel.detail.DetailViewModel;
import com.udacity.baking.viewmodel.detail.DetailViewModelFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.baking.utilities.Constants.ID_TAG;
import static com.udacity.baking.utilities.Constants.STEP_TAG;

/**
 * Created by alex on 12/05/2018.
 */

public class RecipeDetailStepFragment extends Fragment implements Player.EventListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = RecipeDetailStepFragment.class.getSimpleName();

    @BindView(R.id.step_description_tv)
    TextView mStepDescription;
    @BindView(R.id.player_view)
    PlayerView mPlayerView;
    @BindView(R.id.horizontal_half)
    Guideline mGuideline;
    @BindView(R.id.refresh_step)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int mId = 0;
    private int mStepIndex = 0;
    private Step mStep;

    private long playbackPosition;
    private boolean playbackReady = true;
    private int currentWindow;
    private static final String PLAYER_POSITION = "PLAYER_POSITION";
    private static final String PLAYBACK_READY = "PLAYBACK_READY";
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private SimpleExoPlayer mExoPlayer;
    private Snackbar mSnackbar;

    public RecipeDetailStepFragment() {
    }

    public static RecipeDetailStepFragment newInstance(int id, int stepIndex) {
        RecipeDetailStepFragment fragment = new RecipeDetailStepFragment();
        // Set the bundle arguments for the fragment.
        Bundle arguments = new Bundle();
        arguments.putInt(ID_TAG, id);
        arguments.putInt(STEP_TAG, stepIndex);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            playbackPosition = savedInstanceState.getLong(PLAYER_POSITION);
            playbackReady = savedInstanceState.getBoolean(PLAYBACK_READY);
        }

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(ID_TAG)) {
                mId = bundle.getInt(ID_TAG);
                mStepIndex = bundle.getInt(STEP_TAG);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail_step, container, false);

        ButterKnife.bind(this, rootView);

        initObserver();

        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Return the root view
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            playbackReady = mExoPlayer.getPlayWhenReady();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
        }

        outState.putLong(PLAYER_POSITION, playbackPosition);
        outState.putBoolean(PLAYBACK_READY, playbackReady);
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == Player.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == Player.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        mSnackbar = Snackbar.make(getActivity().findViewById(R.id.refresh_step),
                                R.string.video_error, Snackbar.LENGTH_INDEFINITE);
        mSnackbar.setAction(R.string.retry, snackbarOnClickListener);
        mSnackbar.setActionTextColor(getResources().getColor(R.color.lightRed));
        mSnackbar.show();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);

        reinitializePlayer();

        mSwipeRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    View.OnClickListener snackbarOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mSnackbar.dismiss();
            reinitializePlayer();
        }
    };

    private void initObserver() {
        DetailViewModelFactory mFactory = InjectorUtils.provideDetailViewModelFactory(this.getContext(), mId);
        DetailViewModel mViewModel = ViewModelProviders.of(this, mFactory).get(DetailViewModel.class);

        mViewModel.getRecipe().observe(this, recipe -> {
            mStep = recipe.getSteps().get(mStepIndex);
            initUI(mStep);
        });
    }

    private void initUI(Step step) {
        if (step.getVideoURL() != null && !step.getVideoURL().matches("") ||
                step.getThumbnailURL() != null && !step.getThumbnailURL().matches("")) {
            String url = step.getVideoURL();

            if (step.getThumbnailURL() != null && !step.getThumbnailURL().matches("")) {
                url = step.getThumbnailURL();
            }

            initializeMediaSession();
            initializePlayer(Uri.parse(url));

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                hideSystemUI();
                mStepDescription.setVisibility(View.GONE);
                mPlayerView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                mPlayerView.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                mStepDescription.setText(step.getDescription());
            }
        } else {
            mPlayerView.setVisibility(View.GONE);
            mGuideline.setGuidelinePercent(0);

            mStepDescription.setText(step.getDescription());
        }
    }

    private void hideSystemUI() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
        );
    }

    private void initializeMediaSession() {
        mMediaSession = new MediaSessionCompat(getContext(), LOG_TAG);
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mMediaSession.setMediaButtonReceiver(null);
        mStateBuilder = new PlaybackStateCompat.Builder().setActions(
                PlaybackStateCompat.ACTION_PLAY |
                        PlaybackStateCompat.ACTION_PAUSE |
                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
        );
        mMediaSession.setPlaybackState(mStateBuilder.build());
        mMediaSession.setCallback(new StepSessionCallback());
        mMediaSession.setActive(true);
    }

    private void initializePlayer(Uri uri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext());
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            mExoPlayer.addListener(this);

            MediaSource mediaSource = createMediaSource(getContext(), uri);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(playbackReady);
            mExoPlayer.seekTo(currentWindow, playbackPosition);
        }
    }

    private void reinitializePlayer() {
        releasePlayer();
        initUI(mStep);
    }

    private MediaSource createMediaSource(Context context, Uri uri) {
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, context.getPackageName()), bandwidthMeter);
        ExtractorMediaSource.Factory factory = new ExtractorMediaSource.Factory(dataSourceFactory);
        factory.createMediaSource(uri);
        return factory.createMediaSource(uri);
    }

    private void releasePlayer() {
        if (mExoPlayer != null) {
            playbackPosition = mExoPlayer.getCurrentPosition();
            currentWindow = mExoPlayer.getCurrentWindowIndex();
            playbackReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    private class StepSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}
