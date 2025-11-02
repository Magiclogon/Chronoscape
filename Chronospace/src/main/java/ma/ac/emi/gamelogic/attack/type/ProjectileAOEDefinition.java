package ma.ac.emi.gamelogic.attack.type;

import java.awt.Image;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectileAOEDefinition extends ProjectileDefinition{
	private String aoeId;

	public ProjectileAOEDefinition(String id, Image sprite, double baseSpeed, int boundWidth, int boundHeight, String aoeId) {
		super(id, sprite, baseSpeed, boundWidth, boundHeight);
		this.aoeId = aoeId;
		// TODO Auto-generated constructor stub
	}

}
