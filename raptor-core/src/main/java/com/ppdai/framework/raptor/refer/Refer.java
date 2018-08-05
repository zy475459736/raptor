package com.ppdai.framework.raptor.refer;

import com.ppdai.framework.raptor.rpc.Caller;
import com.ppdai.framework.raptor.rpc.URL;

/**
 * 持有：
 *      1）Service的 Interface；
 *      2）Service的 url；
 * */
public interface Refer<T> extends Caller {

    Class<T> getInterface();

    URL getServiceUrl();

}
