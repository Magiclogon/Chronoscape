package ma.ac.emi.fx;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnimationState {
	public static final double DEFAULT_FRAME_RATE = 24;
	private String title;
	private List<Frame> frames;
	
	private transient int currentFrameIndex;
	private transient double timeTracker;
	private transient double playSpeed;
	
	private Sprite defaultSprite;
	private boolean doesLoop;
	
	public AnimationState(String title) {
		this(title, new Sprite());
	}
	
	public AnimationState(String title, Sprite defaultSprite) {
		this.title = title;
		this.frames = new ArrayList<>();
		this.currentFrameIndex = 0;
		this.timeTracker = 0;
		this.doesLoop = true;
		this.defaultSprite = defaultSprite;
		this.playSpeed = 1;
	}
	
	public void addFrame(Sprite sprite, double frameTime) {
		this.frames.add(new Frame(sprite, frameTime));
	}
	
	public void addFrame(Sprite sprite) {
		double frameTime = 1.0 / DEFAULT_FRAME_RATE;
		this.frames.add(new Frame(sprite, frameTime));
	}
	
	public void update(double step) {
		if (frames.isEmpty() || currentFrameIndex >= frames.size()) {
			return;
		}
		
		timeTracker -= step;
		
		if (timeTracker <= 0) {
			boolean isLastFrame = currentFrameIndex == frames.size() - 1;
			
			if (!isLastFrame || doesLoop) {
				currentFrameIndex = (currentFrameIndex + 1) % frames.size();
				if(playSpeed > 0) timeTracker += frames.get(currentFrameIndex).getFrameTime()/playSpeed;

			}
		}
	}
	
	public Sprite getCurrentFrameSprite() {
		if (currentFrameIndex < frames.size()) {
			return frames.get(currentFrameIndex).getSprite();
		}
		return defaultSprite;
	}
	
	public boolean isAnimationDone() {
		return currentFrameIndex == frames.size() - 1;
	}
	
	public void reset() {
		currentFrameIndex = 0;
		if (!frames.isEmpty()) {
			timeTracker = frames.get(0).getFrameTime();
		}
	}
}
