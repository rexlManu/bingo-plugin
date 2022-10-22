package de.rexlmanu.bingo.bootstrap;

import de.rexlmanu.bingo.shared.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public interface PluginImplementation {

  default Plugin context() {
    return (Plugin) this;
  }

  default void runTask(Task task) {
    if (task.async()) {
      Bukkit.getScheduler().runTaskTimerAsynchronously(this.context(), task, 0, task.period());
      return;
    }
    Bukkit.getScheduler().runTaskTimer(this.context(), task, 0, task.period());
  }

  default void runLater(Runnable runnable, long l) {
    Bukkit.getScheduler().runTaskLater(this.context(), runnable, l);
  }
}
