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
import javax.servlet.http.HttpServletRequest;

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
import net.shopxx.entity.FiBankbookBalance;
import net.shopxx.entity.FiBankbookBalance.Type;
import net.shopxx.entity.Member;
import net.shopxx.entity.MemberAttribute;
import net.shopxx.entity.NapaStores;
import net.shopxx.entity.PointLog;
import net.shopxx.entity.User;
import net.shopxx.service.CountryService;
import net.shopxx.service.FiBankbookBalanceService;
import net.shopxx.service.MailService;
import net.shopxx.service.MemberAttributeService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.service.SmsService;
import net.shopxx.util.TimeUtil;
import net.shopxx.util.WebUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


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
	@Value("${url.path}")
	private String urlPath;
	@Value("${url.signature}")
	private String urlSignature;
	@Inject
	MemberAttributeService memberAttributeService;
	@Inject
	FiBankbookBalanceService fiBankbookBalanceService;
	/**
	 * 根据会员邮箱、手机、会员编码查找会员信息
	 */
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
			/*List<Member> memberList = getListMember("'"+value+"'");
			if(memberList.size() > 0){
				return memberList.get(0);
			}else{
				return null;
			}*/
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

	public void addAmount(Member member, BigDecimal amount, BigDecimal couponAmount) {
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
		Assert.state(member.getCouponAmount().add(couponAmount).compareTo(BigDecimal.ZERO) >= 0);
		
		member.setAmount(member.getAmount().add(amount));
		member.setCouponAmount(member.getCouponAmount().add(couponAmount));
//		MemberRank memberRank = member.getMemberRank();
//		if (memberRank != null && BooleanUtils.isFalse(memberRank.getIsSpecial())) {
//			MemberRank newMemberRank = memberRankDao.findByAmount(member.getAmount());
//			if (newMemberRank != null && newMemberRank.getAmount() != null && newMemberRank.getAmount().compareTo(memberRank.getAmount()) > 0) {
//				member.setMemberRank(newMemberRank);
//			}
//		}
		memberDao.flush();
	}
	
	/**
	 * 验证登录
	 * @return
	 */
	@Override
	@Transactional
	public boolean verifyLogin(String usercode,String password){
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("userCode", StringUtils.upperCase(usercode));
		parameterMap.put("password", DigestUtils.md5Hex("a"+password));
		parameterMap.put("timestamp", TimeUtil.getTimestamp());
		parameterMap.put("signature", DigestUtils.md5Hex(TimeUtil.getTimestamp()+urlSignature));

		//登录
		String userCodeMap = WebUtils.postJson(urlPath+"/verifyLoginToShop.html",parameterMap);
		try {
			JSONObject jsStr = JSONObject.fromObject(userCodeMap);
			String errKey = jsStr.getString("errCode");
			if(!"0000".equals(errKey)){
				return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		} 
	}
	/**
	 * 获取多会员信息接口
	 * usercodes
	 * 	会员编码：'CN01505718','CN05787105','CN06181471','CN01165146','CN07281530','LD05376130','LD01906504'
	 * @return
	 */
	@Override
	@Transactional
	public List<Member> getListMember(String userCodes){		
		Map<String, Object> parameterMap = new HashMap<>();
		parameterMap.put("timestamp", TimeUtil.getTimestamp());
		parameterMap.put("signature", DigestUtils.md5Hex(TimeUtil.getTimestamp()+urlSignature));
		parameterMap.put("userCode", StringUtils.upperCase(userCodes));
		System.out.println(parameterMap);
		
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
				//更新区代信息
				NapaStores napaStores = napaStoresService.find(member.getNapaStores().getId());
				if(type == 0){
					napaStores.setNapaCode(null);
				}else{
					napaStores.setNapaCode(napaCode);
				}
				napaStores.setType(type);
				napaStores.setNapaAddress(address);
				napaStores.setMobile(mobile);
				napaStoresService.update(napaStores);
				
				member.setNapaStores(napaStores);		
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
	@Override
	@Transactional
	public List<Member> search(String keyword, Country country, Integer count){
		return memberDao.search(keyword, country, count);
	}
	/**
	 * 创建一个会员
	 * @param companyCode
	 * 			国家码
	 * @param userCode
	 * 			会员编码
	 * @param signature
	 * 			约定码：MD5加密，格式=时间戳+约定值
	 * @param timestamp
	 * 			时间戳
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public boolean create(Member member,String companyCode, String userCode, String signature,String timestamp,HttpServletRequest request, RedirectAttributes redirectAttributes)throws Exception{
		//区代账号创建
		NapaStores napaStores = new NapaStores();
		napaStores.setMobile(null);
		napaStores.setNapaCode(null);
		napaStores.setType(0);
		napaStores.setBalance(BigDecimal.ZERO);
		napaStoresService.save(napaStores);
		member.setNapaStores(napaStores);
		
		member.setUsername(userCode);
		member.setUsercode(userCode);
		
		member.setPassword("a123456");
		member.setEncodedPassword(DigestUtils.md5Hex("a123456"));
		member.setEmail(null);
		member.setMemberRank(memberRankService.find(1L));
		
		member.setIsEnabled(true);
		member.setCountry(countryService.findByName(companyCode));	
		
		System.out.println(countryService.findByName(companyCode).getName());
		member.removeAttributeValue();
		for (MemberAttribute memberAttribute : memberAttributeService.findList(true, true)) {
			String[] values = request.getParameterValues("memberAttribute_" + memberAttribute.getId());
			if (!memberAttributeService.isValid(memberAttribute, values)) {				
				napaStoresService.delete(napaStores);
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
		member.setPointLogs(null);//n
		
		if(null == findByUsercode(userCode)){				
			save(member);				
			//创建会员的存折
			FiBankbookBalance balance1 = new FiBankbookBalance();
			balance1.setBalance(BigDecimal.ZERO);
			balance1.setType(Type.balance);
			balance1.setMember(member);
			fiBankbookBalanceService.save(balance1);
			
			FiBankbookBalance balance2 = new FiBankbookBalance();
			balance2.setBalance(BigDecimal.ZERO);
			balance2.setType(Type.coupon);
			balance2.setMember(member);
			fiBankbookBalanceService.save(balance2);
			
		}else{
			napaStoresService.delete(napaStores);
			throw new IllegalArgumentException("保存失败,已有用户");
		}
		return true;
	}
}
