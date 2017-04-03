package com.lostalgia.ai.fsm;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.lostalgia.ai.astar.AStarMap;
import com.lostalgia.components.Player1Component;
import com.lostalgia.gamesys.GameManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public enum Player1State implements State<Player1Agent> {

    MOVE_UP() {
        @Override
        public void update(Player1Agent entity) {
            if (entity.player1Component.isFixed()) {
                entity.player1Component.currentState = Player1Component.FIXED;
                return;
            }

            entity.player1Component.currentState = Player1Component.MOVE_UP;

            Body body = entity.player1Component.getBody();
            body.applyLinearImpulse(tmpV1.set(0, entity.speed).scl(body.getMass()), body.getWorldCenter(), true);

            if (body.getLinearVelocity().len2() > entity.speed * entity.speed) {
                body.setLinearVelocity(body.getLinearVelocity().scl(entity.speed / body.getLinearVelocity().len()));
            }

            if (checkHitWall(entity, Player1Component.MOVE_UP)) {
                changeState(entity, getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_DOWN)));
                return;
            }

            if (entity.timer > 0.5f && inPosition(entity, 0.05f)) {
                entity.timer = 0;
                int newState = getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_DOWN));
                if (newState != entity.player1Component.currentState) {
                    changeState(entity, newState);
                    return;
                }
            }

//            if (entity.player1Component.weaken) {
//                entity.player1Component.currentState = Player1Component.ESCAPE;
//                if (entity.player1Component.hp <= 0 && inPosition(entity, 0.1f)) {
//                    entity.stateMachine.changeState(DIE);
//                    return;
//                }
//            }

//            if (nearPlayer(entity, PURSUE_RADIUS) && (GameManager.instance.playerIsAlive && !GameManager.instance.playerIsInvincible) && inPosition(entity, 0.1f)) {
//                if (entity.player1Component.weaken) {
//                    entity.stateMachine.changeState(ESCAPE);
//                } else {
//                    entity.stateMachine.changeState(PURSUE);
//                }
//            }
        }

    },
    MOVE_DOWN() {
        @Override
        public void update(Player1Agent entity) {
            if (entity.player1Component.isFixed()) {
                entity.player1Component.currentState = Player1Component.FIXED;
                return;
            }

            entity.player1Component.currentState = Player1Component.MOVE_DOWN;

            Body body = entity.player1Component.getBody();
            body.applyLinearImpulse(tmpV1.set(0, -entity.speed).scl(body.getMass()), body.getWorldCenter(), true);

            if (body.getLinearVelocity().len2() > entity.speed * entity.speed) {
                body.setLinearVelocity(body.getLinearVelocity().scl(entity.speed / body.getLinearVelocity().len()));
            }

            if (checkHitWall(entity, Player1Component.MOVE_DOWN)) {
                changeState(entity, getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_UP)));
                return;
            }

            if (entity.timer > 0.5f && inPosition(entity, 0.05f)) {
                entity.timer = 0;
                int newState = getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_UP));
                if (newState != entity.player1Component.currentState) {
                    changeState(entity, newState);
                    return;
                }
            }

//            if (entity.player1Component.weaken) {
//                entity.player1Component.currentState = Player1Component.ESCAPE;
//                if (entity.player1Component.hp <= 0 && inPosition(entity, 0.1f)) {
//                    entity.stateMachine.changeState(DIE);
//                    return;
//                }
//            }

            if (nearPlayer(entity, PURSUE_RADIUS) 
                    && (GameManager.instance.playerIsAlive 
                    && !GameManager.instance.playerIsInvincible) && inPosition(entity, 0.1f)) {
//                if (entity.player1Component.weaken) {
//                    entity.stateMachine.changeState(ESCAPE);
//                } else {
                    entity.stateMachine.changeState(PURSUE);
//                }
            }
        }
    },
    MOVE_LEFT() {
        @Override
        public void update(Player1Agent entity) {
            if (entity.player1Component.isFixed()) {
                entity.player1Component.currentState = Player1Component.FIXED;
                return;
            }

            entity.player1Component.currentState = Player1Component.MOVE_LEFT;

            Body body = entity.player1Component.getBody();
            body.applyLinearImpulse(tmpV1.set(-entity.speed, 0).scl(body.getMass()), body.getWorldCenter(), true);

            if (body.getLinearVelocity().len2() > entity.speed * entity.speed) {
                body.setLinearVelocity(body.getLinearVelocity().scl(entity.speed / body.getLinearVelocity().len()));
            }

            if (checkHitWall(entity, Player1Component.MOVE_LEFT)) {
                changeState(entity, getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_RIGHT)));
                return;
            }

            if (entity.timer > 0.5f && inPosition(entity, 0.05f)) {
                entity.timer = 0;
                int newState = getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_RIGHT));
                if (newState != entity.player1Component.currentState) {
                    changeState(entity, newState);
                    return;
                }
            }

//            if (entity.player1Component.weaken) {
//                entity.player1Component.currentState = Player1Component.ESCAPE;
//
//                if (entity.player1Component.hp <= 0 && inPosition(entity, 0.1f)) {
//                    entity.stateMachine.changeState(DIE);
//                    return;
//                }
//            }

            if (nearPlayer(entity, PURSUE_RADIUS) && (GameManager.instance.playerIsAlive && !GameManager.instance.playerIsInvincible) && inPosition(entity, 0.1f)) {
//                if (entity.player1Component.weaken) {
//                    entity.stateMachine.changeState(ESCAPE);
//                } else {
                    entity.stateMachine.changeState(PURSUE);
//                }
            }
        }
    },
    MOVE_RIGHT() {
        @Override
        public void update(Player1Agent entity) {
            if (entity.player1Component.isFixed()) {
                entity.player1Component.currentState = Player1Component.FIXED;
                return;
            }

            entity.player1Component.currentState = Player1Component.MOVE_RIGHT;

            Body body = entity.player1Component.getBody();
            body.applyLinearImpulse(tmpV1.set(entity.speed, 0).scl(body.getMass()), body.getWorldCenter(), true);

            if (body.getLinearVelocity().len2() > entity.speed * entity.speed) {
                body.setLinearVelocity(body.getLinearVelocity().scl(entity.speed / body.getLinearVelocity().len()));
            }

            if (checkHitWall(entity, Player1Component.MOVE_RIGHT)) {
                changeState(entity, getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_LEFT)));
                return;
            }

            if (entity.timer > 0.5f && inPosition(entity, 0.05f)) {
                entity.timer = 0;
                int newState = getRandomDirectionChoice(getDirectionChoices(entity, Player1Component.MOVE_LEFT));
                if (newState != entity.player1Component.currentState) {
                    changeState(entity, newState);
                    return;
                }
            }

//            if (entity.player1Component.weaken) {
//                entity.player1Component.currentState = Player1Component.ESCAPE;
//
//                if (entity.player1Component.hp <= 0 && inPosition(entity, 0.1f)) {
//                    entity.stateMachine.changeState(DIE);
//                    return;
//                }
//            }

            if (nearPlayer(entity, PURSUE_RADIUS) && (GameManager.instance.playerIsAlive && !GameManager.instance.playerIsInvincible) && inPosition(entity, 0.1f)) {
//                if (entity.player1Component.weaken) {
//                    entity.stateMachine.changeState(ESCAPE);
//                } else {
                    entity.stateMachine.changeState(PURSUE);
//                }
            }
        }
    },
    PURSUE() {
        @Override
        public void update(Player1Agent entity) {
            if (entity.player1Component.isFixed()) {
                entity.player1Component.currentState = Player1Component.FIXED;
                return;
            }

            // run after the player
            if (GameManager.instance.playerLocation == null || !(GameManager.instance.playerIsAlive && !GameManager.instance.playerIsInvincible)) {
                changeState(entity, MathUtils.random(0, 3));
                return;
            }

            // do path finding every 0.1 second
            if (entity.nextNode == null || entity.timer > 0.1f) {
                entity.nextNode = GameManager.instance.pathfinder.findNextNode(entity.getPosition(), GameManager.instance.playerLocation.getPosition());
                entity.timer = 0;
            }
            if (entity.nextNode == null) {
                // no path found or player is dead
                changeState(entity, MathUtils.random(0, 3));
                return;
            }

            float x = (entity.nextNode.x - MathUtils.floor(entity.getPosition().x)) * entity.speed;
            float y = (entity.nextNode.y - MathUtils.floor(entity.getPosition().y)) * entity.speed;

            Body body = entity.player1Component.getBody();

            if (body.getLinearVelocity().isZero(0.1f) || inPosition(entity, 0.2f)) {
                body.applyLinearImpulse(tmpV1.set(x, y).scl(body.getMass()), body.getWorldCenter(), true);
            }

            if (x > 0) {
                entity.player1Component.currentState = Player1Component.MOVE_RIGHT;
            } else if (x < 0) {
                entity.player1Component.currentState = Player1Component.MOVE_LEFT;
            } else if (y > 0) {
                entity.player1Component.currentState = Player1Component.MOVE_UP;
            } else if (y < 0) {
                entity.player1Component.currentState = Player1Component.MOVE_DOWN;
            }

            if (body.getLinearVelocity().len2() > entity.speed * entity.speed) {
                body.setLinearVelocity(body.getLinearVelocity().scl(entity.speed / body.getLinearVelocity().len()));
            }

            if (!nearPlayer(entity, PURSUE_RADIUS) && inPosition(entity, 0.1f)) {
                changeState(entity, entity.player1Component.currentState);
                return;
            }

//            if (entity.player1Component.weaken) {
//                entity.player1Component.currentState = Player1Component.ESCAPE;
//                if (inPosition(entity, 0.1f)) {
//                    entity.stateMachine.changeState(ESCAPE);
//                }
//            }
        }

    },
    ESCAPE() {
        @Override
        public void update(Player1Agent entity) {
            if (entity.player1Component.isFixed()) {
                entity.player1Component.currentState = Player1Component.FIXED;
                return;
            }

            // get away from the player
            entity.player1Component.currentState = Player1Component.ESCAPE;

            // update path every 0.2f
            if (entity.nextNode == null || entity.timer > 0.2f) {
                AStarMap map = GameManager.instance.pathfinder.map;

                float x = (GameManager.instance.playerLocation.getPosition().x + map.getWidth() / 2);
                float y = (GameManager.instance.playerLocation.getPosition().y + map.getHeight() / 2);

                do {
                    x += 1;
                    y += 1;
                    x = x > map.getWidth() ? x - map.getWidth() : x;
                    y = y > map.getHeight() ? y - map.getHeight() : y;
                } while (map.getNodeAt(MathUtils.floor(x), MathUtils.floor(y)).isWall);

                tmpV1.set(x, y);
                entity.nextNode = GameManager.instance.pathfinder.findNextNode(entity.getPosition(), tmpV1);
                entity.timer = 0;
            }

            if (entity.nextNode == null || !nearPlayer(entity, PURSUE_RADIUS + 1)) {
                // no path found or away from the player
                changeState(entity, MathUtils.random(0, 3));
                return;
            }

            float x = (entity.nextNode.x - MathUtils.floor(entity.getPosition().x)) * entity.speed;
            float y = (entity.nextNode.y - MathUtils.floor(entity.getPosition().y)) * entity.speed;

            Body body = entity.player1Component.getBody();

            if (body.getLinearVelocity().isZero(0.1f) || inPosition(entity, 0.1f)) {
                body.applyLinearImpulse(tmpV1.set(x, y).scl(body.getMass()), body.getWorldCenter(), true);
            }

            if (body.getLinearVelocity().len2() > entity.speed * entity.speed) {
                body.setLinearVelocity(body.getLinearVelocity().scl(entity.speed / body.getLinearVelocity().len()));
            }

//            if (!entity.player1Component.weaken && inPosition(entity, 0.1f)) {
//                entity.stateMachine.changeState(PURSUE);
//                return;
//            }

            if (entity.player1Component.hp <= 0 && inPosition(entity, 0.1f)) {
                entity.stateMachine.changeState(DIE);
            }
        }
    },
    DIE() {
        @Override
        public void update(Player1Agent entity
        ) {
            entity.player1Component.currentState = Player1Component.DIE;
            // respawn when getting back to the respawning postion
            // update path every 0.2f
            if (entity.nextNode == null || entity.timer > 0.2f) {
                entity.nextNode = GameManager.instance.pathfinder.findNextNode(entity.getPosition(), GameManager.instance.enemySpawnPos);
                entity.timer = 0;
            }

            if (entity.nextNode == null || entity.getPosition().dst2(GameManager.instance.playerSpawnPos) < 0.04f) {
                // no path found or reach target
                entity.player1Component.getBody().setTransform(GameManager.instance.playerSpawnPos, 0);
                entity.stateMachine.changeState(RESPAWN);
                return;
            }

            float x = (entity.nextNode.x - MathUtils.floor(entity.getPosition().x)) * entity.speed;
            float y = (entity.nextNode.y - MathUtils.floor(entity.getPosition().y)) * entity.speed;

            Body body = entity.player1Component.getBody();

            if (body.getLinearVelocity().isZero(0.1f) || inPosition(entity, 0.2f)) {
                body.applyLinearImpulse(tmpV1.set(x, y).scl(body.getMass()), body.getWorldCenter(), true);
            }

            if (body.getLinearVelocity().len2() > entity.speed * entity.speed * 4) {
                body.setLinearVelocity(body.getLinearVelocity().scl(entity.speed * 2 / body.getLinearVelocity().len()));
            }
        }

    },
    RESPAWN() {
        @Override
        public void update(Player1Agent entity) {
            entity.player1Component.respawn();
            if (entity.player1Component.isFixed()) {
                entity.player1Component.currentState = Player1Component.FIXED;
            } else {
                entity.stateMachine.changeState(MOVE_UP);
            }
        }
    };

    protected boolean nearPlayer(Player1Agent entity, float distance) {
        if (GameManager.instance.playerLocation == null) {
            return false;
        }
        Vector2 pos = entity.getPosition();
        Vector2 playerPos = GameManager.instance.playerLocation.getPosition();

        return pos.dst2(playerPos) < distance * distance;
    }

    protected boolean inPosition(Player1Agent entity, float radius) {
        float x = entity.getPosition().x;
        float y = entity.getPosition().y;

        float xLow = MathUtils.floor(x) + 0.5f - radius;
        float xHight = MathUtils.floor(x) + 0.5f + radius;

        float yLow = MathUtils.floor(y) + 0.5f - radius;
        float yHight = MathUtils.floor(y) + 0.5f + radius;

        return xLow < x && x < xHight && yLow < y && y < yHight;
    }

    protected void changeState(Player1Agent entity, int state) {
        switch (state) {
            case Player1Component.MOVE_UP: // UP
                entity.stateMachine.changeState(MOVE_UP);
                break;
            case Player1Component.MOVE_DOWN: // DOWN
                entity.stateMachine.changeState(MOVE_DOWN);
                break;
            case Player1Component.MOVE_LEFT: // LEFT
                entity.stateMachine.changeState(MOVE_LEFT);
                break;
            case Player1Component.MOVE_RIGHT: // RIGHT
                entity.stateMachine.changeState(MOVE_RIGHT);
                break;
            case Player1Component.ESCAPE: // ESCAPE
                entity.stateMachine.changeState(ESCAPE);
                break;
            case Player1Component.DIE: // DIE
                entity.stateMachine.changeState(DIE);
                break;
            default:
                break;
        }
    }

    protected static final Vector2 tmpV1 = new Vector2();
    protected static final Vector2 tmpV2 = new Vector2();
    protected static final List<Integer> choicesList = new ArrayList<>(4);
    protected static boolean hitWall = false;

    protected static final float RADIUS = 0.55f;

    protected static final float PURSUE_RADIUS = 5f;

    protected boolean checkHitWall(Player1Agent entity, int state) {
        Body body = entity.player1Component.getBody();
        World world = body.getWorld();
        hitWall = false;

        tmpV1.set(body.getWorldCenter());

        switch (state) {
            case Player1Component.MOVE_UP:
                tmpV2.set(tmpV1).add(0, RADIUS);
                break;
            case Player1Component.MOVE_DOWN:
                tmpV2.set(tmpV1).add(0, -RADIUS);
                break;
            case Player1Component.MOVE_LEFT:
                tmpV2.set(tmpV1).add(-RADIUS, 0);
                break;
            case Player1Component.MOVE_RIGHT:
                tmpV2.set(tmpV1).add(RADIUS, 0);
                break;
            default:
                tmpV2.setZero();
                break;
        }
        world.rayCast(rayCastCallback, tmpV1, tmpV2);

        return hitWall;
    }

    protected Integer[] getDirectionChoices(Player1Agent entity, int state) {
        Body body = entity.player1Component.getBody();
        World world = body.getWorld();

        choicesList.clear();
        for (int i = 0; i < 4; i++) {
            choicesList.add(i);
        }

        choicesList.remove(state);

        tmpV1.set(body.getWorldCenter());

        Iterator<Integer> itor = choicesList.iterator();
        while (itor.hasNext()) {
            Integer integer = itor.next();

            hitWall = false;
            switch (integer) {
                case Player1Component.MOVE_UP: // UP
                    tmpV2.set(tmpV1).add(0, RADIUS);
                    break;
                case Player1Component.MOVE_DOWN: // DOWN
                    tmpV2.set(tmpV1).add(0, -RADIUS);
                    break;
                case Player1Component.MOVE_LEFT: // LEFT
                    tmpV2.set(tmpV1).add(-RADIUS, 0);
                    break;
                case Player1Component.MOVE_RIGHT: // RIGHT
                    tmpV2.set(tmpV1).add(RADIUS, 0);
                    break;
                default:
                    tmpV2.setZero();
                    break;
            }

            world.rayCast(rayCastCallback, tmpV1, tmpV2);
            if (hitWall) {
                itor.remove();
            }
        }

        Integer[] result = choicesList.toArray(new Integer[choicesList.size()]);

        return result;
    }

    protected RayCastCallback rayCastCallback = new RayCastCallback() {
        @Override
        public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
            if (fixture.getFilterData().categoryBits == GameManager.WALL_BIT ||
                    fixture.getFilterData().categoryBits == GameManager.PLAYER_1_BIT ||
                    fixture.getFilterData().categoryBits == GameManager.PLAYER_BIT) {
                hitWall = true;
                return 0;
            }
            return 0;
        }
    };

    protected int getRandomDirectionChoice(Integer[] choices) {
        if (choices.length == 0) {
            return 0;
        }
        int length = choices.length;
        return choices[MathUtils.random(length - 1)];
    }

    @Override
    public void enter(Player1Agent entity) {
        entity.player1Component.getBody().setLinearVelocity(0, 0);
        if (!inPosition(entity, 0.1f)) {
            entity.player1Component.getBody().setTransform(tmpV1.set(MathUtils.floor(entity.getPosition().x) + 0.5f, MathUtils.floor(entity.getPosition().y) + 0.5f), 0);
        }
        entity.timer = 0;
    }

    @Override
    public void exit(Player1Agent entity) {
        entity.nextNode = null;
    }

    @Override
    public boolean onMessage(Player1Agent entity, Telegram telegram) {
        return false;
    }

}
