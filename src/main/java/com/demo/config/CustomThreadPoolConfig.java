package com.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class CustomThreadPoolConfig implements SchedulingConfigurer {

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(5);
		threadPoolTaskScheduler.setThreadNamePrefix("file-watcher");
		threadPoolTaskScheduler.initialize();
		threadPoolTaskScheduler.setAwaitTerminationSeconds(10);
		threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		// let's register our custom thread pool scheduler
		taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
	}

}
