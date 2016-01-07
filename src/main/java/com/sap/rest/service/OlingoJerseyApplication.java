package com.sap.rest.service;

import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

@ApplicationPath("/")
public class OlingoJerseyApplication extends ResourceConfig {
    public OlingoJerseyApplication() {
        super(CarServiceImpl.class);
    }
}