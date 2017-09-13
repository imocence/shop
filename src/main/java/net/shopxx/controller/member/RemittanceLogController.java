package net.shopxx.controller.member;

import java.math.BigDecimal;
import java.util.Date;

import javax.inject.Inject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonView;

import net.shopxx.Pageable;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.Member;
import net.shopxx.entity.RemittanceLog;
import net.shopxx.entity.RemittanceLog.ConfirmStatus;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.RemittanceLogService;

/**
 * Controller - 汇款登记
 * 
 * @author cht
 * @version 1.0.0
 */
@Controller("memberRemittanceLogController")
@RequestMapping("/member/remittance_log")
public class RemittanceLogController extends BaseController{

	/**
	 * 每页记录数
	 */
	private static final int PAGE_SIZE = 10;

	@Inject
	private RemittanceLogService remittanceLogService;
	
	/**
	 * 登记
	 */
	@GetMapping("/add")
	public String add(@ModelAttribute(binding = false, name = "remittanceLog") RemittanceLog remittanceLog, Model model) {
		if (remittanceLog != null) {
			model.addAttribute("remittanceLog", remittanceLog);
		}
		return "member/remittance_log/add";
	}

	/**
	 * 登记
	 */
	@PostMapping("/add")
	public String add(BigDecimal amount,String identityCard, String memo, String name, String account, Date date,String number,String telephone,@CurrentUser Member currentUser,RedirectAttributes redirectAttributes) {
		if (!isValid(RemittanceLog.class, "identityCard", identityCard) || !isValid(RemittanceLog.class, "remittanceNumber", number)) {
			return UNPROCESSABLE_ENTITY_VIEW;
		}
		
		RemittanceLog _remittanceLog = new RemittanceLog();
		_remittanceLog.setMember(currentUser);
		_remittanceLog.setAmount(amount);
		_remittanceLog.setIdentityCard(identityCard);
		_remittanceLog.setMemo(memo);
		_remittanceLog.setName(name);
		_remittanceLog.setRemittanceAccount(account);
		_remittanceLog.setRemittanceDate(date);
		_remittanceLog.setRemittanceNumber(number);
		_remittanceLog.setTelephone(telephone);
		_remittanceLog.setConfirmStatus(ConfirmStatus.unconfirmed);
		remittanceLogService.save(_remittanceLog);
		
		addFlashMessage(redirectAttributes, "member.remittance_log.sendSuccess");
		return "redirect:list";
		
	}
	
	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Integer pageNumber, @CurrentUser Member currentUser, Model model) {
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		model.addAttribute("page", remittanceLogService.findPage(currentUser, pageable));
		return "member/remittance_log/list";
	}

	/**
	 * 列表
	 */
	@GetMapping(path = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@JsonView(BaseEntity.BaseView.class)
	public ResponseEntity<?> list(Integer pageNumber, @CurrentUser Member currentUser) {
		Pageable pageable = new Pageable(pageNumber, PAGE_SIZE);
		return ResponseEntity.ok(remittanceLogService.findPage(currentUser, pageable).getContent());
	}
}
