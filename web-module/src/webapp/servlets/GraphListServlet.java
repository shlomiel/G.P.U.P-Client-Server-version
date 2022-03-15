package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.GraphDTO;
import logic.dataManagment.GraphManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class GraphListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GraphManager graphManager = ServletUtils.getGraphManager(getServletContext());
            Set<GraphDTO> graphList = graphManager.getGraphs();
            String json = gson.toJson(graphList);
            out.println(json);
            out.flush();
        }
    }
}
