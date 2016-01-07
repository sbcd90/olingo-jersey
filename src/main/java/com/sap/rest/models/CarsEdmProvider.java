package com.sap.rest.models;

import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.*;
import org.apache.olingo.commons.api.ex.ODataException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CarsEdmProvider extends CsdlAbstractEdmProvider {

    //Service namespace
    public static final String NAMESPACE = "olingo.odata.sample";

    //EDM Container
    public static final String CONTAINER_NAME = "Container";
    public static final FullQualifiedName CONTAINER_FQN = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

    //Entity Type Name
    public static final FullQualifiedName ET_CAR = new FullQualifiedName(NAMESPACE, "Car");

    //Entity Set Names
    public static final String ES_CARS_NAME = "Cars";

    @Override
    public List<CsdlSchema> getSchemas() throws ODataException {
        List<CsdlSchema> schemas = new ArrayList<>();
        CsdlSchema schema = new CsdlSchema();
        schema.setNamespace(NAMESPACE);

        List<CsdlEntityType> entityTypes = new ArrayList<>();
        entityTypes.add(getEntityType(ET_CAR));
        schema.setEntityTypes(entityTypes);

        schema.setEntityContainer(getEntityContainer());

        schemas.add(schema);
        return schemas;
    }

    @Override
    public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
        CsdlEntityType entityType = new CsdlEntityType();

        switch (entityTypeName.getName()) {
            case "Car":  entityType.setName(entityTypeName.getName())
                                .setKey(Arrays.asList(new CsdlPropertyRef().setName("Id")))
                                .setProperties(Arrays.asList(
                                        new CsdlProperty().setName("Id").setType(EdmPrimitiveTypeKind.Int16.getFullQualifiedName()),
                                        new CsdlProperty().setName("Model").setType(EdmPrimitiveTypeKind.String.getFullQualifiedName())
                                ));
                         break;
        }
        return entityType;
    }

    @Override
    public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {
        CsdlEntitySet entitySet = new CsdlEntitySet();

        switch (entitySetName) {
            case "Cars" : entitySet.setName(entitySetName)
                                .setType(ET_CAR);
        }
        return entitySet;
    }

    @Override
    public CsdlEntityContainer getEntityContainer() throws ODataException {
        CsdlEntityContainer container = new CsdlEntityContainer();
        container.setName(CONTAINER_FQN.getName());

        List<CsdlEntitySet> entitySets = new ArrayList<>();
        entitySets.add(getEntitySet(CONTAINER_FQN, ES_CARS_NAME));
        container.setEntitySets(entitySets);

        return container;
    }

    @Override
    public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {
        if(entityContainerName == null || entityContainerName.equals(CONTAINER_FQN))
            return new CsdlEntityContainerInfo().setContainerName(CONTAINER_FQN);
        return null;
    }
}