package me.cocopopeater.util.tasks;

import me.cocopopeater.util.PlayerUtils;
import me.cocopopeater.util.varmanagers.GlobalColorRegistry;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimedCommandRunner {

    private final ScheduledExecutorService schedular = Executors.newScheduledThreadPool(1);
    private final Queue<Runnable> tasks = new LinkedList<>();

    public void addTask(Runnable task){
        tasks.add(task);
    }

    public void start(long interval, TimeUnit timeunit){
        Text startMsg = Text.literal("Executing %d tasks...".formatted(tasks.size())).withColor(GlobalColorRegistry.getLimeGreen());
        PlayerUtils.sendPlayerMessageChat(
                startMsg
        );

        schedular.scheduleAtFixedRate(() -> {
            if(!tasks.isEmpty()){
                Runnable task = tasks.poll();
                if(task != null){
                    task.run();
                }
            } else{
                schedular.shutdown();
                Text endMsg = Text.literal("Tasks Completed").withColor(GlobalColorRegistry.getLimeGreen());
                PlayerUtils.sendPlayerMessageChat(
                        endMsg
                );

            }
        }, 0, interval, timeunit);


    }
}
