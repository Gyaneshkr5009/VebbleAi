package com.server.vebbleAi.service.games;

import com.server.vebbleAi.model.games.Difficulty;
import com.server.vebbleAi.model.games.Sudoku;
import com.server.vebbleAi.model.games.SudokuModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SudokuApiService {
    public SudokuModel.BoardResponse getNewBoard(int limit , String difficulty , int size){
        List<SudokuModel.Grid> generateBoards = new ArrayList<>();

        int newLimit = Math.min(limit, 7);

        if (size > 16 || size <= 0) {
            size = 9;
        } else if (size != 4 && size != 6 && size != 9 && size != 10 && size != 12 && size != 16) {
            throw new IllegalArgumentException("Unsupported board size dimensions provided: " + size);
        }

        String operationalDifficulty = difficulty;

        try{
            boolean dynamicDifficultyNeeded = (difficulty == null || "null".equalsIgnoreCase(difficulty.trim()));
            String[] options = {"EASY", "MEDIUM", "HARD"};
            for(int i = 0 ; i < newLimit ; i++){
                if (dynamicDifficultyNeeded) {
                    int randomIndex = (int) (Math.random() * options.length);
                    operationalDifficulty = options[randomIndex];
                }
                generateBoards.add(generatingSingleGrid(operationalDifficulty , size));
            }
            SudokuModel.BoardResponse boardResponse = new SudokuModel.BoardResponse();
            boardResponse.setGrids(generateBoards);
            boardResponse.setResults(newLimit);
            boardResponse.setMessage(newLimit < limit
                    ? "Maximum of 7 results allowed per request."
                    : "Data fetched successfully.");
            return boardResponse;

        } catch (RuntimeException e) {
            return new SudokuModel.BoardResponse(
                    new ArrayList<>(),
                    0 ,
                    "An internal server error occurred while generating the board :" +e.getMessage());
        }
    }
    public SudokuModel.Grid generatingSingleGrid(String difficulty , int size){
        SudokuModel.Grid grid = new SudokuModel.Grid();

        //creating a empty sudoku block
        Sudoku generator = new Sudoku(difficulty , size);
        // filling the empty gridValue => valid Sudoku
        generator.fillGridValue(size); // here i have a board with a correct value
        grid.setSolution(generator.deepCopy());
        generator.setRange();
        generator.removeElements();

        grid.setValue(generator.value);
        grid.setDifficulty(Difficulty.valueOf(difficulty.toUpperCase()));

        return grid;
    }
}
