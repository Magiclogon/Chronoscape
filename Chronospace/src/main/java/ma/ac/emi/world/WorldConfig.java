package ma.ac.emi.world;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.gamelogic.wave.WaveConfig;


@Getter
@Setter
public class WorldConfig {
    private int worldNumber;
    private int worldWidth, worldHeight; //in tiles
    private List<WaveConfig> waves;

}