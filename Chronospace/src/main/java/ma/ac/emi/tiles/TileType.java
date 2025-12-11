package ma.ac.emi.tiles;

public enum TileType {
    GROUND_DEFAULT(0),
    TOP_LEFT_OUT(1), 
    TOP_RIGHT_OUT(2), 
    BOTTOM_LEFT_OUT(3), 
    BOTTOM_RIGHT_OUT(4), 
    TOP_LEFT_IN(5), 
    TOP_RIGHT_IN(6), 
    BOTTOM_LEFT_IN(7), 
    BOTTOM_RIGHT_IN(8),
    
    TOP_EDGE_OUT(9), 
    BOTTOM_EDGE_OUT(10), 
    LEFT_EDGE_OUT(11), 
    RIGHT_EDGE_OUT(12),
    TOP_EDGE_IN(13), 
    BOTTOM_EDGE_IN(14), 
    LEFT_EDGE_IN(15), 
    RIGHT_EDGE_IN(16),
    
    VOID(17),
    
    DOOR(18),
    BORDER(19);
    
    private final int value;
    
    TileType(int value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
    
    public static TileType fromValue(int value) {
    	for(TileType type : TileType.values()) {
    		if(type.value == value) return type;
    	}
    	return null;
    }
}