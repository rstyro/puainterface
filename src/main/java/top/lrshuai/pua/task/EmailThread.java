package top.lrshuai.pua.task;

import org.apache.log4j.Logger;

import top.lrshuai.pua.service.impl.MailService;


/**
 * 发送验证码线程
 * 
 * @author tyro
 *
 */
public class EmailThread implements Runnable {

    private MailService mailService;
	private String toAddress;
	private String title;
	private String content;
	private int index;
	private boolean end;

	private Logger log = Logger.getLogger(this.getClass());

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public MailService getMailService() {
		return mailService;
	}

	public void setMailService(MailService mailService) {
		this.mailService = mailService;
	}

	public EmailThread() {
		super();
	}

	public EmailThread(String toAddress, String title,String content) {
		super();
		this.toAddress = toAddress;
		this.title = title;
		this.end = false;
		this.index = 0;
		this.content = content;
	}
	

	public EmailThread(MailService mailService, String toAddress, String title, String content) {
		super();
		this.mailService = mailService;
		this.toAddress = toAddress;
		this.title = title;
		this.content = content;
		this.end = false;
		this.index = 0;
	}

	@Override
	public void run() {
		if (!isEnd()) {
			System.out.println("?");
			try {
				System.out.println("mailService="+mailService);
				mailService.sendHtmlMail(getToAddress(), getTitle(), getContent());
				this.setEnd(true);
				log.info("send mail is success");
			} catch (Exception e) {
				log.error("send mail err:" + e.getMessage(), e);
				this.setEnd(true);
				throw new RuntimeException("发送邮件失败", e);
			}
		} else {
			System.out.println("!!!");
		}
	}

}
