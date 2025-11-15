package ma.ac.emi.fx;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimationState {
	private String title;
	private List<Frame> frames;
	
	private transient int currentFrameIndex;
	private transient double timeTracker;
	
	private Sprite defaultSprite;
	private boolean doesLoop;
	
	public AnimationState(String title) {
		this(title, new Sprite());
	}
	
	public AnimationState(String title, Sprite defaultSprite) {
		this.title = title;
		frames = new ArrayList<>();
		currentFrameIndex = 0;
		timeTracker = 0;
		doesLoop = false;
		defaultSprite = new Sprite();

	}
	
	public void addFrame(Frame frame) {
		this.frames.add(frame);
	}
	
	public void update(double step) {
		if(currentFrameIndex < frames.size()) {
			timeTracker -= step;
			if(timeTracker <= 0) {
				if(currentFrameIndex != frames.size()-1 || doesLoop) {
					currentFrameIndex = (currentFrameIndex + 1) % frames.size();
				}
				timeTracker = frames.get(currentFrameIndex).getFrameTime();
			}
		}
	}
	
	public Sprite getCurrentFrameSprite() {
		if(currentFrameIndex < frames.size()) {
			return frames.get(currentFrameIndex).getSprite();
		}
		return getDefaultSprite();
	}
	
}
