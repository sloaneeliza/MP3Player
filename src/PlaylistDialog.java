import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.util.ArrayList;

public class PlaylistDialog extends JDialog {
   private SloaneP3GUI playerGUI;
   private ArrayList<String> songPaths;

    public PlaylistDialog(SloaneP3GUI playerGUI){
        this.playerGUI = playerGUI;
        songPaths = new ArrayList<>();

        setTitle("Create Playlist");
        setSize(400, 400);
        setResizable(false);
        getContentPane().setBackground(SloaneP3GUI.FRAMECOLOR);
        setLayout(null);
        setModal(true);
        setLocationRelativeTo(playerGUI);

        addDialogComponents();
    }

    private void addDialogComponents(){
        JPanel songContainer = new JPanel();
        songContainer.setLayout(new BoxLayout(songContainer, BoxLayout.Y_AXIS));
        songContainer.setBounds((int)(getWidth() * 0.025), 10, (int)(getWidth()*0.90), (int) (getHeight() * 0.75));
        add(songContainer);

        JButton addSongButton = new JButton("Add");
        addSongButton.setBounds(60, (int) (getHeight() * 0.80), 100, 25);
        addSongButton.setFont(new Font("Verdana", Font.BOLD, 14));
        addSongButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
               // jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3, mp3"));
                jFileChooser.setCurrentDirectory(new File ("src/assets"));
                int result = jFileChooser.showOpenDialog(PlaylistDialog.this);

                File selectedFile = jFileChooser.getSelectedFile();
                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    JLabel filePathLabel = new JLabel(selectedFile.getPath());
                    filePathLabel.setFont(new Font("Verdana", Font.BOLD, 12));
                    filePathLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    songPaths.add(filePathLabel.getText());

                    songContainer.add(filePathLabel);
                    songContainer.revalidate();
                }
            }
        });
        add(addSongButton);

        JButton savePlaylistButton = new JButton("Save");
        savePlaylistButton.setBounds(215, (int) (getHeight() * 0.80), 100, 25);
        savePlaylistButton.setFont(new Font("Verdana", Font.BOLD, 14));
        savePlaylistButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    JFileChooser jFileChooser = new JFileChooser();
                    jFileChooser.setCurrentDirectory(new File ("src/assets"));
                    int result = jFileChooser.showSaveDialog(PlaylistDialog.this);

                    if(result == JFileChooser.APPROVE_OPTION){
                        File selectedFile = jFileChooser.getSelectedFile();

                        if(!selectedFile.getName().substring(selectedFile.getName().length()-4).equalsIgnoreCase(".txt")){
                            selectedFile = new File(selectedFile.getAbsoluteFile() + ".txt");
                        }
                        selectedFile.createNewFile();

                         FileWriter fileWriter = new FileWriter(selectedFile);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        for(String songPath : songPaths){
                            bufferedWriter.write(songPath + "\n");
                        }
                        bufferedWriter.close();

                        JOptionPane.showMessageDialog(PlaylistDialog.this, "Successfully Created Playlist");
                        PlaylistDialog.this.dispose();

                    }
                } catch(Exception e1){
                    e1.printStackTrace();
                }
            }
        });
        add(savePlaylistButton);
    }
}
