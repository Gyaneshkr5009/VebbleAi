package com.server.vebbleAi.model.games;

import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor
public class Sudoku {
    public Character[][] value;
    public String difficulty;
    private int size;

    private int strtRange , endRange;

    public void setRange() {
        int totalCells = size * size; // 4x4 -> 16, 9x9 -> 81, 16x16 -> 256

        double minPercent;
        double maxPercent;

        if ("EASY".equalsIgnoreCase(difficulty)) {
            minPercent = 0.35;
            maxPercent = 0.45;
        } else if ("HARD".equalsIgnoreCase(difficulty)) {
            minPercent = 0.58;
            maxPercent = 0.68;
        } else {
            minPercent = 0.46;
            maxPercent = 0.56;
        }

        this.strtRange = (int) (totalCells * minPercent);
        this.endRange = (int) (totalCells * maxPercent);
    }

    private static final List<Character> MASTER_TOKENS = List.of(
            '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G'
    );

    public void removeElements() {
        int totalCells = size * size;
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        //this will generate random values for the desigered range
        int targetToRemove = strtRange + rand.nextInt((endRange-strtRange)+1);
        int currentRemovalCount = 0;

        List<Integer> cellCoordinates = new ArrayList<>(totalCells);
        for(int i =0 ; i < totalCells ; i++){
            cellCoordinates.add(i);
        }
        // shuffle to remove "targetToRemove" no of elements randomly
        Collections.shuffle(cellCoordinates);

        // conversion of 2d to 1d array
        for(int cellIndex : cellCoordinates){
            if(currentRemovalCount == targetToRemove) break;

            int r = cellIndex / size;
            int c = cellIndex % size;
            //now we have 2d array coordinates

            if(value[r][c] == ' ') continue;

            char backupToken = value[r][c];
            value[r][c] = ' ';
            int solutionCount = cntHelper(0,0,MASTER_TOKENS.subList(0,size)); // for counting no of solution possible

            if(solutionCount == 1) currentRemovalCount++;
            else value[r][c] = backupToken;
        }
        System.out.println("Successfully generated puzzle. Removed holes: " + currentRemovalCount);
    }

    private int cntHelper(int row, int col, List<Character> activeTokens) {
        if(row == size) return 1;

        int nRow = (col == size-1) ? row+1 : row;
        int nCol = (col == size-1) ? 0 : col+1;

        //for safety
        if(value[row][col] != ' ') {
            return cntHelper(nRow , nCol ,activeTokens);
        }
        int totalSolutionsFound  = 0;
        for(char num : activeTokens){
            if(isSafe(row , col , num)){
                value[row][col] = num;
                totalSolutionsFound += cntHelper(nRow, nCol, activeTokens);
                value[row][col] = ' '; // backtrack
                if(totalSolutionsFound > 1) return totalSolutionsFound; // early exit
            }
        }
        return totalSolutionsFound;
    }

    public Sudoku(String difficulty , int size) {
        this.value = new Character[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.value[i][j] = ' '; // Using '-' or ' ' as an empty cell placeholder
            }
        }
        this.size = size;
        this.difficulty = difficulty;
    }

    public void fillGridValue(int size){
        List<Character> activeTokens = new ArrayList<>(MASTER_TOKENS.subList(0, size));
        Collections.shuffle(activeTokens);

        helper(0 , 0 , activeTokens);
    }

    private boolean helper(int row , int col , List<Character> activeTokens){
        if(row == size) return true;

        int nRow = (col == size-1) ? row+1 : row;
        int nCol = (col == size-1) ? 0 : col+1;

        //for safety
        if(value[row][col] != ' ') {
            return helper(nRow , nCol ,activeTokens);
        }

        for(char num : activeTokens){
            if(isSafe(row , col , num)){
                value[row][col] = num;
                if(helper(nRow , nCol , activeTokens)) return true;
                else value[row][col] = ' '; // backtrack
            }
        }
        return false;
    }

    private boolean isSafe(int row , int col , char num){
        // checking horizontally and vertically
        for(int i = 0 ; i < size ; i++){
            if(value[i][col] != ' ' && value[i][col] == num) return false;
            if(value[row][i] != ' ' &&  value[row][i] == num) return false;
        }

        int boxRows;
        int boxCols;

        if(size == 4){
            boxRows = 2; boxCols = 2;
        } else if (size == 6) {
            boxRows = 2; boxCols = 3;
        } else if (size == 10) {
            boxRows = 2; boxCols = 5;
        } else if (size == 12) {
            boxRows = 3; boxCols = 4;
        } else {
            int sqrt = (int) Math.sqrt(size);
            boxRows = sqrt; boxCols = sqrt;
        }
        int subGridRows = (row / boxRows) * boxRows;
        int subGridCols = (col / boxCols) * boxCols;

        for(int i = subGridRows ; i < subGridRows+boxRows ; i++){
            for(int j = subGridCols ; j < subGridCols+boxCols ; j++){
                if(value[i][j] == num) return false;
            }
        }
        return true;
    }

    public Character[][] deepCopy(){
        Character[][] result = new Character[value.length][];
        for (int i = 0; i < value.length; i++) {
            result[i] = value[i].clone();
        }
        return result;
    }
}
