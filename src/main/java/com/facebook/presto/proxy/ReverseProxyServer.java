/*
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
package com.facebook.presto.proxy;

import com.google.gson.Gson;
import io.undertow.Undertow;
import io.undertow.client.UndertowClient;
import io.undertow.server.handlers.proxy.LoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author combine
 */
public class ReverseProxyServer
{
    private static final Logger LOG = LoggerFactory.getLogger(ReverseProxyServer.class);

    private static int FIRST_COORDINATOR = 0;
    private static int SECOND_COORDINATOR = 1;
    private static AtomicInteger BEST_COORDINATOR = new AtomicInteger(1);

    public static void main(final String[] args)
    {
        if (args.length < 3) {
            System.out.println("Usage: java -cp presto-reverse-proxy.jar port coordinator1 coordinator2");
            return;
        }

        int proxyPort = Integer.parseInt(args[0]);
        String firstCoordinatorAddress = args[1];
        String secondCoordinatorAddress = args[2];

        Thread bestServerChecker = new Thread(() -> {
            try {
                while (true) {
                    ClusterInfo firstServer = getRunningQuery(firstCoordinatorAddress);

                    if (firstServer == null || firstServer.getActiveWorkers() == 0) {
                        // The first coordinator is down
                        BEST_COORDINATOR.set(SECOND_COORDINATOR);
                    }
                    else {
                        BEST_COORDINATOR.set(FIRST_COORDINATOR);
                    }
                    Thread.sleep(3000);
                }
            }
            catch (InterruptedException e) {
            }
        });
        bestServerChecker.setName("Coordinator checker");
        bestServerChecker.setDaemon(true);
        bestServerChecker.start();

        try {
            LoadBalancingProxyClient loadBalancer = new LoadBalancingProxyClient(UndertowClient.getInstance(), null, availableHosts -> BEST_COORDINATOR.get())
                    .addHost(new URI(firstCoordinatorAddress))
                    .addHost(new URI(secondCoordinatorAddress));

            Undertow reverseProxy = Undertow.builder()
                    .addHttpListener(proxyPort, "0.0.0.0")
                    .setHandler(ProxyHandler.builder().setProxyClient(loadBalancer).build())
                    .build();
            reverseProxy.start();

        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static ClusterInfo getRunningQuery(String server)
    {
        try {
            String url = server + "/v1/cluster";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                }
                String responseData = response.toString();
                LOG.info("Cluster Info: {} {}", server, responseData);
                return new Gson().fromJson(responseData, ClusterInfo.class);
            }
        }
        catch (Exception e) {
        }
        return null;
    }

}
