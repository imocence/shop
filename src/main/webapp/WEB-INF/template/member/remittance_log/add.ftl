<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.remittanceLog.add")}[#if showPowered] - Powered By SHOP++[/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/member/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/member/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/jquery.validate.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/member/datePicker/WdatePicker.js"></script>
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $add = $("#add");
	
	// 立即发送
	$add.click(function() {
		$inputForm.submit();
	});
	
	// 表单验证
	$inputForm.validate({
		rules: {
			name: {
				required: true
			},
			amount: {
				required: true
			},
			number: {
				required: true
			},
			date: {
				required: true
			},
			memo: {
				required: true
			}
		}
	});

});
</script>
</head>
<body>
	[#assign current = "remittanceAdd" /]
	[#include "/shop/include/header.ftl" /]
	<div class="container member">
		<div class="row">
			[#include "/member/include/navigation.ftl" /]
			<div class="span10">
				<div class="input">
					<div class="title">${message("member.remittanceLog.add")}</div>
					<form id="inputForm" action="add" method="post">
						<input type="hidden" name="remittanceLog" value="${(remittanceLog.id)!}" />
						<table class="input">
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.remittanceLog.name")}:
								</th>
								<td>
									<input type="text" name="name" class="text" value="${(remittanceLog.name)!}" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.remittanceLog.amount")}:
								</th>
								<td>
									<input type="text" name="amount" class="text" value="${(remittanceLog.amount)!}" />
								</td>
							</tr>
							<tr>
								<th>
									${message("member.remittanceLog.account")}:
								</th>
								<td>
									<input type="text" name="account" class="text" value="${(remittanceLog.remittanceLogAccount)!}" />
								</td>
							</tr>
							<tr>
								<th>
									${message("member.remittanceLog.telephone")}:
								</th>
								<td>
									<input type="text"  name="telephone" class="text" value="${(remittanceLog.telephone)!}" />
								</td>
							</tr>
							<tr>
								<th>
									${message("member.remittanceLog.identityCard")}:
								</th>
								<td>
									<input type="text" name="identityCard" class="text" value="${(remittanceLog.identityCard)!}" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.remittanceLog.number")}:
								</th>
								<td>
									<input type="text"  name="number" class="text" value="${(remittanceLog.remittanceLogNumber)!}" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.remittanceLog.date")}:
								</th>
								<td>
									<input type="text" name="date" class="text" value="${(remittanceLog.remittanceLogDate)!}" onfocus="WdatePicker({lang:'${message("Setting.locale.lang")}'});" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("member.remittanceLog.memo")}:
								</th>
								<td>
									<textarea name="memo" class="text">${(remittanceLog.memo)!}</textarea>
								</td>
							</tr>
							<tr>
								<th>
									&nbsp;
								</th>
								<td>
									<input type="button" id="add" class="button" value="${message("member.common.add")}" />
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