package com.ppdai.framework.raptor.filter;

import com.ppdai.framework.raptor.common.RaptorInfo;
import com.ppdai.framework.raptor.common.RaptorMessageConstant;
import com.ppdai.framework.raptor.rpc.Request;
import com.ppdai.framework.raptor.rpc.Response;
import com.ppdai.framework.raptor.rpc.URL;
import lombok.extern.slf4j.Slf4j;

import static com.ppdai.framework.raptor.common.ParamNameConstants.HOST_CLIENT;
import static com.ppdai.framework.raptor.common.ParamNameConstants.HOST_SERVER;

@Slf4j
public class AbstractFilter {

    protected String getClientHost(Request request) {
        if (request != null && request.getAttachments() != null) {
            return request.getAttachments().get(HOST_CLIENT);
        }
        return null;
    }

    protected String getServerHost(Response response) {
        if (response != null && response.getAttachments() != null) {
            return response.getAttachments().get(HOST_SERVER);
        }
        return null;
    }

    protected String getInterfaceVersion(URL serviceUrl) {
        if (serviceUrl != null) {
            return serviceUrl.getVersion();
        }
        return null;
    }

    protected String getAppId() {
        return RaptorInfo.getInstance().getAppId();
    }

    protected String getRaptorVersion() {
        return RaptorInfo.getInstance().getVersion();
    }

    protected String getStatusCode(Response response) {
        if (response == null) {
            return String.valueOf(RaptorMessageConstant.SERVICE_DEFAULT_ERROR_CODE);
        }
        return String.valueOf(response.getCode());
    }

}
