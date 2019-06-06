<#include "../layout/front/layout.ftl"/>
<@html title_="${q.title}-oscer社区">

<div class="layui-container">
    <div class="layui-row layui-col-space15">
        <div class="layui-col-md8 content detail">
            <div class="fly-panel detail-box">
                <h1>${q.title}</h1>
                <div class="fly-detail-info">
                    <#if q.status==2>
                        <span class="layui-badge">审核中</span>
                    </#if>

                    <#if q.reward_point gt 0>
                        <#if q.reward_comment == 0>
                            <span class="layui-badge" style="background-color: #999;">未结</span>
                        <#else >
                            <span class="layui-badge" style="background-color: #5FB878;">已结</span>
                        </#if>
                    </#if>

                    <#if q.top ==1><span class="layui-badge layui-bg-black">顶</span></#if>

                    <#if q.recomm ==1><span class="layui-badge layui-bg-red">推荐</span></#if>

                    <div class="fly-admin-box" data-id="123">
                        <#if login_user?? && login_user.id=q.user>
                            <span class="layui-btn layui-btn-xs jie-admin layui-btn-danger">删除</span>
                        </#if>

                        <#if login_user?? && login_user.id=2>
                            <#if q.recomm ==1>
                                <a onclick="recomm()"><span class="layui-btn layui-btn-xs"style="background-color:#ccc;">取消推荐</span></a>
                            <#else >
                                <a onclick="recomm()"><span class="layui-btn layui-btn-xs " >推荐</span></a>
                            </#if>
                            <#if q.system_top ==1>
                                <a onclick="as_top()"><span class="layui-btn layui-btn-xs" style="background-color:#ccc;">取消置顶</span></a>
                            <#else >
                                <a onclick="as_top()"><span class="layui-btn layui-btn-xs">置顶</span></a>
                            </#if>
                        </#if>
                    </div>

                    <span class="fly-list-nums">
                            <a href="#comment"><i class="iconfont" title="回答">&#xe60c;</i> ${q.comment_count!'0'}</a>
                            <i class="iconfont" title="人气">&#xe60b;</i> ${q.view_count!'0'}
                            <i class="iconfont" title="人气">&#xe67b;</i> ${q.view_count!'0'}
                    </span>
                </div>

                <div class="detail-about">
                    <a class="fly-avatar" href="/u/${u.id}"><img src="${u.headimg}" alt="${u.nickname!u.username!u.name}"></a>
                    <div class="fly-detail-user">
                        <a href="/u/${u.id}" class="fly-link">
                            <cite>${u.nickname!u.username!u.name}</cite>
                            <#--<i class="iconfont icon-renzheng" title="认证信息：{{ rows.user.approve }}"></i>
                            <i class="layui-badge fly-badge-vip">VIP3</i>-->
                        </a>
                        <span>${q.insert_date}</span>
                    </div>
                    <div class="detail-hits" id="LAY_jieAdmin" data-id="123">
                        <#if q.reward_point gt 0>
                            <span style="padding-right: 10px; color: #FF7200">悬赏：${q.reward_point!'0'}积分</span>
                        </#if>
                        <#if login_user?? && login_user.id=q.user>
                            <span class="layui-btn layui-btn-xs jie-admin"><a href="/q/edit/${q.id}">编辑此贴</a></span>
                        </#if>
                        <span class="layui-btn layui-btn-xs jie-admin layui-btn-normal"><a href="add.html">关注</a></span>
                        <span class="layui-btn layui-btn-xs jie-admin layui-btn-warm"><a href="add.html">私信</a></span>
                    </div>
                </div>

                <div class="detail-body photos">${q.content}</div>
            </div>

            <div class="fly-panel detail-box" id="flyReply">
                <fieldset class="layui-elem-field layui-field-title" style="text-align: center;">
                    <legend>回帖</legend>
                </fieldset>

                <ul class="jieda" id="jieda"><div id="commentBodys"></div></ul>
                <div id="page"></div>

                <div class="layui-form layui-form-pane">
                    <form action="/comment/q/${q.id}" method="post" id="commentFrom">
                        <input type="hidden" name="id" value="${q.id}"/>
                        <div class="layui-form-item layui-form-text">
                            <a name="comment"></a>
                            <div class="layui-input-block">
                                <textarea name="content" placeholder="请输入内容" class="layui-textarea fly-editor" style="height: 150px;"></textarea>
                            </div>
                        </div>
                        <div class="layui-form-item">
                            <input type="hidden" name="jid" value="123">
                            <button class="layui-btn" lay-filter="commentAdd" lay-submit>提交回复</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div class="layui-col-md4">
            <dl class="fly-panel fly-list-one">
                <dt class="fly-panel-title">本周热议</dt>
                <dd>
                    <a href="">基于 layui 的极简社区页面模版</a>
                    <span><i class="iconfont icon-pinglun1"></i> 16</span>
                </dd>
                <!-- 无数据时 -->
                <!--
                <div class="fly-none">没有相关数据</div>
                -->
            </dl>

            <#--<div class="fly-panel">
                <div class="fly-panel-title">这里可作为广告区域</div>
                <div class="fly-panel-main">
                    <a href="http://layim.layui.com/?from=fly" target="_blank" class="fly-zanzhu" style="background-color: #5FB878;">LayIM 3.0 - layui 旗舰之作</a>
                </div>
            </div>

            <div class="fly-panel" style="padding: 20px 0; text-align: center;">
                <img src="../../res/images/weixin.jpg" style="max-width: 100%;" alt="layui">
                <p style="position: relative; color: #666;">微信扫码关注 layui 公众号</p>
            </div>-->

        </div>
    </div>
</div>

<script id="commentListTpl" type="text/html">
    {{#  if(d.list!=null&&d.list.length> 0){ }}
    {{#  layui.each(d.list, function(index, item){ }}
    <li data-id="111" class="jieda-daan">
        <a name="item-1111111111"></a>
        <div class="detail-about detail-about-reply">
            <a class="fly-avatar" href="">
                <img src="{{item.cu.headimg}}" alt=" ">
            </a>
            <div class="fly-detail-user">
                <a href="" class="fly-link">
                    <cite>{{item.cu.nickname||item.cu.username}}</cite>
                    <#--<i class="iconfont icon-renzheng" title="认证信息：XXX"></i>-->
                    <i class="layui-badge fly-badge-vip">{{item.cu.vip_text}}</i>
                </a>

                {{# if(item.cu.id==2){ }}<span>(楼主)</span>{{# }}}
                <!--
                <span style="color:#5FB878">(管理员)</span>
                <span style="color:#FF9E3F">（社区之光）</span>
                <span style="color:#999">（该号已被封）</span>
                -->
            </div>

            <div class="detail-hits">
                <span>{{item.sdf_insert_date}}</span>
            </div>

            {{# if(item.bestComment){ }} <i class="iconfont icon-caina" title="最佳答案"></i>{{# }}}


        </div>
        <div class="detail-body jieda-body photos">
            <p>{{item.cq.content}}</p>
        </div>
        <div class="jieda-reply">
              <span class="jieda-zan zanok" type="zan">
                <i class="iconfont icon-zan"></i>
                <em>{{item.cq.praise_count}}</em>
              </span>
            <span type="reply">
                <i class="iconfont icon-svgmoban53"></i>
                回复
              </span>
            <div class="jieda-admin">
                <span type="edit">编辑</span>
                <span type="del">删除</span>
                <!-- <span class="jieda-accept" type="accept">采纳</span> -->
            </div>
        </div>
    </li>
    {{#  }); }}
    {{#} else { }}
    <li class="fly-none">消灭零回复</li>
    {{# }}}

</script>
<script src="http://cdn.bootcss.com/jquery/2.1.3/jquery.min.js"></script>
<script src="/res/js/jquery.form.js"></script>
<script src="/res/layui/layui.js"></script>
<script>
    layui.config({
        version: "3.0.0"
        , base: '../../res/mods/'
    }).extend({
        fly: 'index'
    }).use(['fly', 'face', 'laypage', 'laytpl', 'jquery'], function () {
        var form = layui.form, laypage = layui.laypage, laytpl = layui.laytpl, $ = layui.jquery, layer = layui.layer;

        dataList(1);

        /**
         * 查询数据列表
         * @param number
         */
        function dataList(number) {
            $.ajax({
                url: '/comment/question',
                method: 'post',
                dataType: 'json',
                data: {"id":${q.id}, "number": number},
                success: function (data) {
                    console.log(data);
                    if (data && data.code == 1) {
                        var listData = {"list": data.result.comments};
                        var getTpl = commentListTpl.innerHTML, view = document.getElementById('commentBodys');

                        laytpl(getTpl).render(listData, function (html) {
                            view.innerHTML = html;
                        });
                        if (number === 1) {
                            //分页标签
                            pageBar(data.result.count, 1);
                        }
                    }
                }
            });
        }

        /**
         * 数据分页
         * @param count
         * @param limit
         */
        function pageBar(count, limit) {
            var themes = ['#ff0000', '#eb4310', '#3f9337', '#219167', '#239676', '#24998d', '#1f9baa', '#0080ff', '#3366cc', '#800080', '#a1488e', '#c71585', '#bd2158'];

            laypage.render({
                elem: "page",
                limit: limit,
                count: count,
                first: '首页',
                last: '尾页',
                theme: themes[parseInt(Math.random() * themes.length)],
                layout: ['prev', 'page', 'next'],
                jump: function (obj, first) {
                    if (!first) {
                        $("#number").val(obj.curr);
                        dataList(obj.curr);
                    }
                }
            });
        }

        form.on("submit(commentAdd)", function (data) {
            $.ajax({
                url: '/comment/q/${q.id}',
                method: 'post',
                dataType: 'json',
                data: data.field,
                success: function (res) {
                    if (res.code == 1) {
                        layer.closeAll('page');
                    } else {
                        layer.alert(res.message);
                    }
                }
            });
            return false;
        });

        window.recomm = function (value) {
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

        window.as_top = function (value) {
            $.ajax({
                url: '/q/as_top',
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