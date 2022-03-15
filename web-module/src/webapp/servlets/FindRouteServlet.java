package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.system.Gpup;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class FindRouteServlet extends HttpServlet {

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            String graphNameParameter = request.getParameter("graphName");
            String fromParameter = request.getParameter("from");
            String toParameter = request.getParameter("to");
            String routSelection = request.getParameter("RoutSelection");

            List<String> graphList = Gpup.getInstance().findRoutesBetweenTwoTargets(graphNameParameter,
                    fromParameter,toParameter,routSelection);
            Gson gson = new Gson();
            String json = gson.toJson(graphList);
            out.println(json);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }

    }
