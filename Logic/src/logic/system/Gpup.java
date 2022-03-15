package logic.system;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import logic.DTO.TargetDTO;
import logic.exceptions.*;
import resources.generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import logic.tasks.CompilationTask;
import logic.tasks.SimulationTask;

//import userinterface.taskSceneController;

public class Gpup {

    public synchronized  void waitOnMyPause() throws InterruptedException {
        while(getPause().get() == true){
            wait();
        }
    }

    public synchronized AtomicBoolean getPause() {
        return pause;
    }

    private AtomicBoolean pause = new AtomicBoolean();


    private Graph systemGraph = new Graph();


    private final Map<String,Graph> systemGraphs = new HashMap<>();

    public Map<String, Mission> getSystemMissions() {
        return systemMissions;
    }

    private final Map<String,Mission> systemMissions = new HashMap<>();


    private String xmlFilePath;

    public Map<String, Target> getTargetMapToBuildGraph() {
        return targetMapToBuildGraph;
    }

    private Map<String, Target> targetMapToBuildGraph;

    public Map<String, Map<String, Target>> getTargetMapToBuildGraphMap() {
        return targetMapToBuildGraphMap;
    }

    private Map<String,Map<String, Target>> targetMapToBuildGraphMap = new HashMap<>();

    public Graph getIncrementalGraph() {
        return IncrementalGraph;
    }

    private final Graph IncrementalGraph = new Graph();

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    private String workingDirectory = "C:\\gpup-working-dir";

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    private boolean firstRun = true;
    private int maxParallelism;


    private Task<Boolean> currentRunningTask;
    //private taskSceneController currentController;




    private transient static final Gpup singleGpupInstance = new Gpup();
    private Gpup(){pause.set(false);}

    public synchronized static Gpup getInstance(){ // Singleton class
        return singleGpupInstance;
    }


    public ExecutorService executor;
    private GPUPDescriptor descriptionOfTargets;
    private Map<String,GPUPDescriptor> descriptorMap = new HashMap<>();



    public  void  buildGraphFromXml(StringBuilder fileContent, String createdBy) throws FileNotFoundException, JAXBException, WrongFileTypeException, TargetNameExistsException, UnknownTargetInDescpritionException, ConflictException, SerialSettNameExistsException {
        //this.xmlFilePath = filePath;

        verifyXmlIntegrity(fileContent);

        InputStream inputStream =  new ByteArrayInputStream(fileContent.toString().getBytes
                (Charset.forName("UTF-8")));


        this.descriptionOfTargets = deserializeFrom(inputStream);
        GPUPConfiguration gpupConfiguration = descriptionOfTargets.getGPUPConfiguration();
        String graphName = gpupConfiguration.getGPUPGraphName();
        this.descriptorMap.putIfAbsent(graphName,descriptionOfTargets);

        int pricePerTargetSimulation = 0;
        int pricePerTargetCompilation = 0;
        for (int i =0 ; i <gpupConfiguration.getGPUPPricing().getGPUPTask().size() ; i++) {
            if( gpupConfiguration.getGPUPPricing().getGPUPTask().get(i).getName().equals("Simulation"))
                pricePerTargetSimulation = gpupConfiguration.getGPUPPricing().getGPUPTask().get(i).getPricePerTarget();
            else
                pricePerTargetCompilation = gpupConfiguration.getGPUPPricing().getGPUPTask().get(i).getPricePerTarget();
        }

        Map<String, Target> targetMap = getTargetsFromDescriptor(descriptionOfTargets);
        buildSystemGraph(graphName, createdBy, targetMap, pricePerTargetSimulation, pricePerTargetCompilation);
        addDependenciesToTargets(targetMap,descriptionOfTargets);
        setAllTargetsStatus();
    }



    private void verifyXmlIntegrity(StringBuilder fileContent) throws FileNotFoundException, WrongFileTypeException, JAXBException,
            TargetNameExistsException, UnknownTargetInDescpritionException, ConflictException, SerialSettNameExistsException {



        //if( !filePath.startsWith(".xml", filePath.length()-4) ) // 2.1
          //  throw new WrongFileTypeException("Wrong file type, the required type is .xml");

        InputStream inputStream =  new ByteArrayInputStream(fileContent.toString().getBytes
                (Charset.forName("UTF-8")));
        Map<String,ArrayList<String>> targetsInFile = new HashMap<>();
        GPUPDescriptor descriptionOfTargets = deserializeFrom(inputStream);

        for (GPUPTarget gpupTarget : descriptionOfTargets.getGPUPTargets().getGPUPTarget()) { // 2.2
            if (targetsInFile.containsKey(gpupTarget.getName())) {
                throw new TargetNameExistsException("Failed loading file, this xml file contains two targets with the same name ");
            }

            targetsInFile.put(gpupTarget.getName(), new ArrayList<>());
        }
        for(GPUPTarget gpupTarget: descriptionOfTargets.getGPUPTargets().getGPUPTarget()){
            if(gpupTarget.getGPUPTargetDependencies()!=null) { //2.3
                for (GPUPTargetDependencies.GPUGDependency dependencyTarget : gpupTarget.getGPUPTargetDependencies().getGPUGDependency()) {
                    if (!targetsInFile.containsKey(dependencyTarget.getValue())) {
                        throw new UnknownTargetInDescpritionException("Failed loading file, This xml file contains an undeclared target named " + dependencyTarget.getValue() +
                                " in target "+ gpupTarget.getName() + " dependency list ");
                    }

                    if (dependencyTarget.getType().equals("requiredFor")) {
                        if (targetsInFile.get(dependencyTarget.getValue()).contains(gpupTarget.getName() + dependencyTarget.getType())) {
                            throw new ConflictException("Failed loading file, if target " + gpupTarget.getName() + " is requiredFor " + dependencyTarget.getValue() +
                                    " than " + dependencyTarget.getValue() + " can’t be requiredFor " + gpupTarget.getName());
                        }
                    } else {
                        if (targetsInFile.get(dependencyTarget.getValue()).contains(gpupTarget.getName() + dependencyTarget.getType())) {
                            throw new ConflictException("Failed loading file, if target " + gpupTarget.getName() + " dependsOn  " + dependencyTarget.getValue() +
                                    " then " + dependencyTarget.getValue() + " can’t be dependsOn  " + gpupTarget.getName());
                        }
                    }

                    targetsInFile.get(gpupTarget.getName()).add(dependencyTarget.getValue()+dependencyTarget.getType());
                }
            }
        }
    }

    private void setAllTargetsStatus() {
        systemGraph.setNodesStatus();
    }

    private Map<String, Target>  getTargetsFromDescriptor(GPUPDescriptor descriptionOfTargets) {
        Map<String, Target> targetMap= new HashMap<>();

        String type = determineType(descriptionOfTargets);
        for (GPUPTarget GPUPtarget: descriptionOfTargets.getGPUPTargets().getGPUPTarget()) {
            targetMap.putIfAbsent(GPUPtarget.getName(),
                    new Target(GPUPtarget.getName().toUpperCase(), GPUPtarget.getGPUPUserData(),type ));
        }

        return targetMap;
    }

    private String determineType(GPUPDescriptor descriptionOfTargets) {
        String res = "";

        for ( int i = 0 ;i<descriptionOfTargets.getGPUPConfiguration().getGPUPPricing().getGPUPTask().size();i++){
         res += (" " +   descriptionOfTargets.getGPUPConfiguration().getGPUPPricing().getGPUPTask().get(i).getName());
        }
        return res;
    }

    private void buildSystemGraph(String graphName,String createdBy, Map<String, Target> targetMap, int pricePerTargetSim, int pricePerTargetComp) {
        this.targetMapToBuildGraph = targetMap;
        this.targetMapToBuildGraphMap.putIfAbsent(graphName,targetMap);
        systemGraph = new Graph();
        systemGraph.buildGraph(graphName,createdBy, targetMap, pricePerTargetSim, pricePerTargetComp);
        IncrementalGraph.buildGraph(graphName,createdBy, targetMap, pricePerTargetSim, pricePerTargetComp);
        systemGraphs.putIfAbsent(systemGraph.getGraphName(),systemGraph);
    }

    private  void addDependenciesToTargets(Map<String, Target> targets, GPUPDescriptor descriptionOftargets) {

        GPUPTargets targetsFromFile = descriptionOftargets.getGPUPTargets();

        for (Target target : targets.values()) {
            for (GPUPTarget GPUPtarget : targetsFromFile.getGPUPTarget()) {
                if (target.getTargetName().equals(GPUPtarget.getName()) && GPUPtarget.getGPUPTargetDependencies() != null) {
                    for (GPUPTargetDependencies.GPUGDependency gpupTargetDep : GPUPtarget.getGPUPTargetDependencies().getGPUGDependency()) {
                        if (gpupTargetDep.getType().equals("requiredFor")) {
                            systemGraph.addEdge(gpupTargetDep.getValue(), target.toString());
                            IncrementalGraph.addEdge(gpupTargetDep.getValue(), target.toString());
                        } else {
                            systemGraph.addEdge(target.toString(), gpupTargetDep.getValue());
                            IncrementalGraph.addEdge(target.toString(), gpupTargetDep.getValue());
                        }
                    }
                    break;
                }
            }
        }
    }


    public Graph addDependenciesToTargets(Graph graph) {

            GPUPTargets targetsFromFile = descriptorMap.get(graph.getGraphName().
                    split("-")[0]).getGPUPTargets();
            //GPUPTargets targetsFromFile = descriptionOfTargets.getGPUPTargets();

            for (Target target : this.targetMapToBuildGraph.values()) {
                for (GPUPTarget GPUPtarget : targetsFromFile.getGPUPTarget()) {
                    if (target.getTargetName().equals(GPUPtarget.getName()) && GPUPtarget.getGPUPTargetDependencies() != null) {
                        for (GPUPTargetDependencies.GPUGDependency gpupTargetDep : GPUPtarget.getGPUPTargetDependencies().getGPUGDependency()) {
                            if (gpupTargetDep.getType().equals("requiredFor")) {
                                graph.addEdge(gpupTargetDep.getValue(), target.toString());
                                graph.addEdge(gpupTargetDep.getValue(),target.toString());
                            }

                            else {
                                graph.addEdge(target.toString(), gpupTargetDep.getValue());
                                graph.addEdge(target.toString(),gpupTargetDep.getValue());
                            }
                        }
                        break;
                    }
                }
            }
            return graph;
    }




    private static GPUPDescriptor deserializeFrom(InputStream in) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("resources.generated");
        Unmarshaller u = jc.createUnmarshaller();
        return (GPUPDescriptor) u.unmarshal(in);
    }

    public int getTotalCountOfTargets() {
        return systemGraph.getCountOfNodes();
    }

    public boolean getFirstRun()
    {
        return firstRun;
    }

    public int getCountOfIndependentTargets() {
        return systemGraph.getCountOfIndependentNodes();
    }

    public int getCountOfRootTargets() {
        return systemGraph.getCountOfRootNodes();
    }

    public int getCountOfLeafTargets() {
        return systemGraph.getCountOfLeafNodes();
    }

    public int getCountOfMiddleTargets() {
        return systemGraph.getCountOfMiddleNodes();
    }


    public ArrayList<String> getTargetDependsOnList(String targetName)  {
        Target targetInstance = systemGraph.getSingleTargetFromGraph(targetName);
        if(targetInstance == null)
            return null;

        ArrayList<String> depOnList = new ArrayList<>();
        for (Target neighbor: targetInstance.getDependsOn() ) {
            depOnList.add(neighbor.getTargetName());
        }
        return depOnList;
    }

    public ArrayList<String> getTargetRequiredForList(String targetName) {
        Target targetInstance = systemGraph.getSingleTargetFromGraph(targetName);
        if(targetInstance == null)
            return null;

        ArrayList<String> reqForList = new ArrayList<>();
        for (Target neighbor: targetInstance.getRequiredFor() ) {
            reqForList.add(neighbor.getTargetName());
        }
        return reqForList;

    }

    public String getTargetStatusInGraph(String targetName) {
        Target targetInstance = systemGraph.getSingleTargetFromGraph(targetName);
        return targetInstance.getStatusInGraph().toString();
    }

    public boolean includesTarget(String targetName) {
        return systemGraph.getSingleTargetFromGraph(targetName) != null;
    }

    public List<String> findRoutesBetweenTwoTargets(String graph, String sourceTarget, String destTarget, String routSelection) {
        Graph.RoutSelection route;
        if(routSelection.toString().equals("REQUIRED_FOR")){ route = Graph.RoutSelection.REQUIRED_FOR; }
        else{ route = Graph.RoutSelection.DEPENDS_ON; }

        Graph graphToRunOn = systemGraphs.get(graph);

        List<List<String>> allPaths = graphToRunOn.findAllPaths(sourceTarget,destTarget, route);
        ArrayList<String> res = new ArrayList<>();
        for (List<String> lst : allPaths)
            res.add(lst.toString());

        return res;
    }







       /* public void runSimulationTask(taskSceneController controller, int ms, Graph.RunningTimeType runTimeSelectionEnum,
                                      double successProb, double successWithWarningsProb, Consumer<String> consumer,
                                      int incOrScratch, int numOfThreads, ArrayList<Target> targetsForRun)
                throws InterruptedException, IOException, ParseException {

            currentRunningTask = new SimulationTask(controller,ms, runTimeSelectionEnum, successProb,
                    successWithWarningsProb, consumer, incOrScratch,numOfThreads, targetsForRun);
            currentController = controller;
            currentController.bindTaskToUIComponents(currentRunningTask);//onFinish);

            new Thread(currentRunningTask).start();

        }
*/

    public void setIncrementalGraph(List<Target> runOrder) {
        for (Target target: runOrder) {
            if (target.getlastRunResult() == Target.RunResult.SUCCESS || target.getlastRunResult() == Target.RunResult.WARNING)
                IncrementalGraph.removeTarget(target);
        }

    }

     public void printGeneralInfoOnRun(List<Target> runOrder, String startDateString, Consumer<String> consumer) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        startDateString = startDateString.split(" ")[1];
        startDateString = startDateString.replace(".",":");

        String endDateString = df.format(new Date());
        Date startDate, endDate;

        startDate = df.parse(startDateString);
        endDate = df.parse(endDateString);

        //difference in milliseconds
        long diff = endDate.getTime() - startDate.getTime();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        String taskTime = df.format(new Date(diff));

        consumer.accept("---------------------------------------------------------------------------------");
        int skippedTargets = getTargetCount(runOrder, Target.RunResult.SKIPPED);
        int failedTargets = getTargetCount(runOrder, Target.RunResult.FAILURE);
        int successfulTargets = getTargetCount(runOrder, Target.RunResult.SUCCESS);
        int warningTargets = getTargetCount(runOrder, Target.RunResult.WARNING);

        consumer.accept("Run ended with " + skippedTargets + " targets " + Target.RunResult.SKIPPED + ", " +
                 failedTargets + " targets " + Target.RunResult.FAILURE + ", " +
                successfulTargets + " targets " + Target.RunResult.SUCCESS + ", " + warningTargets + " targets " + Target.RunResult.WARNING);
        consumer.accept("The task ran for: "+ taskTime);
        for (Target target: runOrder) {
            consumer.accept("logic.Target: " + target.getTargetName() + " Result of run: " + target.getlastRunResult() + " task ran on target for: " + df.format(new Date(target.getLastSuccessfulRun()))) ;
        }
        if(IncrementalGraph.isEmpty()) {
            consumer.accept("All of the Targets were successfully simulated, you cannot use incremental run next.");
            firstRun = true;
        }
        consumer.accept("---------------------------------------------------------------------------------");

    }

    private int getTargetCount(List<Target> runOrder, Target.RunResult resultOfRun) {
        int res = 0;
        for (Target target: runOrder) {
            if(target.getlastRunResult() == resultOfRun)
                res++;
        }
        return res;
    }

    public Graph getSystemGraph() {
        return systemGraph;

    }


    public ObservableList<Target> getTargetList() {
        ArrayList<Target> targetList = new ArrayList<Target>(systemGraph.getAdjTargets().keySet());
        ObservableList<Target> observableList = FXCollections.observableList(targetList);
        return observableList;


    }


    public Target getTarget(String targetName) {
        return systemGraph.getSingleTargetFromGraph(targetName);
    }


    public String getWorkingDirectory() {
            return this.workingDirectory;
    }

    public void runCompilationTask(Consumer<String> consumer,
                                   int incOrScratch, int numOfThreads, String sourcePath, String destPath,
                                   ArrayList<Target> targetsForRun)
            throws InterruptedException, IOException, ParseException {

        //currentRunningTask = new CompilationTask(controller, consumer, incOrScratch,numOfThreads, sourcePath, destPath,targetsForRun);
        //currentController = controller;
        //currentController.bindTaskToUIComponents(currentRunningTask);//onFinish);

        new Thread(currentRunningTask).start();


    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public synchronized void unpauseSystem() {
        pause.compareAndSet(true,false);
        notifyAll();
    }

    @Override
    public String toString() {
        return "Gpup{" +
                "graph_name='" + Gpup.getInstance().getSystemGraph().getGraphName() + '\'' +
                ", total_targets=" + Gpup.getInstance().getTotalCountOfTargets() +
                ", independent=" + Gpup.getInstance().getCountOfIndependentTargets() +
                ", leaf=" + Gpup.getInstance().getCountOfLeafTargets() +
                ", root=" + Gpup.getInstance().getCountOfRootTargets() +
                ", middle=" + Gpup.getInstance().getCountOfMiddleTargets() +
                ", targets_list=" + Gpup.getInstance().getTargetList() +
                '}';
    }

    public Map<String,Graph> getSystemGraphs() {
        return systemGraphs;
    }

    public Mission addMission(List<TargetDTO> targets, String graphName, String taskName, String taskType, String username) {
        this.systemMissions.putIfAbsent(taskName, new Mission(targets, this.systemGraphs.get(graphName),
                taskName, taskType, username));
        return systemMissions.get(taskName);
    }

    public void startMission(String missionName) {
       systemMissions.get(missionName).startMission();

    }

    public Mission addMission(List<TargetDTO> targets, String graphName, String taskName, String taskType,
                              String username, int ms, String runTimeSelection,
                              double successProb, double warningProb) {
        this.systemMissions.putIfAbsent(taskName, new Mission(targets, this.systemGraphs.get(graphName),
                taskName, taskType, username, ms, runTimeSelection, successProb, warningProb));
        return systemMissions.get(taskName);
    }

    public Mission addMission(List<TargetDTO> targets, String graphName, String taskName, String taskType,
                              String username, String source, String dest) {
        this.systemMissions.putIfAbsent(taskName,new Mission(targets,this.systemGraphs.get(graphName),
                taskName,taskType,username,source,dest));
        return systemMissions.get(taskName);
    }
}
