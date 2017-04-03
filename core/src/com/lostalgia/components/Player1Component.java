package com.lostalgia.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.physics.box2d.Body;
import com.lostalgia.ai.fsm.Player1Agent;
import com.lostalgia.ai.fsm.Player1State;

public class Player1Component implements Component {

    // state
    public static final int MOVE_UP = 0;
    public static final int MOVE_DOWN = 1;
    public static final int MOVE_LEFT = 2;
    public static final int MOVE_RIGHT = 3;
    public static final int ESCAPE = 4;
    public static final int DIE = 5;
    public static final int FIXED = 6;

    public Player1Agent player1Agent;

    private final Body body;

    public int currentState;

    public String name;
    public int hp;

    public Player1Component(Body body, String name) {
        this.body = body;
        player1Agent = new Player1Agent(this);
        player1Agent.stateMachine.setInitialState(Player1State.MOVE_UP);
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
