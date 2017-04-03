package com.lostalgia.b2dworldutils;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.*;
import com.lostalgia.components.EnemyComponent;
import com.lostalgia.components.Player1Component;
import com.lostalgia.components.PlayerComponent;
import com.lostalgia.gamesys.GameManager;

public class WorldContactListener implements ContactListener {

    public static final String MARSHAL = "1-marshal";
    public static final String GENERAL = "2-general";
    public static final String BOMB = "bomb";
    public static final String COLONEL = "3-colonel";
    public static final String MAJOR = "4-major";
    public static final String CAPTAIN = "5-captain";
    public static final String LIEUTENANT = "6-lieutenant";
    public static final String SARGEANT = "7-sargeant";
    public static final String MINER = "8-miner";
    public static final String SCOUT = "9-scout";
    public static final String SPY = "spy";
    //    private final ComponentMapper<PillComponent> pillM = ComponentMapper.getFor(PillComponent.class);
    private final ComponentMapper<EnemyComponent> enemyM = ComponentMapper.getFor(EnemyComponent.class);
    private final ComponentMapper<PlayerComponent> playerM = ComponentMapper.getFor(PlayerComponent.class);
    private final ComponentMapper<Player1Component> player1M = ComponentMapper.getFor(Player1Component.class);

    public WorldContactListener() {
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

//        if (fixtureA.getFilterData().categoryBits == GameManager.PILL_BIT || fixtureB.getFilterData().categoryBits == GameManager.PILL_BIT) {
//            // pill
//            if (fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
//                Body body = fixtureB.getBody();
//                Entity entity = (Entity) body.getUserData();
//                PillComponent pill = pillM.get(entity);
//                pill.eaten = true;
//                GameManager.instance.bigPillEaten = pill.big;
//            } else if (fixtureB.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
//                Body body = fixtureA.getBody();
//                Entity entity = (Entity) body.getUserData();
//                PillComponent pill = pillM.get(entity);
//                pill.eaten = true;
//                GameManager.instance.bigPillEaten = pill.big;
//            }
//        } else
        if (isEnemy(fixtureA) || isEnemy(fixtureB)) {
            if (isPlayer(fixtureA)) {
                PlayerComponent player = playerM.get((Entity) fixtureA.getBody().getUserData());
                EnemyComponent enemy = enemyM.get((Entity) fixtureB.getBody().getUserData());
                if (enemy.currentState == EnemyComponent.DIE) {
                    return;
                }

                checkRanks(player, enemy);
            } else if (isPlayer1(fixtureA)) {
                Player1Component player1 = player1M.get((Entity) fixtureA.getBody().getUserData());
                EnemyComponent enemy = enemyM.get((Entity) fixtureB.getBody().getUserData());
                if (enemy.currentState == EnemyComponent.DIE) {
                    return;
                }

                checkRanks(player1, enemy);
            } else if (isPlayer(fixtureB)) {
                PlayerComponent player = playerM.get((Entity) fixtureB.getBody().getUserData());
                EnemyComponent enemy = enemyM.get((Entity) fixtureA.getBody().getUserData());
                if (enemy.currentState == EnemyComponent.DIE) {
                    return;
                }

                checkRanks(player, enemy);
            } else if (isPlayer1(fixtureB)) {
                Player1Component player1 = player1M.get((Entity) fixtureB.getBody().getUserData());
                EnemyComponent enemy = enemyM.get((Entity) fixtureA.getBody().getUserData());
                if (enemy.currentState == EnemyComponent.DIE) {
                    return;
                }

                checkRanks(player1, enemy);
            }
        }
    }

    private void checkRanks(PlayerComponent player, EnemyComponent enemy) {
        if (outranks(player, enemy)) {
            enemy.hp--;
            GameManager.instance.addScore(800);
            GameManager.instance.assetManager.get("sounds/ghost_die.ogg", Sound.class).play();
        } else {
            if (!GameManager.instance.playerIsInvincible) {
                player.hp--;
                if (GameManager.instance.playerIsAlive) {
                    GameManager.instance.assetManager.get("sounds/pacman_die.ogg", Sound.class).play();
                }
            }
        }
    }

    private void checkRanks(Player1Component player, EnemyComponent enemy) {
        if (outranks(player, enemy)) {
            enemy.hp--;
            GameManager.instance.addScore(800);
            GameManager.instance.assetManager.get("sounds/ghost_die.ogg", Sound.class).play();
        } else {
            if (!GameManager.instance.playerIsInvincible) {
                player.hp--;
                if (GameManager.instance.playerIsAlive) {
                    GameManager.instance.assetManager.get("sounds/pacman_die.ogg", Sound.class).play();
                }
            }
        }
    }

    private boolean outranks(PlayerComponent player, EnemyComponent enemy) {
        String playerName = player.getName();
        String enemyName = enemy.getName();

        return outranks(playerName, enemyName);
    }

    private boolean outranks(Player1Component player, EnemyComponent enemy) {
        String playerName = player.getName();
        String enemyName = enemy.getName();

        return outranks(playerName, enemyName);
    }

    private boolean outranks(String entity1, String entity2) {
        if (entity1 == null) {
            return false;
        }

        switch (entity1) {
            case MARSHAL:
                if (entity2.equals(BOMB)) {
                    Gdx.app.log("[rank]", entity1 + " blowed up");
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case GENERAL:
                if (entity2.equals(BOMB) || entity2.equals(MARSHAL)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case COLONEL:
                if (entity2.equals(BOMB) || entity2.equals(MARSHAL) || entity2.equals(GENERAL)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case MAJOR:
                if (entity2.equals(BOMB) || entity2.equals(MARSHAL) || entity2.equals(GENERAL) || entity2.equals(COLONEL)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case CAPTAIN:
                if (entity2.equals(BOMB) || entity2.equals(MARSHAL) || entity2.equals(GENERAL) || entity2.equals(COLONEL)
                        || entity2.equals(MAJOR)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case LIEUTENANT:
                if (entity2.equals(BOMB) || entity2.equals(MARSHAL) || entity2.equals(GENERAL) || entity2.equals(COLONEL)
                        || entity2.equals(MAJOR) || entity2.equals(CAPTAIN)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case SARGEANT:
                if (entity2.equals(BOMB) || entity2.equals(MARSHAL) || entity2.equals(GENERAL) || entity2.equals(COLONEL)
                        || entity2.equals(MAJOR) || entity2.equals(CAPTAIN) || entity2.equals(LIEUTENANT)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case MINER:
                if (entity2.equals(MARSHAL) || entity2.equals(GENERAL) || entity2.equals(COLONEL)
                        || entity2.equals(MAJOR) || entity2.equals(CAPTAIN) || entity2.equals(LIEUTENANT)
                        || entity2.equals(SARGEANT)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case SCOUT:
                if (entity2.equals(BOMB) || entity2.equals(MARSHAL) || entity2.equals(GENERAL) || entity2.equals(COLONEL)
                        || entity2.equals(MAJOR) || entity2.equals(CAPTAIN) || entity2.equals(LIEUTENANT)
                        || entity2.equals(SARGEANT) || entity2.equals(MINER)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case SPY:
                if (entity2.equals(MARSHAL)) {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                } else {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                }
            case "bomb":
                if (entity2.equals(MINER)) {
                    Gdx.app.log("[rank]", entity2 + " + took " + entity1);
                    return false;
                } else {
                    Gdx.app.log("[rank]", entity1 + " + took " + entity2);
                    return true;
                }
            case "flag":
                Gdx.app.log("[rank]", "FLAG CAPTURED!!");
                return true;
            default:
                return false;
        }
    }

    private boolean isPlayer(Fixture fixtureA) {
        return fixtureA.getFilterData().categoryBits == GameManager.PLAYER_BIT;
    }

    private boolean isPlayer1(Fixture fixtureA) {
        return fixtureA.getFilterData().categoryBits == GameManager.PLAYER_1_BIT;
    }

    private boolean isEnemy(Fixture fixtureA) {
        return fixtureA.getFilterData().categoryBits == GameManager.ENEMY_BIT;
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
