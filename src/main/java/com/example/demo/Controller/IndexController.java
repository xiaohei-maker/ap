package com.example.demo.Controller;

import com.example.demo.Model.User;
import com.example.demo.Service.QuestionService;
import com.example.demo.Service.UserService;
import com.example.demo.cache.HotTagCache;
import com.example.demo.dto.PaginationDTO;

import com.example.uitils.Base64Utils;
import com.example.uitils.EmailUtils;
import com.example.uitils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;


import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.lang.Math.*;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTagCache hotTagCache;

    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                        @RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "tag", required = false) String tag,
                        @RequestParam(name = "sort", required = false) String sort) {
        PaginationDTO pagination = questionService.list(search, tag, sort, page, size);
        List<String> tags = hotTagCache.getHots();
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tags);
        model.addAttribute("sort", sort);
        return "index";
    }

    //
    @GetMapping("/zhuce")
    public  String zhuce(){
        return "zhuce";
    }
    //注册功能
    @RequestMapping(value = "/zhu",method = RequestMethod.POST)
    public String zhuce(HttpServletRequest request,
                        HttpServletResponse response,
                        Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                        @RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "tag", required = false) String tag,
                        @RequestParam(name = "sort", required = false) String sort) {
        HttpSession session=request.getSession();
        PaginationDTO pagination = questionService.list(search, tag, sort, page, size);
        List<String> tags = hotTagCache.getHots();
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tags);
        model.addAttribute("sort", sort);
        User user=new User();
        //生成标识
        String token= UUID.randomUUID().toString();
        String file=request.getParameter("file");
        String username=request.getParameter("username");
        String password=request.getParameter("upassword1");
        String email=request.getParameter("email");
        String sex=request.getParameter("usex");

        user.setAvatarUrl(file);
        user.setName(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setSex(sex);
        user.setAccountId(username);
        user.setToken(token);
        user.setCode(RandomUtils.createActive());

//        String b="https://tupian.qqw21.com/article/UploadPic/2014-9/201492111331720507.jpg";
//        String c="https://tupian.qqw21.com/article/UploadPic/2014-9/201492111332416492.jpg";
//        String d="https://tupian.qqw21.com/article/UploadPic/2014-9/201492111331422951.jpg";
        String b="http://pic.uuhy.com/uploads/2012/06/26/emma-watson-celebrity.jpg";
        String c="http://pic.uuhy.com/uploads/2012/06/26/emma-watson-actress.jpg";
        String d="http://pic.uuhy.com/uploads/2012/06/26/actress-emma-watson.jpg";
        String  s[]=new  String[2];
        Random random=new Random();
        Integer  f= random.nextInt(3);
        user.setAvatarUrl(s[f]);

        user.setStatus(0);

        String a=userService.createUser(user);
        if(a==null){
            return "error";
        }else {
            //session.setAttribute("userall",user);
            response.addCookie(new Cookie("token",token));
            return "registerSuccess";
        }
    }

    //激活
    @RequestMapping("/jihuo&c={code}")
    //@ResponseBody
    public  String active(@PathVariable(name = "code") String code,
                          HttpServletRequest request,
                          HttpServletResponse response) throws SQLException {
        //String codes=request.getParameter("c");
        String c= Base64Utils.decode(code);
        //String c= String.valueOf(Base64Utils.decodeFromString(request.getParameter("c")));
        Integer i=userService.selectCode(c);
        if(i==0){
            request.setAttribute("msg","激活失败！");
            // return Constants.FORWARD+"/message.jsp";
        }else if(i==1){
            request.setAttribute("msg","激活成功！");
            // return Constants.FORWARD+"/message.jsp";
        }else {
            request.setAttribute("msg","已经激活！");
            // return Constants.FORWARD+"/message.jsp";
        }
        //3.响应
        //request.getParameter("registerMsg")
        return "denglu";
    }

    //登录功能
    @GetMapping("/denglu")
    public  String denglu(){
        return  "denglu";
    }

    //登录功能
    @RequestMapping(value = "/log")
//    @ResponseBody
    public String denglu(
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model,
//                         String ins,
//                         String ua,
//                         String pd,
//                         String cd,
//                         String auto,
                         @RequestParam(name = "page", defaultValue = "1") Integer page,
                         @RequestParam(name = "size", defaultValue = "10") Integer size,
                         @RequestParam(name = "search", required = false) String search,
                         @RequestParam(name = "tag", required = false) String tag,
                         @RequestParam(name = "sort", required = false) String sort) {
        PaginationDTO pagination = questionService.list(search, tag, sort, page, size);
        List<String> tags = hotTagCache.getHots();
        model.addAttribute("pagination", pagination);
        model.addAttribute("search", search);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", tags);
        model.addAttribute("sort", sort);
        String token= UUID.randomUUID().toString();
        String username=request.getParameter("uname");
        String password=request.getParameter("password");
        String auto = request.getParameter("auto");
        String code = request.getParameter("code");
        String code1 = request.getParameter("code1");
        HttpSession session=request.getSession();
        //String codes = ins.toLowerCase();
        //String code = ins;
        if (code.equals(code1)) {
            User user = userService.LoginUser(username);
                        if (user == null) {
                            return "denglu";
                        } else {
                            if(user.getStatus().equals(0)){
                                EmailUtils.sendEmail(user);
                                return  "registerSuccess";
                            }else {
                                session.setAttribute("loginUser", user);
                                // Cookie cookie = new Cookie("autoUser", Base64Utils.encode(username + ":" + password));
                                Cookie cookie=new Cookie("token",user.getToken());
                                cookie.setMaxAge(60 * 60 * 24 * 14);
                                response.addCookie(cookie);
                                return "redirect:/";
                            }
                        }
                    }else{
                        return "denglu";


        }

        //                if (auto == null) {
        //                    //String name=session.user.g;
        //                   // Cookie cookie=new Cookie("token",null);
        //                    //response.addCookie(new Cookie("token",token));
        //                    Cookie cookie=new Cookie("token",user.getToken());
        //                    response.addCookie(cookie);
        //                    return "redirect:/";
        //                } else {
        //                    // Cookie cookie = new Cookie("autoUser", Base64Utils.encode(username + ":" + password));
        //                    Cookie cookie=new Cookie("token",user.getToken());
        //                    cookie.setMaxAge(60 * 60 * 24 * 14);
        //                    response.addCookie(cookie);
        //                    return "redirect:/";
        //                }




//            User user = userService.LoginUser(username);
//            if (user == null) {
//                return "denglu";
//            } else {
//                session.setAttribute("loginUser", user);
//
//                    // Cookie cookie = new Cookie("autoUser", Base64Utils.encode(username + ":" + password));
//                    Cookie cookie=new Cookie("token",user.getToken());
//                    cookie.setMaxAge(60 * 60 * 24 * 14);
//                    response.addCookie(cookie);
//                    return "index";
//
//            }

    }

    @RequestMapping("/r")
    @ResponseBody
    public String r(HttpServletRequest request, HttpServletResponse response) {
        String username = request.getParameter("username");
        String a=userService.selectUsername(username);
        return a;
    }
}
