/*
 * Copyright 2022 the GradleX team.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradlex.javaecosystem.capabilities.rules;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.CacheableRule;
import org.gradle.api.artifacts.ComponentMetadataContext;
import org.gradle.api.artifacts.ComponentMetadataRule;
import org.gradlex.javaecosystem.capabilities.util.VersionNumber;

@CacheableRule
@NonNullApi
public abstract class JavaxWsRsApiRule implements ComponentMetadataRule {

    public static final String CAPABILITY_GROUP = "javax.ws.rs";
    public static final String CAPABILITY_NAME = "jsr311-api";
    public static final String CAPABILITY = CAPABILITY_GROUP + ":" + CAPABILITY_NAME;

    public static final String FIRST_JAKARTA_VERSION = "3.0.0";

    public static final String[] MODULES = {
            "org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_2.1_spec",
            "org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_2.0_spec",
            "org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec",
            "org.jboss.resteasy:jaxrs-api",
            "jakarta.ws.rs:jakarta.ws.rs-api",
            "javax.ws.rs:javax.ws.rs-api"
    };

    @Override
    public void execute(ComponentMetadataContext context) {
        String name = context.getDetails().getId().getName();
        String group = context.getDetails().getId().getGroup();
        String version;
        if (name.contains("jboss-jaxrs-api_")) {
            version = rsApiVersionForJbossName(name);
        } else {
            String moduleVersion = context.getDetails().getId().getVersion();
            if (moduleVersion.endsWith(".Final")) {
                version = moduleVersion.substring(0, moduleVersion.indexOf(".Final"));
            } else if (moduleVersion.endsWith(".GA")) {
                version = moduleVersion.substring(0, moduleVersion.indexOf(".GA"));
            } else {
                version = moduleVersion;
            }
        }

        if ("org.jboss.resteasy".equals(group) ||
                VersionNumber.parse(version).compareTo(VersionNumber.parse(FIRST_JAKARTA_VERSION)) < 0) {
            context.getDetails().allVariants(variant -> variant.withCapabilities(capabilities ->
                    capabilities.addCapability(CAPABILITY_GROUP, CAPABILITY_NAME, version)
            ));
        }
    }

    private static String rsApiVersionForJbossName(String name) {
        int index = "jboss-jaxrs-api_".length();
        return name.substring(index, index + 3) + ".0";
    }
}

