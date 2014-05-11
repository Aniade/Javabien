package tetraword;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {

    JLabel statusbar;

    public GameFrame() throws UnsupportedAudioFileException, IOException {
        /*statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);*/
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(525, 700);
        setTitle("Tetris");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        /*new SoundClipTest();*/
        /*Sound ghost = new Sound();
        ghost.playSound("729.wav");*/
   }

   /*public JLabel getStatusBar() {
       return statusbar;
   }*/

    public static void main(String[] args) {

        /*GameFrame game = new GameFrame();
        game.setLocationRelativeTo(null);
        game.setVisible(true);*/

    } 
}