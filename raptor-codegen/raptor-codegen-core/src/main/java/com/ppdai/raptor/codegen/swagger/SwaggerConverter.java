package com.ppdai.raptor.codegen.swagger;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.squareup.wire.schema.*;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.callbacks.Callback;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.servers.ServerVariable;
import io.swagger.v3.oas.models.servers.ServerVariables;
import io.swagger.v3.oas.models.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Schema 参考 https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#schema
 * <p>
 *
 * 多线程不安全,不能声明为bean
 *
 * @author zhangchengxi
 * Date 2018/4/20
 */
public class SwaggerConverter {
    public static final String DEFAULT_VERSION = "0.0.1";

    private final com.squareup.wire.schema.Schema schmea;

    private ThreadLocal<RefHelper> refHelper;

    public SwaggerConverter(com.squareup.wire.schema.Schema schema) {
        this.schmea = schema;
        refHelper = new ThreadLocal<>();
    }

    public List<OpenAPI> convert() {
        ArrayList<OpenAPI> openApis = Lists.newArrayList();

        ImmutableList<ProtoFile> protoFiles = schmea.protoFiles();
        for (ProtoFile protoFile : protoFiles) {
            // TODO: 2018/4/20 使用service 进行遍历
            for (Service service : protoFile.services()) {
                OpenAPI openAPI = getOpenApi(service, protoFile);
                openApis.add(openAPI);
            }
        }
        return openApis;
    }

    /**
     * https://github.com/OAI/OpenAPI-SpecOpenApiification/blob/master/versions/3.0.1.md#openapi-object
     *
     * @param service
     * @param protoFile
     * @return
     */
    private OpenAPI getOpenApi(Service service, ProtoFile protoFile) {
        refHelper.set(new RefHelper(schmea, protoFile, service));

        OpenAPI openApi = new OpenAPI();

        //required
        openApi.info(getInfo(protoFile, service));
        openApi.paths(getPath(protoFile, service));

        //optional
        openApi.servers(getServers());
        openApi.components(getComponents());
//        openApi.security()
        openApi.tags(getTags());
//        openApi.externalDocs(getExternalDocumentation());

        return openApi;
    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#info-object
     *
     * @param protoFile
     * @return
     */
    private Info getInfo(ProtoFile protoFile, Service service) {
        Info info = new Info();

        // TODO: 2018/5/18 使用swagger converter plus来处理raptor自定义的扩展
        //这是类处理原始版本的proto文件,不使用raptor自定义的一些属性
//        ProtoFileMetaInfo protoFileMetaInfo = ProtoFileMetaInfo.readFrom(protoFile);
//        InterfaceMetaInfo interfaceMetaInfo = InterfaceMetaInfo.readFrom(protoFile, service);


        //required
        info.title(protoFile.name());
        info.version("0.0.1");

        //optional
        info.description(service.documentation());
//        info.termsOfService();
        info.contact(getContact());
        info.license(getLicense());

        return info;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#contact-object
     *
     * @return
     */
    private Contact getContact() {
        Contact contact = new Contact();
        // TODO: 2018/5/18 现在proto文件中还没有定义contact
        return null;
    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#license-object
     *
     * @param
     * @return
     */
    private License getLicense() {
        License license = new License();
        // TODO: 2018/5/18 现在还没有定义license
        return null;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#server-object
     *
     * @return
     */
    private Server getServer() {
        Server server = new Server();
        // TODO: 2018/5/18 现在还没有定义server

        server.url("http://localhost:8080");
        server.description("本地");
        // TODO: 2018/5/18 暂时不知道下面这个有什么用
//        server.variables(getServerVariables());
        return server;
    }

    private List<Server> getServers() {
        List<Server> servers = Lists.newArrayList();
        servers.add(getServer());
        return servers;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#server-variable-object
     *
     * @return
     */
    private ServerVariable getServerVariable() {
        ServerVariable serverVariable = new ServerVariable();
        // TODO: 2018/5/18
        return serverVariable;
    }

    private ServerVariables getServerVariables() {
        ServerVariables serverVariables = new ServerVariables();
        return serverVariables;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#components-object
     * 可重用组件
     *
     * @return
     */
    private Components getComponents() {
        Components components = new Components();
        components.schemas(refHelper.get().buildSchemas());
        return components;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#paths-object
     *
     * @param protoFile
     * @param service
     * @return
     */
    private Paths getPath(ProtoFile protoFile, Service service) {
        Paths paths = new Paths();
        String baseUrl = "/"+protoFile.packageName().replace(".", "/");

        for (Rpc rpc : service.rpcs()) {
            String name = baseUrl + rpc.name();
            paths.addPathItem(name, getPathItem(rpc));
        }

//        paths.addExtension();

        return paths;

    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#path-item-object
     *
     * @param rpc
     * @return
     */
    private PathItem getPathItem(Rpc rpc) {
        PathItem pathItem = new PathItem();
        // 原版raptor 只支持post

        pathItem.description(rpc.documentation());

        pathItem.post(getOperation(rpc));


        return pathItem;

    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#operation-object
     *
     * @param rpc
     * @return
     */
    private Operation getOperation(Rpc rpc) {
        Operation operation = new Operation();

        operation.requestBody(getRequestBody(rpc));

        //requires
        operation.responses(getApiResponses(rpc));

        operation.tags(Lists.newArrayList("operation", "tagds"));
        operation.summary("operation summary");
        operation.description(rpc.documentation());
        operation.operationId("operation operationId");
//        operation.externalDocs();
//        operation.parameters();
//        operation.requestBody();
//        operation.callbacks();
//        operation.deprecated();
//        operation.security();
//        operation.servers();


        return operation;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#externalDocumentationObject
     *
     * @return
     */
    @Deprecated
    private ExternalDocumentation getExternalDocumentation() {
        ExternalDocumentation externalDocumentation = new ExternalDocumentation();
        return externalDocumentation;
    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#parameter-object
     *
     * @param rpc
     * @return
     */
    private List<Parameter> getParameters(Rpc rpc) {
        ArrayList<Parameter> parameters = Lists.newArrayList();

        ProtoType protoType = rpc.requestType();

        Type type = schmea.getType(protoType);


        return parameters;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#requestBodyObject
     *
     * @param rpc
     * @return
     */
    private RequestBody getRequestBody(Rpc rpc) {
        RequestBody requestBody = new RequestBody();
        ProtoType protoType = rpc.requestType();
        Type type = schmea.getType(protoType);
        requestBody.description(type.documentation());
        requestBody.required(true);
        requestBody.content(getContent(rpc.requestType()));
        return requestBody;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#media-type-object
     *
     * @return
     * @param protoType
     */
    private MediaType getMediaType(ProtoType protoType) {
        MediaType mediaType = new MediaType();
        mediaType.schema(getSchema(protoType));
//        mediaType.encoding();
//        mediaType.
        return mediaType;
    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#encodingObject
     *
     * @return
     */
    private Encoding getEncoding() {
        Encoding encoding = new Encoding();
        return encoding;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#responsesObject
     *
     * @return
     */
    private ApiResponses getApiResponses(Rpc rpc) {
        ApiResponses apiResponses = new ApiResponses();

        apiResponses.addApiResponse("200", getSuccessApiResponse(rpc));

        return apiResponses;

    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#responseObject
     *
     * @return
     */


    private ApiResponse getSuccessApiResponse(Rpc rpc) {
        ApiResponse apiResponse = new ApiResponse();

        apiResponse.content(getContent(rpc.responseType()));
        apiResponse.description("ApiResponse  description");
        apiResponse.link("ApiResponse link name", getLink());
        return apiResponse;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#callbackObject
     *
     * @return
     */
    private Callback getCallback() {
        Callback callback = new Callback();
        return callback;
    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#exampleObject
     *
     * @return
     */
    private Example getExample() {
        Example example = new Example();
        return example;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#linkObject
     *
     * @return
     */
    private Link getLink() {
        Link link = new Link();
//        link.$ref();
//        link.addExtension();

        return null;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#headerObject
     *
     * @return
     */
    private Header getHeader() {
        Header header = new Header();
        return header;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#tagObject
     *
     * @return
     */
    private Tag getTag() {
        Tag tag = new Tag();
        return tag;
    }

    private List<Tag> getTags() {
        List<Tag> tagList = Lists.newArrayList();
        return null;
    }

    /**
     *  https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#referenceObject
     * @return
     */
//    private Reference getReference(){
//        Reference reference = new Reference();
//        return reference;
//    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#schemaObject
     *
     * @return
     */
    private Schema getSchema(ProtoType protoType) {
        Schema schema = new Schema();
        schema.set$ref(refHelper.get().getRefer(protoType));
        return schema;
    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#discriminatorObject
     *
     * @return
     */
    private Discriminator getDiscriminator() {
        Discriminator discriminator = new Discriminator();
        return discriminator;
    }

    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#xmlObject
     *
     * @return
     */
    private XML getXml() {
        XML xml = new XML();
        return xml;
    }

    /**
     * @return
     */
    private List<Server> getServices() {
        List<Server> servers = Lists.newArrayList();
        return servers;
    }


    /**
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#securitySchemeObject
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#oauthFlowsObject
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#oauthFlowObject
     * https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.1.md#securityRequirementObject
     */


    private Content getContent(ProtoType protoType) {
        Content content = new Content();
        content.addMediaType("application/json", getMediaType(protoType));
        return content;
    }


}

