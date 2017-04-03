package com.lostalgia.ai.fsm;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.math.Vector2;
import com.lostalgia.ai.astar.Node;
import com.lostalgia.components.Player1Component;

public class Player1Agent implements Telegraph {

    public StateMachine<Player1Agent, Player1State> stateMachine;

    public Player1Component player1Component;

    public float speed = 2.4f;

    public float timer;

    public Node nextNode; // for pursue or escape

    public Player1Agent(Player1Component player1Component) {
        this.player1Component = player1Component;
        stateMachine = new DefaultStateMachine<>(this);

        timer = 0;
    }

    public Vector2 getPosition() {
        return player1Component.getBody().getPosition();
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
