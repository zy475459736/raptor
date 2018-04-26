package com.ppdai.framework.raptor.proto;

import com.ppdai.framework.raptor.annotation.RaptorInterface;

import javax.ws.rs.*;

/**
 * @author yinzuolong
 */
@RaptorInterface(appId = "10010001",appName = "demo",version = "1.0")
@Path("/test")
public interface SimpleExtension {

    @GET
    @Path("/get1")
    AllTypesPojo testGet1(AllTypesPojo request);

    @GET
    @Path("/get2/{p1}")
    AllTypesPojo testGet2(AllTypesPojo request, @PathParam("p1") String p1);

    @POST
    @Path("/post1")
    AllTypesPojo testPost1(AllTypesPojo request);

    @POST
    @Path("/post2/{p1}/{p2}")
    AllTypesPojo testPost2(AllTypesPojo request, @PathParam("p1") String p1, @PathParam("p2") int p2);

    @PUT
    @Path("/put1")
    AllTypesPojo testPut1(AllTypesPojo request);

    @DELETE
    @Path("/delete/{p2}")
    AllTypesPojo testDelete1(AllTypesPojo request, @PathParam("p2") int p2);

}
