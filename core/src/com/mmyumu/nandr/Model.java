package com.mmyumu.nandr;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mmyumu.nandr.controller.KeyboardController;
import com.mmyumu.nandr.loader.NAndRAssetManager;

public class Model {
    public static final int BOING_SOUND = 0;
    public static final int PING_SOUND = 1;

    public final Body player;
    public final World world;
    private final OrthographicCamera camera;
    private final Sound ping;
    private final Sound boing;
    private Body bodyd;
    private Body bodys;
    private Body bodyk;

    public boolean isSwimming = false;
    private KeyboardController controller;
    private NAndRAssetManager assetManager;

    public Model(KeyboardController controller, OrthographicCamera camera, NAndRAssetManager assetManager) {
        this.camera = camera;
        this.controller = controller;
        this.assetManager = assetManager;
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new NandRContactListener(this));
//        createFloor();
        //createObject();
        //createMovingObject();

        // get our body factory singleton and store it in bodyFactory
        BodyFactory bodyFactory = BodyFactory.getInstance(world);

        // add a player
        player = bodyFactory.makeBoxPolyBody(1, 1, 2, 2, BodyFactory.RUBBER, BodyDef.BodyType.DynamicBody, false);

        // add some water
        Body water = bodyFactory.makeBoxPolyBody(1, 0, 40, 12, BodyFactory.RUBBER, BodyDef.BodyType.StaticBody, false);
        water.setUserData("IAMTHESEA");

        // Floor
        bodyFactory.makeBoxPolyBody(1, -10, 40, 8, BodyFactory.RUBBER, BodyDef.BodyType.StaticBody, false);

        // make the water a sensor so it doesn't obstruct our player
        bodyFactory.makeAllFixturesSensors(water);

        // tells our asset manger that we want to load the images set in loadImages method
        assetManager.queueAddSounds();
// tells the asset manager to load the images and wait until finsihed loading.
        assetManager.manager.finishLoading();
// loads the 2 sounds we use
        ping = assetManager.manager.get("sounds/ping.wav", Sound.class);
        boing = assetManager.manager.get("sounds/boing.wav", Sound.class);
    }

    // our game logic here
    public void logicStep(float delta) {
        if (controller.isMouse1Down && pointIntersectsBody(player, controller.mouseLocation)) {
            System.out.println("Player was clicked");
        }

        if (controller.left) {
            player.applyForceToCenter(-10, 0, true);
        } else if (controller.right) {
            player.applyForceToCenter(10, 0, true);
        } else if (controller.up) {
            player.applyForceToCenter(0, 10, true);
        } else if (controller.down) {
            player.applyForceToCenter(0, -10, true);
        }

        if (isSwimming) {
            player.applyForceToCenter(0, 40, true);
        }

        world.step(delta, 3, 3);
    }

    private void createObject() {
        //create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(0, 0);


        // add it to the world
        bodyd = world.createBody(bodyDef);

        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);

        // set the properties of the object ( shape, weight, restitution(bouncyness)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        // create the physical object in our body)
        // without this our body would just be data in the world
        bodyd.createFixture(shape, 0.0f);

        // we no longer use the shape object here so dispose of it.
        shape.dispose();
    }

    private void createFloor() {
        // create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, -10);
        // add it to the world
        bodys = world.createBody(bodyDef);
        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50, 1);
        // create the physical object in our body)
        // without this our body would just be data in the world
        bodys.createFixture(shape, 0.0f);
        // we no longer use the shape object here so dispose of it.
        shape.dispose();
    }

    private void createMovingObject() {

        //create a new body definition (type and location)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(0, -12);


        // add it to the world
        bodyk = world.createBody(bodyDef);

        // set the shape (here we use a box 50 meters wide, 1 meter tall )
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1, 1);

        // set the properties of the object ( shape, weight, restitution(bouncyness)
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;

        // create the physical object in our body)
        // without this our body would just be data in the world
        bodyk.createFixture(shape, 0.0f);

        // we no longer use the shape object here so dispose of it.
        shape.dispose();

        bodyk.setLinearVelocity(0, 0.75f);
    }

    /**
     * Checks if point is in first fixture
     * Does not check all fixtures.....yet
     *
     * @param body          the Box2D body to check
     * @param mouseLocation the point on the screen
     * @return true if click is inside body
     */
    public boolean pointIntersectsBody(Body body, Vector2 mouseLocation) {
        Vector3 mousePos = new Vector3(mouseLocation, 0); //convert mouseLocation to 3D position
        camera.unproject(mousePos); // convert from screen potition to world position
        if (body.getFixtureList().first().testPoint(mousePos.x, mousePos.y)) {
            return true;
        }
        return false;
    }

    public void playSound(int sound) {
        switch (sound) {
            case BOING_SOUND:
                boing.play();
                break;
            case PING_SOUND:
                ping.play();
                break;
        }
    }
}