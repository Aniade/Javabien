package tetraword;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class GameFrame extends JFrame {

    JLabel statusbar;

    public GameFrame() throws UnsupportedAudioFileException, IOException {
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(525, 700);
        setTitle("Tetris");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
   }
}