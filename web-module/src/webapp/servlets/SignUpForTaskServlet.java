package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;


import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import logic.DTO.GraphDTO;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import logic.dataManagment.MissionManager;
import logic.system.Gpup;
import logic.system.Mission;
import logic.utils.ObjectToDTO;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

@WebServlet("/sign-up-task")

public class SignUpForTaskServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String taskName = request.getParameter("taskName");
        String userName = request.getParameter("userName");


        MissionManager missionManager = ServletUtils.getMissionManager(getServletContext());
        //String missionNameFromParameter = request.getParameter("missionName");
        MissionDTO res = null;

        for (MissionDTO mission: missionManager.getMissions()) {
            if(mission.getMissionName().equals(taskName)){
                res = mission;
                break;
            }
        }

        assert res != null;
        res.setNumOfWorkers();


        Mission msn = Gpup.getInstance().getSystemMissions().get(res.getMissionName());
        msn.setNumOfWorkers();
        msn.addWorker(userName);


        //List<TargetDTO> targetList = ObjectToDTO.fromTargetListToDTOList(msn.getReadyForRunTargets());

        //String json = gson.toJson(targetList);
        //out.println(json);
        //out.flush();


    }
}