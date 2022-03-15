package logic.system;

import javafx.concurrent.Task;
import logic.DTO.TargetDTO;
import logic.tasks.CompilationTask;
import logic.tasks.SimulationTask;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.stream.Collectors;


public class Mission {

    private static final String COMPILATION =  "Compilation";

    private static final String SIMULATION = "Simulation";
    private int ms;
    private double successProb;
    private double warningProb;
    private String sourcePath;
    private String destPath;

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getDestPath() {
        return destPath;
    }

    public void setDestPath(String destPath) {
        this.destPath = destPath;
    }

    String runTimeSelection;

    public Mission(List<TargetDTO> targets, Graph graph, String taskName, String taskType,
                   String username, String source, String dest) {
        this.missionName = taskName;
        createMissionGraph(targets,graph);
        this.createdBy = username;
        this.numOfTargets = missionGraph.getCountOfNodes();
        this.taskType = taskType;
        this.totalPrice = calculatePrice(targets,graph);
        this.singlePrice =  calculateSinglePrice(graph);
        this.numOfWorkers = 0;

        this.sourcePath = source;
        this.destPath = dest;
        this.status = "New";
    }

    public Set<String> getWorkers() {
        return workers;
    }

    private Set<String> workers = new HashSet<>();

    private Task<Boolean> task;
    private String missionName;
    private Graph missionGraph;
    private String createdBy;
    private int totalPrice;

    public int getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(int singlePrice) {
        this.singlePrice = singlePrice;
    }

    private int singlePrice;
    private List<Target> runTargets = new ArrayList<>();

    public synchronized void addToReadyForRunTargets(Target neighbor) {
        readyForRunTargets.add(neighbor);
    }

    public List<Target> getReadyForRunTargets() {
        return readyForRunTargets;
    }

    private List<Target> readyForRunTargets = new ArrayList<>();

    private int numOfWorkers;
    private int numOfTargets;
    private String status;
    private String taskType;

    public String getMyLog() {
        return myLog;
    }

    public void setMyLog(String myLog) {
        this.myLog = myLog;
    }

    private String myLog = "";

    public Mission(List<TargetDTO> targets, Graph graph, String taskName, String taskType, String username) {
        this.missionName = taskName;
        createMissionGraph(targets,graph);
        this.createdBy = username;
        this.numOfTargets = missionGraph.getCountOfNodes();
        this.taskType = taskType;
        this.totalPrice = calculatePrice(targets,graph);
        this.singlePrice = calculateSinglePrice(graph);
        this.numOfWorkers = 0;

        if(taskType.equals(COMPILATION)){
            //create new compilation task
        }
        else{
            //create new simulation task
        }

    }

    public int getMs() {
        return ms;
    }

    public double getSuccessProb() {
        return successProb;
    }

    public double getWarningProb() {
        return warningProb;
    }

    public Graph.RunningTimeType getRunTimeSelection() {
        if(Graph.RunningTimeType.RANDOM.toString().equals(runTimeSelection))
            return Graph.RunningTimeType.RANDOM;

        return Graph.RunningTimeType.FIXED;
    }

    public List<Target> getRunTargets() {
        return runTargets;
    }

    public Mission(List<TargetDTO> targets, Graph graph, String taskName, String taskType, String username,
                   int ms, String runTimeSelection, double successProb, double warningProb) {
        this.missionName = taskName;
        createMissionGraph(targets,graph);
        this.createdBy = username;
        this.numOfTargets = missionGraph.getCountOfNodes();
        this.taskType = taskType;
        this.totalPrice = calculatePrice(targets,graph);
        this.singlePrice =  calculateSinglePrice(graph);
        this.numOfWorkers = 0;
        this.status = "New";


        if(taskType.equals(COMPILATION)){
            //create new compilation task
        }
        else{
            this.ms = ms;
            this.runTimeSelection = runTimeSelection;
            this.successProb = successProb;
            this.warningProb = warningProb;
        }

    }

    private int calculateSinglePrice(Graph graph) {
        if(taskType.equals(COMPILATION))
            return graph.getPricePerTargetComp();
        return  graph.getPricePerTargetSim();
    }

    public void startMission() {
        this.setStatus("Running");
        this.runTargets.addAll(missionGraph.getAdjTargets().keySet());

        for (Target t:runTargets) {
            t.setStatusInRun();
            if(t.getStatusInRun().equals(Target.RunningStatus.WAITING)){
                readyForRunTargets.add(t);
            }
        }
    }

    public void addWorker(String userName) {
        this.workers.add(userName);
    }

    public synchronized Target getTargetFromReadyToRun() {
        Target res = readyForRunTargets.get(0);
        readyForRunTargets.remove(res);
        return res;
    }

    public synchronized void removeFromGetRunTargets(String targetName) {
        for (Target t:runTargets){
            if(t.getTargetName().equals(targetName))
                runTargets.remove(t);
        }
    }

    public synchronized void  updateTargetLists() {
        frozenList.clear();
        waitingList.clear();
        skippedList.clear();
        inProcessList.clear();
        finishedList.clear();

        for (Target t: runTargets) {
            if(t.getStatusInRun().equals(Target.RunningStatus.FROZEN))
                frozenList.add(t.getTargetName());
            if(t.getStatusInRun().equals(Target.RunningStatus.WAITING))
                waitingList.add(t.getTargetName());
            if(t.getStatusInRun().equals(Target.RunningStatus.SKIPPED))
                skippedList.add(t.getTargetName());
            if(t.getStatusInRun().equals(Target.RunningStatus.IN_PROCESS))
                inProcessList.add(t.getTargetName());
            if(t.getStatusInRun().equals(Target.RunningStatus.FINISHED))
                finishedList.add(t.getTargetName());
        }
    }

    public synchronized Target getTargetFromRunTargetsByName(String targetName) {
        for(Target target: runTargets){
            if(target.getTargetName().equals(targetName)){
                return target;
            }
        }

        return null;
    }


    public synchronized void setRunResultOnTarget(TargetDTO t){
        getTargetFromRunTargetsByName(t.getTargetName()).setStatusInRun(t.getRunningStatus());
        getTargetFromRunTargetsByName(t.getTargetName()).setLastRunResult(t.getLastRunResult());
        getTargetFromRunTargetsByName(t.getTargetName()).setLastSuccessfulRun(t.getLastSuccessfulRun());
        getTargetFromRunTargetsByName(t.getTargetName()).setMyLog(t.getMylog());
    }


    public enum MissionStatus {
        SUCCESS, WARNING, FAILURE, SKIPPED
    }


    public List<String> getFrozenList() {
        return frozenList;
    }

    public void setFrozenList(List<String> frozenList) {
        this.frozenList = frozenList;
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public void setWaitingList(List<String> waitingList) {
        this.waitingList = waitingList;
    }

    public List<String> getSkippedList() {
        return skippedList;
    }

    public void setSkippedList(List<String> skippedList) {
        this.skippedList = skippedList;
    }

    public List<String> getInProcessList() {
        return inProcessList;
    }

    public void setInProcessList(List<String> inProcessList) {
        this.inProcessList = inProcessList;
    }

    public List<String> getFinishedList() {
        return finishedList;
    }

    public void setFinishedList(List<String> finishedList) {
        this.finishedList = finishedList;
    }

    private List<String> frozenList = new ArrayList();
    private List<String> waitingList = new ArrayList<>();
    private List<String> skippedList = new ArrayList<>();
    private List<String> inProcessList = new ArrayList<>();
    private List<String> finishedList = new ArrayList<>();
    public Task<Boolean> getTask() {
        return task;
    }

    public void setTask(Task<Boolean> task) {
        this.task = task;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public Graph getMissionGraph() {
        return missionGraph;
    }

    public void setMissionGraph(Graph missionGraph) {
        this.missionGraph = missionGraph;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getNumOfWorkers() {
        return numOfWorkers;
    }

    public synchronized void setNumOfWorkers() {
        this.numOfWorkers = numOfWorkers+1;
    }

    public int getNumOfTargets() {
        return numOfTargets;
    }

    public void setNumOfTargets(int numOfTargets) {
        this.numOfTargets = numOfTargets;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }


    private int calculatePrice(List<TargetDTO> targets, Graph graph) {
        if(taskType.equals(COMPILATION))
            return targets.size() * graph.getPricePerTargetComp();
        return targets.size() * graph.getPricePerTargetSim();
    }

    private void createMissionGraph(List<TargetDTO> targets, Graph graph) {
        List<String> targetsNames = targets.stream().map(TargetDTO::getTargetName).collect(Collectors.toList());
        this.missionGraph = new Graph();
        missionGraph.buildGraph(graph.getGraphName() + "-" + missionName,
                Gpup.getInstance().getTargetMapToBuildGraphMap().get(graph.getGraphName()));
        missionGraph = Gpup.getInstance().addDependenciesToTargets(missionGraph);

        for(Target t:graph.getAdjTargets().keySet()){
            if(!targetsNames.contains(t.getTargetName())) {
                missionGraph.removeTarget(t);
            }
            else{
                t.setMissionName(this.missionName);
                t.setMyTask(this);
            }
        }
    }



}
