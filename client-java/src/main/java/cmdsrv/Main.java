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

package cmdsrv;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * @author Christophe Uberri <cuberri@gmail.com>
 */
public class Main {

    private static boolean logToConsole = false;

    /**
     * localhost:8055 {"cmd":["echo", "dummy", "cmdsrv"]}
     *
     * @param args
     */
    public static void main(String args[]) {
        // usage if not enough args
        if (args.length < 2) {
            System.out.println("Usage: java -jar cmdsrv-client-<version>.jar http://host:port/path/to/cmdsrv jsonrequest [debug]");
            System.exit(1);
        }

        logToConsole = args.length > 2 && null != args[2] && "debug".equals(args[2]);

        HttpURLConnection connection = null;
        try {
            stderr("Server location : " + args[0]);
            stderr("Request         : " + args[1]);

            URL url = new URL(args[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(args[1].getBytes().length));
            connection.setUseCaches(false);

            try(DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.writeBytes(args[1]);
                wr.flush();
                wr.close();
            }
            connection.disconnect();

            stderr(connection.getResponseCode() + " : " + connection.getResponseMessage());
            Scanner s = connection.getResponseCode() != 200
                    ? new Scanner(connection.getErrorStream()).useDelimiter("\\Z")
                    : new Scanner(connection.getInputStream()).useDelimiter("\\Z") ;
            System.out.println(s.next());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
            if(null != connection) {
                connection.disconnect();
            }
            System.exit(2);
        }
    }

    private static void stderr(String s) {
        if (logToConsole) {
            System.err.println("[stderr] " + s);
        }
    }
}
