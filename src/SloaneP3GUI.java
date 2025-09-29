import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class SloaneP3GUI extends JFrame{
    public static final Color FRAMECOLOR = Color.BLACK;
    public static final Color TEXTCOLOR = Color.WHITE;

    private SloaneP3Player musicPlayer;

    private JFileChooser jFileChooser;
    private JSlider playbackSlider;

    private JLabel songTitle, songArtist;
    private JPanel playbackButtons;

    public SloaneP3GUI(){
        super("SloaneP3");
        setSize(400,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(FRAMECOLOR);


        musicPlayer = new SloaneP3Player(this);

        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/assets"));

        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3","mp3"));

        addGuiComponents();
    }

    private void setBackgroundImage(String path) {
        ImageIcon backgroundIcon = loadImage(path);
        JLabel backgroundLabel = new JLabel(backgroundIcon);

        backgroundLabel.setBounds(0, 0, getWidth(), getHeight());
        setContentPane(backgroundLabel);

        setLayout(null);
    }


    private void addGuiComponents(){
        addToolbar();

        JLabel songImage = new JLabel(loadImage("src/images/bg1.jpg"));
        songImage.setBounds(70,50,250, 250);
        add(songImage);

        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0,305,getWidth()-10,30);
        songTitle.setFont(new Font("Arial",Font.BOLD | Font.ITALIC,24));
        songTitle.setForeground(TEXTCOLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        songArtist = new JLabel("Artist");
        songArtist.setBounds(0,330,getWidth()-10,30);
        songArtist.setFont(new Font("Arial",Font.BOLD | Font.ITALIC,20));
        songArtist.setForeground(TEXTCOLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2,365, 300,40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JSlider source = (JSlider) e.getSource();

                int frame = source.getValue();
                musicPlayer.setCurrentFrame(frame);
                musicPlayer.setCurrentTimeInMs((int) (frame / 2.08 * musicPlayer.getCurrentSong().getFrameRatePerMs()));

                musicPlayer.playCurrentSong();
                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        addPlaybackButtons();
    }

    private void addToolbar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0,0,getWidth(),20);
        toolBar.setFloatable(false);

        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        JMenu songMenu = new JMenu("Song"); //loading song option
        menuBar.add(songMenu);

        JMenuItem loadSong = new JMenuItem("Load Song");

        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PlaylistDialog(SloaneP3GUI.this).setVisible(true);
            }
        });
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setFileFilter(new FileNameExtensionFilter("Playlist", "txt"));
                jFileChooser.setCurrentDirectory(new File("src/assets"));

                int result = jFileChooser.showOpenDialog(SloaneP3GUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    musicPlayer.stopSong();

                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        playlistMenu.add(loadPlaylist);

        //code to handle loading song
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = jFileChooser.showOpenDialog(SloaneP3GUI.this);
                File selectedFile = jFileChooser.getSelectedFile();

                if(result ==  JFileChooser.APPROVE_OPTION && selectedFile != null){
                    Song song = new Song(selectedFile.getPath()); //creates song
                    musicPlayer.loadSong(song);                  // load song

                    updateSongInfo(song);   //update song title and artist
                    updatePlaybackSlider(song);

                    enablePauseButtonDisablePlayButton();

                }
            }
        });
        songMenu.add(loadSong);
        add(toolBar);
    }


    private void addPlaybackButtons(){
        playbackButtons = new JPanel();
        playbackButtons.setBounds(0,435,getWidth()-10, 80);
        playbackButtons.setBackground(null);

        JButton prevButton = new JButton(loadImage("src/images/previous.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.prevSong();
            }
        });
        playbackButtons.add(prevButton);

        JButton playButton = new JButton(loadImage("src/images/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePauseButtonDisablePlayButton();
                musicPlayer.playCurrentSong();
            }
        });
        playbackButtons.add(playButton);

        JButton pauseButton = new JButton(loadImage("src/images/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButtonDisablePauseButton();
                musicPlayer.pauseSong();
            }
        });
        playbackButtons.add(pauseButton);

        JButton nextButton = new JButton(loadImage("src/images/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //go to next song
                musicPlayer.nextSong();
            }
        });
        playbackButtons.add(nextButton);

        add(playbackButtons);
    }

    public void enablePauseButtonDisablePlayButton() {
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        playButton.setVisible(false);
        playButton.setEnabled(false);

        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton() {
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        playButton.setVisible(true);
        playButton.setEnabled(true);

        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    public void updateSongInfo(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getSongArtist());
    }

    public void setPlaybackSliderValue(int frame){
        playbackSlider.setValue(frame);
    }

    public void updatePlaybackSlider(Song song){
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        JLabel labelBeginning = new JLabel("00:00");
        labelBeginning.setFont(new Font("Dialog", Font.BOLD, 18));
        labelBeginning.setForeground(TEXTCOLOR);

        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Dialog", Font.BOLD, 18));
        labelEnd.setForeground(TEXTCOLOR);

        labelTable.put(0, labelBeginning);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    private ImageIcon loadImage(String imagePath){
        try{
            //image file
            BufferedImage image = ImageIO.read(new File(imagePath));

            return new ImageIcon(image);
        } catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) {
        new SloaneP3GUI();
    }

}