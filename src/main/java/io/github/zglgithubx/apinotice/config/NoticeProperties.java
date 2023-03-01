package io.github.zglgithubx.apinotice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName AbnormalNoticeProperties
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/10/13 09:39
 */
@ConfigurationProperties(prefix = "abnormal")
public class NoticeProperties {
	/** 发件人名称 */
	private String sender="API助手";

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
}
