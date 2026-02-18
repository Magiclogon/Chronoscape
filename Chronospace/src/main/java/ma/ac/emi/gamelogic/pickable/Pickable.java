package ma.ac.emi.gamelogic.pickable;

import lombok.Getter;
import lombok.Setter;
import ma.ac.emi.fx.AnimationState;
import ma.ac.emi.fx.AssetsLoader;
import ma.ac.emi.fx.Frame;
import ma.ac.emi.fx.Sprite;
import ma.ac.emi.fx.SpriteSheet;
import ma.ac.emi.fx.StateMachine;
import ma.ac.emi.gamelogic.entity.Entity;
import ma.ac.emi.gamelogic.physics.AABB;
import ma.ac.emi.gamelogic.player.Player;
import ma.ac.emi.math.Vector3D;

import java.awt.Graphics;

@Getter
@Setter
public class Pickable extends Entity {

    private PickableType type;
    private double dropProbability;
    private double value;
    private boolean isPickedUp;

    private String spritePath;
    private boolean isAnimated;
    private int width;
    private int height;
    private int frameCount;
    private double frameSpeed;

    public Pickable() {
        super();
        this.isPickedUp = false;
    }

    // Copy Constructor
    public Pickable(Pickable prototype) {
        this();
        this.type = prototype.type;
        this.dropProbability = prototype.dropProbability;
        this.value = prototype.value;
        this.width = prototype.width;
        this.height = prototype.height;
        this.isAnimated = prototype.isAnimated;

        setupStateMachineFrom(prototype);

        this.hitbox = new AABB(new Vector3D(), new Vector3D(this.width, this.height));
    }

    public void applyValueMultiplier(double multiplier) {
        this.value *= multiplier;
    }


    public void initGraphics() {
        // Try to get sprite from cache first
        Sprite fullSprite = AssetsLoader.getSprite(spritePath);

        if (fullSprite == null) {
            // If not in cache, load manually
            try {
                fullSprite = new Sprite(spritePath);
            } catch (Exception e) {
                System.err.println("Could not load pickable sprite: " + spritePath);
                return;
            }
        }

        if (isAnimated) {
            SpriteSheet sheet = new SpriteSheet(fullSprite, width, height);
            AnimationState idleState = new AnimationState("IDLE");

            // Slice frames
            Sprite[] frames = sheet.getAnimationRow(0, frameCount);
            for (Sprite frame : frames) {
                idleState.addFrame(frame, frameSpeed);
            }

            this.stateMachine.addAnimationState(idleState);
            this.stateMachine.setDefaultState("IDLE");
        } else {
            // Static image
            AnimationState staticState = new AnimationState("STATIC");
            staticState.addFrame(fullSprite, 1.0);
            this.stateMachine.addAnimationState(staticState);
            this.stateMachine.setDefaultState("STATIC");
        }
    }


    private void setupStateMachineFrom(Pickable prototype) {
        StateMachine protoSM = prototype.getStateMachine();
        if (protoSM == null || protoSM.getDefaultStateTitle() == null) return;

        String defaultTitle = protoSM.getDefaultStateTitle();
        AnimationState protoState = protoSM.getAnimationStateByTitle(defaultTitle);

        if (protoState != null) {
            AnimationState newState = new AnimationState(defaultTitle);
            newState.setDoesLoop(true);

            // Reuse the existing Sprite objects
            for (Frame f : protoState.getFrames()) {
                newState.addFrame(f.getSprite(), f.getFrameTime());
            }

            this.stateMachine.addAnimationState(newState);
            this.stateMachine.setDefaultState(defaultTitle);
        }
    }

    public Pickable createInstance() {
        return new Pickable(this);
    }

    @Override
    public void update(double step) {
        super.update(step); // Physics update

        if (stateMachine != null) {
            stateMachine.update(step);
        }

        hitbox.center = new Vector3D(pos.getX(), pos.getY());

        if (isPickedUp) {
            ma.ac.emi.gamecontrol.GameController.getInstance().removeDrawable(this);
        }
    }

    @Override
    public void draw(Graphics g) {
        if (isPickedUp) return;
        super.draw(g);
    }

    @Override public void initStateMachine() { }
    @Override public void setupAnimations() { }

    public void applyEffect(Player player) {
        if (type == PickableType.HEALTH) {
            player.setHp(Math.min(player.getHp() + value, player.getHpMax()));
        } else if (type == PickableType.MONEY) {
            player.setMoney(player.getMoney() + (int)value);
        }
        this.isPickedUp = true;
    }
}