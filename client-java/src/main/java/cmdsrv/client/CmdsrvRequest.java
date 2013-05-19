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

import com.google.common.base.Joiner;
import com.google.gson.Gson;

import java.util.Arrays;

/**
 * <p>
 *     Model of a request being sent to the command server.
 * </p>
 *
 * <p>
 *     Use Gson library to marshall/unmarshall itself
 * </p>
 *
 * <p>
 *     When you construct a request, you must specify the command to be executed as a series of arguments. The first one
 *     is the executable. The others are the arguments of the command to execute.
 * </p>
 *
 * <p>
 *     Example :
 *     <pre>
 *     {@code
 *     new CmdsrvRequest("ls", "-a", "-l")
 *     }
 *     </pre>
 * </p>
 *
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public class CmdsrvRequest {

    private String[] cmd;

    /**
     * Construct a command request to be executed server side based on the given arguments. <br />
     * The first argument is the executable name. The following arguments are the arguments passed to the executable.
     *
     * @param command The cmd to execute server side
     */
    public CmdsrvRequest(String... command) {
        this.cmd = command;
    }

    /**
     * The command to be executed as an array. The first element is the executable. The following elements are the
     * arguments to be passed to the executable.
     *
     * @return
     */
    public String[] getCmd() {
        return cmd;
    }

    /**
     * Return a joined string representation of the command
     *
     * @return
     */
    public String getCmdJoinedStr() {
        return Joiner.on(' ').join(cmd);
    }

    @Override
    public String toString() {
        return "CmdsrvRequest{" +
                "cmd='" + getCmdJoinedStr() + '\'' +
                '}';
    }

    /**
     * Return a JSON String representation of the request
     *
     * @param req
     * @return
     */
    public static String toJson(CmdsrvRequest req) {
        return new Gson().toJson(req);
    }

    /**
     * Factory method constructing a request based on a JSON String representation
     *
     * @param json
     * @return
     */
    public static CmdsrvRequest fromJson(String json) {
        return new Gson().fromJson(json, CmdsrvRequest.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CmdsrvRequest)) return false;

        CmdsrvRequest that = (CmdsrvRequest) o;

        if (!Arrays.equals(cmd, that.cmd)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return cmd != null ? Arrays.hashCode(cmd) : 0;
    }
}
