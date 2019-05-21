
$(function () {
    function footerPosition(){
        $("footer").removeClass("footer-fixed-bottom");
        var contentHeight = document.body.scrollHeight,//网页正文全文高度
            winHeight = window.innerHeight;//可视窗口高度，不包括浏览器顶部工具栏
        if(!(contentHeight > winHeight)){
            //当网页正文高度小于可视窗口高度时，为footer添加类fixed-bottom
            $("footer").addClass("footer-fixed-bottom");
        }
    }
    footerPosition();
    $(window).resize(footerPosition);

    $(document).on('click', '.xiala li', function(){
        var id = $(this).data("id");
        if(id) {
            $.ajax({
                url: '/remind/read',
                data: {"id": id},
                type: 'post',
                dataType: 'json',
                success: function (d) {
                    if (d || d.code == 1) {
                        layer.msg("评论成功~", {icon: 6});
                        location.reload();
                    } else {
                        layer.msg(d.message ? d.message : "评论失败", {icon: 5});
                    }
                }, error: function (d) {
                    layer.msg(d.message ? d.message : "评论失败", {icon: 5});
                }
            });
        }
    });

    /*$(".show-search-box a").click(function () {
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.ready(function() {
                layer.prompt({title: '请输入搜索关键词', formType: 0}, function(pass, index){
                    if(pass){
                        window.location.href="/search?q="+pass;
                    }
                    layer.close(index);
                });
            })

        });
    });*/

    $(".show-search-box a").click(function () {
        layui.use('layer', function () {
            var layer = layui.layer;
            layer.ready(function() {
                layer.prompt({title: '请输入搜索关键词！', formType: 0,
                        "success":function(){
                            $("input.layui-layer-input").on('keydown',function(e){
                                if (e.which == 13) {
                                    window.location.href="/search?q="+$(this).val();
                                    layer.close(1);
                                }
                            });
                        }},
                    function(pass, index){
                        if(pass){
                            window.location.href="/search?q="+pass;
                        }
                        layer.close(index);
                    });
            })



        });
    });


    $(document).on('click', '.app-guide-title a', function(){
        var id = $(this).data("id");
        if(id) {
            $.ajax({
                url: '/remind/read',
                data: {"id": id},
                type: 'post',
                dataType: 'json',
                success: function (d) {
                    if (d || d.code == 1) {
                        layer.msg("评论成功~", {icon: 6});
                        location.reload();
                    } else {
                        layer.msg(d.message ? d.message : "评论失败", {icon: 5});
                    }
                }, error: function (d) {
                    layer.msg(d.message ? d.message : "评论失败", {icon: 5});
                }
            });
        }
    });

    var modal = $(".cd-user-modal");
	$(".js-show-menu").click(function () {
        $(this).find("ul").css({"display":"block"});
    });

    /**
     * 登陆
     */
	$(".cd-signin,.js-user-login").click(function () {
        login_block();
    });

    /**
     * 注册
     */
    $(".cd-signup,.js-open-register").click(function () {
        register_block();
    });

    /**
     * 忘记密码
     */
    $(".js-forget-passward").click(function () {
        forgetpwd_block();
    });

    /**
     * 关闭
     */
    $(".cd-close-form").click(function () {
        close_block();
    });

	function login_block() {
	    $(".js-show-menu >ul").css({"display":"none"});
        modal.addClass("is-visible");
        modal.find("#cd-signup").css({"display":"none"});
        modal.find("#cd-forgetpwd").css({"display":"none"});
        modal.find("#cd-login").css({"display":"block"});
    }

    function register_block() {
        $(".js-show-menu >ul").css({"display":"none"});
        modal.addClass("is-visible");
        modal.find("#cd-login").css({"display":"none"});
        modal.find("#cd-forgetpwd").css({"display":"none"});
        modal.find("#cd-signup").css({"display":"block"});
    }

    function forgetpwd_block() {
        $(".js-show-menu >ul").css({"display":"none"});
        modal.addClass("is-visible");
        modal.find("#cd-login").css({"display":"none"});
        modal.find("#cd-signup").css({"display":"none"});
        modal.find("#cd-forgetpwd").css({"display":"block"});
    }

    /**
     * 退出登录
     */
    $(".user_logout").click(function () {
        logout();
    });

    $(document).keydown(function (e) {
        if (e.keyCode == 13 && modal.find("#cd-login").css("display")=='block') {
            login_check();
        }
    });



});

function close_block(){
    $(".cd-user-modal").removeClass("is-visible");
    $(".cd-user-modal").find("#cd-signup").css({"display":"none"});
    $(".cd-user-modal").find("#cd-forgetpwd").css({"display":"none"});
    $(".cd-user-modal").find("#cd-login").css({"display":"none"});
}


/**
 * 登录检查
 */
function login_check() {
    layui.use('layer', function(){
        var layer = layui.layer;
        var name = $("#name").val();
        var pwd = $("#pwd").val();
        if (!name) {
            layer.msg("请输入用户名",{icon:5});
            return false;
        } else if (!pwd) {
            layer.msg("请输入密码",{icon:5});
            return false;
        } else {
            $.ajax({
                url: '/user/login',
                type: 'post',
                data: {'name': name, 'pwd': pwd},
                dataType: 'json',
                success: function (d) {
                    if (d.code==1) {
                        layer.msg(d.message?d.message:"登录成功",{icon:6,time:500},function () {
                            location.href = "/";
                        });
                    } else {
                        layer.msg(d.message?d.message:"登录失败",{icon:5});
                    }
                }
            });
        }
    });
}

/**
 * 退出检查
 */
function logout() {
    layui.use('layer', function(){
        var layer = layui.layer;
        $.ajax({
            url: '/u/logout',
            type: 'post',
            dataType: 'json',
            success: function (d) {
                if (d.code==1) {
                    layer.msg(d.message?d.message:"退出成功",{icon:6,time:500},function () {
                        close_block();
                        window.location.reload();
                    });
                } else {
                    layer.msg(d.message?d.message:"登录失败",{icon:5});
                }
            }
        });
    });
}

/**
 * 注册账号
 */
function register_check() {
    layui.use('layer', function(data){
        var layer = layui.layer;
        $(this).attr("disabled","disabled");
        var time = 1200;
        var username = $("#register_username").val();
        var password = $("#register_password").val();
        var email = $("#register_email").val();
        if (!username) {
            layer.msg("请输入用户名!", {icon: 5,time:time});
            setTimeout(function () {
                $(this).removeAttr("disabled");
            },time)
            return;
        }
        if (!password) {
            layer.msg("请输入密码!", {icon: 5,time:time});
            setTimeout(function () {
                $(this).removeAttr("disabled");
            },time)
            return;
        }
        if (!password || password.length<6) {
            layer.msg("密码太简短!", {icon: 5,time:time});
            setTimeout(function () {
                $(this).removeAttr("disabled");
            },time)
            return;
        }
        var pwd_reg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,32}$/;
        if (!pwd_reg.test(password)) {
            layer.msg("请输入正确的密码!", {icon: 5});
            return false;
        }
        if (!email) {
            layer.msg("请输入邮箱!", {icon: 5,time:time});
            setTimeout(function () {
                $(this).removeAttr("disabled");
            },time)
            return;
        }
        var email_reg = /^[\w-]+(.[\w-]+)*@[\w-]+(.[\w-]+)+$/;
        if (!email_reg.test(email)) {
            layer.msg("请填写正确的邮箱!", {icon: 5});
            return false;
        }
        if (!checkRegisterCode(email)) {
            layer.msg("验证码错误!", {icon: 5,time:time});
            setTimeout(function () {
                $(this).removeAttr("disabled");
            },time)
            return;
        }

        $.ajax({
            url: '/u/register',
            type: 'post',
            data: {'username': username, 'password': password,"email":email},
            dataType: 'json',
            success: function (data) {
                if (data && data.code==1) {
                    layer.msg("注册成功~~~",{icon:6});
                    $(".cd-user-modal").removeClass("is-visible");
                }else{
                    layer.msg(data.msg?data.msg:"注册失败~~~",{icon:6});
                }
            }
        });
    })

}

/**
 * 找回密码
 */
function forgetpwd_check() {
    layui.use('layer', function(data){
        var layer = layui.layer;
        $(this).attr("disabled","disabled");
        var time = 1200;
        var username = $("#forget_username").val();
        var email = $("#forget_email").val();
        if (!username) {
            layer.msg("请输入用户名!", {icon: 5,time:time});
            setTimeout(function () {
                $(this).removeAttr("disabled");
            },time)
            return;
        }
        if (!email) {
            layer.msg("请输入邮箱!", {icon: 5,time:time});
            setTimeout(function () {
                $(this).removeAttr("disabled");
            },time)
            return;
        }
        var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
        if (!myreg.test(email)) {
            layer.msg("请填写正确的邮箱!", {icon: 5});
            return false;
        }

        $.ajax({
            url: ("/api/send_forget_email"),
            type: "post",
            data:{"username":username,"email":email},
            dataType: "json",
            async:false,
            success: function (data) {
                if (data && data.code==1) {
                    layer.msg(data.message?data.message:"密码发送至邮箱成功~",{icon:6});
                    window.location.reload();
                } else {
                    layer.msg(data.message?data.message:"发送邮件失败~",{icon:5});
                }
            },
            error: function (data) {
                layer.msg(data.message?data.message:"密码发送至邮箱成功~",{icon:6});
                window.location.reload();
            }
        });
    })
}

function remind_option() {
    if($(".app-guide-box").is(':visible')){
        $(".app-guide-box").hide();
        $(".app-guide-title").hide();
    }else{
        $(".app-guide-box").show();
        $(".app-guide-title").show();
    }

}

/**
 * 动弹点赞操作
 * @param tweetId
 */
function tweet_praise(tweetId) {
    if (!logined()) {
        showLogin();
        return;
    }
    tweet_praise_option(tweetId)
}

/**
 * 给该动弹点赞，或取消点赞
 */
function tweet_praise_option(tweetId) {
    $.ajax({
        url: '/tweet/praise',
        data:{"obj_id":tweetId},
        type: 'post',
        dataType: 'json',
        success: function (d) {
             if (d && d.code == 1) {
                 var value = $("#tweetPraiseCount_" + tweetId).text();
                 console.log(d.result.status);
                 var praise = d.result.status == 1 ? 1: -1;
                 $("#tweetPraiseCount_" + tweetId).text(Number(value) + Number(praise));
                 if(praise==1){
                     $("#tweetPraiseCount_" + tweetId).parent().prev().addClass("like");
                 }else{
                     $("#tweetPraiseCount_" + tweetId).parent().prev().removeClass("like");
                 }
             }else{

             }
        }, error: function (d) {

        }
    });
}

/**
 * 博客点赞操作
 * @param blogId
 */
function blog_praise(blogId) {
    if (!logined()) {
        showLogin();
        return;
    }
    blog_praise_option(blogId)
}

/**
 * 给该博客点赞，或取消点赞
 */
function blog_praise_option(blogId) {
    $.ajax({
        url: '/blog/praise',
        data:{"obj_id":blogId},
        type: 'post',
        dataType: 'json',
        success: function (d) {
            if (d && d.code == 1) {
                var value = $("#blogPraiseCount_" + blogId).text();
                console.log(d.result.status);
                var praise = d.result.status == 1 ? 1: -1;
                $("#blogPraiseCount_" + blogId).text(Number(value) + Number(praise));
                $("#praiseCount").text(Number(value) + Number(praise));
                if(praise==1){
                    $("#blogPraiseCount_" + blogId).parent().prev().addClass("like");
                }else{
                    $("#blogPraiseCount_" + blogId).parent().prev().removeClass("like");
                }

            }
        }, error: function (d) {

        }
    });
}
