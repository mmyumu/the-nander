package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mmyumu.nander.entity.components.AnimationComponent;
import com.mmyumu.nander.entity.components.StateComponent;
import com.mmyumu.nander.entity.components.TextureComponent;

public class AnimationSystem extends IteratingSystem {

    private final ComponentMapper<TextureComponent> textureComponentMapper;
    private final ComponentMapper<AnimationComponent> animationComponentMapper;
    private final ComponentMapper<StateComponent> stateComponentMapper;

    @SuppressWarnings("unchecked")
    public AnimationSystem() {
        super(Family.all(TextureComponent.class,
                AnimationComponent.class,
                StateComponent.class).get());

        textureComponentMapper = ComponentMapper.getFor(TextureComponent.class);
        animationComponentMapper = ComponentMapper.getFor(AnimationComponent.class);
        stateComponentMapper = ComponentMapper.getFor(StateComponent.class);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        AnimationComponent ani = animationComponentMapper.get(entity);
        StateComponent state = stateComponentMapper.get(entity);

        if (ani.animations.containsKey(state.get())) {
            TextureComponent tex = textureComponentMapper.get(entity);
            tex.region = ani.animations.get(state.get()).getKeyFrame(state.time, state.looping);
        }

        state.time += deltaTime;
    }
}