package com.lostalgia.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.lostalgia.components.Player1Component;
import com.lostalgia.components.StateComponent;

public class Player1System extends IteratingSystem {

    private final ComponentMapper<Player1Component> player1M = ComponentMapper.getFor(Player1Component.class);
    private final ComponentMapper<StateComponent> stateM = ComponentMapper.getFor(StateComponent.class);

    public Player1System() {
        super(Family.all(Player1Component.class, StateComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        Player1Component player1 = player1M.get(entity);
        StateComponent state = stateM.get(entity);

        player1.player1Agent.update(deltaTime);
        state.setState(player1.currentState);

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
