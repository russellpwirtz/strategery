package com.lostalgia.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lostalgia.components.EnemyComponent;
import com.lostalgia.components.StateComponent;
import com.lostalgia.gamesys.GameManager;

public class EnemySystem extends IteratingSystem {

    private final ComponentMapper<EnemyComponent> ghostM = ComponentMapper.getFor(EnemyComponent.class);
    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public EnemySystem() {
        super(Family.all(EnemyComponent.class, StateComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        GameManager.instance.bigPillEaten = false;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        EnemyComponent ghost = ghostM.get(entity);
        StateComponent state = stateM.get(entity);

        ghost.enemyAgent.update(deltaTime);
        state.setState(ghost.currentState);

//        if (GameManager.instance.bigPillEaten) {
//            ghost.weak_time = 0;
//        }
//
//        if (ghost.weaken) {
//            ghost.weak_time += deltaTime;
//            if (ghost.weak_time >= EnemyComponent.WEAK_TIME) {
//                ghost.weaken = false;
//                ghost.weak_time = 0;
//            }
//        }

//        if (GameManager.instance.bigPillEaten) {
//            ghost.weaken = true;
//            state.resetStateTime();
//        }

    }

}
