package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Array;
import com.mmyumu.nander.entity.components.OverlayComponent;
import com.mmyumu.nander.entity.components.PositionComponent;
import com.mmyumu.nander.entity.components.TextureComponent;
import com.mmyumu.nander.entity.components.TiledMapComponent;
import com.mmyumu.nander.entity.components.TransformComponent;

import java.util.Comparator;


public class RenderingSystem extends SortedIteratingSystem {

    public static final float PPM = 32.0f; // sets the amount of pixels each metre of box2d objects contains

    // this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
    static final float FRUSTUM_WIDTH = Gdx.graphics.getWidth() / PPM;
    static final float FRUSTUM_HEIGHT = Gdx.graphics.getHeight() / PPM;

    public static final float PIXELS_TO_METRES = 1.0f / PPM; // get the ratio for converting pixels to metres

    // static method to get screen width in metres
//    private static Vector2 meterDimensions = new Vector2();
//    private static Vector2 pixelDimensions = new Vector2();

//    public static Vector2 getScreenSizeInMeters() {
//        meterDimensions.set(Gdx.graphics.getWidth() * PIXELS_TO_METRES,
//                Gdx.graphics.getHeight() * PIXELS_TO_METRES);
//        return meterDimensions;
//    }
//
//    // static method to get screen size in pixels
//    public static Vector2 getScreenSizeInPixesl() {
//        pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        return pixelDimensions;
//    }

    // convenience method to convert pixels to meters
    public static float PixelsToMeters(float pixelValue) {
        return pixelValue * PIXELS_TO_METRES;
    }

    private final BitmapFont font;
    private final SpriteBatch hudBatch;
    private final SpriteBatch batch; // a reference to our spritebatch
    private OrthoCachedTiledMapRenderer mapRenderer;
    private final Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
    private final Array<Entity> overlayRenderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
    private final Comparator<Entity> comparator; // a comparator to sort images based on the z position of the transfromComponent
    private final OrthographicCamera camera; // a reference to our camera

    // component mappers to get components from entities
    private final ComponentMapper<TextureComponent> textureComponentMapper;
    private final ComponentMapper<TransformComponent> transformComponentMapper;
    private final ComponentMapper<PositionComponent> positionComponentMapper;
    private final ComponentMapper<OverlayComponent> overlayComponentMapper;
    private final ComponentMapper<TiledMapComponent> tiledMapComponentMapper;



    @SuppressWarnings("unchecked")
    public RenderingSystem(SpriteBatch batch) {
        // gets all entities with a TransofmComponent and TextureComponent
        super(Family.one(TransformComponent.class, TextureComponent.class, OverlayComponent.class, TiledMapComponent.class).get(), new ZComparator());
//        super(Family.all(TransformComponent.class, TextureComponent.class).one(OverlayComponent.class).get(), new ZComparator());
//        super(Family.one(TransformComponent.class, TextureComponent.class, OverlayComponent.class).get(), new ZComparator());

        //creates out componentMappers
        textureComponentMapper = ComponentMapper.getFor(TextureComponent.class);
        transformComponentMapper = ComponentMapper.getFor(TransformComponent.class);
        positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
        overlayComponentMapper = ComponentMapper.getFor(OverlayComponent.class);
        tiledMapComponentMapper = ComponentMapper.getFor(TiledMapComponent.class);

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

        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
//        batch.enableBlending();
        batch.begin();

        // loop through each entity in our render queue
        for (Entity entity : renderQueue) {
            PositionComponent positionComponent = positionComponentMapper.get(entity);
            TextureComponent textureComponent = textureComponentMapper.get(entity);
            TransformComponent transformComponent = transformComponentMapper.get(entity);


            if (textureComponent.region == null || transformComponent.isHidden()) {
                continue;
            }


            float width = textureComponent.region.getRegionWidth();
            float height = textureComponent.region.getRegionHeight();

            float originX = width / 2f;
            float originY = height / 2f;

            batch.draw(textureComponent.region,
                    positionComponent.getX() - originX, positionComponent.getY() - originY,
                    originX, originY,
                    width, height,
                    PixelsToMeters(transformComponent.getScale().x), PixelsToMeters(transformComponent.getScale().y),
                    transformComponent.getRotation());
//            System.out.println("Draw entity " + entity + " to x=" + positionComponent.getX() + ", y=" + positionComponent.getY());
        }
        renderQueue.clear();

        batch.end();

        hudBatch.begin();

        for (Entity entity : overlayRenderQueue) {
            PositionComponent positionComponent = positionComponentMapper.get(entity);
            OverlayComponent overlayComponent = overlayComponentMapper.get(entity);

            // TODO: use positionComponent.getY()
            font.draw(hudBatch, overlayComponent.getText(), positionComponent.getX(), positionComponent.getY());
//            font.draw(hudBatch, overlayComponent.getText(), 10, 10);
//            System.out.println("Draw overlay to x=" + positionComponent.getX() + ", y=" + positionComponent.getY());
        }
        overlayRenderQueue.clear();

        //Draw using hudBatch
        hudBatch.end();
    }

    @Override
    public void processEntity(Entity entity, float deltaTime) {
        TextureComponent textureComponent = textureComponentMapper.get(entity);
        TransformComponent transformComponent = transformComponentMapper.get(entity);
        OverlayComponent overlayComponent = overlayComponentMapper.get(entity);
        TiledMapComponent tiledMapComponent = tiledMapComponentMapper.get(entity);

        if (overlayComponent != null) {
            overlayRenderQueue.add(entity);
        } else if (tiledMapComponent != null) {
            TiledMapComponent component = entity.getComponent(TiledMapComponent.class);
            mapRenderer = component.getMapRenderer();
        } else if (textureComponent != null && transformComponent != null) {
            renderQueue.add(entity);
        }
    }

    // convenience method to get camera
    public OrthographicCamera getCamera() {
        return camera;
    }
}