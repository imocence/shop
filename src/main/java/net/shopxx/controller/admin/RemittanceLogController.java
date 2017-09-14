package net.shopxx.controller.admin;

import java.util.Date;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.Results;
import net.shopxx.entity.Admin;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.RemittanceLog;
import net.shopxx.entity.RemittanceLog.ConfirmStatus;
import net.shopxx.security.CurrentUser;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.RemittanceLogService;

/**
 * Controller - 汇款登记
 * 
 * @author cht
 * @version 1.0.0
 */
@Controller("adminRemittanceLogController")
@RequestMapping("/admin/remittance_log")
public class RemittanceLogController extends BaseController {
	@Inject
	private RemittanceLogService remittanceLogService;
	
	@Inject 
	private FiBankbookJournalService fiBankbookJournalService;
	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable, Model model) {
		Page<RemittanceLog> page =  remittanceLogService.findPage(pageable);
		model.addAttribute("page",page);
		return "admin/remittance_log/list";
	}
	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody net.shopxx.Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				remittanceLogService.delete(id, null);
			}
		}
		return net.shopxx.Message.success(SUCCESS_MESSAGE);
	}
	/**
	 * 通过
	 * @throws Exception 
	 */
	@PostMapping("/confirmedPass")
	public ResponseEntity<?> confirmedPass(Long remittanceId, @CurrentUser Admin currentUser) throws Exception {
		if (remittanceId != null) {
			RemittanceLog _remittanceLog = remittanceLogService.find(remittanceId);
			_remittanceLog.setAdmin(currentUser);
			_remittanceLog.setConfirmDate(new Date());
			_remittanceLog.setConfirmStatus(ConfirmStatus.confirmedPass);
			remittanceLogService.update(_remittanceLog);
			
			fiBankbookJournalService.recharge(_remittanceLog.getMember().getUsercode(), _remittanceLog.getAmount(), null, FiBankbookJournal.Type.balance, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.offline, "RemittanceLog");
		}
		return Results.OK;
	}
	
	/**
	 * 不通过
	 */
	@PostMapping("/confirmedNoPass")
	public ResponseEntity<?> confirmedNoPass(Long remittanceId, @CurrentUser Admin currentUser) {
		if (remittanceId != null) {
			RemittanceLog _remittanceLog = remittanceLogService.find(remittanceId);
			_remittanceLog.setAdmin(currentUser);
			_remittanceLog.setConfirmDate(new Date());
			_remittanceLog.setConfirmStatus(ConfirmStatus.confirmedNoPass);
			remittanceLogService.update(_remittanceLog);
		}
		return Results.OK;
	}
}
