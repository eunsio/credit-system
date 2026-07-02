package cn.in12.community.controller;

import cn.in12.common.Result;
import cn.in12.community.entity.CommunityPost;
import cn.in12.community.entity.PostReply;
import cn.in12.community.service.CommunityPostService;
import cn.in12.community.service.PostReplyService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "技术求助社区管理")
@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityPostService communityPostService;

    @Autowired
    private PostReplyService postReplyService;

    /**
     * 学生发布求助信息
     */
    @Operation(summary = "发布求助/公告信息 (学生/管理员)")
    @PostMapping("/post")
    public Result<String> createPost(@RequestBody CommunityPost post) {
        if (post.getUserId() == null) {
            return Result.error("用户ID不能为空");
        }
        boolean success = communityPostService.save(post);
        return success ? Result.success("发布成功") : Result.error("发布失败");
    }

    /**
     * 查看他人求助信息需求寻求帮助或帮助他人解决技术难题
     */
    @Operation(summary = "查看所有求助/公告信息")
    @GetMapping("/list")
    public Result<Page<CommunityPost>> list(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return Result.success(communityPostService.lambdaQuery()
                .orderByDesc(CommunityPost::getCreateTime)
                .page(new Page<>(page, size)));
    }

    /**
     * 管理员可对学生们已发布的信息进行删除（不能修改）
     */
    @Operation(summary = "删除信息 (管理员)")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable("id") Long id) {
        boolean success = communityPostService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @Operation(summary = "查看某用户发布的所有帖子")
    @GetMapping("/my-posts/{userId}")
    public Result<List<CommunityPost>> getMyPosts(@PathVariable("userId") Long userId) {
        List<CommunityPost> posts = communityPostService.lambdaQuery()
                .eq(CommunityPost::getUserId, userId)
                .orderByDesc(CommunityPost::getCreateTime)
                .list();
        return Result.success(posts);
    }

    @Operation(summary = "编辑帖子")
    @PutMapping("/update")
    public Result<String> updatePost(@RequestBody CommunityPost post) {
        if (post.getId() == null) {
            return Result.error("帖子ID不能为空");
        }
        CommunityPost exist = communityPostService.getById(post.getId());
        if (exist == null) {
            return Result.error("帖子不存在");
        }
        exist.setTitle(post.getTitle());
        exist.setContent(post.getContent());
        boolean success = communityPostService.updateById(exist);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 根据ID查看详情
     */
    @Operation(summary = "查看信息详情")
    @GetMapping("/{id}")
    public Result<CommunityPost> getById(@PathVariable("id") Long id) {
        return Result.success(communityPostService.getById(id));
    }

    // ========== 回复相关接口 ==========

    @Operation(summary = "回复帖子（学生/管理员）")
    @PostMapping("/reply")
    public Result<String> reply(@RequestBody PostReply reply) {
        if (reply.getPostId() == null || reply.getUserId() == null) {
            return Result.error("参数不完整");
        }
        if (reply.getContent() == null || reply.getContent().trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }
        boolean success = postReplyService.save(reply);
        return success ? Result.success("回复成功") : Result.error("回复失败");
    }

    @Operation(summary = "获取帖子的所有回复")
    @GetMapping("/replies/{postId}")
    public Result<List<PostReply>> getReplies(@PathVariable("postId") Long postId) {
        List<PostReply> replies = postReplyService.lambdaQuery()
                .eq(PostReply::getPostId, postId)
                .orderByAsc(PostReply::getCreateTime)
                .list();
        return Result.success(replies);
    }

    @Operation(summary = "删除回复（管理员）")
    @DeleteMapping("/reply/{id}")
    public Result<String> deleteReply(@PathVariable("id") Long id) {
        boolean success = postReplyService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
