package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import logic.dataManagment.MissionManager;
import logic.dataManagment.UserManager;
import logic.system.Gpup;
import logic.system.Mission;
import utils.ServletUtils;
import utils.SessionUtils;
import webapp.constants.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static webapp.constants.Constants.USERNAME;

public class AddTaskServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        Gson json = new Gson();

        List<TargetDTO> targets = Arrays.asList(json.fromJson(request.getParameter("targets"), TargetDTO[].class));
        String taskName = request.getParameter("taskName");
        String taskType = request.getParameter("taskType");
        String username = request.getParameter("username");
        String graphName = request.getParameter("graphName");


        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        synchronized (this) {
            for (MissionDTO mission : missionManager.getMissions()) {
                if (mission.getMissionName().equals(taskName)) {
                    String errorMessage = "Task " + taskName + " already exists. Please enter a different  task name.";
                    // stands for unauthorized as there is already such task with this name
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getOutputStream().print(errorMessage);
                    return;
                }
            }
        }

        if(taskType.equals("Simulation")) {
            int ms = Integer.parseInt(request.getParameter("ms"));
            String runTimeSelection = request.getParameter("runTimeSelectionEnum");
            double successProb = Double.parseDouble(request.getParameter("successProb"));
            double warningProb = Double.parseDouble(request.getParameter("warningProb"));


            Mission newMission = Gpup.getInstance().addMission(targets, graphName, taskName, taskType, username,
                    ms,runTimeSelection,successProb,warningProb);
            missionManager.addMission(new MissionDTO(newMission));
        }


        else if(taskType.equals("Compilation")) {
            Mission newMission = Gpup.getInstance().addMission(targets,graphName,taskName,taskType,username,
                    request.getParameter("source"),request.getParameter("dest"));
            missionManager.addMission(new MissionDTO(newMission));
        }
        response.getOutputStream().print("Great success");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}