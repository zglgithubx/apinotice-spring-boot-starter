package io.github.zglgithubx.apinotice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;

/**
 * @ClassName com.github.apinotice.NoticeAutoConfiguration
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/10/13 17:01
 */
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(NoticeProperties.class)
public class NoticeAutoConfiguration {
	@Resource
	private JavaMailSender mailSender;
	@Autowired
	private NoticeProperties noticeProperties;
	@Value("${spring.mail.username}")
	private String from;

	@Bean
	public NoticeException noticeException(){
		NoticeException eN=new NoticeException();
		eN.setNoticeProperties(noticeProperties);
		eN.setFrom(from);
		eN.setMailSender(mailSender);
		return eN;
	}
}
