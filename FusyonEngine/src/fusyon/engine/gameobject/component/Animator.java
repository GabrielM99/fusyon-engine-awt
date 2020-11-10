package fusyon.engine.gameobject.component;

import fusyon.engine.util.AnimationClip;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Animator extends Component {

    private SpriteRenderer spriteRenderer;

    private List<AnimationClip> animationClipList = new ArrayList<AnimationClip>();
    private Map<String, Object> conditionMap = new TreeMap<>();

    public Animator() {
        super("Animator");
    }

    @Override
    public void start() {
        spriteRenderer = (SpriteRenderer) getParent().getComponent("SpriteRenderer");
    }

    @Override
    public void update(float delta) {
        for(AnimationClip animationClip : animationClipList){
            boolean isAbleToPlay = true;

            for(String conditionName : animationClip.getEntryConditionMap().keySet()){
                if(animationClip.getEntryConditionMap().get(conditionName) != conditionMap.get(conditionName)){
                    isAbleToPlay = false;
                    break;
                }
            }

            if(isAbleToPlay){
                animationClip.play(delta);
                spriteRenderer.renderModel.setSprite(animationClip.getCurrentStepSprite());
            }else if(animationClip.isPlaying()){
                animationClip.stop();
                spriteRenderer.renderModel.setSprite(animationClip.getCurrentStepSprite());
            }
        }
    }

    @Override
    public void render(Graphics g) {

    }

    @Override
    public void destroy() {

    }

    public void addAnimationClip(AnimationClip animationClip){
        if(!animationClipList.contains(animationClip)){
            animationClipList.add(animationClip);
        }
    }

    public void setCondition(String conditionName, Object object){
        conditionMap.put(conditionName, object);
    }

    public void setSpriteRenderer(SpriteRenderer spriteRenderer) {
        this.spriteRenderer = spriteRenderer;
    }
}
