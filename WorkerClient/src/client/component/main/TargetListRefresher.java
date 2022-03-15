package client.component.main;

import client.util.Constants;
import client.util.http.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import logic.DTO.MissionDTO;
import logic.DTO.TargetDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.util.Constants.*;

public class TargetListRefresher extends TimerTask {
    //private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<TargetDTO>> targetListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;
    private String username;
    private String finalUrl;
    private String missionName;
    private IntegerProperty numOfThreads;
    private mainSceneController myController;

    public TargetListRefresher(BooleanProperty shouldUpdate, Consumer<List<TargetDTO>> missionsListConsumer,
                               String username,IntegerProperty numOfThreads,mainSceneController controller) {
        this.shouldUpdate = shouldUpdate;
        this.targetListConsumer = missionsListConsumer;
        requestNumber = 0;
        this.username = username;
        this.numOfThreads = numOfThreads;
        this.myController = controller;

    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;

        this.finalUrl = HttpUrl
                .parse(GET_TARGETS_TO_WORK)
                .newBuilder()
                .addQueryParameter("userName", username)
                .addQueryParameter("numOfTargets", String.valueOf(numOfThreads.getValue()))
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
                TargetDTO[] targets = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, TargetDTO[].class);
                if(targets.length == 0){
                    System.out.println("[]");
                    return;
                }
                myController.addTargetsToRunTargets(targets);
                //myController.addTaskToMyTasks()
                for(TargetDTO t : targets)
                {
                    myController.addTaskToMyTasks(t.getMission());
                }
                myController.runTargets();
            }
        });
    }
}
