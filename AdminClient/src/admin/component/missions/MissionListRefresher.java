package admin.component.missions;


import admin.util.http.HttpClientUtil;
import admin.util.Constants;
import javafx.beans.property.BooleanProperty;
import logic.DTO.MissionDTO;
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

import static admin.util.Constants.GSON_INSTANCE;


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

        String finalUrl = HttpUrl
                .parse(Constants.MISSIONS_LIST)
                .newBuilder()
                .build()
                .toString();

        final int finalRequestNumber = ++requestNumber;
        //httpRequestLoggerConsumer.accept("About to invoke: " + Constants.USERS_LIST + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(finalUrl, new Callback() {

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
