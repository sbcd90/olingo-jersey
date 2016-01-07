package com.sap.iotas.service;

import com.sap.iotas.models.CarsEdmProvider;
import com.sap.iotas.models.DataEntityCollectionProcessor;
import com.sap.iotas.utils.DummyDataProvider;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import java.util.ArrayList;

@Path("/")
public class CarServiceImpl {
    @Context
    private HttpServletRequest httpServletRequest;
    @Context
    private HttpServletResponse httpServletResponse;
    private static final Logger logger = LoggerFactory.getLogger(CarServiceImpl.class);

    @GET
    @Path("Cars")
    @Produces({
            "application/json"
    })
    public void getCars() {
        try {
            HttpSession session = httpServletRequest.getSession(true);
            DummyDataProvider dataProvider = (DummyDataProvider) session.getAttribute(DummyDataProvider.class.getName());
            if(dataProvider == null) {
                dataProvider = new DummyDataProvider();
                session.setAttribute(DummyDataProvider.class.getName(), dataProvider);
            }

            OData oData = OData.newInstance();

            ServiceMetadata edm = oData.createServiceMetadata(new CarsEdmProvider(), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = oData.createHandler(edm);

            handler.register(new DataEntityCollectionProcessor(dataProvider));
            handler.process(httpServletRequest, httpServletResponse);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
        }
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