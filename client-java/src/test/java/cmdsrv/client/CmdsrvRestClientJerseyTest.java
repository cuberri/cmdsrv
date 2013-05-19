/*
* Copyright 2013 Christophe Uberri <cuberri@gmail.com>
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package cmdsrv.client;

import cmdsrv.client.CmdsrvRequest;
import cmdsrv.client.CmdsrvResponse;
import cmdsrv.client.CmdsrvRestClientJersey;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.client.filter.LoggingFilter;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Request;
import spark.Response;
import spark.Route;

import static org.junit.Assert.assertEquals;
import static spark.Spark.post;

/**
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public class CmdsrvRestClientJerseyTest {

    public static final String JSON_REQ_CMD_1 = "{\"cmd\":[\"ls\",\"-a\",\"-l\"]}";

    public static final String JSON_RES_CMD_1_STDERR = "plip";
    public static final String JSON_RES_CMD_1_STDOUT = "plop";
    public static final String JSON_RES_CMD_1 = "{\"cmd\": [\"ls\", \"-a\", \"-l\"], \"retval\": 0, \"stderr\": \"" + JSON_RES_CMD_1_STDERR + "\", \"stdout\": \"" + JSON_RES_CMD_1_STDOUT + "\"}";

    @BeforeClass
    public static void beforeClass() {
        post(new Route("/cmd/200") {
            @Override
            public Object handle(Request req, Response res) {
                assertEquals(req.headers("Content-Type"), "application/json");
                assertEquals(req.body(), "{\"cmd\":[\"ls\",\"-a\",\"-l\"]}");

                res.status(200);
                res.header("Content-Type", "application/json");
                return JSON_RES_CMD_1;
            }
        });
    }

    @Test
    public void testExecute() {
        // given
        ClientConfig clientConfig = new DefaultClientConfig();
        Client client = Client.create(clientConfig);
        client.addFilter(new LoggingFilter());
        CmdsrvRestClientJersey sut = new CmdsrvRestClientJersey(client.resource("http://localhost:4567/cmd/200"));
        CmdsrvRequest req = new CmdsrvRequest("ls", "-a", "-l");

        // when
        CmdsrvResponse res = sut.execute(req);

        // then
        assertEquals(0, res.getRetval());
        assertEquals(JSON_RES_CMD_1_STDOUT, res.getStdout());
        assertEquals(JSON_RES_CMD_1_STDERR, res.getStderr());
    }

}
