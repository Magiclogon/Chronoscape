package ma.ac.emi.tiles;

import lombok.Getter;

@Getter
public class TileMap {
    private final TileType[][] map;
    private final int width, height;
    
    public TileMap(Integer[][] map) {
    	this.map = new TileType[map.length][map[0].length];
    	for(int i = 0; i < map.length; i++) {
    		for(int j = 0; j < map[0].length; j++) {
    			this.map[i][j] = TileType.fromValue(map[i][j]);
    		}
    	}
    	
    	this.width = this.map[0].length;
    	this.height = this.map.length;
        //map = new TileType[50][50];
        //generateMap();
    }
    
    private void generateMap() {
        // Create a room with walls around the perimeter
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                // Top-left corner
                if (i == 0 && j == 0) {
                    map[i][j] = TileType.TOP_LEFT_OUT;
                }
                // Top-right corner
                else if (i == 0 && j == 49) {
                    map[i][j] = TileType.TOP_RIGHT_OUT;
                }
                // Bottom-left corner
                else if (i == 49 && j == 0) {
                    map[i][j] = TileType.BOTTOM_LEFT_OUT;
                }
                // Bottom-right corner
                else if (i == 49 && j == 49) {
                    map[i][j] = TileType.BOTTOM_RIGHT_OUT;
                }
                // Top edge
                else if (i == 0) {
                    map[i][j] = TileType.TOP_EDGE_OUT;
                }
                // Bottom edge
                else if (i == 49) {
                    map[i][j] = TileType.BOTTOM_EDGE_OUT;
                }
                // Left edge
                else if (j == 0) {
                    map[i][j] = TileType.LEFT_EDGE_OUT;
                }
                // Right edge
                else if (j == 49) {
                    map[i][j] = TileType.RIGHT_EDGE_OUT;
                }
                // Interior ground with some variation
                else {
                    map[i][j] = getRandomGroundTile();
                }
            }
        }
        
        // Optional: Add some interior rooms or obstacles
        createRoom(10, 20, 10, 20);
        createRoom(30, 40, 30, 40);
    }
    
    private TileType getRandomGroundTile() {
        // Randomly place ground variations (80% default, 20% variations)
        return TileType.GROUND_DEFAULT;
         
    }
    
    private void createRoom(int startRow, int endRow, int startCol, int endCol) {
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startCol; j <= endCol; j++) {
                // Top-left corner
                if (i == startRow && j == startCol) {
                    map[i][j] = TileType.TOP_LEFT_IN;
                }
                // Top-right corner
                else if (i == startRow && j == endCol) {
                    map[i][j] = TileType.TOP_RIGHT_IN;
                }
                // Bottom-left corner
                else if (i == endRow && j == startCol) {
                    map[i][j] = TileType.BOTTOM_LEFT_IN;
                }
                // Bottom-right corner
                else if (i == endRow && j == endCol) {
                    map[i][j] = TileType.BOTTOM_RIGHT_IN;
                }
                // Top edge
                else if (i == startRow) {
                    map[i][j] = TileType.TOP_EDGE_IN;
                }
                // Bottom edge
                else if (i == endRow) {
                    map[i][j] = TileType.BOTTOM_EDGE_IN;
                }
                // Left edge
                else if (j == startCol) {
                    map[i][j] = TileType.LEFT_EDGE_IN;
                }
                // Right edge
                else if (j == endCol) {
                    map[i][j] = TileType.RIGHT_EDGE_IN;
                }
                // Interior ground
                else {
                    map[i][j] = TileType.VOID;
                }
            }
        }
    }
    
    public TileType[][] getMap() {
        return map;
    }
    
    public TileType getTile(int row, int col) {
        if (row >= 0 && row < 50 && col >= 0 && col < 50) {
            return map[row][col];
        }
        return null;
    }
    
    public void setTile(int row, int col, TileType tileType) {
        if (row >= 0 && row < 50 && col >= 0 && col < 50) {
            map[row][col] = tileType;
        }
    }
    
    public void printMap() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                System.out.print(getTileSymbol(map[i][j]) + " ");
            }
            System.out.println();
        }
    }
    
    private String getTileSymbol(TileType tile) {
        switch (tile) {
            case TOP_LEFT_OUT: return "┌";
            case TOP_RIGHT_OUT: return "┐";
            case BOTTOM_LEFT_OUT: return "└";
            case BOTTOM_RIGHT_OUT: return "┘";
            case TOP_EDGE_OUT: return "─";
            case BOTTOM_EDGE_OUT: return "─";
            case LEFT_EDGE_OUT: return "│";
            case RIGHT_EDGE_OUT: return "│";
            case BORDER: return "█";
            default: return ".";
        }
    }
    
}