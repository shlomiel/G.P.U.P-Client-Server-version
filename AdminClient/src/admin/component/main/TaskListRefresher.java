package admin.component.main;

import admin.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.collections.FXCollections;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static admin.util.Constants.*;

public class TaskListRefresher extends TimerTask {
    //private final Consumer<String> httpRequestLoggerConsumer;
    //private final Consumer<List<String>> frozenListConsumer;
    //private final Consumer<List<String>> frozenListConsumer;
   // private final Consumer<List<String>> waitingListConsumer;
   // private final Consumer<List<String>> inProcessListConsumer;
   // private final Consumer<List<String>> skippedListConsumer;
    //private final Consumer<List<String>> finishedListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;
    private String taskName;
    private String finalUrl;
    private TaskTabController myController;

    public TaskListRefresher(BooleanProperty shouldUpdate,
                             String taskName,  TaskTabController controller) {
        this.shouldUpdate = shouldUpdate;
        //this.frozenListConsumer = frozenListConsumer;
        requestNumber = 0;
        this.taskName = taskName;

        this.myController = controller;

    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;

        this.finalUrl = HttpUrl
                .parse(GET_MISSION_INFO)
                .newBuilder()
                .addQueryParameter("taskName", taskName)
                .build()
                .toString();

        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //  httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");

            }

            @Override
            public synchronized void  onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames = response.body().string();
                if(jsonArrayOfUsersNames.isEmpty()){
                    System.out.println("no targets");
                    return;
                }
                //    httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);
                MissionDTO msn = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, MissionDTO.class);

                //myController.updateLists(msn);
                myController.setMyTask(msn);


                Platform.runLater(
                        () -> {
                            myController.updateLists();
                        });
                //myController.updateLists();


            }
        });
    }
}
