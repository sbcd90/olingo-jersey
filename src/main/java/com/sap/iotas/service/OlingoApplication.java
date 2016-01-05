package com.sap.iotas.service;

import com.sap.iotas.models.CarsEdmProvider;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.edmx.EdmxReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/olingo/*")
public class OlingoApplication extends HttpServlet {

    private static final Long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(OlingoApplication.class);

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            OData oData = OData.newInstance();

            ServiceMetadata edm = oData.createServiceMetadata(new CarsEdmProvider(), new ArrayList<EdmxReference>());
            ODataHttpHandler handler = oData.createHandler(edm);

            handler.process(req, resp);

        } catch (RuntimeException e) {
            logger.error(e.getMessage());
        }
    }
}