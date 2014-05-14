package frames;

import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;

import tetraword.Board;

/**
 * 
 *  Affiche le jeu
 *
 */
@SuppressWarnings("serial")
public class GameFrame extends JFrame {

    public GameFrame() throws UnsupportedAudioFileException, IOException {
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(525, 700);
        setTitle("Tetraword");
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
   }
}