package logic.utils;

import logic.DTO.GraphDTO;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import logic.system.Graph;
import logic.system.Mission;
import logic.system.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class ObjectToDTO {

    public static GraphDTO fromGraphToDTO(Graph g){
        return new GraphDTO(g);
    }

    public static MissionDTO fromMissionToDTO(Mission m){
        return new MissionDTO(m);
    }


    public static TargetDTO fromTargetToDTO(Target t){
        return new TargetDTO(t);
    }

    public static ArrayList<TargetDTO> fromTargetListToDTOList(List<Target> lst){
        ArrayList<TargetDTO> DTOarr = new ArrayList<>();
        for (Target t : lst) {
            if(!DTOarr.contains(fromTargetToDTO(t)))
                DTOarr.add(fromTargetToDTO(t));
        }
        return DTOarr;
    }


    public static ArrayList<TargetDTO> fromTargetListToDTOList(ArrayBlockingQueue<Target> lst){
        ArrayList<TargetDTO> DTOarr = new ArrayList<>();
        for (Target t : lst) {
            if(!DTOarr.contains(fromTargetToDTO(t)))
                DTOarr.add(fromTargetToDTO(t));
        }
        return DTOarr;
    }

}
