package ma.ac.emi.glgraphics.lighting;

import java.util.ArrayList;
import java.util.List;

import ma.ac.emi.gamecontrol.GameController;

public class LightObjectManager {
	private List<LightObject> lightObjects = new ArrayList<>();
	
	public void addLightObject(LightObject lightObject) {
		lightObjects.add(lightObject);
	}
	
	public void removeLightObject(LightObject lightObject) {
		lightObjects.remove(lightObject);
	}
	
	public void update(double step) {
		lightObjects.forEach(l -> {
			l.update(step);
			if(!l.isAlive()) GameController.getInstance().removeDrawable(l);
		});
		
		lightObjects.removeIf(l -> !l.isAlive());
	}
}
