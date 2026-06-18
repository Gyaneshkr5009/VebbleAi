package com.server.vebbleAi.controller.games;

import com.server.vebbleAi.model.games.SudokuModel;
import com.server.vebbleAi.service.games.SudokuApiService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games/sudoku-app")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SudokuApiController {

    final private SudokuApiService sudokuApiService;

    @QueryMapping
    public SudokuModel.BoardResponse newBoard(@Argument Integer limit ,
                                              @Argument String difficulty ,
                                              DataFetchingEnvironment environment){
        return sudokuApiService.getNewBoard(limit , difficulty);
    }
}
