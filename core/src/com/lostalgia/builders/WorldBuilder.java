package com.lostalgia.builders;

import box2dLight.RayHandler;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.lostalgia.ai.astar.AStarMap;
import com.lostalgia.ai.astar.AStartPathFinding;
import com.lostalgia.components.*;
import com.lostalgia.gamesys.GameManager;

public class WorldBuilder {

    private final TiledMap tiledMap;
    private final World world;
    private final RayHandler rayHandler;
    private final Engine engine;

    private final AssetManager assetManager;
    private final TextureAtlas actorAtlas;

    private boolean wall;

    public WorldBuilder(TiledMap tiledMap, Engine engine, World world, RayHandler rayHandler) {
        this.tiledMap = tiledMap;
        this.engine = engine;
        this.world = world;
        this.rayHandler = rayHandler;

        assetManager = GameManager.instance.assetManager;
        actorAtlas = assetManager.get("images/pieces/pack.atlas", TextureAtlas.class);
    }

    public void buildAll() {
        buildMap();
    }

    private void buildMap() {
        MapLayers mapLayers = tiledMap.getLayers();
        buildWalls(mapLayers);
        buildPathfinding(((TiledMapTileLayer) mapLayers.get(0)).getWidth(), ((TiledMapTileLayer) mapLayers.get(0)).getHeight());
//        builPills(mapLayers);
        buildEnemies(mapLayers);
        buildPlayer(mapLayers);
        buildPlayer1(mapLayers);
    }

    private void buildEnemies(MapLayers mapLayers) {
        MapLayer enemiesLayer = mapLayers.get("Enemy");
        for (MapObject mapObject : enemiesLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            correctRectangle(rectangle);

            GameManager.instance.enemySpawnPos.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

            // create enemies
            createEnemy(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2, mapObject.getName());
        }
    }

    private void buildPlayer1(MapLayers mapLayers) {
        MapLayer player1Layer = mapLayers.get("Player1");
        for (MapObject mapObject : player1Layer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            correctRectangle(rectangle);

//            GameManager.instance.enemySpawnPos.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

            // create players
            createPlayer1(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2, mapObject.getName());
        }
    }

    private void buildPlayer(MapLayers mapLayers) {
        MapLayer playerLayer = mapLayers.get("Player"); // player layer
        for (MapObject mapObject : playerLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            correctRectangle(rectangle);

            GameManager.instance.playerSpawnPos.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

            createPlayer(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
        }
    }

    private void builPills(MapLayers mapLayers) {
        MapLayer pillLayer = mapLayers.get("Pill"); // pill layer
        for (MapObject mapObject : pillLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            correctRectangle(rectangle);

            boolean isBig = false;
            float radius = 0.1f;
            TextureRegion textureRegion;

            if (mapObject.getProperties().containsKey("big")) {
                isBig = true;
                radius = 0.2f;
                textureRegion = new TextureRegion(actorAtlas.findRegion("Pill"), 16, 0, 16, 16);
            } else {
                textureRegion = new TextureRegion(actorAtlas.findRegion("Pill"), 0, 0, 16, 16);
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
            Body body = world.createBody(bodyDef);

            CircleShape circleShape = new CircleShape();
            circleShape.setRadius(radius);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circleShape;
            fixtureDef.filter.categoryBits = GameManager.PILL_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT;
            fixtureDef.isSensor = true;
            body.createFixture(fixtureDef);

            circleShape.dispose();

            Entity entity = new Entity();
            entity.add(new PillComponent(isBig));
            entity.add(new TransformComponent(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2, 5));
            entity.add(new TextureComponent(textureRegion));
            entity.add(new MovementComponent(body));

            engine.addEntity(entity);
            body.setUserData(entity);

            GameManager.instance.totalPills++;
        }
    }

    private void buildPathfinding(int mapWidth, int mapHeight) {
        // create map for A* path finding
        AStarMap aStarMap = new AStarMap(mapWidth, mapHeight);

        QueryCallback queryCallback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                wall = fixture.getFilterData().categoryBits == GameManager.WALL_BIT;
                return false; // stop finding other fixtures in the query area
            }
        };

        for (int y = 0; y < mapHeight; y++) {
            for (int x = 0; x < mapWidth; x++) {
                wall = false;
                world.QueryAABB(queryCallback, x + 0.2f, y + 0.2f, x + 0.8f, y + 0.8f);
                if (wall) {
                   aStarMap.getNodeAt(x, y).isWall = true;
                }
            }
        }
        GameManager.instance.pathfinder = new AStartPathFinding(aStarMap);
    }

    private void buildWalls(MapLayers mapLayers) {
        MapLayer wallLayer = mapLayers.get("Wall"); // wall layer
        for (MapObject mapObject : wallLayer.getObjects()) {
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();

            correctRectangle(rectangle);

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);

            Body body = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.setAsBox(rectangle.width / 2, rectangle.height / 2);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            fixtureDef.filter.categoryBits = GameManager.WALL_BIT;
            fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.PLAYER_1_BIT | GameManager.ENEMY_BIT | GameManager.LIGHT_BIT;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }
    }

    // make rectangle correct position and dimensions
    private void correctRectangle(Rectangle rectangle) {
        rectangle.x = rectangle.x / GameManager.PPM;
        rectangle.y = rectangle.y / GameManager.PPM;
        rectangle.width = rectangle.width / GameManager.PPM;
        rectangle.height = rectangle.height / GameManager.PPM;
    }

    private void createPlayer(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 16f;

        Body body = world.createBody(bodyDef);

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.45f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.PLAYER_BIT;
        fixtureDef.filter.maskBits = GameManager.WALL_BIT | GameManager.GATE_BIT |
                GameManager.ENEMY_BIT | GameManager.PLAYER_1_BIT | GameManager.PILL_BIT;
        body.createFixture(fixtureDef);
        circleShape.dispose();

        // box2d light
//        PointLight pointLight = new PointLight(rayHandler, 50, new Color(0.5f, 0.5f, 0.5f, 1.0f), 12f, 0, 0);
//        pointLight.setContactFilter(GameManager.LIGHT_BIT, GameManager.NOTHING_BIT, GameManager.WALL_BIT);
//        pointLight.setSoft(true);
//        pointLight.setSoftnessLength(2.0f);
//        pointLight.attachToBody(body);

        String playerRegionName = "9-scout";

        TextureRegion textureRegion = new TextureRegion(actorAtlas.findRegion(playerRegionName), 0, 0, 16, 16);

        PlayerComponent player = new PlayerComponent(body);
        GameManager.instance.playerLocation = player.ai;

        Entity entity = new Entity();
        entity.add(player);
        entity.add(new TransformComponent(x, y, 1));
        entity.add(new MovementComponent(body));
        entity.add(new StateComponent(PlayerComponent.IDLE_RIGHT));
        entity.add(new TextureComponent(textureRegion));

        AnimationComponent animationComponent = new AnimationComponent();
        Animation animation;
        Array<TextureRegion> keyFrames = new Array<>();

        // idle
        keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), 16 * 2, 16 * 1, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_RIGHT, animation);
        keyFrames.clear();

        keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), 16 * 0, 16 * 3, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_LEFT, animation);
        keyFrames.clear();

        keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), 16 * 1, 0, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_UP, animation);
        keyFrames.clear();

        keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), 16 * 1, 16 * 2, 16, 16));
        animation = new Animation(0.2f, keyFrames);
        animationComponent.animations.put(PlayerComponent.IDLE_DOWN, animation);
        keyFrames.clear();

        // move
        int j = 1;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_RIGHT, animation);
        keyFrames.clear();

        j = 3;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_LEFT, animation);
        keyFrames.clear();

        j = 0;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_UP, animation);
        keyFrames.clear();

        j = 2;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        animationComponent.animations.put(PlayerComponent.MOVE_DOWN, animation);
        keyFrames.clear();

        j = 0;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), i * 16, j * 16, 16, 16));
        }
        keyFrames.add(new TextureRegion(actorAtlas.findRegion(playerRegionName), 9 * 16, 0, 16, 16)); // invisible
        animation = new Animation(0.1f, keyFrames, Animation.PlayMode.NORMAL);
        animationComponent.animations.put(PlayerComponent.DIE, animation);
        keyFrames.clear();

        entity.add(animationComponent);

        engine.addEntity(entity);
        body.setUserData(entity);
    }

    private void createEnemy(float x, float y, String characterName) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.4f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.ENEMY_BIT;
        fixtureDef.filter.maskBits = GameManager.PLAYER_BIT | GameManager.PLAYER_1_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        fixtureDef.filter.categoryBits = GameManager.ENEMY_BIT;
        fixtureDef.filter.maskBits = GameManager.WALL_BIT | GameManager.GATE_BIT | GameManager.ENEMY_BIT;
        fixtureDef.isSensor = false;
        body.createFixture(fixtureDef);

        // box2d light
//        PointLight pointLight = new PointLight(rayHandler, 50, new Color(0.2f, 0.2f, 0.2f, 1.0f), 12f, 0, 0);
//        pointLight.setContactFilter(GameManager.LIGHT_BIT, GameManager.NOTHING_BIT, GameManager.WALL_BIT);
//        pointLight.setSoft(true);
//        pointLight.setSoftnessLength(2.0f);
//        pointLight.attachToBody(body);

        circleShape.dispose();

        TextureRegion textureRegion = actorAtlas.findRegion("0-enemy");

        AnimationComponent anim = new AnimationComponent();
        Animation animation;
        Array<TextureRegion> keyFrames = new Array<>();

        // move up
        int j = 0;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(EnemyComponent.MOVE_UP, animation);
        keyFrames.clear();

        // move right
        j = 1;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(EnemyComponent.MOVE_RIGHT, animation);
        keyFrames.clear();

        // move down
        j = 2;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(EnemyComponent.MOVE_DOWN, animation);
        keyFrames.clear();

        // move left
        j = 3;
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(EnemyComponent.MOVE_LEFT, animation);
        keyFrames.clear();

        // escape
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(EnemyComponent.ESCAPE, animation);
        keyFrames.clear();

        // die
        for (int i = 0; i < 2; i++) {
            keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 0, 0));
        }
        animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
        anim.animations.put(EnemyComponent.DIE, animation);
        EnemyComponent enemyComponent = new EnemyComponent(body, characterName);

        Entity entity = new Entity();
        entity.add(enemyComponent);
        entity.add(new TransformComponent(x, y, 3));
        entity.add(new MovementComponent(body));
        entity.add(new StateComponent());
        entity.add(new TextureComponent(new TextureRegion(textureRegion, 0, 0, 16, 16)));
        entity.add(anim);

        engine.addEntity(entity);
        body.setUserData(entity);
    }

    private void createPlayer1(float x, float y, String characterName) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);

        Body body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(0.4f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = GameManager.PLAYER_1_BIT;
        fixtureDef.filter.maskBits = GameManager.ENEMY_BIT;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        fixtureDef.filter.categoryBits = GameManager.PLAYER_1_BIT;
        fixtureDef.filter.maskBits = GameManager.WALL_BIT | GameManager.GATE_BIT | GameManager.PLAYER_1_BIT | GameManager.PLAYER_BIT;
        fixtureDef.isSensor = false;
        body.createFixture(fixtureDef);

        // box2d light
//        PointLight pointLight = new PointLight(rayHandler, 50, new Color(0.2f, 0.2f, 0.2f, 1.0f), 12f, 0, 0);
//        pointLight.setContactFilter(GameManager.LIGHT_BIT, GameManager.NOTHING_BIT, GameManager.WALL_BIT);
//        pointLight.setSoft(true);
//        pointLight.setSoftnessLength(2.0f);
//        pointLight.attachToBody(body);

        circleShape.dispose();

        TextureRegion textureRegion = actorAtlas.findRegion(characterName);
        if (textureRegion == null) {
            throw new RuntimeException("No texture found for " + characterName);
        }

        AnimationComponent anim = new AnimationComponent();
        Animation animation;
        Array<TextureRegion> keyFrames = new Array<>();

        if (characterName.equals("bomb")) {
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, 0, 0, 16, 16));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.FIXED, animation);
        } else if (characterName.equals("flag")) {
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, 2 * 16, 16, 16));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.FIXED, animation);
        } else {
            // move up
            int j = 0;
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.MOVE_UP, animation);
            keyFrames.clear();

            // move right
            j = 1;
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.MOVE_RIGHT, animation);
            keyFrames.clear();

            // move down
            j = 2;
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.MOVE_DOWN, animation);
            keyFrames.clear();

            // move left
            j = 3;
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.MOVE_LEFT, animation);
            keyFrames.clear();

            // escape
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 16, 16));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.ESCAPE, animation);
            keyFrames.clear();

            // die
            for (int i = 0; i < 2; i++) {
                keyFrames.add(new TextureRegion(textureRegion, i * 16, j * 16, 0, 0));
            }
            animation = new Animation(0.2f, keyFrames, Animation.PlayMode.LOOP);
            anim.animations.put(Player1Component.DIE, animation);
        }

        Player1Component player1Component = new Player1Component(body, characterName);

        Entity entity = new Entity();
        entity.add(player1Component);
        entity.add(new TransformComponent(x, y, 3));
        entity.add(new MovementComponent(body));
        entity.add(new StateComponent());
        entity.add(new TextureComponent(new TextureRegion(textureRegion, 0, 0, 16, 16)));
        entity.add(anim);

        engine.addEntity(entity);
        body.setUserData(entity);
    }
}
