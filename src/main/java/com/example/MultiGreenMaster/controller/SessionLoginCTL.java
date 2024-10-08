package com.example.MultiGreenMaster.controller;

import com.example.MultiGreenMaster.dto.LoginRequestFRM;
import com.example.MultiGreenMaster.entity.UserENT;
import com.example.MultiGreenMaster.entity.User_RoleENUM;
import com.example.MultiGreenMaster.service.UserSRV;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/session-login")
public class SessionLoginCTL extends SessionCheckCTL {

    @Autowired
    private UserSRV userService;

    /* 홈 페이지로 이동 */
    @GetMapping(value = {"", "/"})
    public String home(Model model) {
        return "sessionLogin/home";
    }

    /* 회원가입 페이지로 이동 */
    @GetMapping("/join")
    public String joinPage(Model model) {
        return "sessionLogin/join";
    }

    /* 로그인 페이지로 이동 */
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestFRM());
        return "sessionLogin/login";
    }

    /* 로그인 처리 */
    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestFRM loginRequest,
                        HttpServletRequest httpServletRequest, Model model, RedirectAttributes rttr) {
        UserENT user = userService.login(loginRequest);

        if (user == null) {
            rttr.addFlashAttribute("error", "로그인에 실패했습니다. 잘못된 ID 또는 비밀번호입니다.");
            return "redirect:/session-login/login";
        }

        httpServletRequest.getSession().invalidate();
        HttpSession session = httpServletRequest.getSession(true);
        session.setAttribute("userId", user.getId());
        session.setMaxInactiveInterval(1800);
        return "redirect:/session-login";
    }

    /* 사용자 정보 페이지로 이동 */
    @GetMapping("/info")
    public String userInfo(@SessionAttribute(name="userId", required = false) Long userId, Model model) {
        UserENT loginUser = userService.getLoginUserById(userId);
        if (loginUser == null) {
            return "redirect:/session-login/login";
        }
        model.addAttribute("user", loginUser);
        return "sessionLogin/info";
    }

    /* 로그아웃 처리 */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    /* 마이 페이지로 이동 */
    @GetMapping("/mypage")
    public String myPage(Model model) {
        return "sessionLogin/mypage";
    }

    /* 관리자 페이지로 이동 */
    @GetMapping("/admin")
    public String adminPage(@SessionAttribute(name="userId", required = false) Long userId, Model model) {
        UserENT loginUser = userService.getLoginUserById(userId);

        // case 1. 로그아웃 상태
        if (loginUser == null) {
            return "redirect:/session-login/login";
        }
        // case 2. 일반 사용자 계정
        if (!loginUser.getRole().equals(User_RoleENUM.ADMIN)) {
            return "redirect:/session-login/mypage";
        }
        // case 3. 관리자 계정
        return "sessionLogin/admin";
    }
}