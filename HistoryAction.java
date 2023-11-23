
package ece326.hw3;

public class HistoryAction {
    int beforeNum, nextNum;
    int row, column;
    
    public HistoryAction(int row, int column, int beforeNum, int nextNum) {
	this.row = row;
	this.column = column;
	this.beforeNum = beforeNum;
	this.nextNum = nextNum;
    }
}
