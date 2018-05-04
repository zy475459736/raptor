package com.ppdai.framework.raptor.spring.serialize;

import com.ppdai.framework.raptor.proto.AllTypesPojo;
import com.squareup.wire.ProtoAdapter;
import org.junit.Test;

import java.io.IOException;

/**
 * @author yinzuolong
 */
public class ProtoSerializeTest {

    @Test
    public void name() throws IOException {
        AllTypesPojo allTypesPojo = new AllTypesPojo();
        allTypesPojo.setInt32(23);
        allTypesPojo.setString("test");

        ProtoAdapter<AllTypesPojo> adapter = ProtoAdapter.get(AllTypesPojo.class);
        byte[] data = adapter.encode(allTypesPojo);

        AllTypesPojo newAllTypesPojo = adapter.decode(data);

        System.out.println(newAllTypesPojo.getInt32());

        System.out.println(newAllTypesPojo.getString());
    }
}
