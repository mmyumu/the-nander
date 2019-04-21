package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.mmyumu.nander.entity.components.PositionComponent;
import com.mmyumu.nander.entity.components.TransformComponent;

import java.util.Comparator;


public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<PositionComponent> positionComponentMapper;

    public ZComparator() {
        positionComponentMapper = ComponentMapper.getFor(PositionComponent.class);
    }

    @Override
    public int compare(Entity entityA, Entity entityB) {
//        float az = positionComponentMapper.get(entityA).getZ();
//        float bz = positionComponentMapper.get(entityB).getZ();
//        int res = 0;
//        if (az > bz) {
//            res = 1;
//        } else if (az < bz) {
//            res = -1;
//        }
//        return res;
        return 0;
    }
}