package net.shopxx.controller.admin;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.RemittanceLog;
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
}
