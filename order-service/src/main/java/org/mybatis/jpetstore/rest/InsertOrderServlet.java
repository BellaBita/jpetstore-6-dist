/**
 *    Copyright (C) 2010-2017 the original author or authors.
 *                  2017 iObserve Project (https://www.iobserve-devops.net)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.rest;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.mybatis.jpetstore.domain.Order;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class AccountRestServlet
 */
@WebServlet("/insert-order")
public class InsertOrderServlet extends AbstractOrderServlet {
    private static final long serialVersionUID = 1L;

    private final static Logger LOG = Logger.getLogger(InsertOrderServlet.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertOrderServlet() {
        super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        InsertOrderServlet.LOG.error("insert-order: GET not supported, requires POST");
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {

        final ObjectMapper mapper = new ObjectMapper();
        final Order order = mapper.readValue(request.getReader(), Order.class);

        if (order != null) {
            InsertOrderServlet.LOG.info("New Order requested : " + order.toString());
            this.service.insertOrder(order);
        } else {
            InsertOrderServlet.LOG.error("insert-order NO ORDER to insert");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

}
