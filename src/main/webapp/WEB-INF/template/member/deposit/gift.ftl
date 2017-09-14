<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.deposit.recharge")}[#if showPowered] - Powered By SHOP++[/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/member/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/member/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/underscore.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/common.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $rechargeForm = $("#rechargeForm");
	var $giftMemberCode = $("#giftMemberCode");
	var $giftAmount = $("#giftAmount");
	var $inputForm = $("#inputForm");
	// 券
	$giftAmount.change(function() {
		var couponPrice = $("#giftAmount").val();
		var $element = $(this);
		if (/^\d+(\.\d{0,${setting.priceScale}})?$/.test($element.val())) {
			var max = ${coupon.balance} >= couponPrice ? couponPrice : ${coupon.balance};
			if (parseFloat($element.val()) > max) {
				$element.val(max);
			}
		} else {
			$element.val("0");
		}
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: {
				required: true
			},
			giftMemberCode: {
				required: true
			},
			giftAmount: {
				required: true
			}
		}
	});
});
</script>
</head>
<body>
	[#assign current = "depositRecharge" /]
	[#include "/shop/include/header.ftl" /]
	<div class="container member">
		<div class="row">
			[#include "/member/include/navigation.ftl" /]
			<div class="span10">
				<div class="input deposit">
					<div class="title">${message("member.deposit.gift")}</div>
					<form id="inputForm" action="gift_do" method="post" target="_blank">
				
						<table class="input">
							<tr>
								<th>
									${message("member.deposit.balance")}:
								</th>
								<td>
									${currency(coupon.balance, true, true)}
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.deposit.name")}:
								</th>
								<td>
									<input type="text" name="name" class="text" value="${(remittanceLog.name)!}" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.deposit.giftMemberCode")}:
								</th>
								<td>
									<input type="text" id="giftMemberCode" name="giftMemberCode" value="${(member.deposit.giftMemberCode)!}" class="text" maxlength="16" onpaste="return false;" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.deposit.giftAmount")}:
								</th>
								<td>
									<input type="text" id="giftAmount" name="giftAmount" class="text" maxlength="16" onpaste="return false;" />
								</td>
							</tr>
							<tr>
								<th>
									&nbsp;
								</th>
								<td>
									<input type="submit" class="button" value="${message("member.common.submit")}" />
									<input type="button" class="button" value="${message("member.common.back")}" onclick="history.back(); return false;" />
								</td>
							</tr>
						</table>
					</form>
				</div>
			</div>
		</div>
	</div>
	[#include "/shop/include/footer.ftl" /]
</body>
</html>