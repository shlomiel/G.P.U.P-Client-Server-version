package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.TargetDTO;
import logic.system.Gpup;
import logic.system.Mission;
import logic.utils.ObjectToDTO;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/mission-info")

public class TaskInfoServlet extends HttpServlet {
        protected  void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String taskName = request.getParameter("taskName");
                Mission m = Gpup.getInstance().getSystemMissions().get(taskName);

                m.updateTargetLists();



                String json = gson.toJson(ObjectToDTO.fromMissionToDTO(m));
                out.println(json);
                out.flush();
            }

        }
}
