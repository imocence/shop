/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;

import net.sf.json.JSONObject;
import net.shopxx.Message;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.service.CountryService;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.MemberAttributeService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.service.UserService;
import net.shopxx.util.TimeUtil;

/**
 * Controller - 会员
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Controller("adminMemberController")
@RequestMapping("/admin/member")
public class MemberController extends BaseController {
	
	@Value("${url.path}")
	private String urlPath;
	@Value("${url.signature}")
	private String urlSignature;
	@Inject
	private MemberService memberService;
	@Inject
	private UserService userService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private MemberAttributeService memberAttributeService;
	@Inject
	FiBankbookBalanceService fiBankbookBalanceService;
	@Inject
	NapaStoresService napaStoresService;
	@Inject
	FiBankbookJournalService fiBankbookJournalService;
	@Inject
	CountryService countryService;
	/**
	 * 检查用户名是否存在
	 */
	@GetMapping("/check_username")
	public @ResponseBody boolean checkUsername(String username) {
		return StringUtils.isNotEmpty(username) && !memberService.usernameExists(username);
	}

	/**
	 * 检查E-mail是否唯一
	 */
	@GetMapping("/check_email")
	public @ResponseBody boolean checkEmail(Long id, String email) {
		return StringUtils.isNotEmpty(email) && memberService.emailUnique(id, email);
	}

	/**
	 * 检查手机是否唯一
	 */
	@GetMapping("/check_mobile")
	public @ResponseBody boolean checkMobile(Long id, String mobile) {
		return StringUtils.isNotEmpty(mobile) && memberService.mobileUnique(id, mobile);
	}

	/**
	 * 查看
	 */
	@GetMapping("/view")
	public String view(Long id, ModelMap model) {
		Member member = memberService.find(id);
		List<Member> memberList = memberService.getListMember("'"+member.getUsercode()+"'");
		member = memberList.get(0);
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("memberAttributes", memberAttributeService.findList(true, true));
		//会员存折
		model.addAttribute("fiBankbookBalanceList", fiBankbookBalanceService.findList(member,null,null,null,null));
		model.addAttribute("member", member);
		
		return "admin/member/view";
	}
	/**
	 * 添加
	 */
	@GetMapping("/add")
	public String add(ModelMap model) {
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("memberAttributes", memberAttributeService.findList(true, true));
		return "admin/member/add";
	}
	/**
	 * 保存
	 */
	@PostMapping(value="/setMember",produces = {"application/json;charset=utf-8"})
	public @ResponseBody JSONObject setMember(Member member,HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Map<String,Object> map = new HashMap<String, Object>();
		String errCode = "\"0000\"";		
		String state = "\"success\"";
				
		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
	        String line = null;
	        while((line = br.readLine())!=null){
	            sb.append(line);
	        }
		} catch (Exception e) {
			 System.out.println("获取post参数请求出现异常！" + e);
	         e.printStackTrace();
			 map.put("errCode", "\"2001\"");
			 map.put("state", "\"异常:\"");
			 JSONObject jsonObject = (JSONObject) JSON.parse(map.toString());
			 return jsonObject;
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map map1 = (Map) JSON.parse(sb.toString());
		String companyCode = map1.get("companyCode").toString();//国别
		String userCode = map1.get("userCode").toString();
		String signature = map1.get("signature").toString();//约定验证码
		String timestamp = map1.get("timestamp").toString();//时间戳

		String signature0 = DigestUtils.md5Hex(timestamp+urlSignature);
		if (!signature0.equals(signature)) {			
			errCode = "\"1001\"";
			state = "\"验签错误\"";
		}else{	
			try {
				memberService.create(member,companyCode,userCode,signature,timestamp,request,redirectAttributes);
				//流水号格式：类型首字母+时间
				String uniqueCode = "ZC"+TimeUtil.getFormatNowTime("yyyyMMddHHmmss");
				//添加一条注册赠送记录
				String success = fiBankbookJournalService.recharge(userCode, new BigDecimal("10000"), uniqueCode, FiBankbookJournal.Type.coupon, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.recharge, "用户注册赠送");
				if(!"success".equals(success)){
					System.out.println("注册赠送券未成功，提醒手动添加，会员编码为："+userCode);
				}
			} catch (Exception e) {
				e.printStackTrace();
				errCode = "\"2001\"";
				state = "\"保存失败\"";
			}
		}
		map.put("errCode", errCode);
		map.put("state", state);
		JSONObject jsonObject = JSONObject.fromObject(map.toString());
		return jsonObject;
	}
	/**
	 * 保存
	 */
	@PostMapping("/save")
	public String save(Member member, Long memberRankId, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		member.setMemberRank(memberRankService.find(memberRankId));
		if (!isValid(member, BaseEntity.Save.class)) {
			return ERROR_VIEW;
		}
		if (memberService.usernameExists(member.getUsername())) {
			return ERROR_VIEW;
		}
		if (memberService.emailExists(member.getEmail())) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(member.getMobile()) && memberService.mobileExists(member.getMobile())) {
			return ERROR_VIEW;
		}
		member.removeAttributeValue();
		System.out.println(memberAttributeService.findList(true, true));
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				return ERROR_VIEW;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		member.setPoint(0L);
		member.setBalance(BigDecimal.ZERO);
		member.setAmount(BigDecimal.ZERO);
		member.setIsLocked(false);
		member.setLockDate(null);
		member.setLastLoginIp(null);
		member.setLastLoginDate(null);
		member.setSafeKey(null);
		member.setCart(null);
		member.setOrders(null);
		member.setPaymentTransactions(null);
		member.setDepositLogs(null);
		member.setCouponCodes(null);
		member.setReceivers(null);
		member.setReviews(null);
		member.setConsultations(null);
		member.setProductFavorites(null);
		member.setProductNotifies(null);
		member.setInMessages(null);
		member.setOutMessages(null);
		member.setPointLogs(null);
		memberService.save(member);
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 编辑
	 */
	@GetMapping("/edit")
	public String edit(Long id, ModelMap model) {
		Member member = memberService.find(id);
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("memberAttributes", memberAttributeService.findList(true, true));
		model.addAttribute("member", member);
		return "admin/member/edit";
	}

	/**
	 * 更新
	 */
	@PostMapping("/update")
	public String update(Member member, Long id, Long memberRankId, Boolean unlock, HttpServletRequest request, RedirectAttributes redirectAttributes) {
		member.setMemberRank(memberRankService.find(memberRankId));
		if (!isValid(member)) {
			return ERROR_VIEW;
		}
		if (!memberService.emailUnique(id, member.getEmail())) {
			return ERROR_VIEW;
		}
		if (StringUtils.isNotEmpty(member.getMobile()) && !memberService.mobileUnique(id, member.getMobile())) {
			return ERROR_VIEW;
		}
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				return ERROR_VIEW;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		Member pMember = memberService.find(id);
		if (pMember == null) {
			return ERROR_VIEW;
		}
		if (BooleanUtils.isTrue(pMember.getIsLocked()) && BooleanUtils.isTrue(unlock)) {
			userService.unlock(member);
			memberService.update(member, "username", "encodedPassword", "point", "balance", "amount", "lastLoginIp", "lastLoginDate", "safeKey", "cart", "orders", "paymentTransactions", "depositLogs", "couponCodes", "receivers", "reviews", "consultations", "productFavorites", "productNotifies",
					"inMessages", "outMessages", "pointLogs");
		} else {
			memberService.update(member, "username", "encodedPassword", "point", "balance", "amount", "isLocked", "lockDate", "lastLoginIp", "lastLoginDate", "safeKey", "cart", "orders", "paymentTransactions", "depositLogs", "couponCodes", "receivers", "reviews", "consultations", "productFavorites",
					"productNotifies", "inMessages", "outMessages", "pointLogs");
		}
		addFlashMessage(redirectAttributes, Message.success(SUCCESS_MESSAGE));
		return "redirect:list";
	}

	/**
	 * 列表
	 */
	@GetMapping("/list")
	public String list(Pageable pageable, ModelMap model) {
		Page<Member> member = memberService.findPage(pageable);
		List<Member> memberList = member.getContent();
		String userCode = "";
		if(memberList != null && memberList.size() > 0){
			for(Member memberDes : memberList){
				userCode = userCode + "'" + memberDes.getUsercode()+"',";
			}
			userCode = userCode.substring(0,userCode.length() - 1);
			List<Member> newMemberList = memberService.getListMember(userCode);

			member.getContent().clear();
			member.getContent().addAll(newMemberList);
		}

		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("memberAttributes", memberAttributeService.findAll());

		model.addAttribute("page", member);
		return "admin/member/list";
	}
	/**
	 * 删除
	 */
	@PostMapping("/delete")
	public @ResponseBody Message delete(Long[] ids) {
		if (ids != null) {
			for (Long id : ids) {
				Member member = memberService.find(id);
				if (member != null && member.getBalance().compareTo(BigDecimal.ZERO) > 0) {
					return Message.error("admin.member.deleteExistDepositNotAllowed", member.getUsername());
				}
			}
			memberService.delete(ids);
		}
		return Message.success(SUCCESS_MESSAGE);
	}

}
