import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class SloaneP3Player extends PlaybackListener {
    private static final Object playSignal = new Object();
    private SloaneP3GUI myGUI;
    private Song currentSong;
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused;
    private boolean songFinished;
    private boolean pressedNext, pressedPrev;
    private int currentFrame;
    private int currentTimeInMs;    //our way to track how much time has passed in a song
    private ArrayList<Song> playlist;

    private int currentPlaylistIndex;

    public void setCurrentTimeInMs(int timeInMs){
        currentTimeInMs = timeInMs;
    }

    public void setCurrentFrame(int frame){
        currentFrame = frame;
    }

    public Song getCurrentSong(){
        return currentSong;
    }

    /**
     * Constructor
     * @param myGUI
     */
    public SloaneP3Player(SloaneP3GUI myGUI){
        this.myGUI = myGUI;
    }

    public void loadSong(Song song){
        currentSong = song;
        playlist = null;

        if(!songFinished){
            stopSong();
        }

        if(currentSong != null){
            currentFrame = 0;
            currentTimeInMs = 0;

            myGUI.setPlaybackSliderValue(0);

            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile){
        playlist = new ArrayList<>();

        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String songPath;
            while((songPath = bufferedReader.readLine()) != null){
                Song song = new Song(songPath);

                playlist.add(song);

            }
        }catch(Exception e){
            e.printStackTrace();
        }

        if(playlist.size() > 0){
            myGUI.setPlaybackSliderValue(0);
            currentTimeInMs = 0;
            currentSong = playlist.get(0);
            currentFrame =  0;

            myGUI.enablePauseButtonDisablePlayButton();
            myGUI.updateSongInfo(currentSong);
            myGUI.updatePlaybackSlider(currentSong);

            playCurrentSong();
        }
    }

    public void pauseSong(){
        if(advancedPlayer != null){
            isPaused = true;

            stopSong();
        }
    }

    public void nextSong(){
        if(playlist == null) return;

        if(currentPlaylistIndex + 1 > playlist.size() -1) return;

        pressedNext = true;

        if(!songFinished){
            stopSong();
        }

        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMs = 0;
        myGUI.enablePauseButtonDisablePlayButton();
        myGUI.updateSongInfo(currentSong);
        myGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    public void prevSong(){
        if(playlist == null) return;

        if(currentPlaylistIndex - 1 < 0 ) return;

        pressedPrev = true;

        if(!songFinished){
            stopSong();
        }

        currentPlaylistIndex--;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMs = 0;
        myGUI.enablePauseButtonDisablePlayButton();
        myGUI.updateSongInfo(currentSong);
        myGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong(){
        if(currentSong == null) return; //no song input handling

        //read mp3 data
        try{
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);
            startMusicThread();

            startPlaybackSliderThread();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    private void startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(isPaused){
                        synchronized (playSignal){
                            isPaused = false;
                            playSignal.notify();
                        }
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    } else {
                        advancedPlayer.play();
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlaybackSliderThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused){
                    try{
                        synchronized (playSignal){
                            playSignal.wait();
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }

                while(!isPaused && !songFinished && !pressedNext && !pressedPrev){
                    try{
                        currentTimeInMs++;

                        int calculatedFrame = (int) ((double) currentTimeInMs * 2.08 * currentSong.getFrameRatePerMs());
                        myGUI.setPlaybackSliderValue(calculatedFrame);

                        Thread.sleep(1);
                    } catch(Exception e){
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent playbackEvent) {
        System.out.println("Playback Started.");
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent playbackEvent) {
        System.out.println("Playback finished.");

        if(isPaused){
            currentFrame += (int) ((double)playbackEvent.getFrame() * currentSong.getFrameRatePerMs());
        } else{
            if(pressedNext || pressedPrev) return;

            songFinished = true;
            if(playlist == null){
                myGUI.enablePlayButtonDisablePauseButton();
            } else{
                if(currentPlaylistIndex == playlist.size() - 1){
                    myGUI.enablePlayButtonDisablePauseButton();
                } else{
                    nextSong();
                }
            }
        }
    }
}
