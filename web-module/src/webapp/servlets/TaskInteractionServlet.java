package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.MissionDTO;
import logic.dataManagment.MissionManager;
import logic.system.Gpup;
import utils.ServletUtils;
import utils.SessionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

public class TaskInteractionServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String reqType = req.getParameter("reqType");
            String missionName = req.getParameter("missionName");

            if(reqType.equals("Start")) {
                if(Gpup.getInstance().getSystemMissions().get(missionName).getStatus().equals("Paused")){
                    Gpup.getInstance().getSystemMissions().get(missionName).setStatus("Running");
                    return;
                }
                Gpup.getInstance().startMission(missionName); //state is set to Running insinde this method

                out.println("Task started");
                out.flush();

            }



            else if(reqType.equals("Pause")){
                Gpup.getInstance().getSystemMissions().get(missionName).setStatus("Paused");
                //String json = gson.toJson(missionList);
                //out.println(json);
                //out.flush();
                out.println("Task paused");
                out.flush();
            }

            else if(reqType.equals("Resume")){
                Gpup.getInstance().getSystemMissions().get(missionName).setStatus("Running");
                out.println("Task resumed");
                out.flush();
            }

            else if(reqType.equals("Stop")){
                Gpup.getInstance().getSystemMissions().get(missionName).setStatus("Stopped");
                out.println("Task stopped");
                out.flush();
            }
            }
        }

}
