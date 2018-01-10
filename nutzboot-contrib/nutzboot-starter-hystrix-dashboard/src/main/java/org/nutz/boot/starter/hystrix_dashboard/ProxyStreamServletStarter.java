/**
 * Copyright 2015 Netflix, Inc.
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
package org.nutz.boot.starter.hystrix_dashboard;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Proxy an EventStream request (data.stream via proxy.stream) since EventStream does not yet support CORS (https://bugs.webkit.org/show_bug.cgi?id=61862)
 * so that a UI can request a stream from a different server.
 */
@IocBean
public class ProxyStreamServletStarter extends HttpServlet implements WebServletFace {
    private static final long serialVersionUID = 1L;
    private static final Log log = Logs.get();

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String origin = request.getParameter("origin");
        String authorization = request.getParameter("authorization");
        if (origin == null) {
            response.setStatus(500);
            response.getWriter().println("Required parameter 'origin' missing. Example: 107.20.175.135:7001");
            return;
        }
        origin = origin.trim();

        InputStream is = null;
        boolean hasFirstParameter = false;
        StringBuilder url = new StringBuilder();
        if (!origin.startsWith("http")) {
            url.append("http://");
        }
        url.append(origin);
        if (origin.contains("?")) {
            hasFirstParameter = true;
        }
        Map<String, String[]> params = request.getParameterMap();
        for (String key : params.keySet()) {
            if (!key.equals("origin") && !key.equals("authorization")) {
                String[] values = params.get(key);
                String value = values[0].trim();
                if (hasFirstParameter) {
                    url.append("&");
                } else {
                    url.append("?");
                    hasFirstParameter = true;
                }
                url.append(key).append("=").append(value);
            }
        }
        String proxyUrl = url.toString();
        log.debugf("Proxy opening connection to: %s", proxyUrl);
        try {
            Request req = Request.create(proxyUrl, METHOD.GET);
            if (authorization != null) {
                req.getHeader().set("Authorization", authorization);
            }
            Response resp = Sender.create(req).setConnTimeout(5000).setTimeout(15000).send();
            if (resp.isOK()) {
                String contentType = resp.getHeader().get("Content-Type");
                if (Strings.isBlank(contentType) || !contentType.startsWith("text/event-stream")) {
                    log.warn("not vaild ContentType = " + contentType);
                    return;
                }
                response.setHeader("Content-Type", contentType);
                // writeTo swallows exceptions and never quits even if outputstream is throwing IOExceptions (such as broken pipe) ... since the inputstream is infinite
                // httpResponse.getEntity().writeTo(new OutputStreamWrapper(response.getOutputStream()));
                // so I copy it manually ...
                is = resp.getStream();

                // copy data from source to response
                OutputStream os = response.getOutputStream();
                int b = -1;
                while ((b = is.read()) != -1) {
                    try {
                        os.write(b);
                        if (b == 10 /** flush buffer on line feed */) {
                            os.flush();
                        }
                    } catch (Exception e) {
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error proxying request: " + url, e);
        } finally {
            Streams.safeClose(is);
        }
    }

    public String getName() {
        return "ProxyStreamServlet";
    }

    public String getPathSpec() {
        return "/hystrix-dashboard/proxy.stream";
    }

    public Servlet getServlet() {
        return this;
    }
}
