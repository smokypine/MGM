package com.example.MultiGreenMaster.controller;

import com.example.MultiGreenMaster.dto.CMRecommentForm;
import com.example.MultiGreenMaster.entity.FreeBoard_CommentENT;
import com.example.MultiGreenMaster.entity.CMRecomment;
import com.example.MultiGreenMaster.service.FreeBoard_CommentSRV;
import com.example.MultiGreenMaster.service.CMRecommentSRV;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/recomments") // "/recomments" 경로와 매핑
public class CMRecommentCTL {

    private static final Logger logger = LoggerFactory.getLogger(CMRecommentCTL.class); // 로그 설정

    @Autowired
    private CMRecommentSRV cmRecommentService;

    @Autowired
    private FreeBoard_CommentSRV cmCommentService; // CMComment 서비스

    @PostMapping("/new") // POST 요청을 "/new" 경로와 매핑
    public String createRecomment(CMRecommentForm form) {
        logger.info("Request to create new recomment: {}", form); // 새 대댓글 생성 요청
        FreeBoard_CommentENT comment = cmCommentService.findCommentById(form.getCmCommentId().getId()); // 댓글 ID로 댓글 조회

        if (comment != null) {
            CMRecomment recomment = new CMRecomment(); // 새로운 대댓글 생성
            recomment.setCmComment(comment); // 대댓글에 댓글 설정
            recomment.setContent(form.getContent()); // 대댓글 내용 설정
            recomment.setLikeCount(0); // 기본 좋아요 수 0으로 설정
            cmRecommentService.saveRecomment(recomment); // 대댓글 저장
            logger.info("Recomment saved successfully: {}", recomment); // 대댓글 저장 완료
        }

        return "redirect:/posts/" + form.getCmCommentId().getCmPost().getId(); // 게시글 상세 페이지로 리디렉션
    }

    @GetMapping("/comment/{commentId}") // GET 요청을 "/comment/{commentId}" 경로와 매핑
    public String listRecomments(@PathVariable Long commentId, Model model) {
        logger.info("Requesting recomment list: Comment ID {}", commentId); // 대댓글 목록 요청
        List<CMRecomment> recomments = cmRecommentService.findRecommentsByCommentId(commentId); // 댓글의 모든 대댓글 조회
        model.addAttribute("recomments", recomments); // 대댓글 리스트를 모델에 추가
        logger.info("Recomment list retrieved successfully: Comment ID {}", commentId); // 대댓글 목록 조회 완료
        return "recommentList"; // "recommentList" 뷰를 반환
    }

    @PostMapping("/{id}/like")
    @ResponseBody
    public int likeRecomment(@PathVariable Long id) {
        logger.info("Request to increase like count: Recomment ID {}", id); // 대댓글 좋아요 증가 요청
        CMRecomment recomment = cmRecommentService.findRecommentById(id);
        cmRecommentService.incrementLikeCount(id);
        return recomment != null ? recomment.getLikeCount() : 0;
    }
}
