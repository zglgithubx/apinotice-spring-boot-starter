package io.github.zglgithubx.apinotice.config;

import io.github.zglgithubx.apinotice.annotation.Notice;
import io.github.zglgithubx.apinotice.config.NoticeProperties;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;

/**
 * @ClassName ExceptionNotice
 * @Author ZhuGuangLiang <786945363@qq.com>
 * @Date 2022/10/13 09:11
 */
@Aspect
public class NoticeException {
	private JavaMailSender mailSender;
	private NoticeProperties noticeProperties;
	private String from;
	private ThreadPoolTaskExecutor executor;

	@Around("@annotation(notice)")
	public Object around(ProceedingJoinPoint pj, Notice notice) throws Throwable {
		long start = System.currentTimeMillis();
		Object proceed ;
		try {
			proceed= pj.proceed();
		} catch (RuntimeException e){
			//异步处理异常
			CompletableFuture.supplyAsync(()->{
				return getMessage(pj,e);
			},executor).whenComplete((res,throwable)->{
				if(!res.isEmpty()){
					concatError(start,res, pj);
				}
			}).exceptionally(throwable -> {
				System.err.println("【异步通知出现异常】："+throwable);
				return throwable.getMessage();
			});
			throw e;
		}
		return proceed;
	}

	private void concatError(long start, String message, ProceedingJoinPoint pj) {
		if(message.isEmpty()){
			return;
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<p>方法耗时：").append(System.currentTimeMillis() - start).append("ms</p>");
		stringBuilder.append(message);
		MethodSignature signature = (MethodSignature) pj.getSignature();
		Method method = signature.getMethod();
		Notice notice = method.getAnnotation(Notice.class);
		String to = notice.email();
		if(isValidEmail(to)){
			sendSimpleMail(to,stringBuilder.toString());
		}
	}

	private String getMessage(ProceedingJoinPoint pj, RuntimeException e) {
		MethodSignature signature = (MethodSignature) pj.getSignature();
		Method method = signature.getMethod();
		Notice notice=method.getAnnotation(Notice.class);
		String charger = "佚名";
		if (notice!=null && !StringUtils.isEmpty(notice.author())) {
			charger = notice.author().trim();
		}
		StringBuilder joiner = new StringBuilder("<p>责任人："+charger+"</p>")
				.append("<p>类名：").append(pj.getTarget().getClass().getName()+"</p>")
				.append("<p>方法名："+pj.getSignature().getName()+"</p>")
				.append("<p>参数：");
		Object[] args = pj.getArgs();
		List<Object> objects = new ArrayList<>();
		for (Object object : args) {
			if (object instanceof MultipartFile || object instanceof File) {
				continue;
			}
			objects.add(object);
		}
		if (objects!=null && !objects.isEmpty()) {
			joiner.append(Arrays.toString(objects.toArray())+"</p>");
		}else{
			joiner.append("无</p>");
		}
		if (Objects.nonNull(e)) {
			joiner.append("<p>异常信息：</p>").append(e);
		}
		return joiner.toString();
	}

	/**
	 * @Author ZhuGuangLiang <786945363@qq.com>
	 * @Description 发送通知给负责人
	 * @Date 2022/10/13 09:25
	 * @Param [to, subject, content]
	 * @return void
	 */

	public void sendSimpleMail(String to, String content) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setFrom(new InternetAddress(from, noticeProperties.getSender(), "UTF-8"));
			helper.setTo(to);
			helper.setSubject("方法异常提醒");
			helper.setText(content, true);
			mailSender.send(message);
		} catch (UnsupportedEncodingException | javax.mail.MessagingException e) {
			throw new RuntimeException("通知发送失败，请重新发送");
		}
	}

	/**
	 * @Author ZhuGuangLiang <786945363@qq.com>
	 * @Description 验证邮箱的有效性
	 * @Date 2022/10/14 21:26
	 * @Param [email]
	 * @return boolean
	 */
	public boolean isValidEmail(String email) {
		if ((email != null) && (!email.isEmpty())) {
			return Pattern.matches("^(\\w+([-.][A-Za-z0-9]+)*){3,18}@\\w+([-.][A-Za-z0-9]+)*\\.\\w+([-.][A-Za-z0-9]+)*$", email);
		}
		return false;
	}

	public void setExecutor(ThreadPoolTaskExecutor executor) { this.executor = executor; }
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}
	public void setNoticeProperties(NoticeProperties noticeProperties) {
		this.noticeProperties = noticeProperties;
	}
	public void setFrom(String from) {
		this.from = from;
	}
}
