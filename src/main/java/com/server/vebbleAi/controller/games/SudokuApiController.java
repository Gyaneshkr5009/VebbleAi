package com.server.vebbleAi.controller.games;

import com.server.vebbleAi.model.games.SudokuModel;
import com.server.vebbleAi.service.games.SudokuApiService;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class SudokuApiController {

    final private SudokuApiService sudokuApiService;

    @QueryMapping(name = "newboard")
    public SudokuModel.BoardResponse newBoard(@Argument int limit ,
                                              @Argument String difficulty ,
                                              @Argument int size,
                                              DataFetchingEnvironment environment){
        return sudokuApiService.getNewBoard(limit , difficulty , size);
    }
}
