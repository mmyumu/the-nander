package com.mmyumu.nander.entity.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.mmyumu.nander.entity.components.ZComponent;

import java.util.Comparator;


public class ZComparator implements Comparator<Entity> {
    private ComponentMapper<ZComponent> zComponentMapper;

    public ZComparator() {
        zComponentMapper = ComponentMapper.getFor(ZComponent.class);
    }

    @Override
    public int compare(Entity entityA, Entity entityB) {
        ZComponent zComponentA = zComponentMapper.get(entityA);
        ZComponent zComponentB = zComponentMapper.get(entityB);

        float zA = 0f;
        float zB = 0f;

        if (zComponentA != null) {
            zA = zComponentA.z;
        }
        if (zComponentB != null) {
            zB = zComponentB.z;
        }

        if (zA > zB) {
            return 1;
        } else if (zA < zB) {
            return -1;
        }
        return 0;
    }
}