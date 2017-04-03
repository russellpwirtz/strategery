package com.lostalgia.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.lostalgia.components.AnimationComponent;
import com.lostalgia.components.StateComponent;
import com.lostalgia.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
    private final ComponentMapper<AnimationComponent> animationM = ComponentMapper.getFor(AnimationComponent.class);
    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public AnimationSystem() {
        super(Family.all(TextureComponent.class, AnimationComponent.class, StateComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TextureComponent tex = textureM.get(entity);
        AnimationComponent anim = animationM.get(entity);
        StateComponent state = stateM.get(entity);


        try {
            Animation animation = anim.animations.get(state.getState());
            if (animation == null) {
                return;
            }

            tex.region.setRegion(animation.getKeyFrame(state.getStateTime()));
        } catch (Exception e) {
            Gdx.app.log("Exception in animation ", e.getMessage());
        }
    }

}
