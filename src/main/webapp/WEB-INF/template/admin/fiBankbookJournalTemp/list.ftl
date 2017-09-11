[#assign shiro = JspTaglibs["/WEB-INF/tld/shiro.tld"] /]
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("admin.fiBankbookJournalTemp.list")} - Powered By SHOP++</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/admin/css/common.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/admin/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/common.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/list.js"></script>
<script type="text/javascript" src="${base}/resources/admin/datePicker/WdatePicker.js"></script>
<script type="text/javascript" src="${base}/resources/admin/js/country.js"></script>
<script type="text/javascript">
$().ready(function() {
	
	[@flash_message /]
	
	var $listForm = $("#listForm");
	var $moneyType = $("#moneyType");
	var $moneyTypeMenu = $("#moneyTypeMenu");
	var $moneyTypeMenuItem = $("#moneyTypeMenu li");
	
	var $type = $("#type");
	var $typeMenu = $("#typeMenu");
	var $typeMenuItem = $("#typeMenu li");
	
	var $confirmStatus = $("#confirmStatus");
	var $confirmStatusMenu = $("#confirmStatusMenu");
	var $confirmStatusMenuItem = $("#confirmStatusMenu li");
	
	var $confirmButton = $("#confirmButton");
	var $ids = $("#listTable input[name='ids']");
	var $contentRow = $("#listTable tr:gt(0)");
	var $listTable = $("#listTable");
	var $selectAll = $("#selectAll");
		
	$moneyTypeMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$moneyTypeMenuItem.click(function() {
		$moneyType.val($(this).attr("val"));
		$listForm.submit();
	});
	
	$typeMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$typeMenuItem.click(function() {
		$type.val($(this).attr("val"));
		$listForm.submit();
	});
	
	$confirmStatusMenu.hover(
		function() {
			$(this).children("ul").show();
		}, function() {
			$(this).children("ul").hide();
		}
	);
	
	$confirmStatusMenuItem.click(function() {
		$confirmStatus.val($(this).attr("val"));
		$listForm.submit();
	});
	
	// 确认
	$confirmButton.click( function() {
		var $this = $(this);
		if ($this.hasClass("disabled")) {
			return false;
		}
		var $checkedIds = $("#listTable input[name='ids']:enabled:checked");
		$.dialog({
			type: "warn",
			content: "${message("admin.dialog.verifyConfirm")}",
			ok: "${message("admin.dialog.ok")}",
			cancel: "${message("admin.dialog.cancel")}",
			onOk: function() {
				$.ajax({
					url: "confirm",
					type: "POST",
					data: $checkedIds.serialize(),
					dataType: "json",
					cache: false,
					success: function(message) {
						$.message(message);
						if (message.type == "success") {
							setTimeout(function() {
									location.reload(true);
								}, 3000);
						}
						$confirmButton.addClass("disabled");
						$selectAll.prop("checked", false);
						$checkedIds.prop("checked", false);
					}
				});
			}
		});
		return false;
	});
	
	// 选择
	$ids.click( function() {
		var $this = $(this);
		if ($this.prop("checked")) {
			$this.closest("tr").addClass("selected");
			$confirmButton.removeClass("disabled");
		} else {
			$this.closest("tr").removeClass("selected");
			if ($("#listTable input[name='ids']:enabled:checked").size() > 0) {
				$confirmButton.removeClass("disabled");
			} else {
				$confirmButton.addClass("disabled");
			}
		}
	});
	
	// 全选
	$selectAll.click( function() {
		var $this = $(this);
		var $enabledIds = $("#listTable input[name='ids']:enabled");
		if ($this.prop("checked")) {
			$enabledIds.prop("checked", true);
			if ($enabledIds.filter(":checked").size() > 0) {
				$confirmButton.removeClass("disabled");
				$contentRow.addClass("selected");
			} else {
				$confirmButton.addClass("disabled");
			}
		} else {
			$enabledIds.prop("checked", false);
			$confirmButton.addClass("disabled");
			$contentRow.removeClass("selected");
		}
	});
});
</script>
</head>
<body>
	<div class="breadcrumb">
		${message("admin.fiBankbookJournalTemp.list")} <span>(${message("admin.page.total", page.total)})</span>
	</div>
	<form id="listForm" action="list" method="get">
		<input type="hidden" id="countryName" name="countryName" value="${countryName}" />
		<input type="hidden" id="type" name="type" value="${type}" />
		<input type="hidden" id="moneyType" name="moneyType" value="${moneyType}" />
		<input type="hidden" id="confirmStatus" name="confirmStatus" value="${confirmStatus}" />
		<div class="bar" style="height:auto;">
			<div class="buttonGroup">
				<a href="javascript:;" id="refreshButton" class="iconButton">
					<span class="refreshIcon">&nbsp;</span>${message("admin.common.refresh")}
				</a>
				<div id="pageSizeMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.page.pageSize")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if page.pageSize == 10] class="current"[/#if] val="10">10</li>
						<li[#if page.pageSize == 20] class="current"[/#if] val="20">20</li>
						<li[#if page.pageSize == 50] class="current"[/#if] val="50">50</li>
						<li[#if page.pageSize == 100] class="current"[/#if] val="100">100</li>
					</ul>
				</div>
				<div id="countryMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("common.country")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if country.name == null] class="current"[/#if] val="">${message("common.country.all")}</li>
						[@country_list]
							[#list countrys as country]
								<li[#if country.name == countryName] class="current"[/#if] val="${country.name}">${message("${country.nameLocal}")}</li>
							[/#list]
						[/@country_list]
					</ul>
				</div>
				<div id="typeMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.fiBankbookJournal.type")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if type == null] class="current"[/#if] val="">${message("admin.common.choose")}</li>
						[#list types as value]
							<li[#if value == type] class="current"[/#if] val="${value}">${message("admin.fiBankbookJournal.type." + value)}</li>
						[/#list]
					</ul>
				</div>
				<div id="moneyTypeMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.fiBankbookJournal.moneyType")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if moneyType == null] class="current"[/#if] val="">${message("admin.common.choose")}</li>
						[#list moneyTypes as value]
							<li[#if value == moneyType] class="current"[/#if] val="${value}">${message("admin.fiBankbookJournal.moneyType." + value)}</li>
						[/#list]
					</ul>
				</div>
				<div id="confirmStatusMenu" class="dropdownMenu">
					<a href="javascript:;" class="button">
						${message("admin.fiBankbookJournalTemp.confirmStatus")}<span class="arrow">&nbsp;</span>
					</a>
					<ul>
						<li[#if confirmStatus == null] class="current"[/#if] val="">${message("admin.common.choose")}</li>
						[#list confirmStatuss as value]
							<li[#if value == confirmStatus] class="current"[/#if] val="${value}">${message("admin.fiBankbookJournalTemp.confirmStatus." + value)}</li>
						[/#list]
					</ul>
				</div>
			</div>
			<div id="searchPropertyMenu" class="dropdownMenu">
				<div class="search">
					<span class="arrow">&nbsp;</span>
					<input type="text" id="searchValue" name="searchValue" value="${page.searchValue}" maxlength="200" />
					<button type="submit">&nbsp;</button>
				</div>
				<ul>
					<li[#if page.searchProperty == "member.usercode"] class="current"[/#if] val="member.usercode">${message("common.member.code")}</li>
					<li[#if page.searchProperty == "createrName"] class="current"[/#if] val="createrName">${message("common.createrName")}</li>
				</ul>
			</div>
			${message("common.createdDate")}:
			<input type="text" id="beginDate" name="beginDate" class="text Wdate" value="[#if beginDate??]${beginDate?string("yyyy-MM-dd HH:mm:ss")}[/#if]" style="width: 140px;" onfocus="WdatePicker({lang:'${message("Setting.locale.lang")}', maxDate: '#F{$dp.$D(\'endDate\')}', dateFmt:'yyyy-MM-dd HH:mm:ss'});" />
			-
			<input type="text" id="endDate" name="endDate" class="text Wdate" value="[#if endDate??]${endDate?string("yyyy-MM-dd HH:mm:ss")}[/#if]" style="width: 140px;" onfocus="WdatePicker({lang:'${message("Setting.locale.lang")}', minDate: '#F{$dp.$D(\'beginDate\')}', dateFmt:'yyyy-MM-dd HH:mm:ss'});" />
			<input type="submit" class="button" value="${message("common.button.search")}" />
			[@shiro.hasPermission name = "admin:fiBankbookJournalTempConfirm"]
				<div class="buttonGroup">
					<input id="confirmButton" type="button" class="button disabled" value="${message("admin.fiBankbookJournalTemp.button.confirm")}" />
				</div>
			[/@shiro.hasPermission]
		</div>
		<table id="listTable" class="list">
			<tr>
				<th class="check">
					<input type="checkbox" id="selectAll" />
				</th>
				<th>
					<a href="javascript:;" class="sort" name="member.usercode">${message("common.member.code")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="member.username">${message("common.member.name")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="type">${message("admin.fiBankbookJournal.type")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="moneyType">${message("admin.fiBankbookJournal.moneyType")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="dealType">${message("admin.fiBankbookJournalTemp.dealType")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="uniqueCode">${message("admin.fiBankbookJournal.uniqueCode")}</a>
				</th>
				<th style="width: 220px;">
					<a href="javascript:;" class="sort" name="notes">${message("admin.fiBankbookJournal.notes")}</a>
				</th>
				<th>
					<a href="javascript:;" >${message("admin.fiBankbookJournal.dealType.deposit")}</a>
				</th>
				<th>
					<a href="javascript:;" >${message("admin.fiBankbookJournal.dealType.takeout")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="confirmStatus">${message("admin.fiBankbookJournalTemp.confirmStatus")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createrName">${message("common.createrName")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="createdDate">${message("common.createdDate")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="confirmName">${message("admin.fiBankbookJournalTemp.confirmName")}</a>
				</th>
				<th>
					<a href="javascript:;" class="sort" name="country">${message("common.country")}</a>
				</th>
			</tr>
			[#list page.content as fiBankbookJournal]
				<tr>
					<td>
						[#if fiBankbookJournal.confirmStatus == 'unconfirmed']
							<input type="checkbox" name="ids" value="${fiBankbookJournal.id}" />
						[/#if]
					</td>
					<td>
						${fiBankbookJournal.member.usercode}
					</td>
					<td>
						${fiBankbookJournal.member.username}
					</td>
					<td>
						${message("admin.fiBankbookJournal.type." + fiBankbookJournal.type)}
					</td>
					<td>
						${message("admin.fiBankbookJournal.moneyType." + fiBankbookJournal.moneyType)}
					</td>
					<td>
						${message("admin.fiBankbookJournal.dealType." + fiBankbookJournal.dealType)}
					</td>
					<td>
						${fiBankbookJournal.uniqueCode}
					</td>
					<td>
						${fiBankbookJournal.notes}
					</td>
					[#if fiBankbookJournal.dealType == 'deposit']
						<td>
							${fiBankbookJournal.money}
						</td>
						<td>
							0.00
						</td>
					[#else]
						<td>
							0.00
						</td>
						<td>
							${fiBankbookJournal.money}
						</td>
					[/#if]
					<td>
						${message("admin.fiBankbookJournalTemp.confirmStatus." + fiBankbookJournal.confirmStatus)}
					</td>
					<td>
						${fiBankbookJournal.createrName}
					</td>
					<td>
						<span title="${fiBankbookJournal.createdDate?string("yyyy-MM-dd HH:mm:ss")}">${fiBankbookJournal.createdDate?string("yyyy-MM-dd HH:mm:ss")}</span>
					</td>
					<td>
						${fiBankbookJournal.confirmName}
					</td>
					<td>
						${message("${fiBankbookJournal.country.nameLocal}")}
					</td>
				</tr>
			[/#list]
		</table>
		[@pagination pageNumber = page.pageNumber totalPages = page.totalPages]
			[#include "/admin/include/pagination.ftl"]
		[/@pagination]
	</form>
</body>
</html>