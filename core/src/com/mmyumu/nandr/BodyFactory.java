package com.mmyumu.nandr;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * The type Body factory.
 */
public class BodyFactory {
    /**
     * The constant STEEL.
     */
    public static final int STEEL = 0;
    /**
     * The constant WOOD.
     */
    public static final int WOOD = 1;
    /**
     * The constant RUBBER.
     */
    public static final int RUBBER = 2;
    /**
     * The constant STONE.
     */
    public static final int STONE = 3;

    private final float DEGTORAD = 0.0174533f;

    private static BodyFactory thisInstance;
    private World world;

    private BodyFactory(World world) {
        this.world = world;
    }

    /**
     * Gets instance.
     *
     * @param world the world
     * @return the instance
     */
    public static BodyFactory getInstance(World world) {
        if (thisInstance == null) {
            thisInstance = new BodyFactory(world);
        }
        return thisInstance;
    }

    /**
     * Make fixture fixture def.
     *
     * @param material the material
     * @param shape    the shape
     * @return the fixture def
     */
    public static FixtureDef makeFixture(int material, Shape shape) {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        switch (material) {
            case STEEL:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.3f;
                fixtureDef.restitution = 0.1f;
                break;
            case WOOD:
                fixtureDef.density = 0.5f;
                fixtureDef.friction = 0.7f;
                fixtureDef.restitution = 0.3f;
                break;
            case RUBBER:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0f;
                fixtureDef.restitution = 1f;
                break;
            case STONE:
                fixtureDef.density = 1f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0f;
                break;
            default:
                fixtureDef.density = 7f;
                fixtureDef.friction = 0.5f;
                fixtureDef.restitution = 0.3f;
        }
        return fixtureDef;
    }

    /**
     * Make circle poly body body.
     *
     * @param posx     the posx
     * @param posy     the posy
     * @param radius   the radius
     * @param material the material
     * @return the body
     */
    public Body makeCirclePolyBody(float posx, float posy, float radius, int material) {
        return makeCirclePolyBody(posx, posy, radius, material, BodyDef.BodyType.DynamicBody, false);
    }

    /**
     * Make circle poly body body.
     *
     * @param posx     the posx
     * @param posy     the posy
     * @param radius   the radius
     * @param material the material
     * @param bodyType the body type
     * @return the body
     */
    public Body makeCirclePolyBody(float posx, float posy, float radius, int material, BodyDef.BodyType bodyType) {
        return makeCirclePolyBody(posx, posy, radius, material, bodyType, false);
    }

    /**
     * Make circle poly body body.
     *
     * @param posx          the posx
     * @param posy          the posy
     * @param radius        the radius
     * @param material      the material
     * @param bodyType      the body type
     * @param fixedRotation the fixed rotation
     * @return the body
     */
    public Body makeCirclePolyBody(float posx, float posy, float radius, int material, BodyDef.BodyType bodyType, boolean fixedRotation) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius / 2);
        boxBody.createFixture(makeFixture(material, circleShape));
        circleShape.dispose();
        return boxBody;
    }

    /**
     * Make box poly body body.
     *
     * @param posx     the posx
     * @param posy     the posy
     * @param width    the width
     * @param height   the height
     * @param material the material
     * @param bodyType the body type
     * @return the body
     */
    public Body makeBoxPolyBody(float posx, float posy, float width, float height, int material, BodyDef.BodyType bodyType) {
        return makeBoxPolyBody(posx, posy, width, height, material, bodyType, false);
    }

    /**
     * Make box poly body body.
     *
     * @param posx          the posx
     * @param posy          the posy
     * @param width         the width
     * @param height        the height
     * @param material      the material
     * @param bodyType      the body type
     * @param fixedRotation the fixed rotation
     * @return the body
     */
    public Body makeBoxPolyBody(float posx, float posy, float width, float height, int material, BodyDef.BodyType bodyType, boolean fixedRotation) {
        // create a definition
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        boxBodyDef.fixedRotation = fixedRotation;

        //create the body to attach said definition
        Body boxBody = world.createBody(boxBodyDef);
        PolygonShape poly = new PolygonShape();
        poly.setAsBox(width / 2, height / 2);
        boxBody.createFixture(makeFixture(material, poly));
        poly.dispose();

        return boxBody;
    }

    /**
     * Make polygon shape body body.
     *
     * @param vertices the vertices
     * @param posx     the posx
     * @param posy     the posy
     * @param material the material
     * @param bodyType the body type
     * @return the body
     */
    public Body makePolygonShapeBody(Vector2[] vertices, float posx, float posy, int material, BodyDef.BodyType bodyType) {
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.type = bodyType;
        boxBodyDef.position.x = posx;
        boxBodyDef.position.y = posy;
        Body boxBody = world.createBody(boxBodyDef);

        PolygonShape polygon = new PolygonShape();
        polygon.set(vertices);
        boxBody.createFixture(makeFixture(material, polygon));
        polygon.dispose();

        return boxBody;
    }

    /**
     * Make cone sensor.
     *
     * @param body the body
     * @param size the size
     */
    public void makeConeSensor(Body body, float size) {

        FixtureDef fixtureDef = new FixtureDef();
        //fixtureDef.isSensor = true; // will add in future

        PolygonShape polygon = new PolygonShape();

        float radius = size;
        Vector2[] vertices = new Vector2[5];
        vertices[0] = new Vector2(0, 0);
        for (int i = 2; i < 6; i++) {
            float angle = (float) (i / 6.0 * 145 * DEGTORAD); // convert degrees to radians
            vertices[i - 1] = new Vector2(radius * ((float) Math.cos(angle)), radius * ((float) Math.sin(angle)));
        }
        polygon.set(vertices);
        fixtureDef.shape = polygon;
        body.createFixture(fixtureDef);
        polygon.dispose();
    }

    public void makeAllFixturesSensors(Body bod) {
        for (Fixture fix : bod.getFixtureList()) {
            fix.setSensor(true);
        }
    }
}