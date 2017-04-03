package com.lostalgia.ai.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.lostalgia.ai.astar.Node;
import com.lostalgia.components.EnemyComponent;

public class EnemyAgent implements Telegraph {

    public StateMachine<EnemyAgent, EnemyState> stateMachine;

    public EnemyComponent enemyComponent;

    public float speed = 2.4f;

    public float timer;

    public Node nextNode; // for pursue or escape

    public EnemyAgent(EnemyComponent enemyComponent) {
        this.enemyComponent = enemyComponent;
        stateMachine = new DefaultStateMachine<>(this);

        timer = 0;
    }

    public Vector2 getPosition() {
        return enemyComponent.getBody().getPosition();
    }

    public void update(float deltaTime) {
        timer += deltaTime;

        stateMachine.update();
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return stateMachine.handleMessage(msg);
    }

}
