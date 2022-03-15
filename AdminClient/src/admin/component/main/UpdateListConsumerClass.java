package admin.component.main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import logic.DTO.TargetDTO;
import logic.system.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class UpdateListConsumerClass implements Consumer {
        private TaskTabController controller;



        public UpdateListConsumerClass(TaskTabController controller){
            this.controller = controller;
        }

        @Override
        public void accept(Object o) {
            Map<String,List<String>> listMap = (Map<String, List<String>>) o; //maps status to a list of target in that status

       //     controller.frozenListt.setItems(setListTarget((Map<String, List<TargetDTO>>) o, Target.RunningStatus.FROZEN.toString()));
         //   controller.skippedList.setItems(setListTarget((Map<String, List<TargetDTO>>) o,Target.RunningStatus.SKIPPED.toString()));
           // controller.waitingList.setItems(setListTarget((Map<String, List<TargetDTO>>) o,Target.RunningStatus.WAITING.toString()));
           //controller.inProcessList.setItems(setListTarget((Map<String, List<TargetDTO>>) o,Target.RunningStatus.IN_PROCESS.toString()));
            //controller.finishedList.setItems(setListTarget((Map<String, List<TargetDTO>>) o,Target.RunningStatus.FINISHED.toString()));

            //controller.frozenListt.setItems( setList(listMap,Target.RunningStatus.FROZEN.toString())) ;
        }

    private ObservableList<TargetDTO> setListTarget(Map<String, List<TargetDTO>> listMap, String list) {
        if(!listMap.isEmpty())
            return FXCollections.observableList(listMap.get(list));
        return null;
    }

    private ObservableList<String> setList(Map<String, List<String>> listMap, String list) {
            if(!listMap.isEmpty())
                return FXCollections.observableList(listMap.get(list));
            return null;
        }


        private ObservableList<String> setFinishedList(Map<String,List<Target>> listMap, String list) {
            ArrayList<String> namesList = new ArrayList<>();
            for (Target t : listMap.get(list))
                namesList.add(t.getTargetName() + " " + t.getlastRunResult());


            return   FXCollections.observableList(namesList);
        }

}


