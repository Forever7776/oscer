package net.oscer.controller;

import net.oscer.beans.*;
import net.oscer.common.ApiResult;
import net.oscer.dao.*;
import net.oscer.db.CacheMgr;
import net.oscer.db.DbQuery;
import net.oscer.db.TransactionService;
import net.oscer.enums.TextCheckEnum;
import net.oscer.enums.ViewEnum;
import net.oscer.framework.FormatTool;
import net.oscer.framework.StringUtils;
import net.oscer.service.UserService;
import net.oscer.service.ViewService;
import net.oscer.vo.CommentQuestionVO;
import net.oscer.vo.QuestionVO;
import net.oscer.vo.ReadVO;
import net.oscer.vo.UserCommentVO;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.oscer.beans.Question.MAX_LENGTH_TITLE;
import static net.oscer.db.Entity.STATUS_NORMAL;

/**
 * 通用api，所有端通用
 *
 * @author kz
 * @create 2019-08-10 13:06
 **/
@RequestMapping("/uni/")
@Controller
public class UniController extends BaseController {

    /**
     * 获取所有顶级节点名称
     *
     * @return
     */
    @PostMapping("first_nodes")
    @ResponseBody
    public ApiResult nodes() {
        List<Node> nodes = NodeDAO.ME.nodes(Node.STATUS_NORMAL, 0);
        Map<String, Object> map = new HashMap<>();
        map.put("nodes", nodes);
        return ApiResult.successWithObject(map);
    }

    /**
     * 获取所有节点名称
     *
     * @return
     */
    @PostMapping("all_nodes")
    @ResponseBody
    public ApiResult all_nodes() {
        List<Node> nodes = NodeDAO.ME.nodes(Node.STATUS_NORMAL, -1);
        Map<String, Object> map = new HashMap<>();
        map.put("nodes", nodes);
        return ApiResult.successWithObject(map);
    }

    /**
     * 帖子列表
     * 首页帖子列表，节点帖子列表
     *
     * @param id
     * @return
     */
    @PostMapping("q/list")
    @ResponseBody
    public ApiResult list(@RequestParam(value = "id", defaultValue = "0", required = false) Long id,
                          @RequestParam(value = "rhtml", defaultValue = "0", required = false) String rhtml) {
        Map<String, Object> map = new HashMap<>();
        //帖子列表
        List<Question> questions = QuestionDAO.ME.all(id, pageNumber, 10);
        map.put("questions", QuestionVO.list(questions, getLoginUser(), rhtml));
        //帖子总数
        int count = QuestionDAO.ME.count(id);
        map.put("count", count);
        return ApiResult.successWithObject(map);
    }

    /**
     * 帖子详情
     *
     * @param id
     * @return
     */
    @PostMapping("q/{id}")
    @ResponseBody
    public ApiResult question_detail(@PathVariable("id") Long id, @RequestParam(value = "user", required = false) Long user,
                                     @RequestParam(value = "rhtml", defaultValue = "0", required = false) String rhtml) {
        Question q = Question.ME.get(id);
        if (null == q || q.getId() <= 0L) {
            return ApiResult.failWithMessage("帖子不存在");
        }
        if (q.getStatus() != 0) {
            return ApiResult.failWithMessage("帖子已屏蔽");
        }
        User u = User.ME.get(q.getUser());
        if (null == u || u.getId() <= 0L || u.getStatus() != User.STATUS_NORMAL) {
            return ApiResult.failWithMessage("用户被屏蔽");
        }
        User loginUser = null;
        if (user != null && user > 0L) {
            loginUser = User.ME.get(user);
        } else {
            loginUser = getLoginUser();
        }
        ViewService.keyCache(q.getId(), ViewEnum.TYPE.QUESTION.getKey());
        if (loginUser != null && loginUser.status_is_normal() && loginUser.getId() != q.getUser()) {
            VisitDAO.ME.save(loginUser.getId(), q.getId(), Visit.QUESTION);
        }
        List<Question> list = Arrays.asList(q);
        Map<String, Object> map = new HashMap<>();
        map.put("q", q);
        map.put("u", u);
        map.put("detail", QuestionVO.list(list, loginUser, rhtml));
        return ApiResult.successWithObject(map);
    }

    /**
     * 评论列表
     *
     * @param id
     * @return
     */
    @PostMapping("q/comments")
    @ResponseBody
    public ApiResult question_comments(@RequestParam(value = "id", defaultValue = "0", required = false) Long id,
                                       @RequestParam(value = "rhtml", defaultValue = "0", required = false) String rhtml) {
        User login_user = getLoginUser();
        Map<String, Object> map = new HashMap<>(2);
        //评论列表 --分页
        String size = request.getParameter("size");
        int s = StringUtils.isEmpty(size) ? 10 : Integer.parseInt(size);
        List<CommentQuestion> comments = CommentQuestionDAO.ME.list(id, pageNumber, s);
        if (CacheMgr.exists(CommentQuestion.ME.CacheRegion(), "rankMap#" + id)) {
            map.put("rankMap", CacheMgr.get(CommentQuestion.ME.CacheRegion(), "rankMap#" + id));
        } else {
            //全部评论
            List<CommentQuestion> allComments = CommentQuestionDAO.ME.list(id);
            if (CollectionUtils.isNotEmpty(allComments)) {
                Map<Long, Integer> rankMap = new HashMap<>(allComments.size());
                for (int i = 0; i < allComments.size(); i++) {
                    rankMap.put(allComments.get(i).getId(), allComments.size() - i);
                }
                map.put("rankMap", rankMap);
                CacheMgr.set(CommentQuestion.ME.CacheRegion(), "rankMap#" + id, rankMap);
            }
        }
        map.put("comments", CommentQuestionVO.list(id, login_user, comments, rhtml));
        //帖子总数
        int count = CommentQuestionDAO.ME.count(id);
        map.put("count", count);
        return ApiResult.successWithObject(map);
    }

    /**
     * 删除帖子方法
     *
     * @return
     */
    @PostMapping("/q/delete/{id}")
    @ResponseBody
    public ApiResult delete(@PathVariable("id") Long id, @RequestParam(value = "user", required = false) Long user) throws Exception {
        User loginUser = null;
        if (user != null && user > 0L) {
            loginUser = User.ME.get(user);
        } else {
            loginUser = getLoginUser();
        }
        if (null == loginUser || loginUser.getStatus() != STATUS_NORMAL) {
            return ApiResult.failWithMessage("请重新登录");
        }
        if (id <= 0L) {
            return ApiResult.failWithMessage("不存在此帖子");
        }
        Question q = Question.ME.get(id);
        if (loginUser.getId() != q.getUser()) {
            return ApiResult.failWithMessage("无权限删除此贴");
        }

        long user_id = loginUser.getId();
        DbQuery.get("mysql").transaction(new TransactionService() {
            @Override
            public void execute() throws Exception {
                q.delete();
                CollectQuestionDAO.ME.deleteByQuestion(id);
                VisitDAO.ME.deleteByObjIdObjType(id, Visit.QUESTION);
                CommentQuestionDAO.ME.delete(id);
                QuestionDAO.ME.evictNode(q.getNode());
                QuestionDAO.ME.evict(user_id);
            }
        });
        return ApiResult.success();
    }

    /**
     * 回答帖子
     *
     * @param id
     * @return
     */
    @PostMapping("/user_pub_q_comment")
    @ResponseBody
    public ApiResult user_pub_q_comment(@RequestParam("id") long id, @RequestParam(value = "user", required = false) Long user) {
        User loginUser = null;
        if (user != null && user > 0L) {
            loginUser = User.ME.get(user);
        } else {
            loginUser = getLoginUser();
        }
        if (null == loginUser || loginUser.getStatus() != STATUS_NORMAL) {
            return ApiResult.failWithMessage("请重新登录");
        }
        if (id <= 0L) {
            return ApiResult.failWithMessage("该帖子不存在");
        }
        String content = param("content");
        long parent = param("parent", 0L);
        if (StringUtils.isBlank(content)) {
            return ApiResult.failWithMessage("请输入内容");
        }
        Question q = Question.ME.get(id);
        if (null == q || q.getId() <= 0L) {
            return ApiResult.failWithMessage("该帖子不存在");
        }
        CommentQuestion c = new CommentQuestion();
        c.setUser(loginUser.getId());
        c.setQuestion(id);
        content = FormatTool.text(content);
        c.setContent(FormatTool.fixContent(false, null, 0L, 0, 0, content));
        c.setParent(parent);
        c.save();
        q.setLast_comment_user(loginUser.getId());
        q.setComment_count(q.getComment_count() + 1);
        q.doUpdate();
        CommentQuestionDAO.ME.evict(id, loginUser.getId());
        return ApiResult.success();
    }

    /**
     * 收藏/取消收藏
     *
     * @param id
     * @return
     */
    @PostMapping("/q/collect")
    @ResponseBody
    public ApiResult collect(@RequestParam("id") long id, @RequestParam(value = "user", required = false) Long user) throws Exception {
        User loginUser = null;
        if (user != null && user > 0L) {
            loginUser = User.ME.get(user);
        } else {
            loginUser = getLoginUser();
        }
        if (null == loginUser || loginUser.getStatus() != STATUS_NORMAL) {
            return ApiResult.failWithMessage("请重新登录");
        }
        Question q = Question.ME.get(id);
        if (null == q) {
            return ApiResult.failWithMessage("该帖子不存在");
        }
        if (q.getStatus() != 0) {
            return ApiResult.failWithMessage("该帖子已删除");
        }
        if (q.getUser() == loginUser.getId()) {
            return ApiResult.failWithMessage("自己的帖子不能被收藏");
        }
        final String[] message = {"收藏成功"};
        User finalLoginUser = loginUser;
        CollectQuestion collectQuestion = null;
        final boolean[] collect = {true};
        DbQuery.get("mysql").transaction(new TransactionService() {
            @Override
            public void execute() throws Exception {
                CollectQuestion collectQuestion = CollectQuestionDAO.ME.getByUser(finalLoginUser.getId(), q.getId());
                if (null == collectQuestion) {
                    collectQuestion = new CollectQuestion();
                    collectQuestion.setUser(finalLoginUser.getId());
                    collectQuestion.setQuestion(id);
                    collectQuestion.save();
                    q.setCollect_count(q.getCollect_count() + 1);
                    q.doUpdate();
                } else {

                    int count = 1;
                    if (collectQuestion.getStatus() == CollectQuestion.STATUS_SHOW) {
                        collectQuestion.setStatus(CollectQuestion.STATUS_HIDE);
                        count = -1;
                        message[0] = "取消收藏成功";
                        collect[0] = false;
                    } else {
                        collectQuestion.setStatus(CollectQuestion.STATUS_SHOW);
                    }
                    q.setCollect_count(q.getCollect_count() + count);
                    q.doUpdate();
                    collectQuestion.doUpdate();
                }
            }

        });
        CollectQuestionDAO.ME.evict(loginUser.getId());
        Map<String, Object> map = new HashMap<>();
        map.put("message", message[0]);
        map.put("collect", collect[0]);
        map.put("count", q.getCollect_count());
        return ApiResult.successWithObject(map);
    }


    /**
     * 用户发表的帖子
     *
     * @param user
     * @return
     */
    @PostMapping("user_pub_articles/{user}")
    @ResponseBody
    public ApiResult user_pub_articles(@PathVariable("user") Long user) {
        List<Question> articles = QuestionDAO.ME.allByUser(user, 1);
        Map<String, Object> map = new HashMap<>(1);
        User loginUser = User.ME.get(user);
        map.put("articles", QuestionVO.list(articles, loginUser, "1"));
        return ApiResult.successWithObject(map);
    }

    /**
     * 用户发表的帖子
     *
     * @param user
     * @return
     */
    @PostMapping("user_collect_articles/{user}")
    @ResponseBody
    public ApiResult user_collect_articles(@PathVariable("user") Long user) {
        List<CollectQuestion> collects = CollectQuestionDAO.ME.list(user, CollectQuestion.STATUS_SHOW);
        if (CollectionUtils.isEmpty(collects)) {
            return ApiResult.failWithMessage("暂无收藏");
        }
        List<Long> ids = collects.stream().filter(c -> (null != c && c.getId() > 0L && c.getQuestion() > 0L)).map(CollectQuestion::getQuestion).collect(Collectors.toList());
        List<Question> list = Question.ME.loadList(ids);
        Map<String, Object> map = new HashMap<>(1);
        User loginUser = User.ME.get(user);
        map.put("collects", QuestionVO.list(list, loginUser, "1"));
        return ApiResult.successWithObject(map);
    }

    /**
     * 用户的帖子访问记录
     *
     * @param user
     * @return
     */
    @PostMapping("user_visits/{user}")
    @ResponseBody
    public ApiResult user_visits(@PathVariable("user") Long user, @RequestParam(value = "obj_type", required = false) Integer obj_type) {
        User loginUser = null;
        if (user != null && user > 0L) {
            loginUser = User.ME.get(user);
        } else {
            loginUser = getLoginUser();
        }
        if (null == loginUser || loginUser.getStatus() != STATUS_NORMAL) {
            return ApiResult.failWithMessage("请重新登录");
        }
        if (obj_type == null || obj_type <= 0) {
            obj_type = Visit.QUESTION;
        }
        List<ReadVO> readVOS = ReadVO.listByUserObjType(loginUser.getId(), obj_type);
        if (CollectionUtils.isEmpty(readVOS)) {
            return ApiResult.failWithMessage("暂无阅读记录");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("reads", readVOS);
        return ApiResult.successWithObject(map);
    }

    /**
     * 用户的帖子评论记录
     * 每个帖子取最后一条评论
     *
     * @param user
     * @return
     */
    @PostMapping("user_comments_q/{user}")
    @ResponseBody
    public ApiResult user_comments_q(@PathVariable("user") Long user) {
        User loginUser = null;
        if (user != null && user > 0L) {
            loginUser = User.ME.get(user);
        } else {
            loginUser = getLoginUser();
        }
        if (null == loginUser || loginUser.getStatus() != STATUS_NORMAL) {
            return ApiResult.failWithMessage("请重新登录");
        }
        List<CommentQuestion> comments = CommentQuestionDAO.ME.allByUser(loginUser.getId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("comments", UserCommentVO.listUserComments(comments));
        return ApiResult.successWithObject(map);
    }

    /**
     * 添加帖子方法
     *
     * @param form
     * @return
     */
    @PostMapping("/q/add")
    @ResponseBody
    public ApiResult add(Question form, @RequestParam("user") Long user) {
        User loginUser = null;
        if (user != null && user > 0L) {
            loginUser = User.ME.get(user);
        } else {
            loginUser = getLoginUser();
        }
        if (null == loginUser || loginUser.getStatus() != STATUS_NORMAL) {
            return ApiResult.failWithMessage("请重新登录");
        }
        ApiResult result = QuestionDAO.ME.check(form, 1);
        if (result == null || result.getCode() == ApiResult.fail) {
            return result;
        }
        if (form.getReward_point() > loginUser.getScore()) {
            return ApiResult.failWithMessage("积分不够哦~");
        }
        if (loginUser.getId() > 2 && QuestionDAO.ME.canPub(loginUser.getId())) {
            return ApiResult.failWithMessage("发帖太快啦");
        }
        form.setUser(loginUser.getId());
        form.setTitle(StringUtils.abbreviate(FormatTool.text(form.getTitle()), MAX_LENGTH_TITLE));
        form.setContent(net.oscer.framework.StringUtils.abbreviate(FormatTool.cleanBody(form.getContent(), false), MAX_LENGTH_TITLE));

        //百度文本审核检测
        result = UserService.content_need_check(loginUser.getId(), form.getTitle() + form.getContent(), TextCheckEnum.TYPE.BAIDU.getKey());
        if (result.getCode() == ApiResult.fail) {
            return result;
        }
        form.save();
        Node n = Node.ME.get(form.getNode());
        QuestionDAO.ME.evictNode(form.getNode());
        QuestionDAO.ME.evict(loginUser.getId());
        return ApiResult.successWithObject(n.getUrl());
    }

}
