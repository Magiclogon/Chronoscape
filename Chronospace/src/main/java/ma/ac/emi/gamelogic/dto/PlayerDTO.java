package ma.ac.emi.gamelogic.dto;

import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector3D;


public class PlayerDTO {
    private Vector3D position;

    public PlayerDTO(Vector3D position) {
        this.position = position;
    }

    public static PlayerDTO mapToDTO(Player player) {
        return new PlayerDTO(player.getPos());
    }
}
