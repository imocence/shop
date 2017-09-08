<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="format-detection" content="telephone=no">
	<meta name="author" content="SHOP++ Team">
	<meta name="copyright" content="SHOP++">
	<title>${message("member.couponCode.transfer")}[#if showPowered] - Powered By SHOP++[/#if]</title>
	<link href="${base}/favicon.ico" rel="icon">
	<link href="${base}/resources/mobile/member/css/bootstrap.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/font-awesome.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/animate.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/common.css" rel="stylesheet">
	<link href="${base}/resources/mobile/member/css/profile.css" rel="stylesheet">
	<!--[if lt IE 9]>
		<script src="${base}/resources/mobile/member/js/html5shiv.js"></script>
		<script src="${base}/resources/mobile/member/js/respond.js"></script>
	<![endif]-->
	<script src="${base}/resources/mobile/member/js/jquery.js"></script>
	<script src="${base}/resources/mobile/member/js/bootstrap.js"></script>
	<script src="${base}/resources/mobile/member/js/bootstrap-datepicker.js"></script>
	<script src="${base}/resources/mobile/member/js/jquery.validate.js"></script>
	<script src="${base}/resources/mobile/member/js/jquery.lSelect.js"></script>
	<script src="${base}/resources/mobile/member/js/underscore.js"></script>
	<script src="${base}/resources/mobile/member/js/common.js"></script>

</head>
<body class="profile">
	<header class="header-fixed">
		<a class="pull-left" href="javascript: history.back();">
			<span class="glyphicon glyphicon-menu-left"></span>
		</a>
		${message("member.couponCode.transfer")}
	</header>
	<main>
		<div class="container-fluid">
			<form id="inputForm" action="update" method="post">
				<input name="id" type="hidden" value="${receiver.id}">
				<div class="panel panel-flat">
					<div class="panel-body">
						<div class="form-group">
							<label for="email">${message("Member.transfer.name")}</label>
							<input id="email" name="email" class="form-control" type="text" value="" maxlength="200">
						</div>
						<div class="form-group">
							<label for="mobile">${message("Member.transfer.number")}</label>
							<input id="mobile" name="mobile" class="form-control" type="text" value="" maxlength="200">
						</div>
                        <div class="form-group">
                            <label for="mobile">${message("Member.transfer.phoneNumber")}</label>
                            <input id="mobile" name="mobile" class="form-control" type="text" value="" maxlength="200">
                        </div>

					</div>
					<div class="panel-footer text-center">
						<button class="btn btn-primary" type="submit">${message("member.common.submit")}</button>
						<a class="btn btn-default" href="${base}/member/index">${message("member.common.back")}</a>
					</div>
				</div>
			</form>
		</div>
	</main>
</body>
</html>