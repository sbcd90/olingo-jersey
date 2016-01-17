package com.sap.rest.utils;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class AbstractBinderImpl extends AbstractBinder {
    public static final Logger logger = LoggerFactory.getLogger(AbstractBinderImpl.class);

    @Override
    protected void configure() {
        DummyDataProvider dummyDataProvider = null;
        try {
            dummyDataProvider = new DummyDataProvider();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
        bind(dummyDataProvider).to(DummyDataProvider.class);
    }
}