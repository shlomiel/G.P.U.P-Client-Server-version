package logic.DTO;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import logic.system.Mission;
import logic.system.Target;
import logic.utils.ObjectToDTO;

import java.util.ArrayList;

public class TargetDTO {

    public long getStartProcess() {
        return startProcess;
    }

    public void setStartProcess(long startProcess) {
        this.startProcess = startProcess;
    }

    private long startProcess;

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    private  String missionName;
    private String targetName;
    private Target.GraphStatus location;
    private MissionDTO myTask;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    private int price;

    public long getLastSuccessfulRun() {
        return LastSuccessfulRun;
    }

    public void setLastSuccessfulRun(long lastSuccessfulRun) {
        LastSuccessfulRun = lastSuccessfulRun;
    }

    private long LastSuccessfulRun;

    public Target.RunResult getLastRunResult() {
        return lastRunResult;
    }

    public void setLastRunResult(Target.RunResult lastRunResult) {
        this.lastRunResult = lastRunResult;
    }

    private Target.RunResult lastRunResult;

    public Target.RunningStatus getRunningStatus() {
        return runningStatus;
    }

    public void setRunningStatus(Target.RunningStatus runningStatus) {
        this.runningStatus = runningStatus;
    }

    private Target.RunningStatus runningStatus;
    private ArrayList<String> directDepOn;
    private  ArrayList<String> allDepOn;
    private ArrayList<String> directReqFor;
    private  ArrayList<String> allReqFor;
    private String freeInfo;
    //private final BooleanProperty choosen = new SimpleBooleanProperty();
    private Boolean choosen;

    public String getMylog() {
        return mylog;
    }

    public void setMylog(String mylog) {
        this.mylog = mylog;
    }

    private String mylog = "";

    public String getTaskType() {
        return taskType;
    }

    private String taskType;

    public boolean isChoosen() {
        return choosen;
    }

    public Boolean choosenProperty() {
        return choosen;
    }

    public void setChoosen(boolean choosen) {
        this.choosen = (choosen);
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setLocation(Target.GraphStatus location) {
        this.location = location;
    }

    public MissionDTO getMyTask() {
        return myTask;
    }

    public void setMyTask(MissionDTO myTask) {
        this.myTask = myTask;
    }

    public void setDirectDepOn(ArrayList<String> directDepOn) {
        this.directDepOn = directDepOn;
    }

    public void setAllDepOn(ArrayList<String> allDepOn) {
        this.allDepOn = allDepOn;
    }

    public void setDirectReqFor(ArrayList<String> directReqFor) {
        this.directReqFor = directReqFor;
    }

    public void setAllReqFor(ArrayList<String> allReqFor) {
        this.allReqFor = allReqFor;
    }

    public void setFreeInfo(String freeInfo) {
        this.freeInfo = freeInfo;
    }

    public Boolean getChoosen() {
        return choosen;
    }

    public void setChoosen(Boolean choosen) {
        this.choosen = choosen;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public TargetDTO(Target t){
        this.targetName = t.getTargetName();
        this.location = t.getStatusInGraph();
        this.directDepOn = t.getDependsOnNames();
        this.allDepOn = t.getAllDependsForNames();
        this.directReqFor = t.getRequiredForNames();
        this.allReqFor = t.getAllRequiredForNames();
        this.freeInfo = t.getUserData();
        this.runningStatus = t.getStatusInRun();
        this.taskType = t.getTaskType();
        this.choosen = false;
        this.missionName = t.getMissionName();
        this.myTask = t.getMyTask();
        this.price = 0;
    }

    public String getTargetName() {
        return targetName;
    }

    public Target.GraphStatus getLocation() {
        return location;
    }

    public ArrayList<String> getDirectDepOn() {
        return directDepOn;
    }

    public ArrayList<String> getAllDepOn() {
        return allDepOn;
    }

    public ArrayList<String> getDirectReqFor() {
        return directReqFor;
    }

    public ArrayList<String> getAllReqFor() {
        return allReqFor;
    }

    public String getFreeInfo() {
        return freeInfo;
    }


    public MissionDTO getMission() {
        return myTask;
    }
}
