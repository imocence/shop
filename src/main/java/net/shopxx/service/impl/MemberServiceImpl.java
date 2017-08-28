/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.persistence.LockModeType;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.DepositLogDao;
import net.shopxx.dao.MemberDao;
import net.shopxx.dao.MemberRankDao;
import net.shopxx.dao.PointLogDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.DepositLog;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberRank;
import net.shopxx.entity.PointLog;
import net.shopxx.entity.User;
import net.shopxx.service.CountryService;
import net.shopxx.service.MailService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.service.SmsService;
import net.shopxx.util.TimeUtil;
import net.shopxx.util.WebUtils;


/**
 * Service - 会员
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class MemberServiceImpl extends BaseServiceImpl<Member, Long> implements MemberService {

	/**
	 * E-mail身份配比
	 */
	private static final Pattern EMAIL_PRINCIPAL_PATTERN = Pattern.compile(".*@.*");

	/**
	 * 手机身份配比
	 */
	private static final Pattern MOBILE_PRINCIPAL_PATTERN = Pattern.compile("\\d+");
	@Inject
	MemberRankService memberRankService;
	@Inject
	NapaStoresService napaStoresService;
	@Inject
	private MemberDao memberDao;
	@Inject
	private MemberRankDao memberRankDao;
	@Inject
	private DepositLogDao depositLogDao;
	@Inject
	private PointLogDao pointLogDao;
	@Inject
	private MailService mailService;
	@Inject
	private SmsService smsService;
	@Inject
	CountryService countryService;

	@Transactional(readOnly = true)
	public Member getUser(Object principal) {
		Assert.notNull(principal);
		Assert.isInstanceOf(String.class, principal);

		String value = (String) principal;
		if (EMAIL_PRINCIPAL_PATTERN.matcher(value).matches()) {
			return findByEmail(value);
		} else if (MOBILE_PRINCIPAL_PATTERN.matcher(value).matches()) {
			return findByMobile(value);
		} else {
			return findByUsername(value);
		}
	}

	@Transactional(readOnly = true)
	public Set<String> getPermissions(User user) {
		Assert.notNull(user);
		Assert.isInstanceOf(Member.class, user);

		return Member.PERMISSIONS;
	}

	@Transactional(readOnly = true)
	public boolean supports(Class<?> userClass) {
		return userClass != null && Member.class.isAssignableFrom(userClass);
	}

	@Transactional(readOnly = true)
	public boolean usernameExists(String username) {
		return memberDao.exists("username", StringUtils.lowerCase(username));
	}

	@Transactional(readOnly = true)
	public Member findByUsername(String username) {
		return memberDao.find("username", StringUtils.lowerCase(username));
	}


	@Transactional(readOnly = true)
	public boolean emailExists(String email) {
		return memberDao.exists("email", StringUtils.lowerCase(email));
	}

	@Transactional(readOnly = true)
	public boolean emailUnique(Long id, String email) {
		return memberDao.unique(id, "email", StringUtils.lowerCase(email));
	}

	@Transactional(readOnly = true)
	public Member findByEmail(String email) {
		return memberDao.find("email", StringUtils.lowerCase(email));
	}

	@Transactional(readOnly = true)
	public boolean mobileExists(String mobile) {
		return memberDao.exists("mobile", StringUtils.lowerCase(mobile));
	}

	@Transactional(readOnly = true)
	public boolean mobileUnique(Long id, String mobile) {
		return memberDao.unique(id, "mobile", StringUtils.lowerCase(mobile));
	}

	@Transactional(readOnly = true)
	public Member findByMobile(String mobile) {
		return memberDao.find("mobile", StringUtils.lowerCase(mobile));
	}
	
	/**
	 * 根据usercode查找会员
	 * 
	 * @param usercode
	 *            用户编号
	 * @return 会员，若不存在则返回null
	 */
	@Transactional(readOnly = true)
	public Member findByUsercode(String usercode){
		return memberDao.find("usercode", usercode);
	}

	@Transactional(readOnly = true)
	public Page<Member> findPage(Member.RankingType rankingType, Pageable pageable) {
		return memberDao.findPage(rankingType, pageable);
	}

	public void addBalance(Member member, BigDecimal amount, DepositLog.Type type, String memo) {
		Assert.notNull(member);
		Assert.notNull(amount);
		Assert.notNull(type);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		if (!LockModeType.PESSIMISTIC_WRITE.equals(memberDao.getLockMode(member))) {
			memberDao.flush();
			memberDao.refresh(member, LockModeType.PESSIMISTIC_WRITE);
		}

		Assert.notNull(member.getBalance());
		Assert.state(member.getBalance().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		member.setBalance(member.getBalance().add(amount));
		memberDao.flush();

		DepositLog depositLog = new DepositLog();
		depositLog.setType(type);
		depositLog.setCredit(amount.compareTo(BigDecimal.ZERO) > 0 ? amount : BigDecimal.ZERO);
		depositLog.setDebit(amount.compareTo(BigDecimal.ZERO) < 0 ? amount.abs() : BigDecimal.ZERO);
		depositLog.setBalance(member.getBalance());
		depositLog.setMemo(memo);
		depositLog.setMember(member);
		depositLogDao.persist(depositLog);
	}

	public void addPoint(Member member, long amount, PointLog.Type type, String memo) {
		Assert.notNull(member);
		Assert.notNull(type);

		if (amount == 0) {
			return;
		}

		if (!LockModeType.PESSIMISTIC_WRITE.equals(memberDao.getLockMode(member))) {
			memberDao.flush();
			memberDao.refresh(member, LockModeType.PESSIMISTIC_WRITE);
		}

		Assert.notNull(member.getPoint());
		Assert.state(member.getPoint() + amount >= 0);

		member.setPoint(member.getPoint() + amount);
		memberDao.flush();

		PointLog pointLog = new PointLog();
		pointLog.setType(type);
		pointLog.setCredit(amount > 0 ? amount : 0L);
		pointLog.setDebit(amount < 0 ? Math.abs(amount) : 0L);
		pointLog.setBalance(member.getPoint());
		pointLog.setMemo(memo);
		pointLog.setMember(member);
		pointLogDao.persist(pointLog);
	}

	public void addAmount(Member member, BigDecimal amount) {
		Assert.notNull(member);
		Assert.notNull(amount);

		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		if (!LockModeType.PESSIMISTIC_WRITE.equals(memberDao.getLockMode(member))) {
			memberDao.flush();
			memberDao.refresh(member, LockModeType.PESSIMISTIC_WRITE);
		}

		Assert.notNull(member.getAmount());
		Assert.state(member.getAmount().add(amount).compareTo(BigDecimal.ZERO) >= 0);

		member.setAmount(member.getAmount().add(amount));
		MemberRank memberRank = member.getMemberRank();
		if (memberRank != null && BooleanUtils.isFalse(memberRank.getIsSpecial())) {
			MemberRank newMemberRank = memberRankDao.findByAmount(member.getAmount());
			if (newMemberRank != null && newMemberRank.getAmount() != null && newMemberRank.getAmount().compareTo(memberRank.getAmount()) > 0) {
				member.setMemberRank(newMemberRank);
			}
		}
		memberDao.flush();
	}
	/**
	 * 验证登录
	 * @return
	 */
	@Override
	@Transactional
	public boolean verifyLogin(String usercode,String password,String urlPath,String urlSignature){
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("userCode", StringUtils.upperCase(usercode));
		parameterMap.put("password", DigestUtils.md5Hex("a"+password));
		parameterMap.put("signature", DigestUtils.md5Hex(TimeUtil.getFormatNowTime("yyyyMMdd")+urlSignature));
		//登录
		String userCodeMap = WebUtils.postJson(urlPath+"/verifyLoginToShop.html",parameterMap);
		JSONObject jsStr = JSONObject.fromObject(userCodeMap); 
		String errKey = jsStr.getString("errCode");
		if(!"0000".equals(errKey)){
			return false;
		}
			return true;
	}
	/**
	 * 获取多会员信息接口
	 * @return
	 */
	@Override
	@Transactional
	public List<Member> getListMember(String userCodes,String urlPath,String urlSignature){		
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("signature", DigestUtils.md5Hex(TimeUtil.getFormatNowTime("yyyyMMdd")+urlSignature));
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

			    String locale = job.get("company_code").toString();//国别
				String email = job.get("email").toString();
				String mobileMe = job.get("mobile").toString();
				String address = job.get("store_address").toString();//地址
				String napaCode = job.get("store_id").toString();//区代编码
				String mobile = job.get("store_mobile").toString();//区代电话
				int type = Integer.parseInt(job.get("type").toString());//类型id
				String user_name = job.get("user_name").toString();
				String user_code = job.get("user_code").toString();

				Member member = findByUsercode(user_code);

				member.setAddress(address);
				member.setCountry(countryService.findByName(locale));
				member.setMobile(mobileMe);
				member.setUsercode(user_code);
				member.setUsername(user_code);
				member.setPhone(mobileMe);
				member.setName(user_name);
				if(email.isEmpty()){
					member.setEmail(null);
				}else{
					member.setEmail(email);
				}				
				member.setMemberRank(memberRankService.find(Long.valueOf(type+1)));			    
				members.add(member);
			  }
			}
		}
		return members;
	}
	@Override
	@Transactional
	public Member save(Member member) {
		Assert.notNull(member);

		Member pMember = super.save(member);
		mailService.sendRegisterMemberMail(pMember);
		smsService.sendRegisterMemberSms(pMember);
		return pMember;
	}
	
	/**
	 * 根据编号和名称查找会员
	 * @param keyword
	 * @param country
	 * @param count
	 * @return
	 */
	public List<Member> search(String keyword, Country country, Integer count){
		return memberDao.search(keyword, country, count);
	}

}
