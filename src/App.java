import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                new SloaneP3GUI().setVisible(true);

                Song song = new Song("src/assets/Duvet.mp3");
                System.out.println(song.getSongTitle());
                System.out.println(song.getSongArtist());
            }
        });
    }
}
