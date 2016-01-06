package com.sap.iotas.models;

import com.sap.iotas.utils.DummyDataProvider;
import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.*;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriInfoResource;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

public class DataEntityCollectionProcessor implements EntityCollectionProcessor {
    private DummyDataProvider dataProvider;
    private OData oData;
    private ServiceMetadata serviceMetadata;
    private static final Logger logger = LoggerFactory.getLogger(DataEntityCollectionProcessor.class);

    public DataEntityCollectionProcessor(DummyDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public void init(OData oData, ServiceMetadata serviceMetadata) {
        this.oData = oData;
        this.serviceMetadata = serviceMetadata;
    }

    @Override
    public void readEntityCollection(ODataRequest oDataRequest, ODataResponse oDataResponse, UriInfo uriInfo, ContentType contentType) throws ODataApplicationException, ODataLibraryException {
        EdmEntitySet edmEntitySet = getEdmEntitySet(uriInfo.asUriInfoResource());

        EntityCollection entityCollection = dataProvider.readAll(edmEntitySet);

        ODataSerializer serializer = oData.createSerializer(contentType);
        ContextURL contextURL = ContextURL.with().entitySet(edmEntitySet).build();

        String id = oDataRequest.getRawBaseUri() + "/" + edmEntitySet.getName();
        EntityCollectionSerializerOptions options = EntityCollectionSerializerOptions.with().id(id).contextURL(contextURL).build();
        SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntitySet.getEntityType(), entityCollection, options);


        oDataResponse.setContent(serializerResult.getContent());
        oDataResponse.setStatusCode(HttpStatusCode.OK.getStatusCode());
        oDataResponse.setHeader(HttpHeader.CONTENT_TYPE, contentType.toContentTypeString());
    }

    private EdmEntitySet getEdmEntitySet(UriInfoResource uriInfoResource) throws ODataApplicationException {
        List<UriResource> resourcePaths = uriInfoResource.getUriResourceParts();

        if(!(resourcePaths.get(0) instanceof UriResourceEntitySet)) {
            throw new ODataApplicationException("Invalid resource type for first segment.", HttpStatusCode.NOT_IMPLEMENTED.getStatusCode(), Locale.ENGLISH);
        }

        UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
        return uriResourceEntitySet.getEntitySet();
    }
}