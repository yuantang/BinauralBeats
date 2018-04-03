package com.coder.binauralbeats.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.RemoteViews;
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
import com.coder.binauralbeats.event.BusEvent;
import com.coder.binauralbeats.graphview.GraphView;
import com.coder.binauralbeats.graphview.LineGraphView;
import com.coder.binauralbeats.utils.Preferences;
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
    //背景展示
    @BindView(R.id.VisualizationView)
    FrameLayout mVizHolder ;
    @BindView(R.id.soundBGVolumeBar)
    //背景音量控制
            SeekBar soundBGVolumeBar;
    @BindView(R.id.soundVolumeBar)
    //音量控制
            SeekBar soundVolumeBar;
    //双耳节拍频率折线图
    @BindView(R.id.graphVoices)
    LinearLayout mGraphVoices;
    //双耳节拍频率
    @BindView(R.id.Status)
    TextView Status;

    enum eState {START, RUNNING, END}
    private static final int MAX_STREAMS = 5;
    private View mVizV;
    private int soundWhiteNoise;
    private int soundUnity;
    private SoundPool mSoundPool;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private int notificationId=999;
    private String ACTION_PAUSE ="action.beat.play.pause";
    private String ACTION_EVENT="playorpause";
    private boolean ACTION_EVENT_CODE=true;
    private Handler mHandler = new Handler();
    private RunProgram programFSM;
    private long pause_time = -1;
    private Vector<StreamVoice> playingStreams;
    private int playingBackground = -1;
    private static final float DEFAULT_VOLUME = 0.6f;
    private static final float BG_VOLUME_RATIO = 0.4f;
    private static final float FADE_INOUT_PERIOD = 5f;
    private static final float FADE_MIN = 0.6f;


    private boolean glMode = false;
    private boolean vizEnabled = true;
    /**节拍音频大小*/
    private float mSoundBeatVolume;
    /**背景音频大小*/
    private float mSoundBGVolume;

    /**播放器*/
    private VoicesPlayer mVoicesPlayer;
    /**当前播放的*/
    private static Program currentProgram;

    private MenuItem playMenu,visableMenu;
    @Override
    protected int getLayout() {
        return R.layout.beat_activity;
    }

    @Override
    protected void initEventAndData() {
       /* Init sounds */
        loadConfig();
        initSounds();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        BusEvent event = EventBus.getDefault().getStickyEvent(BusEvent.class);
        currentProgram = (Program) event.getValue();
        if (currentProgram==null) {
            finish();
        }
        String name = currentProgram.getName();
        startProgram();
        startPreviouslySelectedProgram();
        setSupportActionBar(beatToolbar);
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(name);
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
                        pauseOrResume();
                        break;
                    case R.id.action_visable:
                        setGraphicsEnabled(!vizEnabled);
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

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ACTION_PAUSE);
        registerReceiver(broadcastReceiver,intentFilter);

    }

    @Override
    protected MvpBasePresenter createPresenter() {
        return null;
    }

    @Override
    protected MvpBaseView createView() {
        return null;
    }

    @Override
    protected void superInit(Intent intent) {

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
        if (mVoicesPlayer == null) {
            mVoicesPlayer = new VoicesPlayer();
            mVoicesPlayer.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bbeats, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        playMenu=menu.findItem(R.id.action_play_pause);
        visableMenu=menu.findItem(R.id.action_visable);
        visableMenu.setIcon(vizEnabled ? R.drawable.ic_action_visable:R.drawable.ic_action_visable_off);
        return super.onPrepareOptionsMenu(menu);
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
        playMenu.setIcon(pause_time > 0 ? R.drawable.ic_action_pause:R.drawable.ic_action_play);
        if (mNotification!=null && mNotification.contentView!=null) {
            mNotification.contentView.setImageViewResource(R.id.notification_pause,pause_time > 0 ? R.drawable.ic_action_pause:R.drawable.ic_action_play);
            mNotificationManager.notify(notificationId,mNotification);
        }

        if (pause_time > 0) {
            long delta = getClock() - pause_time;
            programFSM.catchUpAfterPause(delta);
            pause_time = -1;
            unMuteAll();
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
        stopVoicePlayer();
        stopProgram();
        cancelAllNotifications();
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
    /**
     * 暂停播放
     */
    void stopVoicePlayer() {
        try {
            mVoicesPlayer.shutdown();
        } catch (Exception e) {
        }
        mVoicesPlayer = null;
    }
    private void stopProgram() {
        if (programFSM != null) {
            programFSM.stopProgram();
            programFSM = null;
        }
        stopAllSounds();
    }

    private void stopAllSounds() {
        // Stop all sounds
        for (StreamVoice v : playingStreams) {
            mSoundPool.stop(v.streamID);
        }
        playingStreams.clear();
        if (mVoicesPlayer != null) {
            mVoicesPlayer.stopVoices();
        }
    }

    private void cancelAllNotifications() {
        mNotificationManager.cancelAll();
    }

    private void startProgram() {
        if (programFSM != null) {
            programFSM.stopProgram();
        }
        programFSM = new RunProgram(currentProgram, mHandler);
    }

    private void startPreviouslySelectedProgram() {
        glMode = currentProgram.doesUseGL();
        if (glMode) {
            mVizV = new GLVizualizationView(getBaseContext());
        } else {
            mVizV = new CanvasVizualizationView(getBaseContext());
        }
        mVizHolder.addView(mVizV);
        pause_time = -1;
        saveConfig();
        startVoicePlayer();
        startNotification(currentProgram.getName());
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
                }
            }
        }
        if (mVoicesPlayer != null) {
            mVoicesPlayer.setVolume(mSoundBeatVolume);
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
        mVoicesPlayer.setVolume(0);
    }

    /**
     * Loop through all playing voices and set volume back
     */
    void unMuteAll() {
        resetAllVolumes();
    }

    private void playBackgroundSample(SoundLoop background, final float vol) {
        final int playSound;
        switch (background) {
            case WHITE_NOISE:
                playSound=soundWhiteNoise;
                break;
            case UNITY:
                playSound=soundUnity;
                break;
            case NONE:
                playSound=-1;
                playingBackground=-1;
                break;
            default:
                playSound=-1;
                playingBackground=-1;
                break;
        }

        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if (playSound!=-1){
                    int id = mSoundPool.play(playSound, vol * mSoundBeatVolume, vol * mSoundBeatVolume,
                            2, -1, 1.0f);
                    playingStreams.add(new StreamVoice(id, vol * mSoundBeatVolume, vol* mSoundBeatVolume, -1, 1.0f));
                    if (playingStreams.size() > MAX_STREAMS) {
                        StreamVoice v = playingStreams.remove(0);
                        mSoundPool.stop(v.streamID);
                    }
                    if (id!=-1){
                        mSoundPool.setVolume(id, vol * mSoundBGVolume, vol * mSoundBGVolume);
                        playingBackground=id;
                    }
                }
            }
        });
    }

    private void stopBackgroundSample() {
        if (playingBackground != -1) {
            stop(playingBackground);
        }
        playingBackground = -1;
    }


    protected void playVoices(ArrayList<BinauralBeatVoice> voices) {
        mVoicesPlayer.playVoices(voices);
        mVoicesPlayer.setVolume(mSoundBeatVolume);
    }


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
        if (doskew && mVoicesPlayer !=null) {
            float fade_period = Math.min(FADE_INOUT_PERIOD / 2, length / 2);
            if (length < FADE_INOUT_PERIOD) {
                mVoicesPlayer.setFade(1f);
            } else if (pos < fade_period) {
                mVoicesPlayer.setFade(FADE_MIN + pos / fade_period * (1f - FADE_MIN));
            } else if (length - pos < fade_period) {
                float fade = FADE_MIN + (length - pos) / fade_period * (1f - FADE_MIN);
                mVoicesPlayer.setFade(fade);
            }else {
                mVoicesPlayer.setFade(1f);

            }
            mVoicesPlayer.setFreqs(freqs);
        }
        return res;
    }

    protected void stopAllVoices() {
        if (mVoicesPlayer !=null){
            mVoicesPlayer.stopVoices();
        }
    }

    class RunProgram implements Runnable {
        private static final long TIMER_FSM_DELAY = 1000 / 20;
        private static final int GRAPH_VOICE_VIEW_PAST = 60;
        private static final int GRAPH_VOICE_SPAN = 600;
        private static final int GRAPH_VOICE_UPDATE = 5;
        private Program pR;
        private Iterator<Period> periodsIterator;
        private Period currentPeriod;
        private long cT;
        private long startTime;
        private long programLength;
        private String sProgramLength;
        private String formatString;
        private String format_INFO_TIMING_MIN_SEC;
        private long oldDelta; // Utilized to reduce the amount of redraw for the program legend
        private eState s;
        private Handler h;
        LineGraphView graphView;

        private long lastGraphUpdate;

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
            lastGraphUpdate = 0;
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
            mVoicesPlayer.setFade(FADE_MIN);
            //播放背景
            playBackgroundSample(p.background, p.getBackgroundvol());
            Log.e(TAG, String.format("New Period - duration %d", p.length));
        }

        private void inPeriod(long now, Period p, float pos) {
            // Do not refresh too often
            long delta = (now - startTime) / 50;
            float freq = skewVoices(p.voices, pos, p.length, oldDelta != delta);
            ((VizualisationView) mVizV).setFrequency(freq);
            ((VizualisationView) mVizV).setProgress(pos);
            if (oldDelta != delta) {
                oldDelta = delta;
                // Down to seconds
                delta = delta / 20;
                Status.setText(String.format(formatString,
                        freq,
                        formatTimeNumberWithLeadingZero((int) delta / 60),
                        formatTimeNumberWithLeadingZero((int) delta % 60)
                        )
                                +
                                sProgramLength
                );

                if (mNotification!=null && mNotification.contentView!=null) {
                    mNotification.contentView.setTextViewText(R.id.notification_text, getString(R.string.notif_descr, Status.getText()));
                    mNotificationManager.notify(notificationId, mNotification);
                }
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

            if (now >= lastGraphUpdate + GRAPH_VOICE_UPDATE) {
                int viewStart = 0;
                lastGraphUpdate = now;

                if (GRAPH_VOICE_SPAN < programLength) {
                    viewStart = (int) Math.max(0, now - GRAPH_VOICE_VIEW_PAST);
                }
                int viewSize = GRAPH_VOICE_SPAN;

                if (graphView != null) {
                    graphView.setDrawBackground(true);
                    graphView.setDrawBackgroundLimit(now);
                    graphView.setViewPort(viewStart, viewSize);
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
            graphView.addSeries(voiceSeries);
            int viewStart = 0;
            int viewSize = (int) Math.min(programLength, GRAPH_VOICE_SPAN);
            graphView.setManualYAxisBounds(((int) Math.ceil(maxFreq)), 0);
            graphView.setViewPort(viewStart, viewSize);
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
        Notification.Builder builder = new Notification.Builder(this);//新建Notification.Builder对象
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(this, BBeatActivity.class), 0);
        //PendingIntent点击通知后所跳转的页面
        Intent intentPause=new Intent(ACTION_PAUSE).putExtra(ACTION_EVENT,ACTION_EVENT_CODE);
        PendingIntent pause= PendingIntent.getBroadcast(this,112,intentPause,0);
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.layout_notification);
        view.setOnClickPendingIntent(R.id.status_bar_latest_event_content, intent);
        view.setOnClickPendingIntent(R.id.notification_pause, pause);
        view.setTextViewText(R.id.notification_title,getString(R.string.notif_descr, programName));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContent(view);
        builder.setAutoCancel(true);
        builder.setContentIntent(intent);//执行intent
        mNotification = builder.getNotification();//将builder对象转换为普通的notification
        mNotification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
        mNotificationManager.notify(notificationId, mNotification);//运行notification
    }
    private long getClock() {
        return SystemClock.elapsedRealtime();
    }
    private void saveConfig() {
        Preferences.saveVizEnabled(vizEnabled);
    }
    private void loadConfig() {
        vizEnabled= Preferences.isVizEnabled();
    }



    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent!=null && intent.getExtras().getBoolean(ACTION_EVENT)){
                pauseOrResume();
            }
        }
    };

}