package com.sap.rest.service;

import com.sap.rest.models.CarsEdmProvider;
import com.sap.rest.models.DataEntityCollectionProcessor;
import com.sap.rest.utils.DummyDataProvider;
import com.sap.rest.utils.ResponseCreator;
import org.apache.commons.io.IOUtils;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/")
public class CarServiceImpl {
    @Context
    private HttpServletRequest httpServletRequest;
    @Context
    private HttpServletResponse httpServletResponse;
    private DummyDataProvider dataProvider;
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);

    @Inject
    public CarServiceImpl(DummyDataProvider dummyDataProvider) {
        this.dataProvider = dummyDataProvider;
    }

    private ODataHttpHandler prepareHandler() {
        OData oData = OData.newInstance();

        ServiceMetadata edm = oData.createServiceMetadata(new CarsEdmProvider(), new ArrayList<EdmxReference>());
        ODataHttpHandler handler = oData.createHandler(edm);

        return handler;
    }

    private void processRequestWithEntityCollectionProcessor(ODataHttpHandler oDataHttpHandler, EntityCollectionProcessor entityCollectionProcessor) {
        oDataHttpHandler.register(entityCollectionProcessor);
        oDataHttpHandler.process(httpServletRequest, httpServletResponse);
    }

    @GET
    @Path("Cars")
    @Produces({
            "application/json"
    })
    public ResponseCreator getCars() throws SQLException {
        try {
            /**
             * choose appropriate Olingo Processor
             */
            DataEntityCollectionProcessor dataEntityCollectionProcessor = new DataEntityCollectionProcessor(dataProvider);

            /**
             * process Request now with Olingo
             */
            processRequestWithEntityCollectionProcessor(prepareHandler(), dataEntityCollectionProcessor);

            /**
             * process Response with Jersey ResponseWrapper
             */
            return ResponseCreator.OK(new StreamingOutput() {
                        @Override
                        public void write(OutputStream outputStream) throws IOException, WebApplicationException {
                            outputStream.write(IOUtils.toByteArray(dataEntityCollectionProcessor.getContent()));
                        }
                    });

        } catch (RuntimeException e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    @GET
    @Path("dummy")
    @Produces({
            "text/html"
    })
    public String getDummy() {
        return "Hello World";
    }
}