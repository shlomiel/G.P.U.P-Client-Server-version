package logic.DTO;

import logic.system.Graph;
import logic.system.Mission;

import java.util.List;

public class MissionDTO {

    private List<String> frozenList;
    private List<String> waitingList;
    private List<String> inProcessList;
    private List<String> finishedList;
    private List<String> skippedList;
    //  private RadioButton selected;
    private String missionName;
    private String missionType;
    private String graphName;
    private String createdBy;
    private String myLog;

    public String getMyLog() {
        return myLog;
    }

    public void setMyLog(String myLog) {
        this.myLog = myLog;
    }

    private int numOfLeafs;
    private int numOfRoots;
    private int numOfIndis;
    private int numOfMiddle;
    private int totalPrice;
    private int numOfWorkers;
    private int numOfTargets;

    public int getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(int singlePrice) {
        this.singlePrice = singlePrice;
    }

    private int singlePrice;
    private String status;
    private long ms;
    private double successProb;
    private double warningProb;

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }

    public void setNumOfWorkers(int numOfWorkers) {
        this.numOfWorkers = numOfWorkers;
    }

    public long getMs() {
        return ms;
    }

    public void setMs(long ms) {
        this.ms = ms;
    }

    public double getSuccessProb() {
        return successProb;
    }

    public void setSuccessProb(double successProb) {
        this.successProb = successProb;
    }

    public double getWarningProb() {
        return warningProb;
    }

    public void setWarningProb(double warningProb) {
        this.warningProb = warningProb;
    }

    public Graph.RunningTimeType getRunSelection() {
        return runSelection;
    }

    public void setRunSelection(Graph.RunningTimeType runSelection) {
        this.runSelection = runSelection;
    }

    private Graph.RunningTimeType runSelection;


    public List<String> getFrozenList() {
        return frozenList;
    }

    public List<String> getWaitingList() {
        return waitingList;
    }

    public List<String> getInProcessList() {
        return inProcessList;
    }

    public List<String> getFinishedList() {
        return finishedList;
    }

    public List<String> getSkippedList() {
        return skippedList;
    }

    private String sourcePath;

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

    private String destPath;

    public MissionDTO(Mission m) {
        this.missionName = m.getMissionName();
        this.numOfTargets = m.getMissionGraph().getCountOfNodes();
        this.graphName = m.getMissionGraph().getGraphName();
        this.createdBy = m.getCreatedBy();
        this.numOfLeafs = m.getMissionGraph().getCountOfLeafNodes();
        this.numOfRoots = m.getMissionGraph().getCountOfRootNodes();
        this.numOfMiddle = m.getMissionGraph().getCountOfMiddleNodes();
        this.numOfIndis = numOfTargets -(numOfMiddle+numOfRoots+numOfLeafs);
        this.status = "new mission";
        this.numOfWorkers = m.getNumOfWorkers();
        this.totalPrice = m.getTotalPrice();
        this.missionType = m.getTaskType();
        this.singlePrice = m.getSinglePrice();

        this.frozenList = m.getFrozenList();
        this.waitingList = m.getWaitingList();
        this.inProcessList = m.getInProcessList();
        this.skippedList = m.getSkippedList();
        this.finishedList = m.getFinishedList();
        this.myLog = m.getMyLog();
        this.sourcePath = m.getSourcePath();
        this.destPath = m.getDestPath();

        if(this.missionType.contains("Simulation")){
            this.ms = m.getMs();
            this.successProb = m.getSuccessProb();
            this.warningProb = m.getWarningProb();
            this.runSelection = m.getRunTimeSelection();
        }
    }
    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public int getNumOfLeafs() {
        return numOfLeafs;
    }

    public void setNumOfLeafs(int numOfLeafs) {
        this.numOfLeafs = numOfLeafs;
    }

    public int getNumOfRoots() {
        return numOfRoots;
    }

    public void setNumOfRoots(int numOfRoots) {
        this.numOfRoots = numOfRoots;
    }

    public int getNumOfIndis() {
        return numOfIndis;
    }

    public void setNumOfIndis(int numOfIndis) {
        this.numOfIndis = numOfIndis;
    }

    public int getNumOfMiddle() {
        return numOfMiddle;
    }

    public void setNumOfMiddle(int numOfMiddle) {
        this.numOfMiddle = numOfMiddle;
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




}
