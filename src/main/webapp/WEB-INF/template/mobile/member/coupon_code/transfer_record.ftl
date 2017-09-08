<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="format-detection" content="telephone=no">
    <meta name="author" content="SHOP++ Team">
    <meta name="copyright" content="SHOP++">
    <title>${message("member.couponCode.transfer-record")}[#if showPowered] - Powered By SHOP++[/#if]</title>
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
    <a class="pull-left" href="${base}/member/index">
        <span class="glyphicon glyphicon-menu-left"></span>
    </a>
${message("member.couponCode.transfer-record")}
</header>
<main>
    <div class="container-fluid">
    [#if page.content?has_content]
        <div class="panel panel-flat">
            <tr>
                <td>sd</td>
                <td>sd</td>
                <td>sd</td>
                <td>sd</td>
            </tr>
            <tr>
                <td>sd</td>
                <td>sd</td>
                <td>sd</td>
                <td>sd</td>
            </tr>
        </div>
    [#else]
        <p class="no-result">${message("member.common.noResult")}</p>
    [/#if]
    </div>
</main>
<footer class="add footer-fixed">
    <div class="container-fluid">
        <a class="btn btn-primary btn-flat btn-block" href="./transfer">${message("member.couponCode.transfer-add")}</a>
    </div>
</footer>
</body>
</html>