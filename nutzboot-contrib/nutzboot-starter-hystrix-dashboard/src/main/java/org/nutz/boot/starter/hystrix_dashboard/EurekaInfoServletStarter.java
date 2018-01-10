package org.nutz.boot.starter.hystrix_dashboard;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.boot.starter.WebServletFace;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;

/**
 * Copyright 2013 Netflix, Inc.
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

/**
 * Servlet that calls eureka REST api in order to get instances information. <BR>
 * You need provide a url parameter. i.e: eureka?url=http://127.0.0.1:8080/eureka/v2/apps
 * 
 * @author diegopacheco
 *
 */
@IocBean
public class EurekaInfoServletStarter extends HttpServlet implements WebServletFace {
	
	 private static final long serialVersionUID = 1L;
	 
	 protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 
		 String uri = request.getParameter("url");
		 if (uri==null || "".equals(uri)) response.getOutputStream().write("Error. You need supply a valid eureka URL ".getBytes()); 
		 
		 try{
			 response.setContentType("application/xml");
			 response.setHeader("Content-Encoding", "gzip");
			 Streams.write(response.getOutputStream(), UrlUtils.readXmlInputStream(uri));
		 }catch(Exception e){
			 response.getOutputStream().write(("Error. You need supply a valid eureka URL. Ex: " + e + "").getBytes()); 
		 }
		 
	 }

    public String getName() {
        return "EurekaInfoServlet";
    }

    public String getPathSpec() {
        return "/hystrix-dashboard/eureka";
    }

    public Servlet getServlet() {
        return this;
    }
}
