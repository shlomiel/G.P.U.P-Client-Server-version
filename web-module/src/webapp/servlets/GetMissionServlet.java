package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.GraphDTO;
import logic.DTO.MissionDTO;
import logic.dataManagment.GraphManager;
import logic.dataManagment.MissionManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

import static webapp.constants.Constants.GRAPHNAME;

public class GetMissionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //returning JSON objects, not HTML
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
            String missionNameFromParameter = request.getParameter("missionName");
            MissionDTO res = null;
            for (MissionDTO mission: missionManager.getMissions()) {
                if(mission.getMissionName().equals(missionNameFromParameter)){
                    res = mission;
                }
            }
            String json = gson.toJson(res);
            out.println(json);
            out.flush();
        }
    }


}
