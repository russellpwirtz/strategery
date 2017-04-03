package com.lostalgia.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.lostalgia.ai.fsm.EnemyAgent;
import com.lostalgia.ai.fsm.EnemyState;

public class

EnemyComponent implements Component {

    // state
    public static final int MOVE_UP = 0;
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_RIGHT = 3;
    public static final int ESCAPE = 4;
    public static final int DIE = 5;
    public static final int FIXED = 6;

    public EnemyAgent enemyAgent;

    private final Body body;

    public int currentState;

    public String name;
    public int hp;

    public EnemyComponent(Body body, String name) {
        this.body = body;
        enemyAgent = new EnemyAgent(this);
        enemyAgent.stateMachine.setInitialState(EnemyState.MOVE_UP);
        currentState = MOVE_UP;
        this.name = name;
        hp = 1;
    }

    public Body getBody() {
        return body;
    }
    public String getName() { return name; }

    public void respawn() {
        hp = 1;
    }

    public boolean isFixed() {
        return name.equals("bomb") || name.equals("flag");
    }
}
