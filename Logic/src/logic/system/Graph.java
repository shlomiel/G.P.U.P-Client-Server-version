package logic.system;

import logic.DTO.TargetDTO;
import logic.utils.ObjectToDTO;

import java.util.*;

public class Graph {
    private final Map<Target, List<Target>> adjTargets = new HashMap<>();
    private String graphName;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private Map<TargetDTO, List<TargetDTO>> adjTargetsDTO = new HashMap<>();

    public synchronized boolean isLeafOrIndependent(Target targetName) { // true if so else false
        if(targetName == null)
            return false;
       return adjTargets.get(targetName).isEmpty();

    }

    public Map<TargetDTO, List<TargetDTO>> getAdjTargetsDTO() {
      return this.adjTargetsDTO;
    }

    public void setAdjTargetsDTO() {
        for (Target t: this.adjTargets.keySet()) {
            adjTargetsDTO.putIfAbsent(ObjectToDTO.fromTargetToDTO(t),
                    ObjectToDTO.fromTargetListToDTOList(adjTargets.get(t)));
        }
    }

    public List<TargetDTO> getTargetsDTO() {
        ArrayList<TargetDTO> targets = new ArrayList();
        for (Target t : adjTargets.keySet()) {
            targets.add(ObjectToDTO.fromTargetToDTO(t));
        }
        return targets;
    }


    public enum RoutSelection {
        REQUIRED_FOR,
        DEPENDS_ON
    }

    public enum RunningTimeType {
        RANDOM,
        FIXED
    }

    public synchronized Map<Target, List<Target>> getAdjTargets() {
        return adjTargets;
    }

    public void buildGraph(String graphName,String createdBy, Map<String, Target> targetsToBuildFrom, int priceSim, int priceComp) {
        this.graphName = graphName;
        this.userName = createdBy;
        this.pricePerTargetSim = priceSim;
        this.pricePerTargetComp = priceComp;
        adjTargets.clear();

        for (Target target : targetsToBuildFrom.values()) {
            addTarget(target);
        }

        setAdjTargetsDTO();
    }


    public void buildGraph(String graphName, Map<String, Target> targetsToBuildFrom) {
        this.graphName = graphName;
        for (Target target : targetsToBuildFrom.values()) {
            addTarget(target);
        }

        setAdjTargetsDTO();
    }




    protected Target getSingleTargetFromGraph(String targetName) {
        for (Target target: adjTargets.keySet()) {
            if (target.getTargetName().equals(targetName)) {
                return target;
            }
        }
        return null; // logic.Target does not exist in graph
    }

    public String getGraphName() {
        return graphName;
    }

    public void setGraphName(String graphName) {
        this.graphName = graphName;
    }

    public void setAdjTargetsDTO(Map<TargetDTO, List<TargetDTO>> adjTargetsDTO) {
        this.adjTargetsDTO = adjTargetsDTO;
    }

    public int getPricePerTargetSim() {
        return pricePerTargetSim;
    }

    public void setPricePerTargetSim(int pricePerTargetSim) {
        this.pricePerTargetSim = pricePerTargetSim;
    }

    public int getPricePerTargetComp() {
        return pricePerTargetComp;
    }

    public void setPricePerTargetComp(int pricePerTargetComp) {
        this.pricePerTargetComp = pricePerTargetComp;
    }

    private  int pricePerTargetSim;
    private int pricePerTargetComp;


    void addEdge(String source, String dest) {

        for (Target sourceTarget : adjTargets.keySet()) {
            if (sourceTarget.getTargetName().equals(source)) {
                for (Target destTarget : adjTargets.keySet()) {
                    if (destTarget.getTargetName().equals(dest)) {
                        if(!adjTargets.get(sourceTarget).contains(destTarget))
                            adjTargets.get(sourceTarget).add(destTarget);

                        sourceTarget.addTargetToRequiredFor(destTarget);
                        destTarget.addTargetToDependsOn(sourceTarget);
                        break;
                    }
                }
                break;
            }
        }
    }

    void addTarget(Target target) {
        adjTargets.putIfAbsent(target, new ArrayList<>());
    }



     public synchronized Target removeTarget(Target targetToRemove) {
        for (Target targetInGraph: adjTargets.keySet()) {
                targetInGraph.removeNeighborFromDepOn(targetToRemove);
                targetInGraph.removeNeighborFromReqFor(targetToRemove);
                if(adjTargets.get(targetInGraph).contains(targetToRemove)) {
                    adjTargets.get(targetInGraph).remove(targetToRemove);
                }

        }

        adjTargets.remove(targetToRemove);
        for (Target target:adjTargets.keySet()) {
            target.setStatusInGraph();
        }

        return targetToRemove;
    }


    public synchronized Target removeTarget(String targetToRemoveName) {
        return removeTarget(this.getSingleTargetFromGraph(targetToRemoveName));
    }


    protected int getCountOfIndependentNodes(){
        int counter = 0 ;
        for (Target target:adjTargets.keySet() ) {
            if (target.getStatusInGraph() == Target.GraphStatus.INDEPENDENT){
                counter++;
            }
        }
        return counter;
    }


    public int getCountOfRootNodes() {
        int counter = 0 ;
        for (Target target:adjTargets.keySet() ) {
            if (target.getStatusInGraph() == Target.GraphStatus.ROOT){
                counter++;
            }
        }
        return counter;
    }
    public int getCountOfLeafNodes() {
        int counter = 0 ;
        for (Target target:adjTargets.keySet() ) {
            if (target.getStatusInGraph() == Target.GraphStatus.LEAF){
                counter++;
            }
        }
        return counter;
    }

    public int getCountOfMiddleNodes() {
        int counter = 0 ;
        for (Target target:adjTargets.keySet() ) {
            if (target.getStatusInGraph() == Target.GraphStatus.MIDDLE){
                counter++;
            }
        }
        return counter;
    }




    public int getCountOfNodes() {
        return adjTargets.size();
    }

    public boolean isEmpty()
    {
        return adjTargets.isEmpty();
    }

    public void printGraph() {
        for (Target target : adjTargets.keySet()) {
            System.out.print(target.getTargetName() + "--->");

            if (!adjTargets.get(target).isEmpty()) {
                for (Target neighbor : adjTargets.get(target)) {
                    System.out.print(neighbor.getTargetName() + ' ');
                }
            }
            System.out.println();
        }
    }


    public void setNodesStatus() {
        for (Target target:adjTargets.keySet()) {
            target.setStatusInGraph();
            target.setAllDependsForNames(this);
            target.setAllRequiredForNames(this);
        }
    }


    public List<List<String>> findAllPaths(String sourceTarget, String destTarget, RoutSelection routDirection) {


        List<Target> neighbors;
        List<List<String>> result = new ArrayList<>();
        Queue<List<String>> queue = new LinkedList<> ();
        queue.add(Arrays.asList(sourceTarget));

        while(!queue.isEmpty()){
            List<String> path = queue.poll();
            String lastNode = path.get(path.size()-1);

            if(lastNode.equals(destTarget) && path.size() > 1){
                result.add(new ArrayList<>(path));
            }
            else{
                if(routDirection == RoutSelection.REQUIRED_FOR)
                    neighbors = getSingleTargetFromGraph(lastNode).getRequiredFor();
                else
                    neighbors = getSingleTargetFromGraph(lastNode).getDependsOn();


                for(Target neighbor : neighbors){
                    List<String> list = new ArrayList<>(path);
                    if(list.size() > 2*adjTargets.size()) // stuck in cycle
                        return result;
                    list.add(neighbor.getTargetName());
                    queue.add(list);

                }
            }
        }

        return result;
    }




public List<Target> getTopologicalSortOfNodes() {
        Map<Target,Integer> inDegree= new HashMap<>();
        Queue<Target> queue = new LinkedList<>();
        List<Target> result = new LinkedList<>();

        for(Target node:adjTargets.keySet()) {
            inDegree.putIfAbsent(node,node.getRequiredFor().size());
        }
        for (Target node:inDegree.keySet()) {
            if (inDegree.get(node) == 0) {
                queue.add(node);
            }
        }

        while(!queue.isEmpty()) {
            Target u = queue.poll();
            result.add(u);
            for (Target v: u.getDependsOn()) {
                inDegree.put(v, inDegree.get(v)-1 );
                if(inDegree.get(v) == 0){
                    queue.add(v);
                }
            }
        }

        return result;
    }

}


