package logic.tasks;

import client.component.main.mainSceneController;
import javafx.application.Platform;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import logic.system.Gpup;
import logic.system.Mission;
import logic.system.Target;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import logic.utils.WriteToFileConsumerClass;



public class CompilationRunTask implements Runnable {

    private Target target;
    private CompilationTask mainTask;

    private MissionDTO DTOmyTask;
    private TargetDTO DTOtarget;
    private Mission myTask;
    private mainSceneController myController;


    // constructor of the class Tasks
    public CompilationRunTask(Target t, CompilationTask mainTask) {
        this.target = t;
        this.mainTask = mainTask;
    }

    public CompilationRunTask(TargetDTO t, MissionDTO mission, mainSceneController mainSceneController) {
        this.DTOtarget = t;
        this.DTOmyTask = mission;
        this.myController = mainSceneController;
    }

    private synchronized void doAtomicCalcs() throws InterruptedException {
        while (Gpup.getInstance().getPause().get() == true)
            Gpup.getInstance().waitOnMyPause();
        myController.updateRunStatusInServer(DTOtarget);
    }

    public void run()
    {
        try{
            String filePath = Gpup.getInstance().getWorkingDirectory() + "\\" + DTOtarget.getTargetName();
            new File(filePath).mkdirs();
            WriteToFileConsumerClass fileConsumer = new WriteToFileConsumerClass(filePath+'\\' + DTOtarget.getTargetName());
            ArrayList<Consumer> consumers = new ArrayList<>();
            //consumers.add(mainTask.consumer);
            consumers.add(fileConsumer);


            ProcessBuilder builder = getProcessBuilder(DTOmyTask.getSourcePath(), DTOmyTask.getDestPath());


            runTask(consumers,DTOmyTask.getSourcePath(), DTOmyTask.getDestPath(), builder);


            doAtomicCalcs();

            Platform.runLater(
                    () -> {
                        myController.updateRunStatusInUI(DTOtarget); // change the status of the targets, and add the price
                    });

        }

        catch(InterruptedException | IOException ie)
        {
            ie.printStackTrace();
        }
    }


    private ProcessBuilder getProcessBuilder(String targetFolder_d, String targetFolder_cp) {
        String fileFQN = DTOtarget.getFreeInfo();
        String filePath = getFilePathFromFQN(fileFQN);

        ProcessBuilder processBuilderCompilation = new ProcessBuilder(
                "javac",
                "-d", targetFolder_cp,
                "-cp", targetFolder_cp,
                filePath).redirectErrorStream(false);
        processBuilderCompilation.directory(new File(targetFolder_d));

        return processBuilderCompilation;
    }

    private String getFilePathFromFQN(String fileFQN) {
        String newStr = fileFQN.replaceAll("\\.", "/");

        newStr = newStr + ".java";

        return newStr;
    }




    /*private synchronized void updateRunStatus() {
        mainTask.getCurrentRunGraph().removeTarget(target);

        if (mainTask.getCurrentRunGraph().getCountOfNodes() == 0)
            mainTask.executor.shutdown();


        if (target.getlastRunResult().equals(Target.RunResult.SKIPPED) || target.getlastRunResult().equals(Target.RunResult.FAILURE)) {
            for (Target neighbor : target.getDependsOn()) {
                Target.RunningStatus oldStatus = neighbor.getStatusInRun();
                neighbor.setStatusInRun(Target.RunningStatus.SKIPPED);
                mainTask.updateLists(oldStatus,neighbor);

                if (mainTask.getCurrentRunGraph().isLeafOrIndependent(neighbor)) {
                    CompilationRunTask worker = new CompilationRunTask(neighbor, mainTask);
                    mainTask.executor.execute(worker);//calling execute method of ExecutorService
                }
            }
        }
        else {
            for (Target neighbor : target.getDependsOn()) {
                if (neighbor.getStatusInRun().equals(Target.RunningStatus.FROZEN)) {
                    neighbor.setStatusInRun(Target.RunningStatus.WAITING);
                    mainTask.updateLists(Target.RunningStatus.FROZEN, neighbor);
                }

                if (mainTask.getCurrentRunGraph().isLeafOrIndependent(neighbor)) {
                    CompilationRunTask worker = new CompilationRunTask(neighbor, mainTask);
                    mainTask.executor.execute(worker);//calling execute method of ExecutorService
                }

            }
        }

    }
*/

    public void runTask(ArrayList<Consumer> consumers,
             String sourcePath, String destPath,ProcessBuilder builder) throws InterruptedException, IOException {
        if(DTOtarget.getRunningStatus() == Target.RunningStatus.SKIPPED) {
            DTOtarget.setRunningStatus(Target.RunningStatus.SKIPPED);
            DTOtarget.setLastRunResult(Target.RunResult.SKIPPED);
            List<String> targetsThatAreNowBlocked = DTOtarget.getDirectDepOn();
            for (Consumer<String> consumer: consumers) {
                consumer.accept("\nlogic.Target " + DTOtarget.getTargetName() + " Skipped, the targets that are now prevented from processing and will be skipped: " + targetsThatAreNowBlocked);
                DTOtarget.setMylog(DTOtarget.getMylog()+"\nlogic.Target " + DTOtarget.getTargetName() +
                        " Skipped, the targets that are now prevented from processing and will be skipped: " +
                        targetsThatAreNowBlocked );
            }
            return;
        }

        DTOtarget.setRunningStatus(Target.RunningStatus.IN_PROCESS);
        //mainTask.updateLists(Target.RunningStatus.WAITING,target);

        for (Consumer<String> consumer: consumers) {
            consumer.accept("\nStarting to run task on target " + DTOtarget.getTargetName());//1
            DTOtarget.setMylog(DTOtarget.getMylog() + " " +"\nStarting to run task on target " + DTOtarget.getTargetName() );
            consumer.accept(DTOtarget.getFreeInfo());//2
            DTOtarget.getMyTask().setMyLog(DTOtarget.getMyTask().getMyLog() + "\nStarting to run task on target " + DTOtarget.getTargetName());
        }

        Target.RunResult runResult = runCompilationTaskUtil(consumers,sourcePath,destPath,getProcessBuilder(sourcePath,destPath));

        for (Consumer<String> consumer: consumers) {
            consumer.accept("Processing of target " + DTOtarget.getTargetName() + " The result of the run is: " + runResult.toString());//3
            DTOtarget.setMylog(DTOtarget.getMylog() + "Processing of target " + DTOtarget.getTargetName() + " The result of the run is: " + runResult.toString());
            DTOtarget.getMyTask().setMyLog(DTOtarget.getMyTask().getMyLog() + "Processing of target " + DTOtarget.getTargetName() + " The result of the run is: " + runResult.toString());

        }
        if(runResult != Target.RunResult.FAILURE){
            List<String> targetsThatAreNowOpen = DTOtarget.getDirectDepOn();
            for (Consumer<String> consumer : consumers) {
                consumer.accept("New targets that are now available for processing: " + targetsThatAreNowOpen);//4
                DTOtarget.setMylog(DTOtarget.getMylog() + "New targets that are now available for processing: " + targetsThatAreNowOpen);
                DTOtarget.getMyTask().setMyLog(DTOtarget.getMyTask().getMyLog() + "New targets that are now available for processing: " + targetsThatAreNowOpen);
            }

            if(runResult == Target.RunResult.SUCCESS)
                DTOtarget.setLastRunResult(Target.RunResult.SUCCESS);
            else
                DTOtarget.setLastRunResult(Target.RunResult.WARNING);
        }

        else {
            List<String> targetsThatAreNowBlocked = DTOtarget.getDirectDepOn();
            for (Consumer<String> consumer: consumers) {
                consumer.accept("The targets that are now prevented from processing and will be skipped: " + targetsThatAreNowBlocked);
                DTOtarget.setMylog(DTOtarget.getMylog() + " " + "The targets that are now prevented from processing and will be skipped: " + targetsThatAreNowBlocked);//5
                DTOtarget.getMyTask().setMyLog(DTOtarget.getMyTask().getMyLog() + "The targets that are now prevented from processing and will be skipped: " + targetsThatAreNowBlocked);//5

            }

            DTOtarget.setLastRunResult(Target.RunResult.FAILURE);
        }

        DTOtarget.setRunningStatus(Target.RunningStatus.FINISHED);
        //  mainTask.updateLists(Target.RunningStatus.IN_PROCESS,target);
    }

    public Target.RunResult runCompilationTaskUtil(ArrayList<Consumer> consumers, String sourcePath, String destPath, ProcessBuilder processBuilder)
            throws InterruptedException, IOException {

        for (Consumer<String> consumer: consumers)
            consumer.accept("Task on target " + DTOtarget.getTargetName() + " going to run " + processBuilder.command());

        DTOtarget.setStartProcess(System.currentTimeMillis());
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder builder = new StringBuilder();
        String line = null;
        while ( (line = reader.readLine()) != null) {
            builder.append(line);
            builder.append(System.getProperty("line.separator"));
        }
        String result = builder.toString();

        int resultCode = process.waitFor();
        DTOtarget.setLastSuccessfulRun(System.currentTimeMillis() - DTOtarget.getStartProcess());

        //Determine result
        if (resultCode != 0) { // javac failed
            DTOtarget.setLastRunResult(Target.RunResult.FAILURE);
            for (Consumer<String> consumer: consumers)
                consumer.accept("Task on target " + DTOtarget.getTargetName() + " failed  " + result);
        }
        else
            DTOtarget.setLastRunResult(Target.RunResult.SUCCESS);


        for (Consumer<String> consumer: consumers)
            consumer.accept("Task on target " + DTOtarget + " compiler ran for  " + DTOtarget.getLastSuccessfulRun());

        DTOtarget.setRunningStatus( Target.RunningStatus.FINISHED);
        return DTOtarget.getLastRunResult();
    }




}
