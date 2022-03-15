package logic.dataManagment;

import logic.DTO.MissionDTO;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MissionManager {
    private final Set<MissionDTO> missionSet;

    public MissionManager() {
        missionSet = new HashSet<>();
    }

    public synchronized void addMission(MissionDTO mission) {
        missionSet.add(mission);
    }

    public synchronized void removeMission(MissionDTO mission) {
        missionSet.remove(mission);
    }
    public synchronized Set<MissionDTO> getMissions() {
        return Collections.unmodifiableSet(missionSet);
    }

    public boolean isMissionExist(MissionDTO mission) {
        return missionSet.contains(mission);
    }

}
