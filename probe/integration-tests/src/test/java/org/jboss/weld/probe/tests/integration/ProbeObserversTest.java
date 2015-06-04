/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.probe.tests.integration;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.jboss.weld.probe.Strings.BEAN_CLASS;
import static org.jboss.weld.probe.Strings.DATA;
import static org.jboss.weld.probe.Strings.OBSERVED_TYPE;
import static org.jboss.weld.probe.Strings.QUALIFIERS;
import static org.jboss.weld.probe.Strings.RECEPTION;
import static org.jboss.weld.probe.Strings.TX_PHASE;
import static org.jboss.weld.probe.tests.integration.JSONTestUtil.getAllJsonObjectsByClass;
import static org.jboss.weld.probe.tests.integration.JSONTestUtil.getPageAsJSONObject;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.json.JsonArray;
import javax.json.JsonObject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.weld.probe.tests.integration.deployment.InvokingServlet;
import org.jboss.weld.probe.tests.integration.deployment.annotations.Collector;
import org.jboss.weld.probe.tests.integration.deployment.beans.ApplicationScopedObserver;
import org.jboss.weld.probe.tests.integration.deployment.beans.ModelBean;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * @author Tomas Remes
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProbeObserversTest extends ProbeIntegrationTest {

    @ArquillianResource
    private URL url;

    private static final String TEST_ARCHIVE_NAME = "probe-observers-test";

    @Deployment(testable = false)
    public static WebArchive deploy() {
        return ShrinkWrap.create(WebArchive.class, TEST_ARCHIVE_NAME + ".war")
                .addAsWebInfResource(ProbeObserversTest.class.getPackage(), "web.xml", "web.xml")
                .addAsWebInfResource(ProbeObserversTest.class.getPackage(), "beans.xml", "beans.xml")
                .addClass(InvokingServlet.class)
                .addPackage(ModelBean.class.getPackage())
                .addPackage(Collector.class.getPackage());
    }

    @Test
    public void testObserversEndpoint() throws IOException {
        WebClient client = invokeSimpleAction(url);
        JsonObject observers = getPageAsJSONObject(JSONTestUtil.OBSERVERS_PATH, url, client);
        JsonArray observersData = observers.getJsonArray(DATA);
        assertTrue("No observers found !", observersData.size() > 0);

        //check observers
        assertTrue(checkStringInArrayRecursively(ApplicationScopedObserver.class.getName(), BEAN_CLASS, observersData, false));
        assertTrue(checkStringInArrayRecursively(Collector.class.getName().concat("(value=A)"), QUALIFIERS, observersData, false));
        assertTrue(checkStringInArrayRecursively(Collector.class.getName().concat("(value=B)"), QUALIFIERS, observersData, false));
        assertTrue(checkStringInArrayRecursively(TransactionPhase.BEFORE_COMPLETION.name(), TX_PHASE, observersData, false));

        List<JsonObject> jsonObservers = getAllJsonObjectsByClass(ApplicationScopedObserver.class, observersData);

        // find observer only with Collector qualifier with value "B"
        JsonObject observerWithQualifier = null;
        for (JsonObject jsonObject : jsonObservers) {
            if(jsonObject.getJsonArray(QUALIFIERS) != null && jsonObject.getJsonArray(QUALIFIERS).size() == 1 && jsonObject.getJsonArray(QUALIFIERS)
                    .getString(0).equals("@" + Collector.class.getName().concat("(value=B)"))) {
                observerWithQualifier = jsonObject;
                break;
            }
        }
        assertNotNull(observerWithQualifier);
        assertEquals(Reception.ALWAYS.name(), observerWithQualifier.getString(RECEPTION));
        assertEquals(String.class.getName(), observerWithQualifier.getString(OBSERVED_TYPE));
        assertEquals(TransactionPhase.IN_PROGRESS.name(), observerWithQualifier.getString(TX_PHASE));

        // find next observer
        JsonObject observerWithReception = null;
        for (JsonObject jsonObject : jsonObservers) {
            if(jsonObject.getString(RECEPTION).equals(Reception.IF_EXISTS.name())) {
                observerWithReception = jsonObject;
                break;
            }
        }
        assertEquals(Properties.class.getName(), observerWithReception.getString(OBSERVED_TYPE));
        assertEquals(TransactionPhase.BEFORE_COMPLETION.name(), observerWithReception.getString(TX_PHASE));

    }

}