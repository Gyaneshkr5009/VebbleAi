package com.server.vebbleAi.model.games;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class SudokuModel {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardResponse {
        private List<Grid> grids;
        private int results;
        private String message;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Grid {
        private Character[][] value;
        private Character[][] solution;
        private Difficulty difficulty;
    }
}
