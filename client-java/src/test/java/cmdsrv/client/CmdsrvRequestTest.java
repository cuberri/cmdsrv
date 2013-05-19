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
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public class CmdsrvRequestTest {

    public static final String JSON_CMD_1 = "{\"cmd\":[\"ls\",\"-a\",\"-l\"]}";

    @Test
    public void testToJson() throws Exception {
        // given
        CmdsrvRequest req = new CmdsrvRequest("ls", "-a", "-l");

        // when
        String json = CmdsrvRequest.toJson(req);
        System.out.println(json);

        // then
        assertNotNull(json);
        assertEquals(JSON_CMD_1, json);
    }

    @Test
    public void testFromJson() throws Exception {
        // given
        // N.A.

        // when
        CmdsrvRequest req = CmdsrvRequest.fromJson(JSON_CMD_1);

        // then
        assertNotNull(req);
        assertArrayEquals(new String[]{"ls", "-a", "-l"}, req.getCmd());
    }
}
