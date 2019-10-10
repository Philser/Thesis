package servlet;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class JsonServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void sendJsonResponse(HttpServletResponse response, Object responseObject, int status) throws IOException {
		Gson g = new Gson();
		response.setContentType("application/json");
        String jsonResponse = g.toJson(responseObject);
        response.setStatus(status);
        response.getWriter().print(jsonResponse);
	}

}
