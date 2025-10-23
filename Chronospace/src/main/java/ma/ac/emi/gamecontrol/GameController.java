package ma.ac.emi.gamecontrol;

import ma.ac.emi.gamelogic.dto.PlayerDTO;
import ma.ac.emi.gamelogic.player.Player;

public class GameController {
	private Player player;

	public PlayerDTO getPlayerInfo() {
		return PlayerDTO.mapToDTO(player);
	}
}
