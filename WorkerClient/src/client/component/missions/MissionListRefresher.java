package client.component.missions;


import client.util.Constants;
import client.util.http.HttpClientUtil;
import javafx.beans.property.BooleanProperty;
import logic.DTO.MissionDTO;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

import static client.util.Constants.GSON_INSTANCE;


public class MissionListRefresher extends TimerTask {

    //private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<MissionDTO>> missionListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;


    public MissionListRefresher(BooleanProperty shouldUpdate, Consumer<List<MissionDTO>> missionsListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.missionListConsumer = missionsListConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {

        if (!shouldUpdate.get()) {
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        HttpClientUtil.runAsync(Constants.MISSIONS_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
              //  httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonArrayOfUsersNames = response.body().string();
                if(jsonArrayOfUsersNames.isEmpty()){
                    System.out.println("no graphs");
                    return;
                }
            //    httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);
                MissionDTO[] missions = GSON_INSTANCE.fromJson(jsonArrayOfUsersNames, MissionDTO[].class);
                missionListConsumer.accept(Arrays.asList(missions));
            }
        });
    }
}
