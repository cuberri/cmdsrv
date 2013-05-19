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

/**
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public interface ICmdsrvClient {

    /**
     * Execute the command specified in the request.
     *
     * @param req
     * @return The response unmarshalled from server response
     */
    CmdsrvResponse execute(CmdsrvRequest req);

}
