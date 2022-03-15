package logic.tasks;

import client.component.main.mainSceneController;
import javafx.application.Platform;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import logic.system.Graph;
import logic.system.Gpup;
import logic.system.Mission;
import logic.system.Target;
import logic.utils.WriteToFileConsumerClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;


public class SimulationRunTask implements Runnable {

    private  MissionDTO DTOmyTask;
    private  TargetDTO DTOtarget;
    private  Mission myTask;
   // private Target target;
    private SimulationTask mainTask;
    private mainSceneController myController;


    // constructor of the class Tasks
    public SimulationRunTask(Target t, SimulationTask mainTask )
    {
      //  this.target = t;
        this.mainTask = mainTask;
        t.setLongStartTime(System.currentTimeMillis());
    }

    public SimulationRunTask(Target t, Mission task){
      //  this.target = t;
        this.myTask = task;
    }



    public SimulationRunTask(TargetDTO DTOt, MissionDTO task, mainSceneController controller){
        this.DTOtarget = DTOt;
        this.DTOmyTask = task;
        myController = controller;
    }




    public void run()
    {

        try{

        while(Gpup.getInstance().getPause().get() == true)
                wait();

        String filePath = Gpup.getInstance().getWorkingDirectory() + "\\" + DTOtarget.getTargetName();
        new File(filePath).mkdirs();
        WriteToFileConsumerClass fileConsumer = new WriteToFileConsumerClass(filePath+'\\' + DTOtarget.getTargetName());
        ArrayList<Consumer> consumers = new ArrayList<>();
  //      consumers.add(mainTask.consumer);
        consumers.add(fileConsumer);
        //logic.Target.RunningStatus oldStatus = target.getStatusInRun();


        runTask((int) DTOmyTask.getMs(), DTOmyTask.getRunSelection(), DTOmyTask.getSuccessProb(),
                DTOmyTask.getWarningProb(), consumers);

        doAtomicCalcs();

        //mainTask.updateLists(target);

            Platform.runLater(
                    () -> {
                        myController.updateRunStatusInUI(DTOtarget); // change the status of the targets, and add the price
                    });


    } catch(InterruptedException ie)
        {
            ie.printStackTrace();
        }
    }

    public void runTask(int ms, Graph.RunningTimeType runTimeSelection, double successProb, double successWithWarningsProb, ArrayList<Consumer> consumers) throws InterruptedException {
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

        Target.RunResult runResult = runSimulationTaskUtil(ms,runTimeSelection,successProb,successWithWarningsProb,consumers);

        for (Consumer<String> consumer: consumers) {
            consumer.accept("Processing of target " + DTOtarget.getTargetName() + " The result of the run is: " + runResult.toString());//3
            DTOtarget.setMylog(DTOtarget.getMylog() + "\nProcessing of target " + DTOtarget.getTargetName() + " The result of the run is: " + runResult.toString());
            DTOtarget.getMyTask().setMyLog(DTOtarget.getMyTask().getMyLog() + "\nProcessing of target " + DTOtarget.getTargetName() + " The result of the run is: " + runResult.toString());

        }
        if(runResult != Target.RunResult.FAILURE){
            List<String> targetsThatAreNowOpen = DTOtarget.getDirectDepOn();
            for (Consumer<String> consumer : consumers) {
                consumer.accept("New targets that are now available for processing: " + targetsThatAreNowOpen);//4
                DTOtarget.setMylog(DTOtarget.getMylog() + "\nNew targets that are now available for processing: " + targetsThatAreNowOpen);
                DTOtarget.getMyTask().setMyLog(DTOtarget.getMyTask().getMyLog() + "\nNew targets that are now available for processing: " + targetsThatAreNowOpen);
            }

            if(runResult == Target.RunResult.SUCCESS)
                DTOtarget.setLastRunResult(Target.RunResult.SUCCESS);
            else
                DTOtarget.setLastRunResult(Target.RunResult.WARNING);
        }

        else {
            List<String> targetsThatAreNowBlocked = DTOtarget.getDirectDepOn();
            for (Consumer<String> consumer: consumers) {
                consumer.accept("\nThe targets that are now prevented from processing and will be skipped: " + targetsThatAreNowBlocked);
                DTOtarget.setMylog(DTOtarget.getMylog() + "\nThe targets that are now prevented from processing and will be skipped: " + targetsThatAreNowBlocked);//5
                DTOtarget.getMyTask().setMyLog(DTOtarget.getMyTask().getMyLog() + "\nThe targets that are now prevented from processing and will be skipped: " + targetsThatAreNowBlocked);//5

            }

            DTOtarget.setLastRunResult(Target.RunResult.FAILURE);
        }

        DTOtarget.setRunningStatus(Target.RunningStatus.FINISHED);
      //  mainTask.updateLists(Target.RunningStatus.IN_PROCESS,target);
    }


    public Target.RunResult runSimulationTaskUtil(long ms, Graph.RunningTimeType runTimeSelection, double successProb,
                                                  double successWithWarningsProb, ArrayList<Consumer> consumers)
            throws InterruptedException {

            Random rand = new Random();

            if (runTimeSelection == Graph.RunningTimeType.RANDOM)   //Case of Rand
                ms = rand.longs(0,ms).findFirst().getAsLong();

            for (Consumer<String> consumer: consumers)
                consumer.accept("Task on target " + DTOtarget.getTargetName() + " going to sleep for " +
                        ms + " milliseconds  ");
            long start = System.currentTimeMillis();
            Thread.sleep(ms);
            DTOtarget.setLastSuccessfulRun(ms);
            for (Consumer<String> consumer: consumers)
                consumer.accept("Task on target " + DTOtarget.getTargetName() + " slept for  " +
                        (System.currentTimeMillis() - start));

            //Determine result
            if (rand.nextDouble() <= successProb) {//  success or warning
                if (rand.nextDouble() <= successWithWarningsProb)
                    DTOtarget.setLastRunResult(Target.RunResult.WARNING);

                else
                    DTOtarget.setLastRunResult(Target.RunResult.SUCCESS);
            }
            else
                DTOtarget.setLastRunResult(Target.RunResult.FAILURE);

            DTOtarget.setRunningStatus(Target.RunningStatus.FINISHED);
            return DTOtarget.getLastRunResult();
    }



    private synchronized void doAtomicCalcs() throws InterruptedException {
        while(Gpup.getInstance().getPause().get() == true)
            Gpup.getInstance().waitOnMyPause();
        myController.updateRunStatusInServer(DTOtarget);
       // runNewTargets();
    }



    /*
    private synchronized void runNewTargets() {
        for (Target t: mainTask.getRunTargets()) {
            if(t.getStatusInRun().equals(Target.RunningStatus.FINISHED)||
                    t.getStatusInRun().equals(Target.RunningStatus.IN_PROCESS)){continue;}
            if(! mainTask.getCurrentRunGraph().isLeafOrIndependent(t)){continue;}


            /*if(t.getStatusInRun().equals(logic.Target.RunningStatus.WAITING)){
                tasks.SimulationRunTask worker = new tasks.SimulationRunTask(t, mainTask);
                mainTask.executor.execute(worker);//calling execute method of ExecutorService
            }

            if(t.getStatusInRun().equals(Target.RunningStatus.FROZEN)){
                t.setStatusInRun(Target.RunningStatus.WAITING);
                mainTask.updateLists(Target.RunningStatus.FROZEN,t);
                SimulationRunTask worker = new SimulationRunTask(t, mainTask);
                mainTask.executor.execute(worker);//calling execute method of ExecutorService
            }
        }
    }
*/
  /*  private synchronized void updateRunStatus() {
        myTask.getMissionGraph().removeTarget(DTOtarget.getTargetName());

        //if (mainTask.getCurrentRunGraph().getCountOfNodes() == 0)
          //  mainTask.executor.shutdown();


        if (DTOtarget.getLastRunResult().equals(Target.RunResult.SKIPPED) ||
                DTOtarget.getLastRunResult().equals(Target.RunResult.FAILURE)) {
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


}
