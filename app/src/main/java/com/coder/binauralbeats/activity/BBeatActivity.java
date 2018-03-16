package com.coder.binauralbeats.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.coder.binauralbeats.R;
import com.coder.binauralbeats.base.BaseActivity;
import com.coder.binauralbeats.basemvp.MvpBasePresenter;
import com.coder.binauralbeats.basemvp.MvpBaseView;
import com.coder.binauralbeats.beats.BinauralBeatVoice;
import com.coder.binauralbeats.beats.CanvasVizualizationView;
import com.coder.binauralbeats.beats.GLVizualizationView;
import com.coder.binauralbeats.beats.Period;
import com.coder.binauralbeats.beats.Program;
import com.coder.binauralbeats.beats.SoundLoop;
import com.coder.binauralbeats.beats.StreamVoice;
import com.coder.binauralbeats.beats.Visualization;
import com.coder.binauralbeats.beats.VizualisationView;
import com.coder.binauralbeats.beats.VoicesPlayer;
import com.coder.binauralbeats.graphview.GraphView;
import com.coder.binauralbeats.graphview.LineGraphView;
import com.coder.binauralbeats.viz.Black;
import com.coder.binauralbeats.viz.GLBlack;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import butterknife.BindView;

public class BBeatActivity extends BaseActivity {

    private static final String TAG = "BBeatActivity";
    @BindView(R.id.beat_toolbar)
    Toolbar beatToolbar;
    @BindView(R.id.VisualizationView)
    FrameLayout mVizHolder ;
    @BindView(R.id.soundBGVolumeBar)
    SeekBar soundBGVolumeBar;//背景音量控制
    @BindView(R.id.soundVolumeBar)
    SeekBar soundVolumeBar; //音量控制
    @BindView(R.id.graphVoices)
    LinearLayout mGraphVoices;
    @BindView(R.id.Status)
    TextView Status;


    enum eState {START, RUNNING, END}
    enum appState {SETUP, INPROGRAM}

    private static final int MAX_STREAMS = 5;

    private View mVizV;




    private int soundWhiteNoise;
    private int soundUnity;
    private SoundPool mSoundPool;

    private NotificationManager mNotificationManager;



    private Handler mHandler = new Handler();
    private RunProgram programFSM;
    private long pause_time = -1;

    private Vector<StreamVoice> playingStreams;
    private int playingBackground = -1;


    private float mSoundBeatVolume;

    private float mSoundBGVolume;



    private static final float DEFAULT_VOLUME = 0.6f;
    private static final float BG_VOLUME_RATIO = 0.4f;
    private static final float FADE_INOUT_PERIOD = 5f;
    private static final float FADE_MIN = 0.6f;

    private static final String PREFS_NAME = "BBT";
    private static final String PREFS_VIZ = "VIZ";
    private static final String PREFS_NUM_STARTS = "NUM_STARTS";

    private VoicesPlayer vp;
    boolean glMode = false;
    boolean vizEnabled = true;

    private static Program currentProgram;



    @Override
    protected int getLayout() {
        return R.layout.beat_activity;
    }

    @Override
    protected void initEventAndData() {
       /* Init sounds */
        loadConfig();
        initSounds();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Event event= EventBus.getDefault().getStickyEvent(Event.class);
        currentProgram = (Program) event.getValue();

        if (currentProgram==null) {
            finish();
        }
        selectProgram();

        setSupportActionBar(beatToolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("ddddddddd");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        beatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        beatToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_play_pause:
                        Log.e(TAG,"=====pause_time > 0====="+ (pause_time > 0));
                        item.setIcon(pause_time > 0 ? R.drawable.ic_action_pause:R.drawable.ic_action_play);
                        pauseOrResume();
                        break;
                    case R.id.action_visable:
                        setGraphicsEnabled(!vizEnabled);
                        Log.e(TAG,"=====vizEnabled====="+vizEnabled);
                        item.setIcon(vizEnabled ? R.drawable.ic_action_visable:R.drawable.ic_action_visable_off);
                        break;
                }
                return false;
            }
        });

        pause_time = -1;
        soundVolumeBar.setMax(100);
        mSoundBeatVolume = DEFAULT_VOLUME;
        soundVolumeBar.setProgress((int) (mSoundBeatVolume * 100));
        soundVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSoundBeatVolume = ((float) progress) / 100.f;
                resetAllVolumes();
            }
        });
        soundBGVolumeBar.setMax(100);
        mSoundBGVolume = mSoundBeatVolume * BG_VOLUME_RATIO;
        soundBGVolumeBar.setProgress((int) (mSoundBGVolume * 100));
        soundBGVolumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mSoundBGVolume = ((float) progress) / 100.f;
                resetAllVolumes();
            }
        });
    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    protected MvpBaseView createView() {
        return null;
    }


    /**
     * 初始化背景音
     */
    private void initSounds() {
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
        mSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        soundWhiteNoise = mSoundPool.load(this, R.raw.whitenoise, 1);
        soundUnity = mSoundPool.load(this, R.raw.unity, 1);
        playingStreams = new Vector<>(MAX_STREAMS);
        playingBackground = -1;
    }

    /**
     * 开始播放
     */
    private void startVoicePlayer() {
        if (vp == null) {
            vp = new VoicesPlayer();
            vp.start();
        }
    }
    /**
     * 暂停播放
     */
    void stopVoicePlayer() {
        try {
            vp.shutdown();
        } catch (Exception e) {
        }
        vp = null;
    }

    @Override
    protected void onStop() {

        stopVoicePlayer();
        stopProgram();
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bbeats, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isPaused() {
        if (pause_time > 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 暂停控制
     */
    public void pauseOrResume() {

        if (pause_time > 0) {
            long delta = getClock() - pause_time;
            programFSM.catchUpAfterPause(delta);
            pause_time = -1;
            unmuteAll();
        } else {
                /* This is a pause time */
            pause_time = getClock();
            muteAll();
        }

    }

    private void setGraphicsEnabled(boolean on) {

        if (vizEnabled && on == false) {
            // Disable Viz
            Period p = programFSM.getCurrentPeriod();
            Visualization v;
            if (((Object) mVizV).getClass() == GLVizualizationView.class) {
                v = new GLBlack();
            } else {
                v = new Black();
            }
            ((VizualisationView) mVizV).stopVisualization();
            ((VizualisationView) mVizV).startVisualization(v, p.getLength());
            ((VizualisationView) mVizV).setFrequency(p.getVoices().get(0).freqStart);
            vizEnabled = false;
            ToastText(R.string.graphics_off);
        } else if (!vizEnabled && on == true) {
            // Enable viz
            Period p = programFSM.getCurrentPeriod();
            ((VizualisationView) mVizV).stopVisualization();
            ((VizualisationView) mVizV).startVisualization(p.getV(), p.getLength());
            ((VizualisationView) mVizV).setFrequency(p.getVoices().get(0).freqStart);
            vizEnabled = true;
            ToastText(R.string.graphics_on);
        }
        saveConfig();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopAllSounds();
        cancelAllNotifications();
        super.onDestroy();
    }
    private void stopAllSounds() {
        // Stop all sounds
        for (StreamVoice v : playingStreams) {
            mSoundPool.stop(v.streamID);
        }
        playingStreams.clear();
        if (vp != null) {
            vp.stopVoices();
        }
    }




    private void cancelAllNotifications() {
        mNotificationManager.cancelAll();
    }


    private void selectProgram() {
        if (programFSM != null) {
            programFSM.stopProgram();
        }
        StartPreviouslySelectedProgram();
    }

    private void StartPreviouslySelectedProgram() {
        Program p = currentProgram;
        currentProgram = null;
        startNotification(p.getName());
        glMode = p.doesUseGL();
        if (glMode) {
            mVizV = new GLVizualizationView(getBaseContext());
        } else {
            mVizV = new CanvasVizualizationView(getBaseContext());
        }
        mVizHolder.addView(mVizV);

        pause_time = -1;
        programFSM = new RunProgram(p, mHandler);
        saveConfig();
        // Start voice player thread
        startVoicePlayer();
    }

    private void stopProgram() {
        if (programFSM != null) {
            programFSM.stopProgram();
            programFSM = null;
        }
        stopAllSounds();
    }

    int play(int soundID, float leftVolume, float rightVolume, int priority, int loop, float rate) {
        int id = mSoundPool.play(soundID, leftVolume * mSoundBeatVolume, rightVolume * mSoundBeatVolume,
                priority, loop, rate);
        playingStreams.add(new StreamVoice(id, leftVolume, rightVolume, loop, rate));
        if (playingStreams.size() > MAX_STREAMS) {
            StreamVoice v = playingStreams.remove(0);
            mSoundPool.stop(v.streamID);
        }
        return id;
    }

    void stop(int soundID) {
        mSoundPool.stop(soundID);
        playingStreams.removeElement(new Integer(soundID));
    }

    /**
     * Loop through all playing voices and set regular volume back
     */
    private void resetAllVolumes() {
        if (playingStreams != null && mSoundPool != null) {
            for (StreamVoice v : playingStreams) {
                if (v.streamID == playingBackground) {
                    mSoundPool.setVolume(v.streamID, v.leftVol * mSoundBGVolume, v.rightVol * mSoundBGVolume);
                } else {
                    mSoundPool.setVolume(v.streamID, v.leftVol * mSoundBeatVolume, v.rightVol * mSoundBeatVolume);
                }
            }
        }
        if (vp != null) {
            vp.setVolume(mSoundBeatVolume);
        }
    }

    /**
     * Loop through all playing voices and lower volume to 0 but do not stop
     */
    void muteAll() {
        if (playingStreams != null && mSoundPool != null) {
            for (StreamVoice v : playingStreams) {
                mSoundPool.setVolume(v.streamID, 0, 0);
            }
        }
        vp.setVolume(0);
    }

    /**
     * Loop through all playing voices and set volume back
     */
    void unmuteAll() {
        resetAllVolumes();
    }

    private void playBackgroundSample(SoundLoop background, float vol) {
        switch (background) {
            case WHITE_NOISE:
                playingBackground = play(soundWhiteNoise, vol, vol, 2, -1, 1.0f);
                break;
            case UNITY:
                playingBackground = play(soundUnity, vol, vol, 2, -1, 1.0f);
                break;
            case NONE:
                playingBackground = -1;
                break;
            default:
                playingBackground = -1;
                break;
        }

        if (playingBackground != -1) {
            mSoundPool.setVolume(playingBackground, vol * mSoundBGVolume, vol * mSoundBGVolume);
        }
    }

    private void stopBackgroundSample() {
        if (playingBackground != -1) {
            stop(playingBackground);
        }
        playingBackground = -1;
    }

    /**
     * Go through a list of voices and start playing them with their start frequency
     *
     * @param voices list of voices to play
     */
    protected void playVoices(ArrayList<BinauralBeatVoice> voices) {
        vp.playVoices(voices);
        vp.setVolume(mSoundBeatVolume);
    }

    /**
     * @param voices
     * @param pos
     * @param length
     * @return beat frequency of first voice
     */
    protected float skewVoices(ArrayList<BinauralBeatVoice> voices, float pos, float length, boolean doskew) {
        int i = 0;
        float res = -1;
        float freqs[] = new float[voices.size()];
        for (BinauralBeatVoice v : voices) {
            float ratio = (v.freqEnd - v.freqStart) / length;
            if (res == -1) {
                res = ratio * pos + v.freqStart; // Only set res for the first voice
            }
            freqs[i] = res;
            i++;
        }
        if (doskew && vp!=null) {
            float fade_period = Math.min(FADE_INOUT_PERIOD / 2, length / 2);
            if (length < FADE_INOUT_PERIOD) {
                vp.setFade(1f);
            } else if (pos < fade_period) {
                vp.setFade(FADE_MIN + pos / fade_period * (1f - FADE_MIN));
            } else if (length - pos < fade_period) {
                float fade = FADE_MIN + (length - pos) / fade_period * (1f - FADE_MIN);
                vp.setFade(fade);
            }else {
                vp.setFade(1f);

            }
            vp.setFreqs(freqs);
        }
        return res;
    }

    /**
     * Go through all currently running beat voices and stop them
     */
    protected void stopAllVoices() {
        vp.stopVoices();
    }

    class RunProgram implements Runnable {
        private static final long TIMER_FSM_DELAY = 1000 / 20;
        private static final int GRAPH_VOICE_VIEW_PAST = 60;
        private static final int GRAPH_VOICE_SPAN = 600;
        private static final int GRAPH_VOICE_UPDATE = 5;
        private Program pR;
        private Iterator<Period> periodsIterator;
        private Period currentPeriod;
        private long cT; // current Period start time
        private long startTime;
        private long programLength;
        private String sProgramLength;
        private String formatString;
        private String format_INFO_TIMING_MIN_SEC;
        private long oldDelta; // Utilized to reduce the amount of redraw for the program legend
        private eState s;
        private Handler h;
        LineGraphView graphView;

        private long _last_graph_update;

        public RunProgram(Program pR, Handler h) {
            this.pR = pR;
            this.h = h;
            programLength = pR.getLength();
            sProgramLength = getString(R.string.time_format,
                    formatTimeNumberWithLeadingZero((int) programLength / 60),
                    formatTimeNumberWithLeadingZero((int) programLength % 60));
            formatString = getString(R.string.info_timing);
            format_INFO_TIMING_MIN_SEC = getString(R.string.time_format_min_sec);
            startTime = getClock();
            oldDelta = -1;
            _last_graph_update = 0;
            s = eState.START;
            h.postDelayed(this, TIMER_FSM_DELAY);
        }

        public Period getCurrentPeriod() {
            return currentPeriod;
        }

        public void stopProgram() {
            stopAllVoices();
            endPeriod();
            h.removeCallbacks(this);
        }
        private void startPeriod(Period p) {
            if (vizEnabled) {
                ((VizualisationView) mVizV).startVisualization(p.getV(), p.getLength());
            } else {
                Visualization v;
                if (((Object) mVizV).getClass() == GLVizualizationView.class) {
                    v = new GLBlack();
                } else {
                    v = new Black();
                }
                ((VizualisationView) mVizV).startVisualization(v, p.getLength());
            }
            ((VizualisationView) mVizV).setFrequency(p.getVoices().get(0).freqStart);
            playVoices(p.voices);
            vp.setFade(FADE_MIN);
            //播放背景
            playBackgroundSample(p.background, p.getBackgroundvol());
        }

        private void inPeriod(long now, Period p, float pos) {
            long delta = (now - startTime) / 50; // Do not refresh too often
            float freq = skewVoices(p.voices, pos, p.length, oldDelta != delta);
            ((VizualisationView) mVizV).setFrequency(freq);
            ((VizualisationView) mVizV).setProgress(pos);
            if (oldDelta != delta) {
                oldDelta = delta;
                delta = delta / 20; // Down to seconds
                Status.setText(String.format(formatString,
                        freq,
                        formatTimeNumberWithLeadingZero((int) delta / 60),
                        formatTimeNumberWithLeadingZero((int) delta % 60)
                        )
                                +
                                sProgramLength
                );
                updatePeriodGraph((now - startTime) / 1000);
            }
        }

        private void endPeriod() {
            stopBackgroundSample();
            ((VizualisationView) mVizV).stopVisualization();
        }

        public void catchUpAfterPause(long delta) {
            cT += delta;
        }

        @Override
        public void run() {
            long now = getClock();
            switch (s) {
                case START:
                    s = eState.RUNNING;
                    periodsIterator = pR.getPeriodsIterator();
                    cT = now;
                    drawPeriodGraph();
                    nextPeriod();
                    break;
                case RUNNING:
                    if (isPaused()) {
                        break;
                    }
                    float pos = (now - cT) / 1000f;
                    if (pos > currentPeriod.length) {
                        endPeriod();
                        if (!periodsIterator.hasNext()) {
                            s = eState.END;
                        } else {
                            cT = now;
                            nextPeriod();
                        }
                    } else {
                        if (!BBeatActivity.this.isFinishing()) {
                            inPeriod(now, currentPeriod, pos);
                        }
                    }
                    break;
                case END:
                    stopProgram();
                    return;
            }
            h.postDelayed(this, TIMER_FSM_DELAY);
        }

        private void nextPeriod() {
            currentPeriod = periodsIterator.next();
            startPeriod(currentPeriod);
        }

        public Program getProgram() {
            return pR;
        }

        private void updatePeriodGraph(long now) {

            if (now >= _last_graph_update + GRAPH_VOICE_UPDATE) {
                int viewstart = 0;
                _last_graph_update = now;

                if (GRAPH_VOICE_SPAN < programLength) {
                    viewstart = (int) Math.max(0, now - GRAPH_VOICE_VIEW_PAST);
                }
                int viewsize = GRAPH_VOICE_SPAN;

                if (graphView != null) {
                    graphView.setDrawBackground(true);
                    graphView.setDrawBackgroundLimit(now);
                    graphView.setViewPort(viewstart, viewsize);
                }
            }
        }

        /**
         * 绘制双耳节拍频率
         */
        private void drawPeriodGraph() {
            Iterator<Period> iP = pR.getPeriodsIterator();
            int numPeriods = pR.getNumPeriods();
            GraphView.GraphViewData data[] = new GraphView.GraphViewData[numPeriods * 2];
            int i = 0;
            int cursor = 0;
            double maxFreq = 0;
            while (iP.hasNext()) {
                Period cP = iP.next();
                data[i++] = new GraphView.GraphViewData(cursor + 0.01, cP.getMainBeatStart());
                cursor += cP.getLength();
                data[i++] = new GraphView.GraphViewData(cursor, cP.getMainBeatEnd());
                maxFreq = Math.max(maxFreq, cP.getMainBeatStart());
                maxFreq = Math.max(maxFreq, cP.getMainBeatEnd());
            }
            GraphView.GraphViewSeries voiceSeries = new GraphView.GraphViewSeries(data);
            graphView = new LineGraphView(BBeatActivity.this, "Beat frequency") {
                @Override
                protected String formatLabel(double value, boolean isValueX) {
                    if (isValueX) {
                        return String.format(format_INFO_TIMING_MIN_SEC,
                                formatTimeNumberWithLeadingZero((int) value / 60),
                                formatTimeNumberWithLeadingZero((int) value % 60));
                    } else {
                        return String.format("%.1f", value);
                    }
                }
            };
            graphView.addSeries(voiceSeries); // data
            int viewstart = 0;
            int viewsize = (int) Math.min(programLength, GRAPH_VOICE_SPAN);
            graphView.setManualYAxisBounds(((int) Math.ceil(maxFreq)), 0);
            graphView.setViewPort(viewstart, viewsize);
            graphView.setScrollable(true);
            graphView.setDrawBackground(false);
            mGraphVoices.removeAllViews();
            mGraphVoices.addView(graphView);
        }
    }


    private String formatTimeNumberWithLeadingZero(int t) {
        if (t > 9) {
            return String.format("%2d", t);
        } else {
            return String.format("0%1d", t);
        }
    }

    private void ToastText(int id) {
        Toast.makeText(this, getString(id), Toast.LENGTH_SHORT).show();
    }




















    private void startNotification(String programName) {
        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(this);//新建Notification.Builder对象
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, BBeatActivity.class), 0);
        //PendingIntent点击通知后所跳转的页面
        builder.setContentTitle(getString(R.string.notif_started));
        builder.setContentText(getString(R.string.notif_descr, programName));
        builder.setSmallIcon(R.drawable.icon);
        builder.setContentIntent(intent);//执行intent
        Notification notification = builder.getNotification();//将builder对象转换为普通的notification
        notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        manager.notify(1, notification);//运行notification

    }

    private long getClock() {
        return SystemClock.elapsedRealtime();
    }

    private void saveConfig() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(PREFS_VIZ, vizEnabled);
//        editor.putLong(PREFS_NUM_STARTS, numStarts);
        editor.commit();
    }

    private void loadConfig() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        vizEnabled = settings.getBoolean(PREFS_VIZ, true);
//        numStarts = settings.getLong(PREFS_NUM_STARTS, 0);
    }
}