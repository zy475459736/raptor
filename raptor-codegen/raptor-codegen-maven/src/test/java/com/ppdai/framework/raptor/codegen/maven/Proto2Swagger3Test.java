package com.ppdai.framework.raptor.codegen.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.junit.Test;

import java.io.File;

/**
 * Created by zhangyicong on 18-3-1.
 */
public class Proto2Swagger3Test extends AbstractMojoTestCase {

    protected void setUp() throws Exception {
        // required for mojo lookups to work
        super.setUp();

    }

    @Test
    public void testProto2Swagger() throws Exception {
        //File testPom = new File( getBasedir(), "src/test/resources/Proto2Swagger-config.xml" );
        //Proto2Swagger mojo = (Proto2Swagger) lookupMojo ("proto2swagger", testPom );
        //assertNotNull( mojo );
        Proto2Swagger mojo = new Proto2Swagger();
        setVariableValueToObject(mojo, "swaggerVersion", "3.0");
        setVariableValueToObject(mojo, "inputDirectories", new File[] {new File("src/test/protobuf")} );
        setVariableValueToObject(mojo, "outputDirectory", new File( getBasedir(), "target/generated-sources" ));
        setVariableValueToObject(mojo, "includeStdTypes", false);
        setVariableValueToObject(mojo, "extension", ".proto");
        setVariableValueToObject(mojo, "protocDependenciesPath", new File( getBasedir(), "target/protoc-dependencies" ));
        mojo.execute();
    }
}