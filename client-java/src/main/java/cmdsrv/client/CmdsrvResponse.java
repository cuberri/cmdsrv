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

/**
 * Model of the response got from the cmdsrv server. The executed command is recalled, and the result of the execution
 * is provided as :
 * <ul>
 *     <li>retval : the return code of the execution</li>
 *     <li>stdout : the standard output of the execution</li>
 *     <li>stderr : the standard error output of the execution</li>
 * </ul>
 *
 * Example of some data unmarshalled from the server :
 * <pre>
 * {@code
 * {'cmd':entity['cmd'], 'stdout':stdoutdata, 'stderr':stderrdata, 'retval':ret}
 * }
 * </pre>
 *
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public class CmdsrvResponse {

    private String[] cmd;
    private int retval;
    private String stdout;
    private String stderr;

    /**
     * You'd probably not need to construct such an object, but the constructor is made public for convenience
     *
     * @param retval
     * @param stdout
     * @param stderr
     * @param cmd
     */
    public CmdsrvResponse(int retval, String stdout, String stderr, String... cmd) {
        this.cmd = cmd;
        this.retval = retval;
        this.stdout = stdout;
        this.stderr = stderr;
    }

    /**
     * The command executed on the server
     * @return
     */
    public String[] getCmd() {
        return cmd;
    }

    /**
     * The joined string representation of the command executed
     * @return
     */
    public String getCmdJoinedStr() {
        return Joiner.on(' ').join(cmd);
    }

    /**
     * The return code of the process execution on the server
     * @return
     */
    public int getRetval() {
        return retval;
    }

    /**
     * The standard output of the process execution on the server
     * @return
     */
    public String getStdout() {
        return stdout;
    }

    /**
     * The standard error output of the process execution on the server
     * @return
     */
    public String getStderr() {
        return stderr;
    }

    @Override
    public String toString() {
        return "CmdsrvResponse{" +
                "cmd=" + getCmdJoinedStr() +
                ", retval=" + retval +
                ", stdout='" + stdout + '\'' +
                ", stderr='" + stderr + '\'' +
                '}';
    }

    /**
     * Return a JSON String representation of the response
     *
     * @param res
     * @return
     */
    public static String toJson(CmdsrvResponse res) {
        return new Gson().toJson(res);
    }

    /**
     * Factory method unmarshalling from a JSON String representation
     *
     * @param json
     * @return
     */
    public static CmdsrvResponse fromJson(String json) {
        return new Gson().fromJson(json, CmdsrvResponse.class);
    }

}
