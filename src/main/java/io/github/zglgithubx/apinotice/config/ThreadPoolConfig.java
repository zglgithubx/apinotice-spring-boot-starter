package io.github.zglgithubx.apinotice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName ThreadPoolConfig
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2023/03/01 10:27
 */
@ConfigurationProperties(prefix = "abnormal.thread-pool")
public class ThreadPoolConfig {
	/** 核心线程数 */
	private int corePoolSize;

	/** 最大线程数 */
	private int maxPoolSize;

	/** 工作队列容量 */
	private int queueCapacity;

	/** 线程池维护线程所允许的空闲时间 */
	private int keepAliveSeconds;

	/** 拒绝策略 */
	private String rejectedExecutionHandler;

	@Bean
	public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(this.corePoolSize);
		executor.setMaxPoolSize(this.maxPoolSize);
		executor.setQueueCapacity(this.queueCapacity);
		executor.setKeepAliveSeconds(this.keepAliveSeconds);
		try {
			// 反射加载拒绝策略类
			Class clazz = Class.forName("java.util.concurrent.ThreadPoolExecutor$" + this.rejectedExecutionHandler);
			executor.setRejectedExecutionHandler((RejectedExecutionHandler) clazz.newInstance());
		} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
			e.printStackTrace();
			// 默认使用CallerRunsPolicy策略：直接在execute方法的调用线程中运行被拒绝的任务
			executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		}
		return executor;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public int getQueueCapacity() {
		return queueCapacity;
	}

	public void setQueueCapacity(int queueCapacity) {
		this.queueCapacity = queueCapacity;
	}

	public int getKeepAliveSeconds() {
		return keepAliveSeconds;
	}

	public void setKeepAliveSeconds(int keepAliveSeconds) {
		this.keepAliveSeconds = keepAliveSeconds;
	}

	public String getRejectedExecutionHandler() {
		return rejectedExecutionHandler;
	}

	public void setRejectedExecutionHandler(String rejectedExecutionHandler) {
		this.rejectedExecutionHandler = rejectedExecutionHandler;
	}
}

