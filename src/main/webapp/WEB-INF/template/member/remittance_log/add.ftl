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
<script type="text/javascript">
$().ready(function() {

	var $inputForm = $("#inputForm");
	var $isDraft = $("#isDraft");
	var $type = $("input[name='type']");
	var $username = $("#username");
	var $send = $("#send");
	var $save = $("#save");
	
	// 发送类型
	$type.click(function() {
		var $this = $(this);
		if ($this.val() == "member") {
			$username.prop("disabled", false).closest("tr").show();
		} else {
			$username.prop("disabled", true).closest("tr").hide();
		}
	});
	
	// 立即发送
	$send.click(function() {
		$inputForm.submit();
	});
	
	$.validator.addMethod("notEqualsIgnoreCase",
		function(value, element, param) {
			return this.optional(element) || param.toLowerCase() != value.toLowerCase()
		}
	);
	
	// 表单验证
	$inputForm.validate({
		rules: {
			username: {
				required: true,
				notEqualsIgnoreCase: "${currentUser.username}",
				remote: {
					url: "check_username",
					cache: false
				}
			},
			title: {
				required: true
			},
			content: {
				required: true,
				maxlength: 4000
			}
		},
		messages: {
			username: {
				notEqualsIgnoreCase: "${message("member.message.notAllowSelf")}",
				remote: "${message("member.message.memberNotExist")}"
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
					<form id="inputForm" action="send" method="post">
						<input type="hidden" name="remittanceLog" value="${(remittanceLog.id)!}" />
						<table class="input">
							<tr>
								<th>
									<span class="requiredField">*</span>${message("Message.title")}:
								</th>
								<td>
									<input type="text" name="title" class="text" value="${(draftMessage.title)!}" maxlength="200" />
								</td>
							</tr>
							<tr>
								<th>
									<span class="requiredField">*</span>${message("Message.content")}:
								</th>
								<td>
									<textarea name="content" class="text">${(draftMessage.content)!}</textarea>
								</td>
							</tr>
							<tr>
								<th>
									&nbsp;
								</th>
								<td>
									<input type="button" id="send" class="button" value="${message("member.common.add")}" />
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