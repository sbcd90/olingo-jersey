package com.sap.rest.utils;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.*;
import org.apache.olingo.server.api.uri.UriParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyDataProvider {
    private Map<String, EntityCollection> data;
    private static final Logger logger = LoggerFactory.getLogger(DummyDataProvider.class);

    public DummyDataProvider() {
        data = new HashMap<>();
        data.put("Cars", createCars());
    }

    private EntityCollection createCars() {
        EntityCollection entitySet = new EntityCollection();

        Entity entity1 = new Entity().addProperty(createPrimitive("Id", 1)).addProperty(createPrimitive("Model", "Maruti"));
        entity1.setId(createId("Cars", 1));
        entitySet.getEntities().add(entity1);

        Entity entity2 = new Entity().addProperty(createPrimitive("Id", 2)).addProperty(createPrimitive("Model", "Hyundai"));
        entity2.setId(createId("Cars", 2));
        entitySet.getEntities().add(entity2);

        return entitySet;
    }

    private Property createPrimitive(String name, Object value) {
        return new Property(null, name, ValueType.PRIMITIVE, value);
    }

    private URI createId(String entitySetName, Object id) {
        try {
            return new URI(entitySetName + "(" + String.valueOf(id) + ")");
        } catch (URISyntaxException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public EntityCollection readAll(EdmEntitySet edmEntitySet) {
        return data.get(edmEntitySet.getName());
    }

    public Entity read(EdmEntitySet edmEntitySet, List<UriParameter> parameters) {
        EdmEntityType entityType = edmEntitySet.getEntityType();

        EntityCollection entitySet = readAll(edmEntitySet);

        if(entitySet == null)
            return null;
        else {
            try {
                for(Entity entity: entitySet.getEntities()) {
                    Boolean found = true;

                    for(UriParameter parameter: parameters) {
                        EdmProperty property = (EdmProperty) entityType.getProperty(parameter.getName());
                        EdmPrimitiveType type = (EdmPrimitiveType) property.getType();

                        if(type.valueToString(entity.getProperty(parameter.getName()).getValue(), property.isNullable(), property.getMaxLength(), property.getPrecision(),
                                property.getScale(), property.isUnicode()).equals(parameter.getText())) {
                            found = false;
                            break;
                        }
                    }
                    if(found)
                        return entity;
                }
                return null;
            } catch (EdmPrimitiveTypeException e) {
                logger.error(e.getMessage());
            }
        }
        return null;
    }
}