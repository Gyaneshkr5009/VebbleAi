package com.server.vebbleAi.service.games;

import com.server.vebbleAi.model.games.Sudoku;
import com.server.vebbleAi.model.games.SudokuModel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SudokuApiService {
    public SudokuModel.BoardResponse getNewBoard(Integer limit , String difficulty){
        List<SudokuModel.Grid> generateGrids = new ArrayList<>();

        int actualLimit = (limit != null && limit > 0) ? Math.min(limit , 10) : 1;

        try{
            for(int i = 0 ; i < actualLimit ; i++){
                generateGrids.add(generatingSingleBoard());
            }
            return null;
        } catch (RuntimeException e) {
            return null;
        }
    }
    public SudokuModel.Grid generatingSingleBoard(){
        Sudoku generator = new Sudoku();
        return new SudokuModel.Grid();
    }
}
