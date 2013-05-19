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

import cmdsrv.client.CmdsrvResponse;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public class CmdsrvResponseTest {

    public static final String JSON_CMD_1 = "{\"cmd\":[\"ls\",\"-a\",\"-l\"],\"retval\":0,\"stdout\":\"stdout\",\"stderr\":\"stderr\"}";
    public static final String JSON_RES_CMD_1 = "{\"cmd\": [\"ls\", \"-a\", \"-l\"], \"retval\": 0, \"stderr\": \"\", \"stdout\": \"total 332\\ndrwxr-xr-x 2 vagrant vagrant   4096 Apr 18 06:07 .\\ndrwxr-xr-x 5 vagrant vagrant   4096 Apr 18 04:43 ..\\n-rw-r--r-- 1 vagrant vagrant 129141 Apr 10 19:03 bottle.py\\n-rw-r--r-- 1 vagrant vagrant 154077 Apr 18 06:06 bottle.pyc\\n-rw-r--r-- 1 vagrant vagrant    479 Apr 18  2013 cmdsrv.cfg\\n-rw-rw-r-- 1 vagrant vagrant  20100 Apr 18 06:22 cmdsrv.log\\n-rw-rw-r-- 1 vagrant vagrant      5 Apr 18 06:06 cmdsrv.pid\\n-rw-r--r-- 1 vagrant vagrant   4432 Apr 18  2013 cmdsrv.py\\n-rw-r--r-- 1 vagrant vagrant   4707 Apr 18 06:07 cmdsrv.sh\\n\"}";

    @Test
    public void testToJson() throws Exception {
        // given
        CmdsrvResponse res = new CmdsrvResponse(0, "stdout", "stderr", "ls", "-a", "-l");

        // when
        String json = CmdsrvResponse.toJson(res);
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
        CmdsrvResponse res = CmdsrvResponse.fromJson(JSON_RES_CMD_1);

        // then
        assertNotNull(res);
        assertArrayEquals(new String[]{"ls", "-a", "-l"}, res.getCmd());
    }

}
