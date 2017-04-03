package com.lostalgia;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.lostalgia.gamesys.GameManager;
import com.lostalgia.screens.PlayScreen;

public class Strategery extends Game {
    
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        GameManager.instance.dispose();
    }
}
