package connect4;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

// 
// Created by Sleepyhead08
// 
public class Connect4 extends JPanel implements KeyListener, MouseListener, MouseMotionListener {

    public static void main(String[] args) {
        Connect4 project = new Connect4("Connect 4");
    }

    public static int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    public JFrame frame;
    public Random randy = new Random();
    public boolean startScreen = true;
    public boolean gameScreen = false;
    public String playerOne;
    public String playerTwo;
    Color playerOneColor = Color.red;
    Color playerTwoColor = Color.blue;
    public ArrayList<Rectangle> blueSpots = new ArrayList();
    public ArrayList<Rectangle> redSpots = new ArrayList();
    public boolean playerOneTurn = true;
    public boolean playerTwoTurn = false;
    public int[][] board = new int[6][7]; // 0 = unoccupied, 1 = Player1 (, 2 = Player2
    //public Graphics g = getGraphics();
    public int mouseX;
    public int frameX;
    public int rowEmpty;
    public boolean gameOver = false;
    public int winner;

    Timer timer = new Timer(100/*change to vary frequency*/, new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            //what the timer does every run through
        }
    });

    public Connect4(String title) {

        frame = new JFrame(title);
        frame.setSize(800, 700);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.add(this);
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.addMouseMotionListener(this);
        timer.start();

    }

    public void paint(Graphics g) {
        super.paint(g);
        frameX = frame.getX();
        if (startScreen) {
            g.drawString("Press enter to start", 400, 350);
        }
        if (gameScreen) {
            if (playerOneTurn) {
                g.drawString("Player 1's Turn", 0, 20);
            } else if (playerTwoTurn) {
                g.drawString("Player 2's Turn", 0, 20);
            }
            boardMethod(g);
            drawCircleToBePlaced(g);
        }
        if (gameOver) {
            g.drawString("Winner = Player " + winner, 400, 350);
            g.drawString("Press r to restart", 400, 375);
        }

        repaint();
    }

    public void boardMethod(Graphics g) {
        g.setColor(Color.yellow);
        g.fillRect(50, 50, 700, 600);
        g.setColor(Color.black);
        g.drawRect(50, 50, 700, 600);
        for (int i = 0; i < 7; i++) {
            g.drawLine(50 + i * 100, 50, 50 + i * 100, 650);
        }
        for (int j = 0; j < 6; j++) {
            g.drawLine(50, 50 + j * 100, 750, 50 + j * 100);
        }
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 6; j++) {
                switch (board[j][i]) {
                    case 0:
                        g.setColor(Color.white);
                        break;
                    case 1:
                        g.setColor(Color.blue);
                        break;
                    case 2:
                        g.setColor(Color.red);
                        break;
                    default:
                        break;
                }
                g.fillOval(68 + i * 100, 68 + j * 100, 65, 65);
            }
        }
    }

    public void drawCircleToBePlaced(Graphics g) {
        mouseX = MouseInfo.getPointerInfo().getLocation().x;
        if (playerOneTurn) {
            g.setColor(Color.blue);
        } else if (playerTwoTurn) {
            g.setColor(Color.red);
        }
        if (mouseX > 50 + frameX && mouseX < 150 + frameX) {
            g.fillOval(68, 0, 65, 65);
        }
        if (mouseX > 150 + frameX && mouseX < 250 + frameX) {
            g.fillOval(168, 0, 65, 65);
        }
        if (mouseX > 250 + frameX && mouseX < 350 + frameX) {
            g.fillOval(268, 0, 65, 65);
        }
        if (mouseX > 350 + frameX && mouseX < 450 + frameX) {
            g.fillOval(368, 0, 65, 65);
        }
        if (mouseX > 450 + frameX && mouseX < 550 + frameX) {
            g.fillOval(468, 0, 65, 65);
        }
        if (mouseX > 550 + frameX && mouseX < 650 + frameX) {
            g.fillOval(568, 0, 65, 65);
        }
        if (mouseX > 650 + frameX && mouseX < 750 + frameX) {
            g.fillOval(668, 0, 65, 65);
        }
    }

    public void checkColumn(int c) {
        for (int i = 5; i >= 0; i--) {
            if (board[i][c] == 0) {
                rowEmpty = i;
                placePiece(c, rowEmpty);
                break;
            }
        }
    }

    public void placePiece(int column, int rowEmpty) {
        if (gameScreen) {
            if (playerOneTurn) {
                board[rowEmpty][column] = 1;
                playerOneTurn = false;
                checkWin(column, rowEmpty, 1);
                playerTwoTurn = true;
            } else if (playerTwoTurn) {
                board[rowEmpty][column] = 2;
                checkWin(column, rowEmpty, 2);
                playerTwoTurn = false;
                playerOneTurn = true;
            }
            playDropSound();
        }
    }

    /**
     * Play a sound for when a player's piece is dropped
     *
     */
    public void playDropSound() {
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource("sounds/drop.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Open audio clip and load samples from the audio input stream.
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
        }
    }

    public void checkWin(int column, int row, int player) {
        int count = 1;
        try {
            //CHECK IF THE PLAYER WINS HERE FROM THE PLACE THEY PUT IT --> board[row][column]
            for (int i = 1; i < 4; i++) {
                if (board[row][column + i] == player) { //Check to the right
                    count++;
                    checkCount(count, player);
                } else {
                    count = 1;
                    break;
                }
            }
        } catch (Exception e) {

        }
        try {
            for (int i = 1; i < 4; i++) {
                if (board[row][column - i] == player) { //Check to the left
                    count++;
                    checkCount(count, player);
                } else {
                    count = 1;
                    break;
                }
            }
        } catch (Exception e) {

        }
        try {
            if (board[row + 3][column] == player && board[row + 2][column] == player && board[row + 1][column] == player) { //Check downwards
                count += 3;
                checkCount(count, player);
            }
        } catch (Exception e) {

        }
        try {
            if (board[row - 2][column - 2] == player && board[row - 1][column - 1] == player && board[row + 1][column + 1] == player) { //Going 2top left --> 1bottom right
                count += 3;
                checkCount(count, player);
            }
        } catch (Exception e) {

        }
        try {
            if (board[row - 1][column - 1] == player && board[row + 1][column + 1] == player && board[row + 2][column + 2] == player) {  //Going 1top left --> 2bottom right
                count += 3;
                checkCount(count, player);
            }
        } catch (Exception e) {

        }
        try {
            if (board[row + 2][column - 2] == player && board[row + 1][column - 1] == player && board[row - 1][column + 1] == player) { //going 2bottom left --> 1 top right
                count += 3;
                checkCount(count, player);
            }
        } catch (Exception e) {

        }
        try {
            if (board[row + 1][column - 1] == player && board[row - 1][column + 1] == player && board[row - 2][column + 2] == player) { //going 1bottom left --> 2 top right
                count += 3;
                checkCount(count, player);
            }
        } catch (Exception e) {

        }
        try { //Going 3 top left
            for (int i = 1; i < 4; i++) {
                if (board[row - i][column - i] == player) {
                    count++;
                    checkCount(count, player);
                } else {
                    count = 1;
                    break;
                }
            }
        } catch (Exception e) {

        }
        try { //Going 3 bottom left
            for (int i = 1; i < 4; i++) {
                if (board[row + i][column - i] == player) {
                    count++;
                    checkCount(count, player);
                } else {
                    count = 1;
                    break;
                }
            }
        } catch (Exception e) {

        }
        try { //Going 3 top right
            for (int i = 1; i < 4; i++) {
                if (board[row - i][column + i] == player) {
                    count++;
                    checkCount(count, player);
                } else {
                    count = 1;
                    break;
                }
            }
        } catch (Exception e) {

        }
        try { //Going 3 bottom right
            for (int i = 1; i < 4; i++) {
                if (board[row + i][column + i] == player) {
                    count++;
                    checkCount(count, player);
                } else {
                    count = 1;
                    break;
                }
            }
        } catch (Exception e) {

        }

    }

    public void checkCount(int count, int player) {
        if (count == 4) {
            gameOverMethod(player);
        }
    }

    public void gameOverMethod(int player) {
        winner = player;
        gameOver = true;
        gameScreen = false;
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_R) {
            gameOver = false;
            gameScreen = true;
            board = new int[6][7];
        }
        if (ke.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(69);
        }

        if (ke.getKeyCode() == KeyEvent.VK_C) {
            //
        }

        if (startScreen) {
            if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                startScreen = false;
                gameScreen = true;
            }
        }

        if (gameOver && ke.getKeyCode() == KeyEvent.VK_R) {
            gameOver = false;
            gameScreen = true;
            board = new int[6][7];
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {

    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (me.getX() > 50 && me.getX() < 150) {
            checkColumn(0);
        }
        if (me.getX() > 150 && me.getX() < 250) {
            checkColumn(1);
        }
        if (me.getX() > 250 && me.getX() < 350) {
            checkColumn(2);
        }
        if (me.getX() > 350 && me.getX() < 450) {
            checkColumn(3);
        }
        if (me.getX() > 450 && me.getX() < 550) {
            checkColumn(4);
        }
        if (me.getX() > 550 && me.getX() < 650) {
            checkColumn(5);
        }
        if (me.getX() > 650 && me.getX() < 750) {
            checkColumn(6);
        }

    }

    @Override
    public void mousePressed(MouseEvent me) {
        if (me.getX() >= width / 2 - 100 && me.getX() <= width / 2 + 100 && me.getY() >= height / 2 - 100 && me.getY() <= height / 2 + 100) {
            //when the mouse clicks the center, execute this code
        }
    }

    @Override
    public void mouseReleased(MouseEvent me) {

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent me) {

    }

}
