package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.MissionDTO;
import logic.dataManagment.MissionManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class MissionsListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
            Set<MissionDTO> missionList = missionManager.getMissions();
            String json = gson.toJson(missionList);
            out.println(json);
            out.flush();
        }
    }
}


