package net.shopxx.service.impl;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.RemittanceLogDao;
import net.shopxx.entity.Member;
import net.shopxx.entity.RemittanceLog;
import net.shopxx.service.RemittanceLogService;

/**
 * Service - 汇款登记
 * 
 * @author cht
 * @version 1.0.0
 */
@Service
public class RemittanceLogServiceImpl extends BaseServiceImpl<RemittanceLog, Long> implements RemittanceLogService {
	@Inject
	private RemittanceLogDao remittanceLogDao;
	
	@Override
	public Page<RemittanceLog> findPage(Member member, Pageable pageable) {
		return remittanceLogDao.findPage(member, pageable);
	}

}
