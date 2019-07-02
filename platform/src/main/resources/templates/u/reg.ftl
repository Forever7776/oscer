<#include "../layout/front/layout.ftl"/>
<@html title_="注册-oscer社区">

    <div class="layui-container fly-marginTop">
        <div class="fly-panel fly-panel-user" pad20>
            <div class="layui-tab layui-tab-brief" lay-filter="user">
                <ul class="layui-tab-title">
                    <li><a href="login">登入</a></li>
                    <li class="layui-this">注册</li>
                </ul>
                <div class="layui-form layui-tab-content" id="LAY_ucm" style="padding: 20px 0;">
                    <div class="layui-tab-item layui-show">
                        <div class="layui-form layui-form-pane">
                            <form method="post">
                                <div class="layui-form-item">
                                    <label for="L_email" class="layui-form-label">邮箱</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_email" name="email" required lay-verify="email"
                                               autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid layui-word-aux">请输入正确的邮箱地址</div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_username" class="layui-form-label">昵称</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_username" name="username" required
                                               lay-verify="required" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_pass" class="layui-form-label">密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_pass" name="pass" required lay-verify="required"
                                               autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid layui-word-aux">6到16个字符</div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_repass" class="layui-form-label">确认密码</label>
                                    <div class="layui-input-inline">
                                        <input type="password" id="L_repass" name="repass" required
                                               lay-verify="required" autocomplete="off" class="layui-input">
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <label for="L_vercode" class="layui-form-label">验证码</label>
                                    <div class="layui-input-inline">
                                        <input type="text" id="L_vercode" name="vercode" required lay-verify="required"
                                               placeholder="请输入邮箱收到的验证码" autocomplete="off" class="layui-input">
                                    </div>
                                    <div class="layui-form-mid" style="padding: 0!important;">
                                        <button type="button" class="layui-btn layui-btn-normal" onclick="sendEmail()">
                                            获取验证码
                                        </button>
                                    </div>
                                </div>
                                <div class="layui-form-item">
                                    <button class="layui-btn" lay-filter="*" lay-submit>立即注册</button>
                                </div>
                                <div class="layui-form-item fly-form-app">
                                    <span>或者直接使用社交账号快捷注册</span>
                                    <a href="/oauth/before_bind?rp=gitee"
                                       onclick="layer.msg('正在通过Gitee登入', {icon:16, shade: 0.1, time:0})"
                                       title="Gitee登入">
                                        <img src="/res/images/logo_gitee_light_cn_with_domain_name.png"
                                             style="max-height: 36px;">
                                    </a>
                                    <a href="/oauth/before_bind?rp=github"
                                       onclick="layer.msg('正在通过Github登入', {icon:16, shade: 0.1, time:0})"
                                       title="Github登入">
                                        <img src="/res/images/github.png" style="max-height: 36px;">
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <script src="../../res/layui/layui.js"></script>
    <script>
        layui.config({
            version: "3.0.0"
            , base: '../../res/mods/'
        }).extend({
            fly: 'index'
        }).use(['fly', 'face', 'laypage', 'laytpl', 'jquery'], function () {
            var form = layui.form, $ = layui.jquery, layer = layui.layer;


            form.on("submit(commentAdd)", function (data) {
                $.ajax({
                    url: '/comment/q/${q.id}',
                    method: 'post',
                    dataType: 'json',
                    data: data.field,
                    success: function (res) {
                        if (res.code == 1) {
                            layer.msg('评论成功', {icon:6, shade: 0.1, time:500});
                            location.reload();

                        } else {
                            layer.alert(res.message);
                        }
                    }
                });
                return false;
            });

            window.sendEmail = function () {
                $.ajax({
                    url: '/q/recomm',
                    method: 'post',
                    dataType: 'json',
                    data: {"id":${q.id}},
                    success: function (data) {
                        console.log(data);
                        if (data && data.code == 1) {
                            layer.msg("操作成功", {icon: 6});
                            window.location.reload();
                        } else {
                            layer.msg("操作失败", {icon: 6});
                        }
                    }
                });
            }

        });
    </script>

</@html>