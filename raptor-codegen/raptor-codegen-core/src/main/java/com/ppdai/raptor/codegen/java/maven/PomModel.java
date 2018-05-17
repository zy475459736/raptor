package com.ppdai.raptor.codegen.java.maven;

import lombok.Setter;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author zhangchengxi
 * Date 2018/5/17
 */
@Setter
public class PomModel {
    private static final String DEFAULT_GOURP_ID = "com.ppdai.raptor.framework";
    private static final String DEFAULT_ARTIFACT_ID = "raptor-demo";
    private static final String DEFAULT_VERSION = "0.0.1-SNAPSHOT";


    private String groupId;
    private String artifactId;
    private String version;

    public String getGroupId() {
        return StringUtils.isNotBlank(groupId) ? groupId : DEFAULT_GOURP_ID;
    }

    public String getArtifactId() {
        return StringUtils.isNotBlank(artifactId) ? artifactId : DEFAULT_ARTIFACT_ID;
    }

    public String getVersion() {
        return StringUtils.isNotBlank(version) ? version : DEFAULT_VERSION;
    }

}
