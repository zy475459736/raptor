package com.ppdai.raptor.codegen2.java.test;

import com.ppdai.raptor.codegen2.java.JavaGenerator;
import com.ppdai.raptor.codegen2.java.Profile;
import com.ppdai.raptor.codegen2.java.ProfileLoader;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.wire.schema.Schema;
import com.squareup.wire.schema.SchemaLoader;
import com.squareup.wire.schema.Type;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;


/**
 * @author zhangchengxi
 * Date 2018/4/26
 */
public class JavaGerneratorTest {


    // TODO: 2018/4/26 换成resource路径下
    private static final String PROTO_FILE_DIR = "src/test/resources/proto";
    private Schema schema;
    private Profile profile;

    @Before
    public void setup() throws IOException {
        SchemaLoader schemaLoader = new SchemaLoader();
        schemaLoader.addSource(new File(PROTO_FILE_DIR));
        schema = schemaLoader.load();
        profile = loadProfile(schema);
    }

    @After
    public void clean() {

    }

    @Test
    public void testGeneratePOJO() {
        JavaGenerator javaGenerator = JavaGenerator.get(schema)
                .withProfile(profile);
        Type type = schema.getType("squareup.dinosaurs.Dinosaur");
        TypeSpec typeSpec = javaGenerator.generateType(type);
        String cs = typeSpec.toString();

        Assert.assertTrue(StringUtils.isNotBlank(cs));
        System.out.println(cs);

    }

    private Profile loadProfile(Schema schema) throws IOException {
        return new ProfileLoader("java").schema(schema).load();
    }
}
