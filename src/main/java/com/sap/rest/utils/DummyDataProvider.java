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
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class DummyDataProvider {
    private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
    private String protocol = "jdbc:derby:";
    private String dbName;
    private Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(DummyDataProvider.class);

    public DummyDataProvider() throws SQLException {
        dbName = "derbyDB";

        initializeConnection();
        createCarsTable();
        createCars();
        connection.commit();
    }

    private void initializeConnection() throws SQLException {
        Properties properties = new Properties();
        properties.put("user", "user1");
        properties.put("password", "user1");

        connection = DriverManager.getConnection(protocol + dbName + ";create=true", properties);

        Statement statement = connection.createStatement();
        statement.execute("drop table Cars");
    }

    private void createCarsTable() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("create table Cars(id int, model varchar(40))");
    }

    private void createCars() throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement("insert into Cars values(?, ?)");
        preparedStatement.setInt(1, 1);
        preparedStatement.setString(2, "Maruti");
        preparedStatement.executeUpdate();

        preparedStatement.setInt(1, 2);
        preparedStatement.setString(2, "Hyundai");
        preparedStatement.executeUpdate();
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

    public EntityCollection readAll(EdmEntitySet edmEntitySet) throws SQLException {
        Statement statement = connection.createStatement();

        ResultSet rs = statement.executeQuery("SELECT id, model FROM Cars ORDER BY id");
        EntityCollection entityCollection = new EntityCollection();

        while (rs.next()) {
            Entity entity = new Entity().addProperty(createPrimitive("Id", rs.getInt(1))).addProperty(createPrimitive("Model", rs.getString(2)));
            entity.setId(createId("Cars", rs.getInt(1)));
            entityCollection.getEntities().add(entity);
        }

        return entityCollection;
    }

    public Entity read(EdmEntitySet edmEntitySet, List<UriParameter> parameters) throws SQLException {
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