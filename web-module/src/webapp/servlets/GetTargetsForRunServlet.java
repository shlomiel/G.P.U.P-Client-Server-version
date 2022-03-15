package webapp.servlets;


import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import logic.dataManagment.MissionManager;
import logic.system.Gpup;
import logic.system.Mission;
import logic.utils.ObjectToDTO;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/get-targets")
public class GetTargetsForRunServlet extends HttpServlet {
    @Override
    protected synchronized void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String username = request.getParameter("userName");
        int numOfTargets = Integer.parseInt(request.getParameter("numOfTargets"));

        Set<TargetDTO> targetList = new HashSet<>();
        //Synchronized{

        for (String missionName: Gpup.getInstance().getSystemMissions().keySet()) {

            Mission msn = Gpup.getInstance().getSystemMissions().get(missionName);
            if(/*msn.getStatus() != null &&*/ !msn.getStatus().equals("Running")){
                out.println(Collections.unmodifiableSet(targetList));
                out.flush();
                return;
            }
            if(msn.getWorkers().contains(username)){
                if(!msn.getReadyForRunTargets().isEmpty()){
                    for(int i = 0;!msn.getReadyForRunTargets().isEmpty() && i<numOfTargets ;i++){
                        targetList.add(ObjectToDTO.fromTargetToDTO(msn.getTargetFromReadyToRun()));
                    }
                }
            }
        }

        if(targetList.isEmpty()) {
            out.println(Collections.unmodifiableSet(targetList));
            out.flush();
            return;
        }
        String json = gson.toJson(targetList);
        out.println(json);
        out.flush();
    }
}
