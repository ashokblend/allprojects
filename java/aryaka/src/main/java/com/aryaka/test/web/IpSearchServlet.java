package com.aryaka.test.web;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aryaka.test.query.QueryData;

/**
 * Servlet implementation class IpSearchServlet
 */
public class IpSearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private QueryData queryData;

	/**
	 * Default constructor.
	 */
	public IpSearchServlet() {

	}

	public void init() throws ServletException {
		String storePath = getServletConfig().getInitParameter("storepath");
		if (null == storePath) {
			throw new RuntimeException("Provide store path as servlet parameter");
		}
		try {
			this.queryData = new QueryData(storePath);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to initialise querydata" + e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			String ip = request.getParameter("ip");
			String city = queryData.queryCity(ip);
			response.getWriter().write(city);
			response.getWriter().flush();
		} catch (Exception e) {
			e.printStackTrace();
			response.getWriter().write("Got error during search, check logs for more detail");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
