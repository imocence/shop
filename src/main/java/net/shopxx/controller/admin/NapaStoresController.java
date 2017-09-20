package net.shopxx.controller.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;
import net.shopxx.entity.Member;
import net.shopxx.entity.NapaStores;
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
			br = new BufferedReader(new InputStreamReader(request.getInputStream()));
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
			try {
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
			} catch (Exception e) {
				errCode = "\"2001\"";
				state = "\"异常:\"";
			}			
		}
		map.put("errCode", errCode);
		map.put("state", state);
		JSONObject jsonObject = JSONObject.fromObject(map.toString());
		return jsonObject;
	}
}
