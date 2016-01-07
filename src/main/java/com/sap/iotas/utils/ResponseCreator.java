package com.sap.iotas.utils;

import com.sap.iotas.support.ResponseWrapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

public class ResponseCreator extends ResponseWrapper {
    public ResponseCreator(Response delegate) {
        super(delegate);
    }

    public static ResponseCreator OK(StreamingOutput entity) {
        Response.ResponseBuilder responseBuilder = Response.status(200);
        responseBuilder = responseBuilder.header("Content-Type", "application/json");

        responseBuilder.entity(entity);

        return new ResponseCreator(responseBuilder.build());
    }
}