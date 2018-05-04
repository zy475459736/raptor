package com.ppdai.framework.raptor.proto;

//package com.ppdai.raptor.codegen2.java.test;
//

import java.io.IOException;

/**
 * @author zhangchengxi
 * Date 2018/5/3
 */
// TODO: 2018/5/4 删除这个包
public class Main {
    public static void main(String[] args) throws IOException {
        AllTypes allTypes = new AllTypes();
        allTypes.setInt32(1994);
        byte[] encode = AllTypes.ADAPTER.encode(allTypes);

        AllTypes decode = AllTypes.ADAPTER.decode(encode);
        System.out.println(decode.getInt32());
    }
}
