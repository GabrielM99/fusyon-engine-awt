package fusyon.engine.util;

import fusyon.engine.gfx.AnimatedImage;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AnimationClip {

    private boolean isPlaying;

    private int animationStep;

    private float timePerStep, timer;

    private AnimatedImage animatedImage;

    private Map<String, Object> entryConditionMap;

    public AnimationClip(AnimatedImage animatedImage, float timePerStep){
        this.animatedImage = animatedImage;
        this.timePerStep = timePerStep;

        entryConditionMap = new TreeMap<>();
    }

    public void addEntryCondition(String name, Object value){
        entryConditionMap.put(name, value);
    }

    public void play(float delta) {
        if(timer >= timePerStep) {
            isPlaying = true;

            animationStep++;

            if(animationStep >= animatedImage.getSpriteList().size()) {
                setAnimationStep(0);
            }

            timer = 0;
        }

        timer += delta;
    }

    public void stop(){
        if(!isPlaying) return;

        setAnimationStep(0);
        isPlaying = false;
    }

    public void setAnimationStep(int step) {
        if(step >= animatedImage.getSpriteList().size()) step = animatedImage.getSpriteList().size() - 1;

        animationStep = step;
    }

    public AnimatedImage getAnimatedImage() {
        return animatedImage;
    }

    public Map<String, Object> getEntryConditionMap() {
        return entryConditionMap;
    }

    public BufferedImage getCurrentStepSprite() {
        return animatedImage.getSpriteList().get(animationStep);
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
