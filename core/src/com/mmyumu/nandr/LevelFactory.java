package com.mmyumu.nandr;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mmyumu.nandr.entity.components.AnimationComponent;
import com.mmyumu.nandr.entity.components.B2dBodyComponent;
import com.mmyumu.nandr.entity.components.BulletComponent;
import com.mmyumu.nandr.entity.components.CollisionComponent;
import com.mmyumu.nandr.entity.components.EnemyComponent;
import com.mmyumu.nandr.entity.components.ParticleEffectComponent;
import com.mmyumu.nandr.entity.components.PlayerComponent;
import com.mmyumu.nandr.entity.components.StateComponent;
import com.mmyumu.nandr.entity.components.TextureComponent;
import com.mmyumu.nandr.entity.components.TransformComponent;
import com.mmyumu.nandr.entity.components.TypeComponent;
import com.mmyumu.nandr.entity.components.WallComponent;
import com.mmyumu.nandr.entity.components.WaterFloorComponent;
import com.mmyumu.nandr.loader.NAndRAssetManager;
import com.mmyumu.nandr.simplexnoise.OpenSimplexNoise;
import com.mmyumu.nandr.simplexnoise.SimplexNoise;

public class LevelFactory {
    private final ParticleEffectManager pem;
    private BodyFactory bodyFactory;
    public World world;
    private PooledEngine engine;
    private SimplexNoise sim; // a semi-smoothe noise for generating level parts
    private SimplexNoise simRough; // a more rough noise for generating more randomly placed items
    public int currentLevel = 0;
    private TextureRegion floorTex;
    private TextureRegion enemyTex;
    private TextureRegion waterTex;
    private TextureRegion platformTex;
    private TextureRegion bulletTex;
    private TextureAtlas atlas;
    private OpenSimplexNoise openSim;
    public Entity player;

    public LevelFactory(PooledEngine engine, NAndRAssetManager assetManager) {
        this.engine = engine;
        this.atlas = assetManager.manager.get("images/game.atlas", TextureAtlas.class);;
        //floorTex = DFUtils.makeTextureRegion(40*RenderingSystem.PPM, 0.5f*RenderingSystem.PPM, "111111FF");
        floorTex = atlas.findRegion("reallybadlydrawndirt");
        //enemyTex = DFUtils.makeTextureRegion(1*RenderingSystem.PPM,1*RenderingSystem.PPM, "331111FF");
        enemyTex = atlas.findRegion("waterdrop");


        waterTex = atlas.findRegion("water");
        bulletTex = DFUtils.makeTextureRegion(10, 10, "444444FF");
        //platformTex = DFUtils.makeTextureRegion(2*RenderingSystem.PPM, 0.1f*RenderingSystem.PPM, "221122FF");
        platformTex = atlas.findRegion("platform");
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new NAndRContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        // create a new SimplexNoise (size,roughness,seed)
        //sim = new SimplexNoise(1024, 1f, MathUtils.random(3));

        openSim = new OpenSimplexNoise(MathUtils.random(2000l));

        //simRough = new SimplexNoise(512, 1, MathUtils.random(3));

        pem = new ParticleEffectManager();
        pem.addParticleEffect(ParticleEffectManager.FIRE, assetManager.manager.get("particles/fire.pe",ParticleEffect.class),1f/128f);
        pem.addParticleEffect(ParticleEffectManager.WATER, assetManager.manager.get("particles/water.pe",ParticleEffect.class),1f/16f);
        pem.addParticleEffect(ParticleEffectManager.SMOKE, assetManager.manager.get("particles/smoke.pe",ParticleEffect.class),1f/64f);

    }


    /**
     * Creates a pair of platforms per level up to yLevel
     *
     * @param ylevel
     */
    public void generateLevel(int ylevel) {
        while (ylevel > currentLevel) {
            int range = 15;
            for (int i = 1; i < 5; i++) {
                generateSingleColumn(genNForL(i * 1, currentLevel)
                        , genNForL(i * 100, currentLevel)
                        , genNForL(i * 200, currentLevel)
                        , genNForL(i * 300, currentLevel)
                        , range, i * 10);
            }
            currentLevel++;
        }
    }

    // generate noise for level
    private float genNForL(int level, int height) {
        return (float) openSim.eval(height, level);
    }

    private void generateSingleColumn(float n1, float n2, float n3, float n4, int range, int offset) {
        if (n1 > -0.5f) {
            createPlatform(n2 * range + offset, currentLevel * 2);
            if (n3 > 0.3f) {
                // add bouncy platform
                createBouncyPlatform(n2 * range + offset, currentLevel * 2);
            }
            if (n4 > 0.2f) {
                // add an enemy
                createEnemy(enemyTex, n2 * range + offset, currentLevel * 2 + 1);
            }
        }
    }

    public void createPlatform(float x, float y) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(x, y, 3f, 0.3f, BodyFactory.STONE, BodyType.StaticBody);
        b2dbody.body.setUserData(entity);
        entity.add(b2dbody);

        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = platformTex;
        entity.add(texture);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SCENERY;
        entity.add(type);


        TransformComponent trans = engine.createComponent(TransformComponent.class);
        trans.position.set(x, y, 0);
        entity.add(trans);

        engine.addEntity(entity);

    }

    public Entity createBouncyPlatform(float x, float y) {
        Entity entity = engine.createEntity();
        // create body component
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        b2dbody.body = bodyFactory.makeBoxPolyBody(x, y, 1f, 1f, BodyFactory.STONE, BodyType.StaticBody);
        //make it a sensor so not to impede movement
        bodyFactory.makeAllFixturesSensors(b2dbody.body);

        // give it a texture..todo get another texture and anim for springy action
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        texture.region = platformTex;

        TransformComponent trans = engine.createComponent(TransformComponent.class);
        trans.position.set(x, y, 0);
        entity.add(trans);

        TypeComponent type = engine.createComponent(TypeComponent.class);
        type.type = TypeComponent.SPRING;

        b2dbody.body.setUserData(entity);
        entity.add(b2dbody);
        entity.add(texture);
        entity.add(type);
        engine.addEntity(entity);

        return entity;
    }

    public void createFloor() {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);

        position.position.set(20, 0, 0);
        texture.region = floorTex;
        type.type = TypeComponent.SCENERY;
        b2dbody.body = bodyFactory.makeBoxPolyBody(20, -16, 46, 32, BodyFactory.STONE, BodyType.StaticBody);

        entity.add(b2dbody);
        entity.add(texture);
        entity.add(position);
        entity.add(type);

        b2dbody.body.setUserData(entity);

        engine.addEntity(entity);
    }

    public Entity createEnemy(TextureRegion tex, float x, float y) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        EnemyComponent enemy = engine.createComponent(EnemyComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);

        b2dbody.body = bodyFactory.makeCirclePolyBody(x, y, 1, BodyFactory.STONE, BodyType.KinematicBody, true);
        position.position.set(x, y, 0);
        texture.region = tex;
        enemy.xPosCenter = x;
        type.type = TypeComponent.ENEMY;
        b2dbody.body.setUserData(entity);

        entity.add(colComp);
        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(enemy);
        entity.add(type);

        engine.addEntity(entity);

        return entity;
    }

    public Entity createPlayer(OrthographicCamera cam) {
        TextureRegion tex = atlas.findRegion("player");
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        AnimationComponent animCom = engine.createComponent(AnimationComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);


        player.cam = cam;
        b2dbody.body = bodyFactory.makeCirclePolyBody(10, 1, 1, BodyFactory.STONE, BodyType.DynamicBody, true);
        // set object position (x,y,z) z used to define draw order 0 first drawn
        Animation anim = new Animation(0.1f, atlas.findRegions("flame_a"));
        //anim.setPlayMode(Animation.PlayMode.LOOP);
        animCom.animations.put(StateComponent.STATE_NORMAL, anim);
        animCom.animations.put(StateComponent.STATE_MOVING, anim);
        animCom.animations.put(StateComponent.STATE_JUMPING, anim);
        animCom.animations.put(StateComponent.STATE_FALLING, anim);
        animCom.animations.put(StateComponent.STATE_HIT, anim);

        position.position.set(10, 1, 0);
        texture.region = tex;
        type.type = TypeComponent.PLAYER;
        stateCom.set(StateComponent.STATE_NORMAL);
        b2dbody.body.setUserData(entity);

        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(animCom);
        entity.add(player);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);

        engine.addEntity(entity);
        this.player = entity;
        return entity;
    }

    public void createWalls(TextureRegion tex) {

        for (int i = 0; i < 2; i++) {
            System.out.println("Making wall " + i);
            Entity entity = engine.createEntity();
            B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
            TransformComponent position = engine.createComponent(TransformComponent.class);
            TextureComponent texture = engine.createComponent(TextureComponent.class);
            TypeComponent type = engine.createComponent(TypeComponent.class);
            WallComponent wallComp = engine.createComponent(WallComponent.class);

            //make wall
            b2dbody.body = b2dbody.body = bodyFactory.makeBoxPolyBody(0 + (i * 40), 30, 1, 60, BodyFactory.STONE, BodyType.KinematicBody, true);
            position.position.set(0 + (i * 40), 30, 0);
            texture.region = tex;
            type.type = TypeComponent.SCENERY;

            entity.add(b2dbody);
            entity.add(position);
            entity.add(texture);
            entity.add(type);
            entity.add(wallComp);
            b2dbody.body.setUserData(entity);

            engine.addEntity(entity);
        }
    }


    /**
     * Creates the water entity that steadily moves upwards towards player
     *
     * @return
     */
    public Entity createWaterFloor() {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        WaterFloorComponent waterFloor = engine.createComponent(WaterFloorComponent.class);

        type.type = TypeComponent.ENEMY;
        texture.region = waterTex;
        b2dbody.body = bodyFactory.makeBoxPolyBody(20, -40, 40, 44, BodyFactory.STONE, BodyType.KinematicBody, true);
        position.position.set(20, -15, 0);
        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(type);
        entity.add(waterFloor);

        b2dbody.body.setUserData(entity);

        engine.addEntity(entity);

        makeParticleEffect(ParticleEffectManager.WATER, b2dbody, 0, -20);
        return entity;
    }

    public Entity createBullet(float x, float y, float xVel, float yVel) {
        System.out.println("Making bullet" + x + ":" + y + ":" + xVel + ":" + yVel);
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent position = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        AnimationComponent animCom = engine.createComponent(AnimationComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        BulletComponent bul = engine.createComponent(BulletComponent.class);


        b2dbody.body = bodyFactory.makeCirclePolyBody(x, y, 0.5f, BodyFactory.STONE, BodyType.DynamicBody, true);
        b2dbody.body.setBullet(true); // increase physics computation to limit body travelling through other objects
        bodyFactory.makeAllFixturesSensors(b2dbody.body); // make bullets sensors so they don't move player
        position.position.set(x, y, 0);
        texture.region = bulletTex;
        Animation anim = new Animation(0.05f, DFUtils.spriteSheetToFrames(atlas.findRegion("FlameSpriteAnimation"), 7, 1));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animCom.animations.put(0, anim);

        type.type = TypeComponent.BULLET;
        b2dbody.body.setUserData(entity);
        bul.xVel = xVel;
        bul.yVel = yVel;

        //attach party to bullet
        bul.particleEffect = makeParticleEffect(ParticleEffectManager.FIRE, b2dbody);

        entity.add(bul);
        entity.add(colComp);
        entity.add(b2dbody);
        entity.add(position);
        entity.add(texture);
        entity.add(animCom);
        entity.add(stateCom);
        entity.add(type);

        engine.addEntity(entity);
        return entity;
    }

    public void removeEntity(Entity ent) {
        engine.removeEntity(ent);
    }

    /**
     * Make particle effect at xy
     *
     * @param x
     * @param y
     * @return the Particle Effect Entity
     */
    public Entity makeParticleEffect(int type, float x, float y) {
        Entity entPE = engine.createEntity();
        ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent.class);
        pec.particleEffect = pem.getPooledParticleEffect(type);
        pec.particleEffect.setPosition(x, y);
        entPE.add(pec);
        engine.addEntity(entPE);
        return entPE;
    }

    /**
     * Attache particle effect to body from body component
     *
     * @param type    the type of particle effect to show
     * @param b2dbody the bodycomponent with the body to attach to
     * @return the Particle Effect Entity
     */
    public Entity makeParticleEffect(int type, B2dBodyComponent b2dbody) {
        return makeParticleEffect(type, b2dbody, 0, 0);
    }

    /**
     * Attache particle effect to body from body component with offsets
     *
     * @param type    the type of particle effect to show
     * @param b2dbody the bodycomponent with the body to attach to
     * @param xo      x offset
     * @param yo      y offset
     * @return the Particle Effect Entity
     */
    public Entity makeParticleEffect(int type, B2dBodyComponent b2dbody, float xo, float yo){
        Entity entPE = engine.createEntity();
        ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent.class);
        pec.particleEffect = pem.getPooledParticleEffect(type);
        pec.particleEffect.setPosition(b2dbody.body.getPosition().x, b2dbody.body.getPosition().y);
        pec.particleEffect.getEmitters().first().setAttached(true); //manually attach for testing
        pec.isattached = true;
        pec.particleEffect.getEmitters().first().setContinuous(true);
        pec.attachedBody = b2dbody.body;

        pec.particleEffect.start();
        entPE.add(pec);
        engine.addEntity(entPE);
        return entPE;
    }

    public void resetWorld() {
        currentLevel = 0;
        openSim = new OpenSimplexNoise(MathUtils.random(2000l));
        Array<Body> bods = new Array<Body>();
        world.getBodies(bods);
        for(Body bod:bods){
            world.destroyBody(bod);
        }
    }
}