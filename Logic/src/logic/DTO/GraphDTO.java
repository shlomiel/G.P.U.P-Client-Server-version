package logic.DTO;

import logic.system.Graph;

import java.util.*;

public class GraphDTO {

        private List<TargetDTO> targets;
        private String name;
        private String userName;
        private int numOfNodes;
        private int numOfIndependent;
        private int numOfRoot;
        private int numOfMiddle;
        private int numOfLeaf;
        private int priceSimulation;
        private int priceCompilation;

    public String getUserName() {
        return userName;
    }

    public List<TargetDTO> getTargets() {
        return targets;
    }

    public int getPriceSimulation() {
        return priceSimulation;
    }

    public int getPriceCompilation() {
        return priceCompilation;
    }

    public GraphDTO(Graph g){
            this.targets = g.getTargetsDTO();
            this.name = g.getGraphName();
            this.userName = g.getUserName();
            this.numOfNodes = g.getCountOfNodes();
            this.numOfLeaf = g.getCountOfLeafNodes();
            this.numOfRoot = g.getCountOfRootNodes();
            this.numOfMiddle = g.getCountOfMiddleNodes();
            this.numOfIndependent = numOfNodes - (numOfMiddle + numOfRoot + numOfLeaf);
            this.priceSimulation = g.getPricePerTargetSim();
            this.priceCompilation = g.getPricePerTargetComp();
        }


    public String getName() {
        return name;
    }

    public int getNumOfNodes() {
        return numOfNodes;
    }

    public int getNumOfIndependent() {
        return numOfIndependent;
    }

    public int getNumOfRoot() {
        return numOfRoot;
    }

    public int getNumOfMiddle() {
        return numOfMiddle;
    }

    public int getNumOfLeaf() {
        return numOfLeaf;
    }


        public String getGraphName() {
            return name;
        }

        public boolean isEmpty()
        {
            return targets.isEmpty();
        }


}
