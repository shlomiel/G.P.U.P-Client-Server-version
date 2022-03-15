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

import static webapp.constants.Constants.*;

public class GetGraphServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            GraphManager graphManager = ServletUtils.getGraphManager(getServletContext());
            String graphNameFromParameter = request.getParameter(GRAPHNAME);
            GraphDTO res = null;
            for (GraphDTO graph: graphManager.getGraphs()) {
                if(graph.getGraphName().equals(graphNameFromParameter)){
                    res = graph;
                }
            }
            String json = gson.toJson(res);
            out.println(json);
            out.flush();
        }
    }
}
