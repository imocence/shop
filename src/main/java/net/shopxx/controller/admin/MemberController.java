/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.controller.admin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.shopxx.Message;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.entity.Area;
import net.shopxx.entity.BaseEntity;
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.Member;
import net.shopxx.entity.Member.Gender;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.entity.NapaStores;
import net.shopxx.service.AreaService;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.MemberAttributeService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.service.UserService;
import net.shopxx.util.TimeUtil;
import net.shopxx.util.WebUtils;

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
	private static String FORATNOWTIME = TimeUtil.getFormatNowTime("yyyyMMdd");
	@Inject
	private MemberService memberService;
	@Inject
	private AreaService areaService;
	@Inject
	private UserService userService;
	@Inject
	private NapaStoresService napaStoresService;
	@Inject
	private MemberRankService memberRankService;
	@Inject
	private MemberAttributeService memberAttributeService;
	@Inject
	FiBankbookBalanceService fiBankbookBalanceService;
	
	@Inject
	FiBankbookJournalService fiBankbookJournalService;
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
		List<Member> memberList = getListMember("'"+member.getUsercode()+"'");
		member = memberList.get(0);
		model.addAttribute("genders", Member.Gender.values());
		model.addAttribute("memberAttributes", memberAttributeService.findList(true, true));
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
	@PostMapping("/setMember")
	public @ResponseBody JSONObject setMember(Member member,String companyCode, //国别
												String email,
												String mobile,
												String store_address,//地址
												String store_id,//区代编码
												String store_mobile,//区代电话
												String user_name,
												String userCode,
												Long type,//类型id
												String signature,//验证码
												HttpServletRequest request, RedirectAttributes redirectAttributes) {
		Map<String,Object> map = new HashMap<String, Object>();
		String errCode = "0000";
		
		member.setUsername(userCode);
		member.setUsercode(userCode);
		
		member.setPassword("123123");
		member.setEncodedPassword("123123");
		if(null == email){
			member.setEmail("greenleaf@qq.com");
		}
		if(type == null){
			member.setMemberRank(memberRankService.find(1L));
		}else{
			member.setMemberRank(memberRankService.find(type));
		}
		
		member.setIsEnabled(true);
		member.setLocale(companyCode);

		Long memberRankId = 1L;
		member.setMemberRank(memberRankService.find(memberRankId));
		
		
		if (!isValid(member, BaseEntity.Save.class)) {
			map.put("errCode", "2001");
			JSONObject jsonObject = JSONObject.fromObject(map.toString());
			return jsonObject;
		}

		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {
				map.put("errCode", "2001");
				JSONObject jsonObject = JSONObject.fromObject(map.toString());
				return jsonObject;
			}
			Object memberAttributeValue = memberAttributeService.toMemberAttributeValue(memberAttribute, values);
			member.setAttributeValue(memberAttribute, memberAttributeValue);
		}
		
		String signature0 = DigestUtils.md5Hex(FORATNOWTIME+urlSignature);
		if (!signature0.equals(signature)) {			
			errCode = "1001";
		}else{			
			
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
			member.setPointLogs(null);//n
			try {
				if(null == memberService.findByUsercode(userCode)){
					memberService.save(member);
					
					
					FiBankbookBalance balance1 = new FiBankbookBalance();
					balance1.setBalance(BigDecimal.ZERO);
					balance1.setType("1");
					balance1.setUserCode(userCode);
					fiBankbookBalanceService.save(balance1);
					
					FiBankbookBalance balance2 = new FiBankbookBalance();
					balance2.setBalance(BigDecimal.ZERO);
					balance2.setType("2");
					balance2.setUserCode(userCode);
					fiBankbookBalanceService.save(balance2);
					
				}else{
					memberService.update(member);
				}
				
			} catch (Exception e) {
				errCode = "2001";
			}
		}
		map.put("errCode", errCode);
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
			List<Member> newMemberList = getListMember(userCode);
			member.getContent().clear();
			member.getContent().addAll(newMemberList);
		}

		model.addAttribute("memberRanks", memberRankService.findAll());
		model.addAttribute("memberAttributes", memberAttributeService.findAll());
		model.addAttribute("page", member);
		return "admin/member/list";
	}
	/**
	 * 获取多会员信息接口
	 * @return
	 */
	public List<Member> getListMember(String userCodes){		
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("signature", DigestUtils.md5Hex(FORATNOWTIME+urlSignature));
		parameterMap.put("userCode", userCodes);
		System.out.println(parameterMap.toString());
		
		String userCodeMap = WebUtils.postJson(urlPath+"/getMemberInfoToShop.html",parameterMap);
		List<Member> members = new ArrayList<Member>();
		JSONObject jsStr = JSONObject.fromObject(userCodeMap); 
		String errKey = jsStr.getString("errCode");
		if(!"0000".equals(errKey)){
			return null;
		}else{
			String result = jsStr.getString("result");
			JSONArray memberJson = JSONArray.fromObject(result); 
			if(memberJson.size()>0){
			
			  for(int i=0;i<memberJson.size();i++){
			    JSONObject job = memberJson.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象

			    Member member = json(job);
			    members.add(member);
			  }
			}
		}
		return members;
	}
	/**
	 * 解析json中的数据
	 */
	public Member json(JSONObject jsonObject){

		String locale = jsonObject.get("company_code").toString();//国别
		String email = jsonObject.get("email").toString();
		String mobileMe = jsonObject.get("mobile").toString();
		String address = jsonObject.get("store_address").toString();//地址
		String napaCode = jsonObject.get("store_id").toString();//区代编码
		String mobile = jsonObject.get("store_mobile").toString();//区代电话
		Long id = Long.valueOf(jsonObject.get("type").toString());//类型id
		String user_name = jsonObject.get("user_name").toString();
		String user_code = jsonObject.get("user_code").toString();

		Member member = memberService.findByUsercode(user_code);//這個方法不是框架裡的嗎  我寫的我刚写的 好的 我看看

		member.setAddress(address);
		member.setLocale(locale);
		member.setMobile(mobileMe);
		member.setUsercode(user_code);
		member.setUsername(user_code);
		member.setPhone(mobileMe);
		member.setName(user_name);
		member.setEmail(email);
		member.setMemberRank(memberRankService.find(id));
		
		//区代
		NapaStores napaStores =napaStoresService.findByNapaCode(napaCode);
		member.setNapaStores(napaStores);
		
		return member;
		
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
