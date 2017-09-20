package net.shopxx.controller.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.shopxx.entity.FiBankbookJournal;
import net.shopxx.entity.Member;
import net.shopxx.entity.NapaStores;
import net.shopxx.service.FiBankbookJournalService;
import net.shopxx.service.MemberRankService;
import net.shopxx.service.MemberService;
import net.shopxx.service.NapaStoresService;
import net.shopxx.util.TimeUtil;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;

/**
 * Controller - 区代
 * 
 * @author sihao
 * @version 5.0.3
 */
@Controller("adminNapaStoresController")
@RequestMapping("/admin/store")
public class NapaStoresController extends BaseController{
	@Inject
	NapaStoresService napaStoresService;
	@Inject
	MemberService memberService;
	@Inject
	MemberRankService memberRankService;
	@Value("${url.signature}")
	private String urlSignature;
	@Inject
	FiBankbookJournalService fiBankbookJournalService;
	/**
	 * 跟新区代信息
	 */
	@PostMapping(value="/setStore",produces = {"application/json;charset=utf-8"})
	public @ResponseBody JSONObject setStore(NapaStores napaStores,HttpServletRequest request, RedirectAttributes redirectAttributes){
		Map<String,Object> map = new HashMap<String, Object>();
		String errCode = "\"0000\"";
		String state = "\"success\"";

		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
	        String line = null;
	        while((line = br.readLine())!=null){
	            sb.append(line);
	        }
		} catch (Exception e) {
			 //System.out.println("获取post参数请求出现异常！" + e);
	         e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map map1 = (Map) JSON.parse(sb.toString());
		String store_id = map1.get("storeId").toString();//区代编码
		String userCode = map1.get("userCode").toString();
		String type = map1.get("type").toString();//类型id
		String signature = map1.get("signature").toString();//约定验证码
		String timestamp = map1.get("timestamp").toString();//时间戳
		
		
		String signature0 = DigestUtils.md5Hex(timestamp+urlSignature);
		if (!signature0.equals(signature)) {			
			errCode = "\"1001\"";
			state = "\"验签错误\"";
		}else{
			//区代
			Member member = memberService.findByUsercode(userCode);	
			napaStores = member.getNapaStores();
			
			if(Integer.parseInt(type) == 0){
				napaStores.setNapaCode(null);
			}else{
				napaStores.setNapaCode(store_id);
			}
			napaStores.setMobile(null);
			napaStores.setNapaAddress(null);
			napaStores.setType(Integer.parseInt(type));
			napaStoresService.update(napaStores);
			member.setNapaStores(napaStores);
			memberService.update(member);	
		}
		map.put("errCode", errCode);
		map.put("state", state);
		JSONObject jsonObject = JSONObject.fromObject(map.toString());
		return jsonObject;
	}
	/**
	 * 赠送积分接口
	 * @throws Exception 
	 */
	@PostMapping(value="/increase",produces = {"application/json;charset=utf-8"})
	public @ResponseBody JSONObject increase(Member member,HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception{
		Map<String,Object> map = new HashMap<String, Object>();
		String errCode = "\"0000\"";
		String state = "\"success\"";

		StringBuilder sb = new StringBuilder();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
	        String line = null;
	        while((line = br.readLine())!=null){
	            sb.append(line);
	        }
		} catch (Exception e) {
			 //System.out.println("获取post参数请求出现异常！" + e);
	         e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Map map1 = (Map) JSON.parse(sb.toString());
		String jifen = map1.get("jifen").toString();//赠送券数
		String type = map1.get("type").toString();//类型id,1服务中心送券，3加盟店送券
		String userCode = map1.get("userCode").toString();
		String signature = map1.get("signature").toString();//约定验证码
		String timestamp = map1.get("timestamp").toString();//时间戳
		
		
		String signature0 = DigestUtils.md5Hex(timestamp+urlSignature);
		if (!signature0.equals(signature)) {			
			errCode = "\"1001\"";
			state = "\"验签错误\"";
		}else{
			//会员
			member = memberService.findByUsercode(userCode);	
			if(member != null){
				String code = "";
				if(type.equals("1")){
					code = "FWZX";
				}else if(type.equals("3")){
					code = "JMD";
				}
				String uniqueCode = code+TimeUtil.getFormatNowTime("yyyyMMddHHmmss");
				FiBankbookJournal fiBankbookJournal = fiBankbookJournalService.findByMemberAndCode(member, code);
				if(fiBankbookJournal == null){				
					//添加一条注册赠送记录
					String success = fiBankbookJournalService.recharge(userCode, new BigDecimal(jifen), uniqueCode, FiBankbookJournal.Type.coupon, FiBankbookJournal.DealType.deposit, FiBankbookJournal.MoneyType.recharge, "区代等级提升赠送");
					if(!"success".equals(success)){
						errCode = "\"2001\"";
						state = "\"区代等级提升赠送券未成功，会员编码为"+userCode+"\"";
						System.out.println("区代等级提升赠送券未成功，提醒手动添加，会员编码为："+userCode);
					}
				}else{
					errCode = "\"1002\"";
					state = "\"重复赠送\"";
				}
			}else{
				errCode = "\"1003\"";
				state = "\"暂未有此会员信息\"";
			}
		}
		map.put("errCode", errCode);
		map.put("state", state);
		JSONObject jsonObject = JSONObject.fromObject(map.toString());
		return jsonObject;
	}
}
