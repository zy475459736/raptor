package com.ppdai.framework.raptor.proto;

import com.ppdai.framework.raptor.annotation.RaptorInterface;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author yinzuolong
 */
@RaptorInterface(appId = "10010001", appName = "demo", version = "1.0")
@RequestMapping("/test")
public interface SimpleExtension {

    @RequestMapping("/get1")
    AllTypesPojo testGet1(AllTypesPojo request);

    @RequestMapping("/get2/{p1}")
    AllTypesPojo testGet2(AllTypesPojo request, @PathVariable("p1") String p1);

    @RequestMapping(path = "/post1", method = RequestMethod.POST)
    AllTypesPojo testPost1(AllTypesPojo request);

    @RequestMapping(path = "/post2/{p1}/{p2}", method = RequestMethod.POST)
    AllTypesPojo testPost2(AllTypesPojo request, @PathVariable("p1") String p1, @PathVariable("p2") int p2);

    @RequestMapping(path = "/put1", method = RequestMethod.PUT)
    AllTypesPojo testPut1(AllTypesPojo request);

    @RequestMapping(path = "/delete/{p2}", method = RequestMethod.DELETE)
    AllTypesPojo testDelete1(AllTypesPojo request, @PathVariable("p2") int p2);

}
