package com.mmyumu.nander;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mmyumu.nander.entity.components.AnimationComponent;
import com.mmyumu.nander.entity.components.B2dBodyComponent;
import com.mmyumu.nander.entity.components.BulletComponent;
import com.mmyumu.nander.entity.components.CollisionComponent;
import com.mmyumu.nander.entity.components.FPSComponent;
import com.mmyumu.nander.entity.components.OverlayComponent;
import com.mmyumu.nander.entity.components.ParticleEffectComponent;
import com.mmyumu.nander.entity.components.PlayerComponent;
import com.mmyumu.nander.entity.components.PositionComponent;
import com.mmyumu.nander.entity.components.RenderingComponent;
import com.mmyumu.nander.entity.components.StateComponent;
import com.mmyumu.nander.entity.components.TextureComponent;
import com.mmyumu.nander.entity.components.TiledMapComponent;
import com.mmyumu.nander.entity.components.TransformComponent;
import com.mmyumu.nander.entity.components.TypeComponent;
import com.mmyumu.nander.entity.components.WallComponent;
import com.mmyumu.nander.entity.components.ZComponent;
import com.mmyumu.nander.loader.NanderAssetManager;

import java.util.ArrayList;
import java.util.List;

public class LevelFactory {
    /**
     * Maps constants
     **/
    private static final float TILED_MAP_RATIO = 1 / 8f;
    private static final int MAP_ZONE_DIMENSION = 5;
    private static final int MAP_ZONE_SIZE = 20;
    private static final int MAP_TILE_HEIGHT = 8;
    private static final int MAP_TILE_WIDTH = 8;
    private static final String BACKGROUND_LAYER_NAME = "background";
    private static final String FOREGROUND_LAYER_NAME = "foreground";

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
        pem.addParticleEffect(ParticleEffectManager.TRAIL, assetManager.manager.get("particles/trail.pe", ParticleEffect.class), 1f / 128f);
        pem.addParticleEffect(ParticleEffectManager.FIRE, assetManager.manager.get("particles/fire.pe", ParticleEffect.class), 1f / 128f);
        pem.addParticleEffect(ParticleEffectManager.WATER, assetManager.manager.get("particles/water.pe", ParticleEffect.class), 1f / 16f);
        pem.addParticleEffect(ParticleEffectManager.SMOKE, assetManager.manager.get("particles/smoke.pe", ParticleEffect.class), 1f / 64f);

    }

    public Entity createPlayer(OrthographicCamera camera) {
        Entity entity = engine.createEntity();
        B2dBodyComponent b2dbody = engine.createComponent(B2dBodyComponent.class);
        TextureComponent textureComponent = engine.createComponent(TextureComponent.class);
        TransformComponent transformComponent = engine.createComponent(TransformComponent.class);
        PlayerComponent playerComponent = engine.createComponent(PlayerComponent.class);
        CollisionComponent colComp = engine.createComponent(CollisionComponent.class);
        TypeComponent type = engine.createComponent(TypeComponent.class);
        StateComponent stateCom = engine.createComponent(StateComponent.class);
        PositionComponent positionComponent = engine.createComponent(PositionComponent.class);
        positionComponent.x = 1f;
        positionComponent.y = 1f;

//        textureComponent.region = playerTex;
//        textureComponent.region = new TextureRegion(assetManager.manager.get("images/character1.png", Texture.class));
//        textureComponent.region = new TextureRegion(assetManager.manager.get("images/character24.png", Texture.class));
        textureComponent.region = new TextureRegion(assetManager.manager.get("images/character30.png", Texture.class));
//        textureComponent.region.setRegionHeight(32);
//        textureComponent.region.setRegionWidth(32);
        transformComponent.scale.x = 2f;
        transformComponent.scale.y = 2f;

        playerComponent.camera = camera;
        b2dbody.body = bodyFactory.makeBoxPolyBody(
                positionComponent.x,
                positionComponent.y,
                1.875f,
                1.875f,
                BodyFactory.STONE,
                BodyType.DynamicBody,
                true);

        type.type = TypeComponent.PLAYER;
        stateCom.set(StateComponent.STATE_NORMAL);
        b2dbody.body.setUserData(entity);

        RenderingComponent renderingComponent = engine.createComponent(RenderingComponent.class);
        renderingComponent.renderingType = RenderingComponent.RenderingType.OBJECTS;

        ZComponent zComponent = engine.createComponent(ZComponent.class);
        zComponent.z = 10f;

        entity.add(b2dbody);
        entity.add(transformComponent);
        entity.add(playerComponent);
        entity.add(colComp);
        entity.add(type);
        entity.add(stateCom);
        entity.add(positionComponent);
        entity.add(textureComponent);
        entity.add(renderingComponent);
        entity.add(zComponent);


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
            b2dbody.body = bodyFactory.makeBoxPolyBody(i * 40, 30, 1, 60, BodyFactory.STONE, BodyType.KinematicBody, true);
            positionComponent.x = i * 40f;
            positionComponent.y = 30f;
            texture.region = tex;
            type.type = TypeComponent.SCENERY;

            entity.add(b2dbody);
            entity.add(transformComponent);
            entity.add(texture);
            entity.add(type);
            entity.add(wallComp);
            entity.add(positionComponent);
            b2dbody.body.setUserData(entity);

            engine.addEntity(entity);
        }
    }

    public Entity createBullet(float x, float y, float xVel, float yVel) {
        System.out.println("Making bullet x=" + x + ", y=" + y + ", xVelocity=" + xVel + ", yVelocity=" + yVel);
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


        b2dbody.body = bodyFactory.makeCirclePolyBody(x, y, 0.5f, BodyFactory.STONE, BodyType.DynamicBody, true);
        b2dbody.body.setBullet(true); // increase physics computation to limit body travelling through other objects
        bodyFactory.makeAllFixturesSensors(b2dbody.body); // make bullets sensors so they don't move player
        positionComponent.x = x;
        positionComponent.y = y;
        texture.region = bulletTex;
        Animation anim = new Animation(0.05f, DFUtils.spriteSheetToFrames(atlas.findRegion("FlameSpriteAnimation"), 7, 1));
        anim.setPlayMode(Animation.PlayMode.LOOP);
        animCom.animations.put(0, anim);

        type.type = TypeComponent.BULLET;
        b2dbody.body.setUserData(entity);
        bul.xVelocity = xVel;
        bul.yVelocity = yVel;

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

    public void createFPS() {
        OverlayComponent overlayComponent = new OverlayComponent();
        PositionComponent positionComponent = new PositionComponent();
        positionComponent.x = 10f;
        positionComponent.y = Gdx.graphics.getHeight() - 10f;

        RenderingComponent renderingComponent = engine.createComponent(RenderingComponent.class);
        renderingComponent.renderingType = RenderingComponent.RenderingType.OVERLAY;

        Entity fpsEntity = engine.createEntity();
        fpsEntity.add(new FPSComponent());
        fpsEntity.add(overlayComponent);
        fpsEntity.add(renderingComponent);
        fpsEntity.add(positionComponent);
        engine.addEntity(fpsEntity);
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
        List<MapLayer> generatedLayers = createMapTileLayer(maps);
        for (MapLayer generatedLayer : generatedLayers) {
            layers.add(generatedLayer);
        }

        TiledMapComponent mapComponent = engine.createComponent(TiledMapComponent.class);
        mapComponent.mapRenderer = new OrthoCachedTiledMapRenderer(map, TILED_MAP_RATIO, 8191);
        mapComponent.backgroundLayers = new int[]{map.getLayers().getIndex(BACKGROUND_LAYER_NAME)};
        mapComponent.foregroundLayers = new int[]{map.getLayers().getIndex(FOREGROUND_LAYER_NAME)};

        RenderingComponent renderingComponent = engine.createComponent(RenderingComponent.class);
        renderingComponent.renderingType = RenderingComponent.RenderingType.MAP;

        entity.add(mapComponent);
        entity.add(renderingComponent);
        engine.addEntity(entity);
        return entity;
    }

    /**
     * Create generated map
     *
     * @param maps the list of existing maps
     * @return the generated map
     */
    private List<MapLayer> createMapTileLayer(List<TiledMap> maps) {
        List<MapLayer> layers = new ArrayList<>();

        TiledMapTileLayer backgroundLayer = new TiledMapTileLayer(
                MAP_ZONE_SIZE * MAP_ZONE_DIMENSION,
                MAP_ZONE_SIZE * MAP_ZONE_DIMENSION,
                MAP_TILE_WIDTH,
                MAP_TILE_HEIGHT);
        TiledMapTileLayer foregroundLayer = new TiledMapTileLayer(
                MAP_ZONE_SIZE * MAP_ZONE_DIMENSION,
                MAP_ZONE_SIZE * MAP_ZONE_DIMENSION,
                MAP_TILE_WIDTH,
                MAP_TILE_HEIGHT);
        backgroundLayer.setName(BACKGROUND_LAYER_NAME);
        foregroundLayer.setName(FOREGROUND_LAYER_NAME);

        for (int i = 0; i < MAP_ZONE_DIMENSION; i++) {
            for (int j = 0; j < MAP_ZONE_DIMENSION; j++) {
                createMapTileLayerZone(maps, backgroundLayer, foregroundLayer, i * MAP_ZONE_SIZE, j * MAP_ZONE_SIZE);
            }
        }

        layers.add(backgroundLayer);
        layers.add(foregroundLayer);
        return layers;
    }

    /**
     * Create a zone at the given offsets
     *
     * @param maps            the list of existing maps
     * @param backgroundLayer the background layer to populate with the generated zone
     * @param foregroundLayer the foreground layer to populate with the generated zone
     * @param offsetX         the X offset
     * @param offsetY         the Y offset
     */
    private void createMapTileLayerZone(List<TiledMap> maps,
                                        TiledMapTileLayer backgroundLayer,
                                        TiledMapTileLayer foregroundLayer,
                                        int offsetX,
                                        int offsetY) {
        int randomIndex = (int) (Math.random() * maps.size());
        TiledMap zoneTemplate = maps.get(randomIndex);
        TiledMapTileLayer backgroundLayerTemplate = (TiledMapTileLayer) zoneTemplate.getLayers().get(BACKGROUND_LAYER_NAME);
        TiledMapTileLayer foregroundLayerTemplate = (TiledMapTileLayer) zoneTemplate.getLayers().get(FOREGROUND_LAYER_NAME);

        for (int y = 0; y < 20; y++) {
            for (int x = 0; x < 20; x++) {
                if (backgroundLayerTemplate != null) {
                    backgroundLayer.setCell(x + offsetX, y + offsetY, createCell(backgroundLayerTemplate, x, y));
                }
                if (foregroundLayerTemplate != null) {
                    foregroundLayer.setCell(x + offsetX, y + offsetY, createCell(foregroundLayerTemplate, x, y));
                }
            }
        }

        MapLayer objectLayerTemplate = zoneTemplate.getLayers().get("obstacle");
        for (MapObject object : objectLayerTemplate.getObjects()) {
            if (object instanceof RectangleMapObject) {
                RectangleMapObject rectangleMapObject = (RectangleMapObject) object;
                Rectangle rectangle = rectangleMapObject.getRectangle();

                bodyFactory.makeBoxPolyBody((rectangle.x + rectangle.width / 2) * TILED_MAP_RATIO + offsetX,
                        (rectangle.y + rectangle.height / 2) * TILED_MAP_RATIO + offsetY,
                        rectangle.width * TILED_MAP_RATIO,
                        rectangle.height * TILED_MAP_RATIO,
                        BodyFactory.STONE,
                        BodyType.StaticBody);
            }
        }
    }

    private TiledMapTileLayer.Cell createCell(TiledMapTileLayer layerTemplate, int x, int y) {
        TiledMapTileLayer.Cell cellTemplate = layerTemplate.getCell(x, y);
        if (cellTemplate == null) {
            return null;
        }
        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
        cell.setTile(new StaticTiledMapTile(cellTemplate.getTile().getTextureRegion()));
        cell.setFlipHorizontally(cellTemplate.getFlipHorizontally());
        cell.setFlipVertically(cellTemplate.getFlipVertically());
        cell.setRotation(cellTemplate.getRotation());

        return cell;
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

    public Entity makeTrail(B2dBodyComponent b2dbody) {
        return makeParticleEffect(ParticleEffectManager.TRAIL, b2dbody);
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
     * @param xOffset x offset
     * @param yOffset y offset
     * @return the Particle Effect Entity
     */
    public Entity makeParticleEffect(int type, B2dBodyComponent b2dbody, float xOffset, float yOffset) {

        ParticleEffectComponent particleEffectComponent = engine.createComponent(ParticleEffectComponent.class);
        ZComponent zComponent = engine.createComponent(ZComponent.class);
        zComponent.z = 5f;

        RenderingComponent renderingComponent = engine.createComponent(RenderingComponent.class);
        renderingComponent.renderingType = RenderingComponent.RenderingType.PARTICLES;

        particleEffectComponent.particleEffect = pem.getPooledParticleEffect(type);
        particleEffectComponent.particleEffect.setPosition(b2dbody.body.getPosition().x, b2dbody.body.getPosition().y);
        particleEffectComponent.attached = true;
        for (ParticleEmitter emitter : particleEffectComponent.particleEffect.getEmitters()) {
            emitter.setContinuous(true);
        }

        particleEffectComponent.attachedBody = b2dbody.body;
        particleEffectComponent.xOffset = xOffset;
        particleEffectComponent.yOffset = yOffset;

        particleEffectComponent.particleEffect.start();

        Entity particleEffectEntity = engine.createEntity();
        particleEffectEntity.add(particleEffectComponent);
        particleEffectEntity.add(renderingComponent);
        particleEffectEntity.add(zComponent);

        engine.addEntity(particleEffectEntity);
        return particleEffectEntity;
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