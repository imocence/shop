/*
 * Copyright 2005-2017 shopxx.net. All rights reserved.
 * Support: http://www.shopxx.net
 * License: http://www.shopxx.net/license
 */
package net.shopxx.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import net.shopxx.Page;
import net.shopxx.Pageable;
import net.shopxx.dao.MemberRankDao;
import net.shopxx.entity.Country;
import net.shopxx.entity.MemberRank;
import net.shopxx.service.MemberRankService;

/**
 * Service - 会员等级
 * 
 * @author SHOP++ Team
 * @version 5.0.3
 */
@Service
public class MemberRankServiceImpl extends BaseServiceImpl<MemberRank, Long> implements MemberRankService {

	@Inject
	private MemberRankDao memberRankDao;

	@Transactional(readOnly = true)
	public boolean amountExists(BigDecimal amount) {
		return memberRankDao.exists("amount", amount);
	}

	@Transactional(readOnly = true)
	public boolean amountUnique(Long id, BigDecimal amount) {
		return memberRankDao.unique(id, "amount", amount);
	}

	@Transactional(readOnly = true)
	public MemberRank findDefault() {
		return memberRankDao.findDefault();
	}

	@Transactional(readOnly = true)
	public MemberRank findByAmount(BigDecimal amount) {
		return memberRankDao.findByAmount(amount);
	}

	@Override
	@Transactional
	public MemberRank save(MemberRank memberRank) {
		Assert.notNull(memberRank);

		if (BooleanUtils.isTrue(memberRank.getIsDefault())) {
			memberRankDao.clearDefault();
		}
		return super.save(memberRank);
	}

	@Override
	@Transactional
	public MemberRank update(MemberRank memberRank) {
		Assert.notNull(memberRank);

		MemberRank pMemberRank = super.update(memberRank);
		if (BooleanUtils.isTrue(pMemberRank.getIsDefault())) {
			memberRankDao.clearDefault(pMemberRank);
		}
		return pMemberRank;
	}

    @Override
    public Page<MemberRank> findPage(Country country, Pageable pageable) {
        return memberRankDao.findPage(country,pageable);
    }
    /**
     * 根据国家取等级信息
     */
    @Override
	@Transactional
	public MemberRank findByCountry(Country country,MemberRank.Type type){
    	return memberRankDao.findByCountry(country,type);
    }
    /**
     * 根据国家取等级信息
     */
    @Override
    @Transactional
    public List<MemberRank> findMemberRankByType(MemberRank.Type type){
    	return memberRankDao.findByType(type);
    }
}