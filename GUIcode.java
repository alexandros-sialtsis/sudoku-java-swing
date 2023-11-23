
package ece326.hw3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GUIcode {

    public static final int WIDTH = 600;
    public static final int HEIGHT = 700;

    public static final int BOX_WIDTH = 40;
    public static final int BOX_HEIGHT = 30;

    public static final int BUTTON_WIDTH = 40;
    public static final int BUTTON_HEIGHT = 30;

    public static final int ICON_WIDTH = 27;
    public static final int ICON_HEIGHT = 27;

    public static GameCode sudoku;
    public static JTextArea[][] SudokuGrid;
    public static JDialog winnerWindow;
    public static JButton []numBttn;
    public static JButton eraserBttn, rubicBttn, undoBttn;
    public static JCheckBox verifyCheckBox;
    public static LinkedList<HistoryAction> history;

    public static int iSelectedCell, jSelectedCell;
    public static char prevSelectedNum;

    public static void showGUI() {
	// Window
	JFrame window = new JFrame();
	window.setSize(WIDTH, HEIGHT);
	window.setTitle("Sudoku");
	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	window.setVisible(true);

	// Menu Bar
	JMenu menu = new JMenu("New Game");
	JMenuItem easy = new JMenuItem("Easy");
	menu.add(easy);
	easy.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		reset();
		sudoku = new GameCode('e');
		sudoku.solve();
		connectWithOriginal();
	    }
	});
	JMenuItem intermediate = new JMenuItem("Intermediate");
	menu.add(intermediate);
	intermediate.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		reset();
		sudoku = new GameCode('i');
		sudoku.solve();
		connectWithOriginal();
	    }
	});
	JMenuItem expert = new JMenuItem("Expert");
	menu.add(expert);
	expert.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		reset();
		sudoku = new GameCode('x');
		sudoku.solve();
		connectWithOriginal();
	    }
	});

	JMenuBar menuBar = new JMenuBar();
	menuBar.add(menu);
	window.setJMenuBar(menuBar);

	history = new LinkedList<>();

	// Sudoku Grid
	SudokuGrid = new JTextArea[9][9];
	int x = 10, y = 100;
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		SudokuGrid[i][j] = new JTextArea("   ");
		SudokuGrid[i][j].setBounds(x, y, BOX_WIDTH, BOX_HEIGHT);
		SudokuGrid[i][j].setMaximumSize(new Dimension(BOX_WIDTH, BOX_HEIGHT));
		SudokuGrid[i][j].setBorder(BorderFactory.createLineBorder(Color.blue,1));
		SudokuGrid[i][j].setBackground(Color.white);
		SudokuGrid[i][j].setVisible(true);
		SudokuGrid[i][j].setEditable(false);
		window.add(SudokuGrid[i][j]);
		x = x + BOX_WIDTH + 2;
		if((j + 1) % 3 == 0)x = x + 7;

		int row = i, column = j;
		SudokuGrid[i][j].addMouseListener(new MouseListener() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
			prevSelectedNum = sudoku.currentGame[iSelectedCell][jSelectedCell];
			if(sudoku.isOriginal[iSelectedCell][jSelectedCell]) {
			    SudokuGrid[iSelectedCell][jSelectedCell].setBackground(new Color(200,200,200));
			}
			else{
			    SudokuGrid[iSelectedCell][jSelectedCell].setBackground(Color.WHITE);
			}
			char num = sudoku.currentGame[iSelectedCell][jSelectedCell];
			if(num != '.') unmarkAllSameNumber(num);
			if(sudoku.isOriginal[row][column] == false) {
			    SudokuGrid[row][column].setBackground(new Color(255,255,200));
			    if(sudoku.currentGame[row][column] != '.') {
				markAllSameNumber(sudoku.currentGame[row][column]);
			    }
			}
			setSelectedCell(row,column);
		    }
		    @Override
		    public void mousePressed(MouseEvent e) {
		    }
		    @Override
		    public void mouseReleased(MouseEvent e) {
		    }
		    @Override
		    public void mouseEntered(MouseEvent e) {
		    }
		    @Override
		    public void mouseExited(MouseEvent e) {
		    }
		});

		SudokuGrid[i][j].addKeyListener(new KeyListener() {
		    @Override
		    public void keyTyped(KeyEvent e) {
			char num = e.getKeyChar();
			int numInt = Character.getNumericValue(num);
			if(Character.isDigit(num) && num != '0') {
			    char prev = sudoku.currentGame[iSelectedCell][jSelectedCell];
			    if(sudoku.changeCell(iSelectedCell, jSelectedCell, numInt) == true) {
				prevSelectedNum = prev;
				if(prev == '.') history.add(new HistoryAction(iSelectedCell, jSelectedCell, 0, numInt));
				else history.add(new HistoryAction(iSelectedCell, jSelectedCell, Character.getNumericValue(prev), numInt));
				if(prevSelectedNum != '.') unmarkAllSameNumber(prevSelectedNum);
				else sudoku.count++;
				SudokuGrid[iSelectedCell][jSelectedCell].setText("   " + num);
				SudokuGrid[iSelectedCell][jSelectedCell].setFont(new Font("Serif", Font.PLAIN, 20));
				markAllSameNumber(sudoku.currentGame[iSelectedCell][jSelectedCell]);
				//prevSelectedNum = prev;
			    }
			}
			else if(num == KeyEvent.VK_BACK_SPACE) {
			    char prev = sudoku.currentGame[iSelectedCell][jSelectedCell];
			    if(sudoku.clearCell(iSelectedCell, jSelectedCell) == true) {
				SudokuGrid[iSelectedCell][jSelectedCell].setText("   ");
				if(prev != '.') {
				    history.add(new HistoryAction(iSelectedCell, jSelectedCell, Character.getNumericValue(prev), 0));
				    unmarkAllSameNumber(prev);
				    sudoku.count--;
				}
			    }
			}
			if(sudoku.count == 30 && sudoku.isWinning()) {
			    for(int i = 0; i < 9; i++) {
				numBttn[i].setEnabled(false);
			    }
			    eraserBttn.setEnabled(false);
			    undoBttn.setEnabled(false);
			    rubicBttn.setEnabled(false);
			    verifyCheckBox.setEnabled(false);
			    winnerWindow = new JDialog(window, "Win!", true);
			    winnerWindow.setSize(300, 200);
			    JLabel winnerLabel = new JLabel("Winner Winner Chicken Dinner!!!");
			    winnerLabel.setBounds(100, 100, 200, 300);
			    winnerWindow.add(winnerLabel);
			    winnerWindow.setVisible(true);
			}
		    }
		    @Override
		    public void keyPressed(KeyEvent e) {
		    }
		    @Override
		    public void keyReleased(KeyEvent e) {
		    }
		});
	    }
	    x = 10;
	    y = y + BOX_HEIGHT + 3;
	    if((i + 1) % 3 == 0) y = y + 7;
	}

	// Buttons
	x = 20;
	y = y + 50;
	numBttn = new JButton[9];
	for(int i = 0; i < 9; i++) {
	    numBttn[i] = new JButton(Integer.toString(i+1));
	    numBttn[i].setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
	    numBttn[i].setVisible(true);
	    window.add(numBttn[i]);
	    x = x + BUTTON_WIDTH + 5;
	    if(i == 7) {
		x = 20;
		y = y + BUTTON_HEIGHT + 7;
	    }

	    int numInt = i + 1;
	    String numString = Integer.toString(numInt);
	    numBttn[i].addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    char prev = sudoku.currentGame[iSelectedCell][jSelectedCell];
		    if(sudoku.changeCell(iSelectedCell, jSelectedCell, numInt) == true) {
			prevSelectedNum = prev;
			if(prev == '.') history.add(new HistoryAction(iSelectedCell, jSelectedCell, 0, numInt));
			else history.add(new HistoryAction(iSelectedCell, jSelectedCell, Character.getNumericValue(prev), numInt));
			if(prevSelectedNum != '.') unmarkAllSameNumber(prevSelectedNum);
			SudokuGrid[iSelectedCell][jSelectedCell].setText("   " + numString);
			SudokuGrid[iSelectedCell][jSelectedCell].setFont(new Font("Serif", Font.PLAIN, 20));
			markAllSameNumber(sudoku.currentGame[iSelectedCell][jSelectedCell]);
			if(sudoku.count == 81 && sudoku.isWinning()) {
			    System.out.println("WINNER WINNER CHICKEN DINNER");
			    for(int i = 0; i < 9; i++) {
				numBttn[i].setEnabled(false);
			    }
			    eraserBttn.setEnabled(false);
			    undoBttn.setEnabled(false);
			    rubicBttn.setEnabled(false);
			    verifyCheckBox.setEnabled(false);
			    winnerWindow = new JDialog(window, "Win!", true);
			    winnerWindow.setSize(300, 200);
			    JLabel winnerLabel = new JLabel("Winner Winner Chicken Dinner!!!");
			    winnerLabel.setBounds(100, 100, 200, 300);
			    winnerWindow.add(winnerLabel);
			    winnerWindow.setVisible(true);
			}
		    }
		}
	    });
	}


	ImageIcon eraserIcon = new ImageIcon("eraser.png");
	Image img = eraserIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT);
	eraserIcon = new ImageIcon(img);
	eraserBttn = new JButton(eraserIcon);
	eraserBttn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
	eraserBttn.setVisible(true);
	window.add(eraserBttn);
	x = x + BUTTON_WIDTH + 5;

	eraserBttn.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		char prev = sudoku.currentGame[iSelectedCell][jSelectedCell];
		if(sudoku.clearCell(iSelectedCell, jSelectedCell) == true) {
		    SudokuGrid[iSelectedCell][jSelectedCell].setText("   ");
		    if(prev != '.') {
			unmarkAllSameNumber(prev);
			history.add(new HistoryAction(iSelectedCell, jSelectedCell, Character.getNumericValue(prev), 0));
		    }
		}
	    }
	});

	ImageIcon undoIcon = new ImageIcon("undo.png");
	img = undoIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT);
	undoIcon = new ImageIcon(img);
	undoBttn = new JButton(undoIcon);
	undoBttn.setBounds(x, y, BUTTON_WIDTH, BUTTON_HEIGHT);
	undoBttn.setVisible(true);
	window.add(undoBttn);

	undoBttn.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		unmarkAllSameNumber(sudoku.currentGame[iSelectedCell][jSelectedCell]);
		HistoryAction last = history.pollLast();
		if(last != null) {
		    if(last.beforeNum == 0) {
			sudoku.clearCell(last.row, last.column);
			SudokuGrid[last.row][last.column].setText("   ");
		    }
		    else {
			sudoku.changeCell(last.row, last.column, last.beforeNum);
			SudokuGrid[last.row][last.column].setText("   " + last.beforeNum);
			SudokuGrid[iSelectedCell][jSelectedCell].setFont(new Font("Serif", Font.PLAIN, 20));
		    }
		}
	    }
	});

	ImageIcon rubikIcon = new ImageIcon("rubik.png");
	img = rubikIcon.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_DEFAULT);
	rubikIcon = new ImageIcon(img);
	rubicBttn = new JButton(rubikIcon);
	rubicBttn.setBounds(335, y, BUTTON_WIDTH, BUTTON_HEIGHT);
	rubicBttn.setVisible(true);
	window.add(rubicBttn);

	x = x + BUTTON_WIDTH + 5;
	verifyCheckBox = new JCheckBox("Verify Against Solution");
	verifyCheckBox.setBounds(x, y + (BUTTON_HEIGHT/4)-3, 4*BUTTON_WIDTH, 2*BUTTON_HEIGHT/3);
	verifyCheckBox.setSelected(false);
	verifyCheckBox.setVisible(true);
	window.add(verifyCheckBox);

	verifyCheckBox.addItemListener(new ItemListener() {
	    @Override
	    public void itemStateChanged(ItemEvent e) {
		if(e.getStateChange() == ItemEvent.SELECTED) {
		    checkIfCorrect();
		}
		else {
		    uncheckIfCorrect();
		}
	    }
	});

	rubicBttn.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		unmarkAllSameNumber(sudoku.currentGame[iSelectedCell][jSelectedCell]);
		for(int i = 0; i < 9; i++) {
		    for(int j = 0; j < 9; j++) {
			SudokuGrid[i][j].setText("   " + sudoku.solution[i][j]);
			SudokuGrid[i][j].setFont(new Font("Serif", Font.PLAIN, 20));
		    }
		    numBttn[i].setEnabled(false);
		}
		eraserBttn.setEnabled(false);
		undoBttn.setEnabled(false);
		rubicBttn.setEnabled(false);
		verifyCheckBox.setEnabled(false);
	    }
	});

	//
	JButton mm = new JButton("");
	mm.setVisible(false);
	window.add(mm);
	//
    }



    static void setSelectedCell(int row, int column) {
	iSelectedCell = row;
	jSelectedCell = column;
    }

    static void connectWithOriginal() {
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		if(sudoku.currentGame[i][j] != '.') {
		    SudokuGrid[i][j].setText("   "+ sudoku.currentGame[i][j]);
		    SudokuGrid[i][j].setFont(new Font("Serif", Font.PLAIN, 20));
		    SudokuGrid[i][j].setBackground(new Color(200,200,200));
		}
	    }
	}
    }

    static void markAllSameNumber(char num) {
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		if(sudoku.currentGame[i][j] == num) {
		    if(i == iSelectedCell ^ j == jSelectedCell) {
			SudokuGrid[i][j].setBackground(Color.RED);
		    }
		    else {
			SudokuGrid[i][j].setBackground(new Color(255,255,200));
		    }
		}
	    }
	}
    }

    static void unmarkAllSameNumber(char num) {
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		if(sudoku.currentGame[i][j] == num) {
		    if(sudoku.isOriginal[i][j]) {
			SudokuGrid[i][j].setBackground(new Color(200,200,200));
		    }
		    else {
			SudokuGrid[i][j].setBackground(Color.WHITE);
		    }
		}
	    }
	}
    }

    static void checkIfCorrect() {
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		if(!sudoku.isOriginal[i][j] && sudoku.currentGame[i][j] != '.' &&
		   sudoku.currentGame[i][j] != ('0' + sudoku.solution[i][j])) {
		    SudokuGrid[i][j].setBackground(Color.BLUE);
		}
	    }
	}
    }

    static void uncheckIfCorrect() {
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		if(!sudoku.isOriginal[i][j]) {
		    SudokuGrid[i][j].setBackground(Color.WHITE);
		}
	    }
	}
    }

    static void reset() {
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		SudokuGrid[i][j].setBackground(Color.WHITE);
		SudokuGrid[i][j].setText("   ");
	    }
	    numBttn[i].setEnabled(true);
	}
	eraserBttn.setEnabled(true);
	rubicBttn.setEnabled(true);
	undoBttn.setEnabled(true);
	verifyCheckBox.setEnabled(true);
    }
}
