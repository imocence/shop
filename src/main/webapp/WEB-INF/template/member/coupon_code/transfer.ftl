<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="content-type" content="text/html;charset=utf-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>${message("member.couponCode.transfer")}[#if showPowered] - Powered By SHOP++[/#if]</title>
<meta name="author" content="SHOP++ Team" />
<meta name="copyright" content="SHOP++" />
<link href="${base}/resources/member/css/animate.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/common.css" rel="stylesheet" type="text/css" />
<link href="${base}/resources/member/css/member.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${base}/resources/member/js/jquery.js"></script>
<script type="text/javascript" src="${base}/resources/member/js/common.js"></script>
    <script type="text/javascript">
        $().ready(function() {

            var $inputForm = $("#inputForm");
            var $areaId = $("#areaId");

        [#if flashMessage?has_content]
            $.alert("${flashMessage}");
        [/#if]

            // 地区选择
            $areaId.lSelect({
                url: "${base}/common/area"
            });

            // 表单验证
            $inputForm.validate({
                rules: {
                    email: {
                        required: true,
                        email: true,
                        remote: {
                            url: "check_email",
                            cache: false
                        }
                    },
                    mobile: {
                        pattern: /^1[3|4|5|7|8]\d{9}$/,
                        remote: {
                            url: "check_mobile",
                            cache: false
                        }
                    }
                [@member_attribute_list]
                    [#list memberAttributes as memberAttribute]
                        [#if memberAttribute.isRequired || memberAttribute.pattern?has_content]
                            ,memberAttribute_${memberAttribute.id}: {
                            [#if memberAttribute.isRequired]
                                required: true
                                [#if memberAttribute.pattern?has_content],[/#if]
                            [/#if]
                            [#if memberAttribute.pattern?has_content]
                                pattern: /${memberAttribute.pattern}/
                            [/#if]
                        }
                        [/#if]
                    [/#list]
                [/@member_attribute_list]
                },
                messages: {
                    email: {
                        remote: "${message("common.validate.exist")}"
                    },
                    mobile: {
                        remote: "${message("common.validate.exist")}"
                    }
                }
            });

        });
    </script>
</head>
<body>
	[#assign current = "couponCodeTransferRecord" /]
	[#include "/shop/include/header.ftl" /]
	<div class="container member">
		<div class="row">
			[#include "/member/include/navigation.ftl" /]
			<div class="span10">
				<div class="list">
					<div class="title" style="display: inline-block">${message("member.couponCode.transfer-record")}</div><div style="display: inline-block" class="title">${message("member.couponCode.transfer")}</div>
                    <form id="inputForm" action="update" method="post">
                        <table class="input">
                            <tr>
                                <th>
								${message("Member.username")}:
                                </th>
                                <td>
								${currentUser.username}
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <span class="requiredField">*</span>${message("Member.transfer.number")}:
                                </th>
                                <td>
                                    <input type="text" name="email" class="text" value="" maxlength="200" />
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <span class="requiredField">*</span>${message("Member.transfer.name")}:
                                </th>
                                <td>
                                    <input type="text" name="email" class="text" value="" maxlength="200" />
                                </td>
                            </tr>
                            <tr>
                                <th>
                                    <span class="requiredField">*</span>${message("Member.transfer.phoneNumber")}:
                                </th>
                                <td>
                                    <input type="text" name="email" class="text" value="" maxlength="200" />
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
                            <tr>
                                <th>
                                    &nbsp;
                                </th>
                                <td>
                                    &nbsp;
                                </td>
                            </tr>
                        </table>
                    </form>

				</div>
				[@pagination pageNumber = page.pageNumber totalPages = page.totalPages pattern = "?pageNumber={pageNumber}"]
					[#include "/member/include/pagination.ftl"]
				[/@pagination]
			</div>
		</div>
	</div>
	[#include "/shop/include/footer.ftl" /]
</body>
</html>