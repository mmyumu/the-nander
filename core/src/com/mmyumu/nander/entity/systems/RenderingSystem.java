package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.mmyumu.nander.entity.components.OverlayComponent;
import com.mmyumu.nander.entity.components.ParticleEffectComponent;
import com.mmyumu.nander.entity.components.PositionComponent;
import com.mmyumu.nander.entity.components.RenderingComponent;
import com.mmyumu.nander.entity.components.TextureComponent;
import com.mmyumu.nander.entity.components.TiledMapComponent;
import com.mmyumu.nander.entity.components.TransformComponent;

import java.util.Comparator;


public class RenderingSystem extends IteratingSystem {
    /**
     * this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
     **/
    public static final float PPM = 32.0f; // sets the amount of pixels each metre of box2d objects contains
    private static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth() / PPM;
    private static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight() / PPM;
    private static final float PIXELS_TO_METRES = 1.0f / PPM; // get the ratio for converting pixels to metres

    /**
     * Rendering attributes
     **/
    private final BitmapFont font;
    private final SpriteBatch hudBatch;
    private final SpriteBatch batch; // a reference to our spritebatch
    private OrthoCachedTiledMapRenderer mapRenderer;
    private int[] backgroundLayers;
    private int[] foregroundLayers;
    private final Array<Entity> renderQueue;
    private final Array<Entity> overlayRenderQueue;
    private final Comparator<Entity> comparator;
    private final OrthographicCamera camera;

    /**
     * Component mappers to get components from entities
     **/
    private final ComponentMapper<TextureComponent> textureComponentMapper;
    private final ComponentMapper<TransformComponent> transformComponentMapper;
    private final ComponentMapper<PositionComponent> positionComponentMapper;
    private final ComponentMapper<OverlayComponent> overlayComponentMapper;
    private final ComponentMapper<TiledMapComponent> tiledMapComponentMapper;
    private final ComponentMapper<RenderingComponent> renderingComponentMapper;
    private final ComponentMapper<ParticleEffectComponent> particleEffectComponentMapper;


    @SuppressWarnings("unchecked")
    public RenderingSystem(SpriteBatch batch) {
        super(Family.all(RenderingComponent.class).get());

        textureComponentMapper = ComponentMapper.getFor(TextureComponent.class);
        transformComponentMapper = ComponentMapper.getFor(TransformComponent.class);
        positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
        overlayComponentMapper = ComponentMapper.getFor(OverlayComponent.class);
        tiledMapComponentMapper = ComponentMapper.getFor(TiledMapComponent.class);
        renderingComponentMapper = ComponentMapper.getFor(RenderingComponent.class);
        particleEffectComponentMapper = ComponentMapper.getFor(ParticleEffectComponent.class);

        renderQueue = new Array<>();
        overlayRenderQueue = new Array<>();

        this.hudBatch = new SpriteBatch();
        this.batch = batch;  // set our batch to the one supplied in constructor

        comparator = new ZComparator();

        // set up the camera to match our screen size
        camera = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        camera.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);

        this.font = new BitmapFont();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // sort the renderQueue based on z index
        renderQueue.sort(comparator);

        // update camera and sprite batch
        camera.update();

        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render(backgroundLayers);
        }


        batch.setProjectionMatrix(camera.combined);
//        batch.enableBlending();
        batch.begin();

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
            RenderingComponent renderingComponent = renderingComponentMapper.get(entity);

            if (renderingComponent.renderingType == RenderingComponent.RenderingType.OBJECTS) {
                PositionComponent positionComponent = positionComponentMapper.get(entity);
                TextureComponent textureComponent = textureComponentMapper.get(entity);
                TransformComponent transformComponent = transformComponentMapper.get(entity);


                if (textureComponent.region == null || transformComponent.hidden) {
                    continue;
                }


                float width = textureComponent.region.getRegionWidth();
                float height = textureComponent.region.getRegionHeight();

                float originX = width / 2f;
                float originY = height / 2f;

                batch.draw(textureComponent.region,
                        positionComponent.x - originX, positionComponent.y - originY,
                        originX, originY,
                        width, height,
                        PixelsToMeters(transformComponent.scale.x), PixelsToMeters(transformComponent.scale.y),
                        transformComponent.rotation);
            } else if (renderingComponent.renderingType == RenderingComponent.RenderingType.PARTICLES) {
                ParticleEffectComponent particleEffectComponent = particleEffectComponentMapper.get(entity);
                particleEffectComponent.particleEffect.draw(batch, deltaTime);
            }


        }
        renderQueue.clear();

        batch.end();

        if (mapRenderer != null) {
            mapRenderer.render(foregroundLayers);
        }

        hudBatch.begin();

        for (Entity entity : overlayRenderQueue) {
            PositionComponent positionComponent = positionComponentMapper.get(entity);
            OverlayComponent overlayComponent = overlayComponentMapper.get(entity);

            font.draw(hudBatch, overlayComponent.text, positionComponent.x, positionComponent.y);
        }
        overlayRenderQueue.clear();

        hudBatch.end();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        RenderingComponent renderingComponent = renderingComponentMapper.get(entity);

        switch (renderingComponent.renderingType) {
            case MAP:
                TiledMapComponent component = tiledMapComponentMapper.get(entity);
                mapRenderer = component.mapRenderer;
                backgroundLayers = component.backgroundLayers;
                foregroundLayers = component.foregroundLayers;
                break;
            case OVERLAY:
                overlayRenderQueue.add(entity);
                break;
            case OBJECTS:
                renderQueue.add(entity);
                break;
            case PARTICLES:
                ParticleEffectComponent pec = particleEffectComponentMapper.get(entity);
                if (pec.isDead) {
                    pec.timeTilDeath -= deltaTime;
                }

                // Move PE if attached
                if (pec.isattached) {
                    pec.particleEffect.setPosition(
                            pec.attachedBody.getPosition().x + pec.xOffset,
                            pec.attachedBody.getPosition().y + pec.yOffset);
                }
                // free PE if completed
                if (pec.particleEffect.isComplete() || pec.timeTilDeath <= 0) {
                    getEngine().removeEntity(entity);
                } else {
                    renderQueue.add(entity);
                }
                break;
        }
    }

    // convenience method to get camera
    public OrthographicCamera getCamera() {
        return camera;
    }

    private static float PixelsToMeters(float pixelValue) {
        return pixelValue * PIXELS_TO_METRES;
    }
}