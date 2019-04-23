package com.mmyumu.nander;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mmyumu.nander.entity.components.AnimationComponent;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.BulletComponent;
import com.mmyumu.nander.entity.components.CollisionComponent;
import com.mmyumu.nander.entity.components.ParticleEffectComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.components.PositionComponent;
import com.mmyumu.nander.entity.components.StateComponent;
import com.mmyumu.nander.entity.components.TextureComponent;
import com.mmyumu.nander.entity.components.TiledMapComponent;
import com.mmyumu.nander.entity.components.TransformComponent;
import com.mmyumu.nander.entity.components.TypeComponent;
import com.mmyumu.nander.entity.components.WallComponent;
import com.mmyumu.nander.loader.NanderAssetManager;

import java.util.ArrayList;
import java.util.List;

public class LevelFactory {
    private final ParticleEffectManager pem;
    private BodyFactory bodyFactory;
    public World world;
    private PooledEngine engine;
    public int currentLevel = 0;
    private TextureRegion bulletTex;
    private TextureRegion playerTex;
    private TextureAtlas atlas;
    private NanderAssetManager assetManager;
    public Entity player;

    public LevelFactory(PooledEngine engine, NanderAssetManager assetManager) {
        this.engine = engine;
        this.atlas = assetManager.manager.get("images/game.atlas", TextureAtlas.class);
        this.assetManager = assetManager;

        playerTex = this.atlas.findRegion("player");
        bulletTex = DFUtils.makeTextureRegion(10, 10, "444444FF");
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new NanderContactListener());
        bodyFactory = BodyFactory.getInstance(world);

        pem = new ParticleEffectManager();
        pem.addParticleEffect(ParticleEffectManager.FIRE, assetManager.manager.get("particles/fire.pe", ParticleEffect.class), 1f / 128f);
        pem.addParticleEffect(ParticleEffectManager.WATER, assetManager.manager.get("particles/water.pe", ParticleEffect.class), 1f / 16f);
        pem.addParticleEffect(ParticleEffectManager.SMOKE, assetManager.manager.get("particles/smoke.pe", ParticleEffect.class), 1f / 64f);

    }

    public Entity createPlayer(OrthographicCamera camera) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        PlayerComponent player = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);
        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);
        positionComponent.setX(0f);
        positionComponent.setY(0f);
        positionComponent.setZ(0f);

        textureComponent.region = playerTex;

        player.setCamera(camera);
        b2dbody.setBody(bodyFactory.makeBoxPolyBody(positionComponent.getX(), positionComponent.getY(), 1, 1, BodyFactory.STONE, BodyType.DynamicBody));


        type.type = TypeComponent.PLAYER;
        stateCom.set(StateComponent.STATE_NORMAL);
        b2dbody.getBody().setUserData(entity);

        entity.add(b2dbody);
        entity.add(transformComponent);
        entity.add(player);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);
        entity.add(positionComponent);
        entity.add(textureComponent);

        engine.addEntity(entity);
        this.player = entity;
        return entity;
    }

    public void createWalls(TextureRegion tex) {

        for (int i = 0; i < 2; i++) {
            System.out.println("Making wall " + i);
            Entity entity = engine.createEntity();
            B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
            TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
            TextureComponent texture = engine.createComponent(TextureComponent.class);
            TypeComponent type = engine.createComponent(TypeComponent.class);
            WallComponent wallComp = engine.createComponent(WallComponent.class);
            PositionComponent positionComponent = engine.createComponent(PositionComponent.class);

            //make wall
            b2dbody.setBody(bodyFactory.makeBoxPolyBody(i * 40, 30, 1, 60, BodyFactory.STONE, BodyType.KinematicBody, true));
            positionComponent.setX(i * 40f);
            positionComponent.setY(30f);
            positionComponent.setZ(0f);
            texture.region = tex;
            type.type = TypeComponent.SCENERY;

            entity.add(b2dbody);
            entity.add(transformComponent);
            entity.add(texture);
            entity.add(type);
            entity.add(wallComp);
            entity.add(positionComponent);
            b2dbody.getBody().setUserData(entity);

            engine.addEntity(entity);
        }
    }

    public Entity createBullet(float x, float y, float xVel, float yVel) {
        System.out.println("Making bullet x=" + x + ", y=" + y + ", xVel=" + xVel + ", yVel=" + yVel);
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        TextureComponent texture = engine.createComponent(TextureComponent.class);
        AnimationComponent animCom = engine.createComponent(AnimationComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        BulletComponent bul = engine.createComponent(BulletComponent.class);
        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);


        b2dbody.setBody(bodyFactory.makeCirclePolyBody(x, y, 0.5f, BodyFactory.STONE, BodyType.DynamicBody, true));
        b2dbody.getBody().setBullet(true); // increase physics computation to limit body travelling through other objects
        bodyFactory.makeAllFixturesSensors(b2dbody.getBody()); // make bullets sensors so they don't move player
        positionComponent.setX(x);
        positionComponent.setY(y);
        positionComponent.setZ(0f);
        texture.region = bulletTex;
        Animation anim = new Animation(0.05f, DFUtils.spriteSheetToFrames(atlas.findRegion("FlameSpriteAnimation"), 7, 1));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animCom.animations.put(0, anim);

        type.type = TypeComponent.BULLET;
        b2dbody.getBody().setUserData(entity);
        bul.xVel = xVel;
        bul.yVel = yVel;

        //attach party to bullet
        bul.particleEffect = makeParticleEffect(ParticleEffectManager.FIRE, b2dbody);

        entity.add(bul);
        entity.add(colComp);
        entity.add(b2dbody);
        entity.add(transformComponent);
        entity.add(texture);
        entity.add(animCom);
        entity.add(stateCom);
        entity.add(type);
        entity.add(positionComponent);

        engine.addEntity(entity);
        return entity;
    }

    /**
     * Create the map entity
     *
     * @return the map entity
     */
    public Entity createMap() {
        Entity entity = engine.createEntity();

        List<TiledMap> maps = new ArrayList<>();
        maps.add(assetManager.manager.get("maps/test2-4.tmx"));
        maps.add(assetManager.manager.get("maps/test2-3.tmx"));

        TiledMap map = new TiledMap();
        MapLayers layers = map.getLayers();
        layers.add(createMapTileLayer(maps));

        TiledMapComponent mapComponent = engine.createComponent(TiledMapComponent.class);
        mapComponent.setMapRenderer(new OrthoCachedTiledMapRenderer(map, 0.125f));

        entity.add(mapComponent);
        engine.addEntity(entity);
        return entity;
    }

    /**
     * Create generated map
     *
     * @param maps the list of existing maps
     * @return the generated map
     */
    private TiledMapTileLayer createMapTileLayer(List<TiledMap> maps) {
        TiledMapTileLayer layer = new TiledMapTileLayer(40, 40, 8, 8);

        createMapTileLayerZone(maps, layer, 0, 0);
        createMapTileLayerZone(maps, layer, 0, 20);
        createMapTileLayerZone(maps, layer, 20, 0);
        createMapTileLayerZone(maps, layer, 20, 20);

        return layer;
    }

    /**
     * Create a zone at the given offsets
     *
     * @param maps    the list of existing maps
     * @param layer   the layer to populate with the generated zone
     * @param offsetX the X offset
     * @param offsetY the Y offset
     */
    private void createMapTileLayerZone(List<TiledMap> maps, TiledMapTileLayer layer, int offsetX, int offsetY) {
        int randomIndex = (int) (Math.random() * maps.size());
        TiledMap zoneTemplate = maps.get(randomIndex);
        TiledMapTileLayer layerTemplate = (TiledMapTileLayer) zoneTemplate.getLayers().get("background");
        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                TiledMapTileLayer.Cell cellTemplate = layerTemplate.getCell(x, y);
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(cellTemplate.getTile().getTextureRegion()));
                cell.setFlipHorizontally(cellTemplate.getFlipHorizontally());
                cell.setFlipVertically(cellTemplate.getFlipVertically());
                cell.setRotation(cellTemplate.getRotation());
                layer.setCell(x + offsetX, y + offsetY, cell);
            }
        }
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
    public Entity makeParticleEffect(int type, B2dBodyComponent b2dbody, float xo, float yo) {
        Entity entPE = engine.createEntity();
        ParticleEffectComponent pec = engine.createComponent(ParticleEffectComponent.class);
        pec.particleEffect = pem.getPooledParticleEffect(type);
        pec.particleEffect.setPosition(b2dbody.getBody().getPosition().x, b2dbody.getBody().getPosition().y);
        pec.particleEffect.getEmitters().first().setAttached(true); //manually attach for testing
        pec.isattached = true;
        pec.particleEffect.getEmitters().first().setContinuous(true);
        pec.attachedBody = b2dbody.getBody();

        pec.particleEffect.start();
        entPE.add(pec);
        engine.addEntity(entPE);
        return entPE;
    }

    public void resetWorld() {
        currentLevel = 0;
        Array<Body> bods = new Array<Body>();
        world.getBodies(bods);
        for (Body bod : bods) {
            world.destroyBody(bod);
        }
    }
}