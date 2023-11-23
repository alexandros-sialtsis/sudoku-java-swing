
package ece326.hw3;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.net.MalformedURLException;
public class GameCode {
    public char [][]currentGame;
    boolean [][]isOriginal;
    int [][]solution;
    int count;
    
    public GameCode(char c) {
	URL url;
	
	try {
	    switch (c) {
		case 'e':
		    url = new URL("http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=easy");
		    break;
		case 'i':
		    url = new URL("http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=intermediate");
		    break;
		default:
		    url = new URL("http://gthanos.inf.uth.gr/~gthanos/sudoku/exec.php?difficulty=expert");
		    break;
	    }
	
	    Scanner sc = new Scanner(url.openStream());
	    String str;
	    char line[];
	    currentGame = new char[9][9];
	    solution = new int[9][9];
	    isOriginal = new boolean[9][9];
	    for(int i = 0; i < 9; i++) {
		str = sc.nextLine();
		line = str.toCharArray();
		for(int j = 0; j < 9; j++) {
		    currentGame[i][j] = line[j];
		    if(line[j] != '.') {
			solution[i][j] = Character.getNumericValue(line[j]);
			count++;
		    }
		    isOriginal[i][j] = line[j] != '.';
		}
	    }
	}
	catch (MalformedURLException ex) {
	    System.out.println("problen opening url");
	}
	catch (IOException ex) {
	    System.out.println("problem with the stream");
	}
    }
    
    boolean solve() {
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		if(solution[i][j] == 0) {
		    for(int k = 1; k <= 9; k++) {
			solution[i][j] = k;
			if(isValid(i,j) && solve()) {
			    return(true);
			}
			solution[i][j] = 0;
		    }
		    return(false);
		}
	    }
	}
	return(true);
    }
    
    boolean rowCheck(int row) {
	int num;
	boolean []existance = new boolean[9];
	for(int i = 0; i < 9; i++) {
	    num = solution[row][i];
	    if(num == 0) continue;
	    if(existance[num - 1] == false) existance[num - 1] = true;
	    else return(false);
	}
	return(true);
    }
    
    boolean columnCheck(int column) {
	int num;
	boolean []existance = new boolean[9];
	for(int i = 0; i < 9; i++) {
	    num = solution[i][column];
	    if(num == 0) continue;
	    if(existance[num - 1] == false) existance[num - 1] = true;
	    else return(false);
	}
	return(true);
    }
    
    int getSqare(int row, int column) {
	if(row >= 0 && row <= 2) {
	    if(column >= 0 && column <= 2) return(1);
	    else if(column >= 3 && column <= 5) return(2);
	    else return(3);
	}
	else if(row >= 3 && row <= 5) {
	    if(column >= 0 && column <= 2) return(4);
	    else if(column >= 3 && column <= 5) return(5);
	    else return(6);
	}
	else {
	    if(column >= 0 && column <= 2) return(7);
	    else if(column >= 3 && column <= 5) return(8);
	    else return(9);
	}
    }
    
    boolean squareCheck(int square) {
	int num;
	boolean []existance = new boolean[9];
	int []rows = new int[3];
	int []columns = new int[3];
	
	if(square == 1 || square == 2 || square == 3) {
	    rows[0] = 0; rows[1] = 1; rows[2] = 2;
	}
	else if(square == 4 || square == 5 || square == 6) {
	    rows[0] = 3; rows[1] = 4; rows[2] = 5;
	}
	else {
	    rows[0] = 6; rows[1] = 7; rows[2] = 8;
	}
	
	if(square == 1 || square == 4 || square == 7) {
	    columns[0] = 0; columns[1] = 1; columns[2] = 2;
	}
	else if(square == 2 || square == 5 || square == 8) {
	    columns[0] = 3; columns[1] = 4; columns[2] = 5;
	}
	else {
	    columns[0] = 6; columns[1] = 7; columns[2] = 8;
	}
	
	for(int i = 0; i < 3; i++) {
	    for(int j = 0; j < 3; j++) {
		num = solution[rows[i]][columns[j]];
		if(num == 0) continue;
		if(existance[num - 1] == false) existance[num - 1] = true;
		else return(false);
	    }
		
	}
	return(true);
    }
    
    boolean isValid(int row, int column) {
	return(rowCheck(row) && columnCheck(column) &&
		squareCheck(getSqare(row, column)));
    }
    
    void print() {
	StringBuilder cc = new StringBuilder("");
	StringBuilder sol = new StringBuilder("");
	
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		cc.append(currentGame[i][j]);
		sol.append(solution[i][j]);
	    }
	    cc.append("\n");
	    sol.append("\n");
	}
	System.out.println(cc);
	System.out.println("\n" + sol);
    }
    
    boolean changeCell(int row, int column, int num) {
	if(isOriginal[row][column] == true) return(false);
	currentGame[row][column] = (char) ('0' + num);
	return(true);
    }
    
    boolean clearCell(int row, int column) {
	if(isOriginal[row][column] == true) return(false);
	currentGame[row][column] = '.';
	return(true);
    }
    
    boolean isWinning() {
	if(count != 81) return(false);
	for(int i = 0; i < 9; i++) {
	    for(int j = 0; j < 9; j++) {
		if(Character.getNumericValue(currentGame[i][j]) != solution[i][j]) {
		    return(false);
		}
	    }
	}
	return(true);
    }
}
