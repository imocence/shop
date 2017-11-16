<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="format-detection" content="telephone=no">
	<meta name="author" content="SHOP++ Team">
	<meta name="copyright" content="SHOP++">
	<title>${message("member.deposit.log")}[#if showPowered] [/#if]</title>
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
	<script src="${base}/resources/mobile/member/js/underscore.js"></script>
	<script src="${base}/resources/mobile/member/js/moment.js"></script>
	<script src="${base}/resources/mobile/member/js/common.js"></script>
	<script id="fiBankbookJournalTemplate" type="text/template">
		<%
			function depositText(type) {
				switch(type) {
					case "balance":
						return "${message("DepositLog.Type.balance")}";
					case "coupon":
						return "${message("DepositLog.Type.coupon")}";
				}
			}
		%>
		<%_.each(fiBankbookJournals, function(fiBankbookJournal, i) {%>
			<div class="list-group list-group-flat">
				<div class="list-group-item small">
					${message("member.common.createdDate")}:
					<span class="pull-right" title="<%-moment(new Date(fiBankbookJournal.createdDate)).format('YYYY-MM-DD HH:mm:ss')%>"><%-moment(new Date(fiBankbookJournal.createdDate)).format('YYYY-MM-DD')%></span>
				</div>
				<div class="list-group-item small">
					${message("DepositLog.type")}
					<span class="pull-right"><%-depositText(fiBankbookJournal.type)%></span>
				</div>
				<div class="list-group-item small">
					${message("DepositLog.credit")}
					<span class="pull-right">
						<%if (fiBankbookJournal.dealType == "deposit") {%>
							<%-currency(fiBankbookJournal.money,true)%>
						<%} else {%>
							-
						<%}%>	
					</span>
				</div>
				<div class="list-group-item small">
					${message("DepositLog.debit")}
					<span class="pull-right">
						<%if (fiBankbookJournal.dealType == "takeout") {%>
							<%-currency(fiBankbookJournal.money,true)%>
						<%} else {%>
							-
						<%}%>						
					</span>
				</div>
				<div class="list-group-item small">
					${message("DepositLog.balance")}
					<em class="pull-right orange"><%-currency(fiBankbookJournal.balance, true)%></em>
				</div>
				<div class="list-group-item small height" style="height: 80px">
					${message("DepositLog.memo")}
					<em class="pull-right width" style="width: 60%">
						<%-fiBankbookJournal.notes%>
					</em>
				</div>
			</div>
		<%})%>
	</script>
	<script type="text/javascript">
	$().ready(function() {
		
		var $fiBankbookJournalItems = $("#fiBankbookJournalItems");
		var fiBankbookJournalTemplate = _.template($("#fiBankbookJournalTemplate").html());
		
		// 无限滚动加载
		$fiBankbookJournalItems.infiniteScroll({
			url: function(pageNumber) {
				return "${base}/member/deposit/log?pageNumber=" + pageNumber+"&type=${type!'0'}";
			},
			pageSize: 10,
			template: function(pageNumber, data) {
				return fiBankbookJournalTemplate({
					fiBankbookJournals: data
				});
			}
		});
		
	});
	</script>
</head>
<body class="profile">
	<header class="header-fixed">
		<a class="pull-left" href="${base}/member/index">
			<span class="glyphicon glyphicon-menu-left"></span>
		</a>
		${message("member.deposit.log")}
	</header>
	<main>
		<div class="container-fluid">
			<div id="fiBankbookJournalItems"></div>
		</div>
	</main>
</body>
</html>