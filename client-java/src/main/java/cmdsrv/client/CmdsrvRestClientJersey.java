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

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;

/**
 * <p>
 *     Jersey WebResource backed implementation of ICmdsrvClient.
 * </p>
 *
 * <p>
 *     You need to provide a WebResource pointing to the cmdsrv server location. <br />
 *     This work is intentionally not done in the class in order to let the developer inject its own WebResource with
 *     its own Client configuration (and besides it helps testability).
 * </p>
 *
 * <p>
 *     Example :
 *
 *     <pre>
 *     {@code
 *      Client client = Client.create();
 *      WebResource resource = client.resource("http://localhost:4567/cmd")
 *      CmdsrvRestClientJersey restClient = new CmdsrvRestClientJersey(resource);
 *      restClient.execute(new CmdsrvRequest("ls", "-a", "-l"))
 *     }
 *     </pre>
 * </p>
 *
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public class CmdsrvRestClientJersey implements ICmdsrvClient {

    private static Logger LOGGER = LoggerFactory.getLogger(CmdsrvRestClientJersey.class);

    private WebResource cmdsrvResource;

    /**
     * Need a WebResource pointing to the cmdsrv's server location
     *
     * @param cmdsrvResource
     */
    public CmdsrvRestClientJersey(WebResource cmdsrvResource) {
        this.cmdsrvResource = cmdsrvResource;
    }

    /**
     * @see ICmdsrvClient
     * @param req
     * @return
     */
    @Override
    public CmdsrvResponse execute(CmdsrvRequest req) {
        LOGGER.info("Posting request [{}] to [{}]", req, cmdsrvResource.getURI());
        String toSend = CmdsrvRequest.toJson(req);

        ClientResponse res = cmdsrvResource
                .type(MediaType.APPLICATION_JSON_TYPE)
                .header("Hint-Content-Length", toSend.length())
                .accept("application/json")
                .post(ClientResponse.class, toSend.getBytes());
        LOGGER.info("Request [{}] sent. Received status : [{}]", req, res.getStatus());

        // make sure to buffer the entity in order to release the connection
        res.bufferEntity();
        String body = res.hasEntity() ? res.getEntity(String.class) : null;
        if (ClientResponse.Status.OK != res.getClientResponseStatus()) {
            String message = "Cmdsrv response error sending request [" + req + "]. Received status : [" + res.getStatus() + "]. Received body [" + body + "]";
            LOGGER.error(message);
            throw new CmdsrvClientException(message);
        }
        LOGGER.debug("Cmdsrv body recevied : {}", body);

        return CmdsrvResponse.fromJson(body);
    }

}
