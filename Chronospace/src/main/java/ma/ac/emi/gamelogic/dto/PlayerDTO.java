package ma.ac.emi.gamelogic.dto;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector2D;


public class PlayerDTO {
    private Vector2D position;

    public PlayerDTO(Vector2D position) {
        this.position = position;
    }

    public static PlayerDTO mapToDTO(Player player) {
        return new PlayerDTO(player.getPos());
    }
}
