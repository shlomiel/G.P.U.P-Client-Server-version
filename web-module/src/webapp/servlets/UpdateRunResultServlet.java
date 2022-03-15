package webapp.servlets;

import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import logic.DTO.TargetDTO;
import logic.system.Gpup;
import logic.system.Mission;
import logic.system.Target;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet( "/update-run-result")
public class UpdateRunResultServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            TargetDTO t = gson.fromJson(request.getParameter("target"),TargetDTO.class);
            Mission m = Gpup.getInstance().getSystemMissions().get(t.getMissionName());
            m.setMyLog(m.getMyLog() + t.getMylog());
            updateRunStatus(m,t);
            updateMissionLists(m);
        }

    }

    private synchronized void updateMissionLists(Mission m) {
        m.updateTargetLists();
    }


    private synchronized void updateRunStatus(Mission mainTask, TargetDTO targetDTO) {
        //Target target = mainTask.getMissionGraph().removeTarget(targetDTO.getTargetName());
        mainTask.setRunResultOnTarget(targetDTO);
        Target target = mainTask.getTargetFromRunTargetsByName(targetDTO.getTargetName());


        if(! target.getlastRunResult().equals(Target.RunResult.FAILURE)){
            mainTask.getMissionGraph().removeTarget(targetDTO.getTargetName());
        }



        if (targetDTO.getLastRunResult().equals(Target.RunResult.SKIPPED) ||
                targetDTO.getLastRunResult().equals(Target.RunResult.FAILURE)) {
            for (Target neighbor : target.getDependsOn()) {
                Target.RunningStatus oldStatus = neighbor.getStatusInRun();
                neighbor.setStatusInRun(Target.RunningStatus.SKIPPED);
                //mainTask.updateLists(oldStatus,neighbor);
                mainTask.addToReadyForRunTargets(neighbor);

                if (mainTask.getMissionGraph().isLeafOrIndependent(neighbor)) {
                    mainTask.addToReadyForRunTargets(neighbor);
                }
            }
        }
        else {
            for (Target neighbor : target.getDependsOn()) {
                if (neighbor.getStatusInRun().equals(Target.RunningStatus.FROZEN)) {
                    neighbor.setStatusInRun(Target.RunningStatus.WAITING);
                  //  mainTask.updateLists(Target.RunningStatus.FROZEN, neighbor);
                }
                if (mainTask.getMissionGraph().isLeafOrIndependent(neighbor)) {
                    //CompilationRunTask worker = new CompilationRunTask(neighbor, mainTask);
                    //mainTask.executor.execute(worker);//calling execute method of ExecutorService
                    mainTask.addToReadyForRunTargets(neighbor);
                }
            }
        }

    }




}
