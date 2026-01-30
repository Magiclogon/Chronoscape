package ma.ac.emi.gamelogic.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ParticleEmitterManager {
	private List<ParticleEmitter> emitters;
	
	public ParticleEmitterManager() {
		emitters = new ArrayList<>();
	}
	
	public void addEmitter(ParticleEmitter emitter) {
		this.emitters.add(emitter);
	}
	
	public void removeEmitter(ParticleEmitter emitter) {
		this.emitters.remove(emitter);
	}
	
	public void update(double step) {
		emitters.forEach(e -> {
			if(e.isShouldEmit()) e.update(step);
		});
		
		emitters.removeIf(e -> !e.isActive());
	}

	public void clear() {
		emitters.clear();
	}

	public List<ParticleEmitter> getEmitters() {
		return emitters;
	}
}
